/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services

import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.network.NetworkService

/**
 * A convenience class for storing the data supported by the SDK.
 *
 * @property networkService A [NetworkService].
 * @property diagramService A [DiagramService].
 * @property customerService A [CustomerService].
 */
open class Services(
    val networkService: NetworkService = NetworkService(),
    val diagramService: DiagramService = DiagramService(),
    val customerService: CustomerService = CustomerService()
) {

    /**
     * Accessor of the [networkService] to allow for destructuring.
     */
    operator fun component1(): NetworkService = networkService

    /**
     * Accessor of the [diagramService] to allow for destructuring.
     */
    operator fun component2(): DiagramService = diagramService

    /**
     * Accessor of the [customerService] to allow for destructuring.
     */
    operator fun component3(): CustomerService = customerService

}
