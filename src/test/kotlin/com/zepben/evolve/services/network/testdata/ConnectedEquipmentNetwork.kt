/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.services.network.NetworkService

object ConnectedEquipmentNetwork {

    //
    // n1--s2--s1--start--s3--s4--n2
    //     bo  no         co  bo
    //
    // bo = both open
    // no = normally open
    // co = currently open
    //
    fun create() = NetworkService().also { n ->
        val start = createNodeForConnecting(n, "start", 2)
        val s1 = createSwitchForConnecting(n, "s1", 2).apply { setNormallyOpen(true) }
        val s2 = createSwitchForConnecting(n, "s2", 2).apply { setNormallyOpen(true).setOpen(true) }
        val n1 = createNodeForConnecting(n, "n1", 1)
        val s3 = createSwitchForConnecting(n, "s3", 2).apply { setOpen(true) }
        val s4 = createSwitchForConnecting(n, "s4", 2).apply { setNormallyOpen(true).setOpen(true) }
        val n2 = createNodeForConnecting(n, "n2", 1)

        n.connect(start.terminals[0], s1.terminals[0])
        n.connect(s1.terminals[1], s2.terminals[0])
        n.connect(s2.terminals[1], n1.terminals[0])
        n.connect(start.terminals[1], s3.terminals[0])
        n.connect(s3.terminals[1], s4.terminals[0])
        n.connect(s4.terminals[1], n2.terminals[0])
    }

}
