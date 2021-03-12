/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.services.network.testdata.LoopingNetwork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.util.*

class DownstreamTreeTest {

    @Test
    fun treeNodeTest() {
        val treeNodes: MutableList<DownstreamTree.TreeNode> = ArrayList()
        for (i in 0..9) {
            val node = DownstreamTree.TreeNode(Junction("node$i"))
            treeNodes.add(node)
            assertThat(node.conductingEquipment().mRID, equalTo("node$i"))
            assertThat(node.parent(), nullValue())
            assertThat(node.children().size, equalTo(0))
        }

        treeNodes[1].setParent(treeNodes[0])
        treeNodes[2].setParent(treeNodes[0])
        treeNodes[3].setParent(treeNodes[0])
        treeNodes[4].setParent(treeNodes[3])
        treeNodes[5].setParent(treeNodes[3])
        treeNodes[6].setParent(treeNodes[5])
        treeNodes[7].setParent(treeNodes[6])
        treeNodes[8].setParent(treeNodes[7])
        treeNodes[9].setParent(treeNodes[8])

        val children = treeNodes[0].children()
        assertThat(children.contains(treeNodes[1]), equalTo(true))
        assertThat(children.contains(treeNodes[2]), equalTo(true))
        assertThat(children.contains(treeNodes[3]), equalTo(true))

        assertChildren(treeNodes, intArrayOf(3, 0, 0, 2, 0, 1, 1, 1, 1, 0))
        assertParents(treeNodes, intArrayOf(-1, 0, 0, 0, 3, 3, 5, 6, 7, 8))
    }

    @Test
    fun downstreamTreeTest() {
        val n = LoopingNetwork.create()

        Tracing.setPhases().run(n)

        val start: ConductingEquipment = n["node1"]!!
        assertThat(start, notNullValue())
        val root = Tracing.normalDownstreamTree().run(start)

        assertThat(root, notNullValue())
        assertTreeAsset(root, n["node1"], null, arrayOf(n["acLineSegment1"], n["acLineSegment3"]))

        var testNode = root.children()[0]
        assertTreeAsset(testNode, n["acLineSegment1"], n["node1"], arrayOf(n["node2"]))

        testNode = testNode.children()[0]
        assertTreeAsset(testNode, n["node2"], n["acLineSegment1"], arrayOf(n["acLineSegment2"]))

        testNode = testNode.children()[0]
        assertTreeAsset(testNode, n["acLineSegment2"], n["node2"], arrayOf(n["node3"]))

        testNode = testNode.children()[0]
        assertTreeAsset(testNode, n["node3"], n["acLineSegment2"], arrayOf(n["acLineSegment4"]))

        testNode = testNode.children()[0]
        assertTreeAsset(testNode, n["acLineSegment4"], n["node3"], arrayOf(n["node6"]))

        testNode = testNode.children()[0]
        assertTreeAsset(testNode, n["node6"], n["acLineSegment4"], arrayOf())

        testNode = root.children()[1]
        assertTreeAsset(testNode, n["acLineSegment3"], n["node1"], arrayOf(n["node4"]))

        testNode = testNode.children()[0]
        assertTreeAsset(testNode, n["node4"], n["acLineSegment3"], arrayOf(n["acLineSegment5"], n["acLineSegment6"]))

        assertThat(findNodes(root, "node0").size, equalTo(0))
        assertThat(findNodes(root, "acLineSegment0").size, equalTo(0))
        assertThat(findNodes(root, "node1").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment1").size, equalTo(1))
        assertThat(findNodes(root, "node2").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment2").size, equalTo(1))
        assertThat(findNodes(root, "node3").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment3").size, equalTo(1))
        assertThat(findNodes(root, "node4").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment4").size, equalTo(1))
        assertThat(findNodes(root, "node5").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment5").size, equalTo(1))
        assertThat(findNodes(root, "node6").size, equalTo(2))
        assertThat(findNodes(root, "acLineSegment6").size, equalTo(1))
        assertThat(findNodes(root, "node7").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment7").size, equalTo(1))
        assertThat(findNodes(root, "node8").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment8").size, equalTo(1))
        assertThat(findNodes(root, "node9").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment9").size, equalTo(1))
        assertThat(findNodes(root, "node10").size, equalTo(1))
        assertThat(findNodes(root, "acLineSegment10").size, equalTo(1))
        assertThat(findNodes(root, "node11").size, equalTo(3))
        assertThat(findNodes(root, "acLineSegment11").size, equalTo(3))
        assertThat(findNodes(root, "node12").size, equalTo(3))
        assertThat(findNodes(root, "acLineSegment12").size, equalTo(4))
        assertThat(findNodes(root, "node13").size, equalTo(3))
        assertThat(findNodes(root, "acLineSegment13").size, equalTo(3))
        assertThat(findNodes(root, "node14").size, equalTo(4))
        assertThat(findNodes(root, "acLineSegment14").size, equalTo(3))
        assertThat(findNodes(root, "acLineSegment15").size, equalTo(4))
        assertThat(findNodes(root, "acLineSegment16").size, equalTo(4))

        assertThat(findNodeDepths(root, "node0"), equalTo(emptyList<Any>()))
        assertThat(findNodeDepths(root, "acLineSegment0"), equalTo(emptyList<Any>()))
        assertThat(findNodeDepths(root, "node1"), equalTo(listOf(0)))
        assertThat(findNodeDepths(root, "acLineSegment1"), equalTo(listOf(1)))
        assertThat(findNodeDepths(root, "node2"), equalTo(listOf(2)))
        assertThat(findNodeDepths(root, "acLineSegment2"), equalTo(listOf(3)))
        assertThat(findNodeDepths(root, "node3"), equalTo(listOf(4)))
        assertThat(findNodeDepths(root, "acLineSegment3"), equalTo(listOf(1)))
        assertThat(findNodeDepths(root, "node4"), equalTo(listOf(2)))
        assertThat(findNodeDepths(root, "acLineSegment4"), equalTo(listOf(5)))
        assertThat(findNodeDepths(root, "node5"), equalTo(listOf(4)))
        assertThat(findNodeDepths(root, "acLineSegment5"), equalTo(listOf(3)))
        assertThat(findNodeDepths(root, "node6"), equalTo(listOf(6, 10)))
        assertThat(findNodeDepths(root, "acLineSegment6"), equalTo(listOf(3)))
        assertThat(findNodeDepths(root, "node7"), equalTo(listOf(4)))
        assertThat(findNodeDepths(root, "acLineSegment7"), equalTo(listOf(9)))
        assertThat(findNodeDepths(root, "node8"), equalTo(listOf(6)))
        assertThat(findNodeDepths(root, "acLineSegment8"), equalTo(listOf(5)))
        assertThat(findNodeDepths(root, "node9"), equalTo(listOf(8)))
        assertThat(findNodeDepths(root, "acLineSegment9"), equalTo(listOf(7)))
        assertThat(findNodeDepths(root, "node10"), equalTo(listOf(6)))
        assertThat(findNodeDepths(root, "acLineSegment10"), equalTo(listOf(5)))
        assertThat(findNodeDepths(root, "node11"), equalTo(listOf(8, 10, 12)))
        assertThat(findNodeDepths(root, "acLineSegment11"), equalTo(listOf(7, 11, 13)))
        assertThat(findNodeDepths(root, "node12"), equalTo(listOf(8, 10, 10)))
        assertThat(findNodeDepths(root, "acLineSegment12"), equalTo(listOf(7, 10, 11, 14)))
        assertThat(findNodeDepths(root, "node13"), equalTo(listOf(10, 12, 12)))
        assertThat(findNodeDepths(root, "acLineSegment13"), equalTo(listOf(9, 9, 11)))
        assertThat(findNodeDepths(root, "node14"), equalTo(listOf(8, 9, 12, 13)))
        assertThat(findNodeDepths(root, "acLineSegment14"), equalTo(listOf(9, 11, 11)))
        assertThat(findNodeDepths(root, "acLineSegment15"), equalTo(listOf(7, 10, 12, 13)))
        assertThat(findNodeDepths(root, "acLineSegment16"), equalTo(listOf(8, 9, 11, 14)))
    }

    private fun assertChildren(treeNodes: List<DownstreamTree.TreeNode>, childCounts: IntArray) {
        for (i in treeNodes.indices)
            assertThat(treeNodes[i].children().size, equalTo(childCounts[i]))
    }

    private fun assertParents(treeNodes: List<DownstreamTree.TreeNode>, parents: IntArray) {
        for (i in treeNodes.indices) {
            if (parents[i] < 0)
                assertThat(treeNodes[i].parent(), nullValue())
            else
                assertThat(treeNodes[i].parent(), equalTo(treeNodes[parents[i]]))
        }
    }

    private fun assertTreeAsset(
        treeNode: DownstreamTree.TreeNode,
        asset: ConductingEquipment?,
        parent: ConductingEquipment?,
        children: Array<ConductingEquipment?>
    ) {
        assertThat(treeNode.conductingEquipment(), equalTo(asset))

        if (parent != null) {
            val treeParent = treeNode.parent()
            assertThat(treeParent, notNullValue())
            assertThat(treeParent!!.conductingEquipment(), equalTo(parent))
        } else
            assertThat(treeNode.parent(), nullValue())

        assertThat(treeNode.children().size, equalTo(children.size))
        for (i in children.indices) {
            assertThat(treeNode.children()[i].conductingEquipment(), equalTo(children[i]))
        }
    }

    private fun findNodes(root: DownstreamTree.TreeNode, assetId: String): List<DownstreamTree.TreeNode> {
        val matches: MutableList<DownstreamTree.TreeNode> = ArrayList()
        val processNodes: Deque<DownstreamTree.TreeNode> = ArrayDeque()
        processNodes.addLast(root)

        while (processNodes.size > 0) {
            val node = Objects.requireNonNull(processNodes.pollFirst())
            if (node.conductingEquipment().mRID == assetId)
                matches.add(node)

            node.children().forEach(processNodes::addLast)
        }

        return matches
    }

    private fun findNodeDepths(root: DownstreamTree.TreeNode, assetId: String): List<Int> {
        val nodes = findNodes(root, assetId)
        val depths: MutableList<Int> = ArrayList()

        nodes.forEach { depths.add(depthInTree(it)) }

        return depths
    }

    private fun depthInTree(treeNode: DownstreamTree.TreeNode): Int {
        var depth = -1
        var node: DownstreamTree.TreeNode? = treeNode
        while (node != null) {
            node = node.parent()
            ++depth
        }

        return depth
    }

}
