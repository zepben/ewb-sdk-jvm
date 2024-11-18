/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.actions

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.testdata.LoopingNetwork
import com.zepben.evolve.services.network.testdata.addFeederDirections
import com.zepben.evolve.services.network.tracing.feeder.DirectionLogger
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.StepPath
import com.zepben.evolve.services.network.tracing.networktrace.Tracing
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.downstream
import com.zepben.evolve.services.network.tracing.networktrace.run
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*

internal class EquipmentTreeBuilderTest {
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    fun `computes initial value`() {
        val builder = EquipmentTreeBuilder()
        val path = mockk<StepPath>()
        val ce = mockk<ConductingEquipment>()
        every { path.toEquipment } returns ce
        val initialValue = builder.computeInitialValue(NetworkTraceStep(path, Unit))
        assertThat(initialValue.parent, nullValue())
        assertThat(initialValue.identifiedObject, sameInstance(ce))
        assertThat(initialValue.children, empty())

        // Make sure that if a step with the same equipment is computed initially it returns the same tree node, not a new node
        val initialValue2 = builder.computeInitialValue(NetworkTraceStep(path, Unit))
        assertThat(initialValue2, sameInstance(initialValue))
    }

    @Test
    fun `compute next value returns existing value on internal step`() {
        val nextPath = mockk<StepPath>()
        every { nextPath.tracedInternally } returns true
        val nextItem = mockk<NetworkTraceStep<*>>()
        val treeNode = mockk<TreeNode<ConductingEquipment>>()

        val builder = EquipmentTreeBuilder()
        val nextNode = builder.computeNextValue(NetworkTraceStep(nextPath, Unit), nextItem, treeNode)
        assertThat(nextNode, sameInstance(treeNode))
    }

    @Test
    fun `compute next value returns new tree node on external step`() {
        val ce = mockk<ConductingEquipment>()
        val nextPath = mockk<StepPath>()
        every { nextPath.tracedInternally } returns false
        every { nextPath.toEquipment } returns ce
        val nextItem = mockk<NetworkTraceStep<*>>()
        val currentNode = mockk<TreeNode<ConductingEquipment>>()

        val builder = EquipmentTreeBuilder()
        val nextNode = builder.computeNextValueTyped(NetworkTraceStep(nextPath, Unit), nextItem, currentNode)
        assertThat(nextNode.parent, sameInstance(currentNode))
        assertThat(nextNode.identifiedObject, sameInstance(ce))
        assertThat(nextNode.children, empty())
    }

    @Test
    fun `add child to parent on applying step`() {
        val builder = EquipmentTreeBuilder()
        val item = mockk<NetworkTraceStep<*>>()
        val ce = mockk<ConductingEquipment>()
        val node = mockk<TreeNode<ConductingEquipment>>()
        val parent = mockk<TreeNode<ConductingEquipment>>()
        every { node.parent } returns parent
        justRun { parent.addChild(node) }
        val context = mockk<StepContext>()
        every { context.getValue<TreeNode<ConductingEquipment>>(builder.key) } returns node
        builder.apply(item, context)

        verify(exactly = 1) { parent.addChild(node) }
    }

    @Test
    fun `full tree integration test`() {
        val n = LoopingNetwork.create()

        n.get<ConductingEquipment>("j0")!!.addFeederDirections().also { DirectionLogger.trace(it) }

        val start: ConductingEquipment = n["j1"]!!
        assertThat(start, Matchers.notNullValue())
        val treeBuilder = EquipmentTreeBuilder()
        Tracing.networkTraceBranching()
            .addNetworkCondition { downstream() }
            .addStepAction(treeBuilder)
            .run(start)

        val root = treeBuilder.roots.first()

        assertThat(root, Matchers.notNullValue())
        assertTreeAsset(root, n["j1"], null, arrayOf(n["acLineSegment1"], n["acLineSegment3"]))

        var testNode = root.children[0]
        assertTreeAsset(testNode, n["acLineSegment1"], n["j1"], arrayOf(n["j2"]))

        testNode = testNode.children[0]
        assertTreeAsset(testNode, n["j2"], n["acLineSegment1"], arrayOf(n["acLineSegment2"]))

        testNode = testNode.children[0]
        assertTreeAsset(testNode, n["acLineSegment2"], n["j2"], arrayOf(n["j3"]))

        testNode = testNode.children[0]
        assertTreeAsset(testNode, n["j3"], n["acLineSegment2"], arrayOf(n["acLineSegment4"]))

        testNode = testNode.children[0]
        assertTreeAsset(testNode, n["acLineSegment4"], n["j3"], arrayOf(n["j6"]))

        testNode = testNode.children[0]
        assertTreeAsset(testNode, n["j6"], n["acLineSegment4"], arrayOf())

        testNode = root.children[1]
        assertTreeAsset(testNode, n["acLineSegment3"], n["j1"], arrayOf(n["j4"]))

        testNode = testNode.children[0]
        assertTreeAsset(testNode, n["j4"], n["acLineSegment3"], arrayOf(n["acLineSegment5"], n["acLineSegment6"]))

        assertThat(findNodes(root, "j0"), hasSize(0))
        assertThat(findNodes(root, "acLineSegment0"), hasSize(0))
        assertThat(findNodes(root, "j1"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment1"), hasSize(1))
        assertThat(findNodes(root, "j2"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment2"), hasSize(1))
        assertThat(findNodes(root, "j3"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment3"), hasSize(1))
        assertThat(findNodes(root, "j4"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment4"), hasSize(1))
        assertThat(findNodes(root, "j5"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment5"), hasSize(1))
        assertThat(findNodes(root, "j6"), hasSize(2))
        assertThat(findNodes(root, "acLineSegment6"), hasSize(1))
        assertThat(findNodes(root, "j7"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment7"), hasSize(1))
        assertThat(findNodes(root, "j8"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment8"), hasSize(1))
        assertThat(findNodes(root, "j9"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment9"), hasSize(1))
        assertThat(findNodes(root, "j10"), hasSize(1))
        assertThat(findNodes(root, "acLineSegment10"), hasSize(1))

        // NOTE: We short circuit reprocessing loops to avoid reprocessing on unlikely weird looping connectivity
        //       which can cause massive computation blowout on large networks. This being the case, because j10-t2
        //       gets run first, j12-t1 only ends up with an UPSTREAM because when j10-t3 goes to be run, it already
        //       has BOTH and thus does not try to continue. The issue with this is that j12-t1 is never visited
        //       in the opposing direction and thus not flowing both the other way around the loop back to j10-t2.
        assertThat(findNodes(root, "j11"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "acLineSegment11"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "j12"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "acLineSegment12"), hasSize(4))
        assertThat(findNodes(root, "j13"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "acLineSegment13"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "j14"), hasSize(4))
        assertThat(findNodes(root, "acLineSegment14"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "acLineSegment15"), hasSize(4))
        assertThat(findNodes(root, "acLineSegment16"), hasSize(4))

        assertThat(findNodeDepths(root, "j0"), equalTo(emptyList<Any>()))
        assertThat(findNodeDepths(root, "acLineSegment0"), equalTo(emptyList<Any>()))
        assertThat(findNodeDepths(root, "j1"), equalTo(listOf(0)))
        assertThat(findNodeDepths(root, "acLineSegment1"), equalTo(listOf(1)))
        assertThat(findNodeDepths(root, "j2"), equalTo(listOf(2)))
        assertThat(findNodeDepths(root, "acLineSegment2"), equalTo(listOf(3)))
        assertThat(findNodeDepths(root, "j3"), equalTo(listOf(4)))
        assertThat(findNodeDepths(root, "acLineSegment3"), equalTo(listOf(1)))
        assertThat(findNodeDepths(root, "j4"), equalTo(listOf(2)))
        assertThat(findNodeDepths(root, "acLineSegment4"), equalTo(listOf(5)))
        assertThat(findNodeDepths(root, "j5"), equalTo(listOf(4)))
        assertThat(findNodeDepths(root, "acLineSegment5"), equalTo(listOf(3)))
        assertThat(findNodeDepths(root, "j6"), equalTo(listOf(6, 10)))
        assertThat(findNodeDepths(root, "acLineSegment6"), equalTo(listOf(3)))
        assertThat(findNodeDepths(root, "j7"), equalTo(listOf(4)))
        assertThat(findNodeDepths(root, "acLineSegment7"), equalTo(listOf(9)))
        assertThat(findNodeDepths(root, "j8"), equalTo(listOf(6)))
        assertThat(findNodeDepths(root, "acLineSegment8"), equalTo(listOf(5)))
        assertThat(findNodeDepths(root, "j9"), equalTo(listOf(8)))
        assertThat(findNodeDepths(root, "acLineSegment9"), equalTo(listOf(7)))
        assertThat(findNodeDepths(root, "j10"), equalTo(listOf(6)))
        assertThat(findNodeDepths(root, "acLineSegment10"), equalTo(listOf(5)))
        assertThat(findNodeDepths(root, "j11"), equalTo(listOf(8))) // Would have been 8, 10, 12 if the intermediate loop was reprocessed.
        assertThat(findNodeDepths(root, "acLineSegment11"), equalTo(listOf(7))) // Would have been 7, 11, 13 if the intermediate loop was reprocessed.
        assertThat(findNodeDepths(root, "j12"), equalTo(listOf(10))) // Would have been 8, 10, 10 if the intermediate loop was reprocessed.
        assertThat(findNodeDepths(root, "acLineSegment12"), equalTo(listOf(7, 10, 11, 14)))
        assertThat(findNodeDepths(root, "j13"), equalTo(listOf(12))) // Would have been 10, 12, 12 if the intermediate loop was reprocessed.
        assertThat(findNodeDepths(root, "acLineSegment13"), equalTo(listOf(9))) // Would have been 9, 9, 11 if the intermediate loop was reprocessed.
        assertThat(findNodeDepths(root, "j14"), equalTo(listOf(8, 9, 12, 13)))
        assertThat(findNodeDepths(root, "acLineSegment14"), equalTo(listOf(11))) // Would have been 9, 11, 11 if the intermediate loop was reprocessed.
        assertThat(findNodeDepths(root, "acLineSegment15"), equalTo(listOf(7, 10, 12, 13)))
        assertThat(findNodeDepths(root, "acLineSegment16"), equalTo(listOf(8, 9, 11, 14)))
    }

    private fun assertTreeAsset(
        treeNode: TreeNode<ConductingEquipment>,
        asset: ConductingEquipment?,
        parent: ConductingEquipment?,
        children: Array<ConductingEquipment?>
    ) {
        assertThat(treeNode.identifiedObject, equalTo(asset))

        if (parent != null) {
            val treeParent = treeNode.parent
            assertThat(treeParent, Matchers.notNullValue())
            assertThat(treeParent!!.identifiedObject, equalTo(parent))
        } else
            assertThat(treeNode.parent, Matchers.nullValue())

        assertThat(treeNode.children, hasSize(children.size))
        for (i in children.indices) {
            assertThat(treeNode.children[i].identifiedObject, equalTo(children[i]))
        }
    }

    private fun findNodes(root: TreeNode<ConductingEquipment>, assetId: String): List<TreeNode<ConductingEquipment>> {
        val matches = mutableListOf<TreeNode<ConductingEquipment>>()
        val processNodes = ArrayDeque<TreeNode<ConductingEquipment>>()
        processNodes.addLast(root)

        while (processNodes.size > 0) {
            val node = Objects.requireNonNull(processNodes.pollFirst())
            if (node.identifiedObject.mRID == assetId)
                matches.add(node)

            node.children.forEach(processNodes::addLast)
        }

        return matches
    }

    private fun findNodeDepths(root: TreeNode<ConductingEquipment>, assetId: String): List<Int> {
        val nodes = findNodes(root, assetId)
        val depths = mutableListOf<Int>()

        nodes.forEach { depths.add(depthInTree(it)) }

        return depths
    }

    private fun depthInTree(treeNode: TreeNode<ConductingEquipment>): Int {
        var depth = -1
        var node: TreeNode<ConductingEquipment>? = treeNode
        while (node != null) {
            node = node.parent
            ++depth
        }

        return depth
    }
}
