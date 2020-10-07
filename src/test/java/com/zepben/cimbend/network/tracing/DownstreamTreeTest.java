/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing;

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.cimbend.cim.iec61970.base.wires.Junction;
import com.zepben.cimbend.network.NetworkService;
import com.zepben.cimbend.testdata.TestNetworks;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DownstreamTreeTest {

    private NetworkService n = null;

    @Test
    public void treeNodeTest() {
        List<DownstreamTree.TreeNode> treeNodes = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            DownstreamTree.TreeNode node = new DownstreamTree.TreeNode(new Junction("node" + i));
            treeNodes.add(node);
            assertEquals("node" + i, node.conductingEquipment().getMRID());
            assertNull(node.parent());
            assertEquals(0, node.children().size());
        }

        treeNodes.get(1).setParent(treeNodes.get(0));
        treeNodes.get(2).setParent(treeNodes.get(0));
        treeNodes.get(3).setParent(treeNodes.get(0));
        treeNodes.get(4).setParent(treeNodes.get(3));
        treeNodes.get(5).setParent(treeNodes.get(3));
        treeNodes.get(6).setParent(treeNodes.get(5));
        treeNodes.get(7).setParent(treeNodes.get(6));
        treeNodes.get(8).setParent(treeNodes.get(7));
        treeNodes.get(9).setParent(treeNodes.get(8));

        List<DownstreamTree.TreeNode> children = treeNodes.get(0).children();
        assertTrue(children.contains(treeNodes.get(1)));
        assertTrue(children.contains(treeNodes.get(2)));
        assertTrue(children.contains(treeNodes.get(3)));

        assertChildren(treeNodes, new int[]{3, 0, 0, 2, 0, 1, 1, 1, 1, 0});
        assertParents(treeNodes, new int[]{-1, 0, 0, 0, 3, 3, 5, 6, 7, 8});
    }

    @Test
    public void downstreamTreeTest() {
        n = TestNetworks.getNetwork(4);

        Tracing.setPhases().run(n);

        ConductingEquipment start = get("node1");
        assertNotNull(start);
        DownstreamTree.TreeNode root = Tracing.normalDownstreamTree().run(start);

        assertNotNull(root);
        assertTreeAsset(root, get("node1"), null, new ConductingEquipment[]{get("acLineSegment1"), get("acLineSegment3")});

        DownstreamTree.TreeNode testNode = root.children().get(0);
        assertTreeAsset(testNode, get("acLineSegment1"), get("node1"), new ConductingEquipment[]{get("node2")});

        testNode = testNode.children().get(0);
        assertTreeAsset(testNode, get("node2"), get("acLineSegment1"), new ConductingEquipment[]{get("acLineSegment2")});

        testNode = testNode.children().get(0);
        assertTreeAsset(testNode, get("acLineSegment2"), get("node2"), new ConductingEquipment[]{get("node3")});

        testNode = testNode.children().get(0);
        assertTreeAsset(testNode, get("node3"), get("acLineSegment2"), new ConductingEquipment[]{get("acLineSegment4")});

        testNode = testNode.children().get(0);
        assertTreeAsset(testNode, get("acLineSegment4"), get("node3"), new ConductingEquipment[]{get("node6")});

        testNode = testNode.children().get(0);
        assertTreeAsset(testNode, get("node6"), get("acLineSegment4"), new ConductingEquipment[]{});

        testNode = root.children().get(1);
        assertTreeAsset(testNode, get("acLineSegment3"), get("node1"), new ConductingEquipment[]{get("node4")});

        testNode = testNode.children().get(0);
        assertTreeAsset(testNode, get("node4"), get("acLineSegment3"), new ConductingEquipment[]{get("acLineSegment5"), get("acLineSegment6")});

        assertEquals(0, findNodes(root, "node0").size());
        assertEquals(0, findNodes(root, "acLineSegment0").size());
        assertEquals(1, findNodes(root, "node1").size());
        assertEquals(1, findNodes(root, "acLineSegment1").size());
        assertEquals(1, findNodes(root, "node2").size());
        assertEquals(1, findNodes(root, "acLineSegment2").size());
        assertEquals(1, findNodes(root, "node3").size());
        assertEquals(1, findNodes(root, "acLineSegment3").size());
        assertEquals(1, findNodes(root, "node4").size());
        assertEquals(1, findNodes(root, "acLineSegment4").size());
        assertEquals(1, findNodes(root, "node5").size());
        assertEquals(1, findNodes(root, "acLineSegment5").size());
        assertEquals(2, findNodes(root, "node6").size());
        assertEquals(1, findNodes(root, "acLineSegment6").size());
        assertEquals(1, findNodes(root, "node7").size());
        assertEquals(1, findNodes(root, "acLineSegment7").size());
        assertEquals(1, findNodes(root, "node8").size());
        assertEquals(1, findNodes(root, "acLineSegment8").size());
        assertEquals(1, findNodes(root, "node9").size());
        assertEquals(1, findNodes(root, "acLineSegment9").size());
        assertEquals(1, findNodes(root, "node10").size());
        assertEquals(1, findNodes(root, "acLineSegment10").size());
        assertEquals(3, findNodes(root, "node11").size());
        assertEquals(3, findNodes(root, "acLineSegment11").size());
        assertEquals(3, findNodes(root, "node12").size());
        assertEquals(4, findNodes(root, "acLineSegment12").size());
        assertEquals(3, findNodes(root, "node13").size());
        assertEquals(3, findNodes(root, "acLineSegment13").size());
        assertEquals(4, findNodes(root, "node14").size());
        assertEquals(3, findNodes(root, "acLineSegment14").size());
        assertEquals(4, findNodes(root, "acLineSegment15").size());
        assertEquals(4, findNodes(root, "acLineSegment16").size());

        assertEquals(Collections.emptyList(), findNodeDepths(root, "node0"));
        assertEquals(Collections.emptyList(), findNodeDepths(root, "acLineSegment0"));
        assertEquals(Collections.singletonList(0), findNodeDepths(root, "node1"));
        assertEquals(Collections.singletonList(1), findNodeDepths(root, "acLineSegment1"));
        assertEquals(Collections.singletonList(2), findNodeDepths(root, "node2"));
        assertEquals(Collections.singletonList(3), findNodeDepths(root, "acLineSegment2"));
        assertEquals(Collections.singletonList(4), findNodeDepths(root, "node3"));
        assertEquals(Collections.singletonList(1), findNodeDepths(root, "acLineSegment3"));
        assertEquals(Collections.singletonList(2), findNodeDepths(root, "node4"));
        assertEquals(Collections.singletonList(5), findNodeDepths(root, "acLineSegment4"));
        assertEquals(Collections.singletonList(4), findNodeDepths(root, "node5"));
        assertEquals(Collections.singletonList(3), findNodeDepths(root, "acLineSegment5"));
        assertEquals(Arrays.asList(6, 10), findNodeDepths(root, "node6"));
        assertEquals(Collections.singletonList(3), findNodeDepths(root, "acLineSegment6"));
        assertEquals(Collections.singletonList(4), findNodeDepths(root, "node7"));
        assertEquals(Collections.singletonList(9), findNodeDepths(root, "acLineSegment7"));
        assertEquals(Collections.singletonList(6), findNodeDepths(root, "node8"));
        assertEquals(Collections.singletonList(5), findNodeDepths(root, "acLineSegment8"));
        assertEquals(Collections.singletonList(8), findNodeDepths(root, "node9"));
        assertEquals(Collections.singletonList(7), findNodeDepths(root, "acLineSegment9"));
        assertEquals(Collections.singletonList(6), findNodeDepths(root, "node10"));
        assertEquals(Collections.singletonList(5), findNodeDepths(root, "acLineSegment10"));
        assertEquals(Arrays.asList(8, 10, 12), findNodeDepths(root, "node11"));
        assertEquals(Arrays.asList(7, 11, 13), findNodeDepths(root, "acLineSegment11"));
        assertEquals(Arrays.asList(8, 10, 10), findNodeDepths(root, "node12"));
        assertEquals(Arrays.asList(7, 10, 11, 14), findNodeDepths(root, "acLineSegment12"));
        assertEquals(Arrays.asList(10, 12, 12), findNodeDepths(root, "node13"));
        assertEquals(Arrays.asList(9, 9, 11), findNodeDepths(root, "acLineSegment13"));
        assertEquals(Arrays.asList(8, 9, 12, 13), findNodeDepths(root, "node14"));
        assertEquals(Arrays.asList(9, 11, 11), findNodeDepths(root, "acLineSegment14"));
        assertEquals(Arrays.asList(7, 10, 12, 13), findNodeDepths(root, "acLineSegment15"));
        assertEquals(Arrays.asList(8, 9, 11, 14), findNodeDepths(root, "acLineSegment16"));
    }

    private void assertChildren(List<DownstreamTree.TreeNode> treeNodes, int[] childCounts) {
        for (int i = 0; i < treeNodes.size(); ++i)
            assertEquals(childCounts[i], treeNodes.get(i).children().size());
    }

    private void assertParents(List<DownstreamTree.TreeNode> treeNodes, int[] parents) {
        for (int i = 0; i < treeNodes.size(); ++i) {
            int index = parents[i];
            if (index < 0)
                assertNull(treeNodes.get(i).parent());
            else
                assertEquals(treeNodes.get(parents[i]), treeNodes.get(i).parent());
        }
    }

    private void assertTreeAsset(DownstreamTree.TreeNode treeNode, ConductingEquipment asset, ConductingEquipment parent, ConductingEquipment[] children) {
        assertEquals(asset, treeNode.conductingEquipment());

        if (parent != null) {
            DownstreamTree.TreeNode treeParent = treeNode.parent();
            assertNotNull(treeParent);
            assertEquals(parent, treeParent.conductingEquipment());
        } else
            assertNull(treeNode.parent());

        assertEquals(children.length, treeNode.children().size());
        for (int i = 0; i < children.length; ++i) {
            assertEquals(children[i], treeNode.children().get(i).conductingEquipment());
        }
    }

    private ConductingEquipment get(String id) {
        assert (n != null);
        return n.get(ConductingEquipment.class, id);
    }

    private List<DownstreamTree.TreeNode> findNodes(DownstreamTree.TreeNode root, String assetId) {
        List<DownstreamTree.TreeNode> matches = new ArrayList<>();
        Deque<DownstreamTree.TreeNode> processNodes = new ArrayDeque<>();
        processNodes.addLast(root);

        while (processNodes.size() > 0) {
            DownstreamTree.TreeNode node = Objects.requireNonNull(processNodes.pollFirst());
            if (node.conductingEquipment().getMRID().equals(assetId))
                matches.add(node);

            node.children().forEach(processNodes::addLast);
        }

        return matches;
    }

    private List<Integer> findNodeDepths(DownstreamTree.TreeNode root, String assetId) {
        List<DownstreamTree.TreeNode> nodes = findNodes(root, assetId);
        List<Integer> depths = new ArrayList<>();

        nodes.forEach(n -> depths.add(depthInTree(n)));

        return depths;
    }

    private int depthInTree(DownstreamTree.TreeNode treeNode) {
        int depth = -1;
        DownstreamTree.TreeNode node = treeNode;
        while (node != null) {
            node = node.parent();
            ++depth;
        }

        return depth;
    }

}
