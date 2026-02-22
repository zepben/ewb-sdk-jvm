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
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectCreation
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectDeletion
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ObjectModification
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.services.variant.VariantServiceComparator
import com.zepben.ewb.services.variant.testdata.fillFields
import com.zepben.ewb.services.variant.translator.toPb
import com.zepben.ewb.services.variant.translator.variantObject
import com.zepben.ewb.streaming.get.testservices.TestVariantConsumerService
import com.zepben.ewb.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.protobuf.vc.GetChangeSetResponse
import com.zepben.protobuf.vc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.vc.GetIdentifiedObjectsResponse
import com.zepben.protobuf.vc.VariantConsumerGrpc
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.*
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

internal class VariantConsumerClientTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()

    private val consumerService = TestVariantConsumerService()

    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spy(VariantConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor()))
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val consumerClient = spy(VariantConsumerClient(stub).apply { addErrorHandler(onErrorHandler) })
    private val service = consumerClient.service

    private val serverException = IllegalStateException("custom message")

    @BeforeEach
    internal fun beforeEach() {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(consumerService).build().start())
    }

    private fun responseOf(objects: List<Identifiable>): MutableIterator<GetIdentifiedObjectsResponse> {
        val responses = mutableListOf<GetIdentifiedObjectsResponse>()
        objects.forEach {
            responses.add(GetIdentifiedObjectsResponse.newBuilder().apply { addIdentifiableObjects(variantObject(it)) }.build())
        }
        return responses.iterator()
    }

    @Test
    internal fun `can get change set`() {

        val expectedService = VariantService()
        val changeSet = ChangeSet("test").fillFields(expectedService).also { expectedService.add(it) }

        consumerService.onGetChangeSets =
            spy { _, response -> response.onNext(GetChangeSetResponse.newBuilder().setChangeSet(changeSet.toPb()).build()) }
        consumerService.onGetIdentifiedObjects =
            spy { request, response ->
                responseOf(request.mridsList.map { expectedService[it]!! }).forEach { response.onNext((it)) }
            }

        val result = consumerClient.getChangeSet("test").throwOnError()

        verify(consumerService.onGetIdentifiedObjects, times(2)).invoke(isA<GetIdentifiedObjectsRequest>(), any())
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
        val project = NetworkModelProject("project").also { expectedService.add(it); it.addChild(stage); stage.parent = it }
        val changeSet = ChangeSet("changeSet").also { expectedService.add(it); it.networkModelProjectStage = stage; stage.changeSet = it }
        val dependentOn = AnnotatedProjectDependency("dependentOn").also { expectedService.add(it); it.dependencyDependentOnStage = stage; stage.addDependentOnStage(it) }
        val creation = ObjectCreation().also { it.changeSet = changeSet; it.targetObjectMRID = "target1"; changeSet.addMember(it); expectedService.add(it)}
        val deletion = ObjectDeletion().also { it.changeSet = changeSet; it.targetObjectMRID = "target2"; changeSet.addMember(it); expectedService.add(it)}
        val modification = ObjectModification().also { it.changeSet = changeSet; it.targetObjectMRID = "target3"; it.populateReverseModification(expectedService); changeSet.addMember(it); expectedService.add(it)}

        val stage2 = NetworkModelProjectStage("stage2").also { expectedService.add(it) }
        val changeSet2 = ChangeSet("changeSet2").also { expectedService.add(it); it.networkModelProjectStage = stage2; stage2.changeSet = it }
        val depending = AnnotatedProjectDependency("depending").also { expectedService.add(it); it.dependencyDependingStage= stage2; stage2.addDependingStage(it) }

        consumerService.onGetChangeSets =
            spy { _, response -> response.onNext(GetChangeSetResponse.newBuilder().setChangeSet(changeSet.toPb()).build()) }
        consumerService.onGetIdentifiedObjects =
            spy { request, response ->
                responseOf(request.mridsList.map { expectedService[it]!! }).forEach { response.onNext((it)) }
            }

        val result = consumerClient.getChangeSet("changeSet").throwOnError()

        assertThat("getChangeSet should succeed", result.wasSuccessful)
        val differences = VariantServiceComparator().compare(expectedService, consumerClient.service)
        assertThat("unexpected objects found in read service: ${differences.missingFromTarget()}", differences.missingFromTarget(), containsInAnyOrder(changeSet2.mRID, stage2.mRID, depending.mRID))
        assertThat("unexpected modifications ${differences.modifications()}", differences.modifications(), anEmptyMap())
        assertThat("objects missing from read service: ${differences.missingFromSource()}", differences.missingFromSource(), empty())

        val result2 = consumerClient.getChangeSet("changeSet2").throwOnError()
        assertThat("getChangeSet should succeed", result2.wasSuccessful)
        val differences2 = VariantServiceComparator().compare(expectedService, consumerClient.service)
        assertThat("unexpected objects found in read service: ${differences2.missingFromTarget()}", differences2.missingFromTarget(), empty())
        assertThat("unexpected modifications ${differences2.modifications()}", differences2.modifications(), anEmptyMap())
        assertThat("objects missing from read service: ${differences2.missingFromSource()}", differences2.missingFromSource(), empty())
    }
}
