/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services

import com.zepben.ewb.annotations.ZepbenExperimental
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService

/**
 * A convenience class for storing the data supported by the SDK.
 *
 * @property networkService A [NetworkService].
 * @property diagramService A [DiagramService].
 * @property customerService A [CustomerService].
 * @property customerDiagramService A [DiagramService].
 */
// TODO: Create task to implement customerDiagramService in the database reading, at which point ZepbenExperimental can be removed.
open class Services @ZepbenExperimental constructor(
    open val networkService: NetworkService = NetworkService(),
    open val diagramService: DiagramService = DiagramService(),
    open val customerService: CustomerService = CustomerService(),
    @property:ZepbenExperimental open val customerDiagramService: DiagramService,    // NOTE: Do not use unless you know better. Talk to Anthony/Kurt.
) {

    @OptIn(ZepbenExperimental::class)
    constructor(
        networkService: NetworkService = NetworkService(),
        diagramService: DiagramService = DiagramService(),
        customerService: CustomerService = CustomerService(),
    ): this(networkService, diagramService, customerService, DiagramService())

    /**
     * Accessor of the [networkService] to allow for destructuring.
     */
    open operator fun component1(): NetworkService = networkService

    /**
     * Accessor of the [diagramService] to allow for destructuring.
     */
    open operator fun component2(): DiagramService = diagramService

    /**
     * Accessor of the [customerService] to allow for destructuring.
     */
    open operator fun component3(): CustomerService = customerService

    /**
     * Accessor of the [customerDiagramService] to allow for destructuring.
     */
    @OptIn(ZepbenExperimental::class)
    open operator fun component4(): DiagramService = customerDiagramService

}
