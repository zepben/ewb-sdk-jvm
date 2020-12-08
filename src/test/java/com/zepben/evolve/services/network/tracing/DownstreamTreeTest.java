/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing;

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment;
import com.zepben.evolve.cim.iec61970.base.wires.Junction;
import com.zepben.evolve.services.network.NetworkService;
import com.zepben.evolve.services.network.testdata.TestNetworks;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DownstreamTreeTest {

    private NetworkService n = null;

    @Test
    public void treeNodeTest() {
        List<DownstreamTree.TreeNode> treeNodes = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            DownstreamTree.TreeNode node = new DownstreamTree.TreeNode(new Junction("node" + i));
            treeNodes.add(node);
            assertThat(node.conductingEquipment().getMRID(), equalTo("node" + i));
            assertThat(node.parent(), nullValue());
            assertThat(node.children().size(), equalTo(0));
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
        assertThat(children.contains(treeNodes.get(1)), equalTo(true));
        assertThat(children.contains(treeNodes.get(2)), equalTo(true));
        assertThat(children.contains(treeNodes.get(3)), equalTo(true));

        assertChildren(treeNodes, new int[]{3, 0, 0, 2, 0, 1, 1, 1, 1, 0});
        assertParents(treeNodes, new int[]{-1, 0, 0, 0, 3, 3, 5, 6, 7, 8});
    }

    @Test
    public void downstreamTreeTest() {
        n = TestNetworks.getNetwork(4);

        Tracing.setPhases().run(n);

        ConductingEquipment start = get("node1");
        assertThat(start, notNullValue());
        DownstreamTree.TreeNode root = Tracing.normalDownstreamTree().run(start);

        assertThat(root, notNullValue());
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

        assertThat(findNodes(root, "node0").size(), equalTo(0));
        assertThat(findNodes(root, "acLineSegment0").size(), equalTo(0));
        assertThat(findNodes(root, "node1").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment1").size(), equalTo(1));
        assertThat(findNodes(root, "node2").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment2").size(), equalTo(1));
        assertThat(findNodes(root, "node3").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment3").size(), equalTo(1));
        assertThat(findNodes(root, "node4").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment4").size(), equalTo(1));
        assertThat(findNodes(root, "node5").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment5").size(), equalTo(1));
        assertThat(findNodes(root, "node6").size(), equalTo(2));
        assertThat(findNodes(root, "acLineSegment6").size(), equalTo(1));
        assertThat(findNodes(root, "node7").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment7").size(), equalTo(1));
        assertThat(findNodes(root, "node8").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment8").size(), equalTo(1));
        assertThat(findNodes(root, "node9").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment9").size(), equalTo(1));
        assertThat(findNodes(root, "node10").size(), equalTo(1));
        assertThat(findNodes(root, "acLineSegment10").size(), equalTo(1));
        assertThat(findNodes(root, "node11").size(), equalTo(3));
        assertThat(findNodes(root, "acLineSegment11").size(), equalTo(3));
        assertThat(findNodes(root, "node12").size(), equalTo(3));
        assertThat(findNodes(root, "acLineSegment12").size(), equalTo(4));
        assertThat(findNodes(root, "node13").size(), equalTo(3));
        assertThat(findNodes(root, "acLineSegment13").size(), equalTo(3));
        assertThat(findNodes(root, "node14").size(), equalTo(4));
        assertThat(findNodes(root, "acLineSegment14").size(), equalTo(3));
        assertThat(findNodes(root, "acLineSegment15").size(), equalTo(4));
        assertThat(findNodes(root, "acLineSegment16").size(), equalTo(4));

        assertThat(findNodeDepths(root, "node0"), equalTo(Collections.emptyList()));
        assertThat(findNodeDepths(root, "acLineSegment0"), equalTo(Collections.emptyList()));
        assertThat(findNodeDepths(root, "node1"), equalTo(Collections.singletonList(0)));
        assertThat(findNodeDepths(root, "acLineSegment1"), equalTo(Collections.singletonList(1)));
        assertThat(findNodeDepths(root, "node2"), equalTo(Collections.singletonList(2)));
        assertThat(findNodeDepths(root, "acLineSegment2"), equalTo(Collections.singletonList(3)));
        assertThat(findNodeDepths(root, "node3"), equalTo(Collections.singletonList(4)));
        assertThat(findNodeDepths(root, "acLineSegment3"), equalTo(Collections.singletonList(1)));
        assertThat(findNodeDepths(root, "node4"), equalTo(Collections.singletonList(2)));
        assertThat(findNodeDepths(root, "acLineSegment4"), equalTo(Collections.singletonList(5)));
        assertThat(findNodeDepths(root, "node5"), equalTo(Collections.singletonList(4)));
        assertThat(findNodeDepths(root, "acLineSegment5"), equalTo(Collections.singletonList(3)));
        assertThat(findNodeDepths(root, "node6"), equalTo(Arrays.asList(6, 10)));
        assertThat(findNodeDepths(root, "acLineSegment6"), equalTo(Collections.singletonList(3)));
        assertThat(findNodeDepths(root, "node7"), equalTo(Collections.singletonList(4)));
        assertThat(findNodeDepths(root, "acLineSegment7"), equalTo(Collections.singletonList(9)));
        assertThat(findNodeDepths(root, "node8"), equalTo(Collections.singletonList(6)));
        assertThat(findNodeDepths(root, "acLineSegment8"), equalTo(Collections.singletonList(5)));
        assertThat(findNodeDepths(root, "node9"), equalTo(Collections.singletonList(8)));
        assertThat(findNodeDepths(root, "acLineSegment9"), equalTo(Collections.singletonList(7)));
        assertThat(findNodeDepths(root, "node10"), equalTo(Collections.singletonList(6)));
        assertThat(findNodeDepths(root, "acLineSegment10"), equalTo(Collections.singletonList(5)));
        assertThat(findNodeDepths(root, "node11"), equalTo(Arrays.asList(8, 10, 12)));
        assertThat(findNodeDepths(root, "acLineSegment11"), equalTo(Arrays.asList(7, 11, 13)));
        assertThat(findNodeDepths(root, "node12"), equalTo(Arrays.asList(8, 10, 10)));
        assertThat(findNodeDepths(root, "acLineSegment12"), equalTo(Arrays.asList(7, 10, 11, 14)));
        assertThat(findNodeDepths(root, "node13"), equalTo(Arrays.asList(10, 12, 12)));
        assertThat(findNodeDepths(root, "acLineSegment13"), equalTo(Arrays.asList(9, 9, 11)));
        assertThat(findNodeDepths(root, "node14"), equalTo(Arrays.asList(8, 9, 12, 13)));
        assertThat(findNodeDepths(root, "acLineSegment14"), equalTo(Arrays.asList(9, 11, 11)));
        assertThat(findNodeDepths(root, "acLineSegment15"), equalTo(Arrays.asList(7, 10, 12, 13)));
        assertThat(findNodeDepths(root, "acLineSegment16"), equalTo(Arrays.asList(8, 9, 11, 14)));
    }

    private void assertChildren(List<DownstreamTree.TreeNode> treeNodes, int[] childCounts) {
        for (int i = 0; i < treeNodes.size(); ++i)
            assertThat(treeNodes.get(i).children().size(), equalTo(childCounts[i]));
    }

    private void assertParents(List<DownstreamTree.TreeNode> treeNodes, int[] parents) {
        for (int i = 0; i < treeNodes.size(); ++i) {
            int index = parents[i];
            if (index < 0)
                assertThat(treeNodes.get(i).parent(), nullValue());
            else
                assertThat(treeNodes.get(i).parent(), equalTo(treeNodes.get(parents[i])));
        }
    }

    private void assertTreeAsset(DownstreamTree.TreeNode treeNode, ConductingEquipment asset, ConductingEquipment parent, ConductingEquipment[] children) {
        assertThat(treeNode.conductingEquipment(), equalTo(asset));

        if (parent != null) {
            DownstreamTree.TreeNode treeParent = treeNode.parent();
            assertThat(treeParent, notNullValue());
            assertThat(treeParent.conductingEquipment(), equalTo(parent));
        } else
            assertThat(treeNode.parent(), nullValue());

        assertThat(treeNode.children().size(), equalTo(children.length));
        for (int i = 0; i < children.length; ++i) {
            assertThat(treeNode.children().get(i).conductingEquipment(), equalTo(children[i]));
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
