/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.tree

import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.services.network.tracing.networktrace.actions.TreeNode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class TreeNodeTest {

    @Test
    internal fun accessors() {
        val treeNode0 = TreeNode(Junction("j0"), null)
        val treeNode1 = TreeNode(Junction("j1"), treeNode0)
        val treeNode2 = TreeNode(Junction("j2"), treeNode0)
        val treeNode3 = TreeNode(Junction("j3"), treeNode0)
        val treeNode4 = TreeNode(Junction("j4"), treeNode3)
        val treeNode5 = TreeNode(Junction("j5"), treeNode3)
        val treeNode6 = TreeNode(Junction("j6"), treeNode5)
        val treeNode7 = TreeNode(Junction("j7"), treeNode6)
        val treeNode8 = TreeNode(Junction("j8"), treeNode7)
        val treeNode9 = TreeNode(Junction("j9"), treeNode8)

        assertThat(treeNode0.identifiedObject.mRID, equalTo("j0"))
        assertThat(treeNode0.parent, nullValue())

        treeNode0.addChild(treeNode1)
        treeNode0.addChild(treeNode2)
        treeNode0.addChild(treeNode3)
        treeNode3.addChild(treeNode4)
        treeNode3.addChild(treeNode5)
        treeNode5.addChild(treeNode6)
        treeNode6.addChild(treeNode7)
        treeNode7.addChild(treeNode8)
        treeNode8.addChild(treeNode9)

        assertThat(treeNode0.children, contains(treeNode1, treeNode2, treeNode3))

        val treeNodes = listOf(treeNode0, treeNode1, treeNode2, treeNode3, treeNode4, treeNode5, treeNode6, treeNode7, treeNode8, treeNode9)
        assertChildren(treeNodes, intArrayOf(3, 0, 0, 2, 0, 1, 1, 1, 1, 0))
        assertParents(treeNodes, intArrayOf(-1, 0, 0, 0, 3, 3, 5, 6, 7, 8))
    }

    private fun assertChildren(treeNodes: List<TreeNode<*>>, childCounts: IntArray) {
        for (i in treeNodes.indices)
            assertThat(treeNodes[i].children, hasSize(childCounts[i]))
    }

    private fun assertParents(treeNodes: List<TreeNode<*>>, parents: IntArray) {
        for (i in treeNodes.indices) {
            if (parents[i] < 0)
                assertThat(treeNodes[i].parent, nullValue())
            else
                assertThat(treeNodes[i].parent, equalTo(treeNodes[parents[i]]))
        }
    }

}
