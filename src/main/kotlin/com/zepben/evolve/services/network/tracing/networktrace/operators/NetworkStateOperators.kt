/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.services.network.tracing.networktrace.NetworkTrace
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStepPathProvider

/**
 * Interface providing access to and operations on specific network state properties and functions for items within a network.
 * This interface consolidates several other state operator interfaces, enabling unified management of operations for a network state.
 * Refer to the individual state operator interfaces for detailed information on each available operation.
 *
 * Although this is an open interface allowing for custom implementations, this is generally unnecessary. The standard
 * instances, [NetworkStateOperators.NORMAL] for the normal state and [NetworkStateOperators.CURRENT] for the current state,
 * should suffice for most use cases.
 *
 * This interface is primarily utilized by the [NetworkTrace], enabling trace definitions to be reused across different network states.
 * By using this interface, you can apply identical conditions and steps without needing to track which state is active
 * or creating redundant trace implementations for different network states.
 */
interface NetworkStateOperators :
    OpenStateOperators,
    FeederDirectionStateOperations,
    EquipmentContainerStateOperators,
    InServiceStateOperators,
    PhaseStateOperators,
    ConnectivityStateOperators {

    companion object {
        /**
         * Instance that operates on the normal state of network objects.
         */
        @JvmField
        val NORMAL: NetworkStateOperators = NormalNetworkStateOperators()

        /**
         * Instance that operates on the current state of network objects.
         */
        @JvmField
        val CURRENT: NetworkStateOperators = CurrentNetworkStateOperators()
    }
}

private class NormalNetworkStateOperators : NetworkStateOperators,
    OpenStateOperators by OpenStateOperators.NORMAL,
    FeederDirectionStateOperations by FeederDirectionStateOperations.NORMAL,
    EquipmentContainerStateOperators by EquipmentContainerStateOperators.NORMAL,
    InServiceStateOperators by InServiceStateOperators.NORMAL,
    PhaseStateOperators by PhaseStateOperators.NORMAL {

    private val networkTraceStepPathProvider = NetworkTraceStepPathProvider(this)

    override fun nextPaths(path: NetworkTraceStep.Path): Sequence<NetworkTraceStep.Path> =
        networkTraceStepPathProvider.nextPaths(path)
}

private class CurrentNetworkStateOperators : NetworkStateOperators,
    OpenStateOperators by OpenStateOperators.CURRENT,
    FeederDirectionStateOperations by FeederDirectionStateOperations.CURRENT,
    EquipmentContainerStateOperators by EquipmentContainerStateOperators.CURRENT,
    InServiceStateOperators by InServiceStateOperators.CURRENT,
    PhaseStateOperators by PhaseStateOperators.CURRENT {

    private val networkTraceStepPathProvider = NetworkTraceStepPathProvider(this)

    override fun nextPaths(path: NetworkTraceStep.Path): Sequence<NetworkTraceStep.Path> =
        networkTraceStepPathProvider.nextPaths(path)
}
