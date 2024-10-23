/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.tree

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.services.network.testdata.LoopingNetwork
import com.zepben.evolve.services.network.testdata.addFeederDirections
import com.zepben.evolve.services.network.tracing.feeder.DirectionLogger
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*

internal class DownstreamTreeTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun downstreamTreeTest() {
        val n = LoopingNetwork.create()

        n.get<ConductingEquipment>("j0")!!.addFeederDirections().also { DirectionLogger.trace(it) }

        val start: ConductingEquipment = n["j1"]!!
        assertThat(start, notNullValue())
        val root = DownstreamTree(NetworkStateOperators.NORMAL).run(start)

        assertThat(root, notNullValue())
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
        assertThat(findNodes(root, "j11"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "acLineSegment11"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "j12"), hasSize(3))
        assertThat(findNodes(root, "acLineSegment12"), hasSize(4))
        assertThat(findNodes(root, "j13"), hasSize(3))
        assertThat(findNodes(root, "acLineSegment13"), hasSize(1)) // Would have been 3 if the intermediate loop was reprocessed.
        assertThat(findNodes(root, "j14"), hasSize(4))
        assertThat(findNodes(root, "acLineSegment14"), hasSize(3))
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
        assertThat(findNodeDepths(root, "j12"), equalTo(listOf(8, 10, 10)))
        assertThat(findNodeDepths(root, "acLineSegment12"), equalTo(listOf(7, 10, 11, 14)))
        assertThat(findNodeDepths(root, "j13"), equalTo(listOf(10, 12, 12)))
        assertThat(findNodeDepths(root, "acLineSegment13"), equalTo(listOf(9))) // Would have been 9, 9, 11 if the intermediate loop was reprocessed.
        assertThat(findNodeDepths(root, "j14"), equalTo(listOf(8, 9, 12, 13)))
        assertThat(findNodeDepths(root, "acLineSegment14"), equalTo(listOf(9, 11, 11)))
        assertThat(findNodeDepths(root, "acLineSegment15"), equalTo(listOf(7, 10, 12, 13)))
        assertThat(findNodeDepths(root, "acLineSegment16"), equalTo(listOf(8, 9, 11, 14)))
    }

    private fun assertTreeAsset(
        treeNode: TreeNode,
        asset: ConductingEquipment?,
        parent: ConductingEquipment?,
        children: Array<ConductingEquipment?>
    ) {
        assertThat(treeNode.conductingEquipment, equalTo(asset))

        if (parent != null) {
            val treeParent = treeNode.parent
            assertThat(treeParent, notNullValue())
            assertThat(treeParent!!.conductingEquipment, equalTo(parent))
        } else
            assertThat(treeNode.parent, nullValue())

        assertThat(treeNode.children, hasSize(children.size))
        for (i in children.indices) {
            assertThat(treeNode.children[i].conductingEquipment, equalTo(children[i]))
        }
    }

    private fun findNodes(root: TreeNode, assetId: String): List<TreeNode> {
        val matches = mutableListOf<TreeNode>()
        val processNodes = ArrayDeque<TreeNode>()
        processNodes.addLast(root)

        while (processNodes.size > 0) {
            val node = Objects.requireNonNull(processNodes.pollFirst())
            if (node.conductingEquipment.mRID == assetId)
                matches.add(node)

            node.children.forEach(processNodes::addLast)
        }

        return matches
    }

    private fun findNodeDepths(root: TreeNode, assetId: String): List<Int> {
        val nodes = findNodes(root, assetId)
        val depths = mutableListOf<Int>()

        nodes.forEach { depths.add(depthInTree(it)) }

        return depths
    }

    private fun depthInTree(treeNode: TreeNode): Int {
        var depth = -1
        var node: TreeNode? = treeNode
        while (node != null) {
            node = node.parent
            ++depth
        }

        return depth
    }

}
