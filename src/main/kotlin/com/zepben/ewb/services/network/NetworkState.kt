/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network

/**
 * Indicates which state of the network an operation should be performed on.
 */
enum class NetworkState {

    /**
     * The operation should be performed on all states of the network.
     */
    ALL,

    /**
     * The operation should be performed on the normal state of the network.
     */
    NORMAL,

    /**
     * The operation should be performed on the current state of the network.
     */
    CURRENT,

}
