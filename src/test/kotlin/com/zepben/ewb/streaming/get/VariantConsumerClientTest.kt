/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.VariantServiceComparator
import com.zepben.ewb.services.variant.testdata.fillFields
import com.zepben.ewb.services.variant.translator.variantObject
import com.zepben.ewb.streaming.get.testservices.TestVariantConsumerService
import com.zepben.ewb.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.protobuf.vc.*
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.testing.GrpcCleanupRule
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.concurrent.Executors

internal class VariantConsumerClientTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()

    private val consumerService = TestVariantConsumerService()

    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spyk(VariantConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor()))
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient = spyk(VariantConsumerClient(stub, VariantService()).apply { addErrorHandler(onErrorHandler) })

    @BeforeEach
    internal fun beforeEach() {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(consumerService).build().start())
    }

    private fun responseOf(obj: Identifiable): GetIdentifiedObjectsResponse {
        return GetIdentifiedObjectsResponse.newBuilder().apply { addIdentifiableObjects(variantObject(obj)) }.build()
    }

    private fun responseOfChangeSet(objects: Iterable<Identifiable>): MutableIterator<GetChangeSetResponse> {
        val responses = mutableListOf<GetChangeSetResponse>()
        objects.forEach {
            responses.add(GetChangeSetResponse.newBuilder().apply { identifiableObject = variantObject(it) }.build())
        }
        return responses.iterator()
    }

    @Test
    fun `constructor coverage`() {
        VariantConsumerClient(channel = mockk<GrpcChannel>(relaxed = true), variantService = mockk(), callCredentials = mockk())
    }

    @Test
    internal fun `can get change set`() {

        val expectedService = VariantService()
        ChangeSet("test").fillFields(expectedService).also { expectedService.add(it) }

        consumerService.onGetChangeSet =
            spyk<(GetChangeSetRequest, StreamObserver<GetChangeSetResponse>) -> Unit>(@JvmSerializableLambda { _, response ->
                val changeSetObjects = (expectedService.sequenceOf<ChangeSet>() + expectedService.sequenceOf<ChangeSetMember>())
                responseOfChangeSet(changeSetObjects.toList()).forEach { response.onNext(it) }
            })
        consumerService.onGetIdentifiedObjects =
            spyk<(GetIdentifiedObjectsRequest, StreamObserver<GetIdentifiedObjectsResponse>) -> Unit>(@JvmSerializableLambda { request, response ->
                request.mridsList.forEach {
                    responseOf(expectedService[it]!!).also { response.onNext(it) }
                }
            })

        val result = consumerClient.getChangeSet("test").throwOnError()

        verify(exactly = 1) { consumerService.onGetIdentifiedObjects(any(), any()) }

        assertThat("getChangeSet should succeed", result.wasSuccessful)
        val differences = VariantServiceComparator().compare(expectedService, consumerClient.service)
        assertThat("unexpected objects found in read service: ${differences.missingFromTarget()}", differences.missingFromTarget(), empty())
        assertThat("unexpected modifications ${differences.modifications()}", differences.modifications(), anEmptyMap())
        assertThat("objects missing from read service: ${differences.missingFromSource()}", differences.missingFromSource(), empty())
    }

    @Test
    internal fun `get change set with all references`() {

        val expectedService = VariantService()
        val stage = NetworkModelProjectStage("stage").also { expectedService.add(it) }
        val dependingStage = NetworkModelProjectStage("dependingStage").also { expectedService.add(it) }
        NetworkModelProject("project").also { expectedService.add(it); it.addChild(stage); stage.parent = it }
        val changeSet = ChangeSet("changeSet").also { expectedService.add(it); it.networkModelProjectStage = stage; stage.changeSet = it }
        AnnotatedProjectDependency(generateId()).also {
            expectedService.add(it); it.dependencyDependentOnStage = stage; stage.addDependency(it); dependingStage.addDependency(it)
        }
        val creation = ObjectCreation().also { it.changeSet = changeSet; it.targetObjectMRID = "target1"; changeSet.addMember(it); expectedService.add(it) }
        val deletion = ObjectDeletion().also { it.changeSet = changeSet; it.targetObjectMRID = "target2"; changeSet.addMember(it); expectedService.add(it) }
        val modification =
            ObjectModification().also { it.changeSet = changeSet; it.targetObjectMRID = "target3"; changeSet.addMember(it); expectedService.add(it) }

        val dependentOnStage = NetworkModelProjectStage("otherStage").also { expectedService.add(it) }
        val stage2 = NetworkModelProjectStage("stage2").also { expectedService.add(it) }
        val changeSet2 = ChangeSet("changeSet2").also { expectedService.add(it); it.networkModelProjectStage = stage2; stage2.changeSet = it }
        val depending = AnnotatedProjectDependency(generateId()).also {
            expectedService.add(it); it.dependencyDependingStage = stage2; stage2.addDependency(it); dependentOnStage.addDependency(it)
        }

        consumerService.onGetChangeSet =
            spyk<(GetChangeSetRequest, StreamObserver<GetChangeSetResponse>) -> Unit>(@JvmSerializableLambda { _, response ->
                // Note we mimic the server behaviour here of sending the whole contents of the variants database, but that requires us to
                // explicitly filter these out of the getIdentifiedObjects responses below so that we don't accidentally retrieve them via the unresolved references.
                val changeSetObjects = listOf(changeSet, creation, deletion, modification)
                responseOfChangeSet(changeSetObjects).forEach { response.onNext(it) }
            })
        consumerService.onGetIdentifiedObjects =
            spyk<(GetIdentifiedObjectsRequest, StreamObserver<GetIdentifiedObjectsResponse>) -> Unit>(@JvmSerializableLambda { request, response ->
                request.mridsList.forEach {
                    if (it !in setOf(creation.mRID, deletion.mRID, modification.mRID)) {
                        responseOf(expectedService[it]!!).also { response.onNext(it) }
                    }
                }
            })

        val result = consumerClient.getChangeSet("changeSet").throwOnError()

        assertThat("getChangeSet should succeed", result.wasSuccessful)
        val differences = VariantServiceComparator().compare(expectedService, consumerClient.service)
        assertThat(
            "unexpected objects found in read service: ${differences.missingFromTarget()}",
            differences.missingFromTarget(),
            containsInAnyOrder(changeSet2.mRID, stage2.mRID, depending.mRID, dependentOnStage.mRID, dependingStage.mRID)
        )
        assertThat("unexpected modifications ${differences.modifications()}", differences.modifications(), anEmptyMap())
        assertThat("objects missing from read service: ${differences.missingFromSource()}", differences.missingFromSource(), empty())

        consumerService.onGetChangeSet =
            spyk<(GetChangeSetRequest, StreamObserver<GetChangeSetResponse>) -> Unit>(@JvmSerializableLambda { _, response ->
                val changeSetObjects = listOf(changeSet2)
                responseOfChangeSet(changeSetObjects).forEach { response.onNext(it) }
            })

        val result2 = consumerClient.getChangeSet("changeSet2").throwOnError()
        assertThat("getChangeSet should succeed", result2.wasSuccessful)
        val differences2 = VariantServiceComparator().compare(expectedService, consumerClient.service)
        assertThat(
            "unexpected objects found in read service: ${differences2.missingFromTarget()}",
            differences2.missingFromTarget(),
            containsInAnyOrder(dependentOnStage.mRID, dependingStage.mRID)
        )
        assertThat("unexpected modifications ${differences2.modifications()}", differences2.modifications(), anEmptyMap())
        assertThat("objects missing from read service: ${differences2.missingFromSource()}", differences2.missingFromSource(), empty())
    }
}
