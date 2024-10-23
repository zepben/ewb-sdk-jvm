/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

interface NetworkStateOperators :
    OpenStateOperators,
    FeederDirectionStateOperations,
    EquipmentContainerStateOperations,
    InServiceStateOperators,
    PhaseStateOperators {

    companion object {
        val NORMAL: NetworkStateOperators = object : NetworkStateOperators,
            OpenStateOperators by OpenStateOperators.NORMAL,
            FeederDirectionStateOperations by FeederDirectionStateOperations.NORMAL,
            EquipmentContainerStateOperations by EquipmentContainerStateOperations.NORMAL,
            InServiceStateOperators by InServiceStateOperators.NORMAL,
            PhaseStateOperators by PhaseStateOperators.NORMAL {}

        val CURRENT: NetworkStateOperators = object : NetworkStateOperators,
            OpenStateOperators by OpenStateOperators.CURRENT,
            FeederDirectionStateOperations by FeederDirectionStateOperations.CURRENT,
            EquipmentContainerStateOperations by EquipmentContainerStateOperations.CURRENT,
            InServiceStateOperators by InServiceStateOperators.CURRENT,
            PhaseStateOperators by PhaseStateOperators.CURRENT {}
    }
}
