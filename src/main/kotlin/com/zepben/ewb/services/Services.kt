/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services

import com.zepben.ewb.annotations.ZepbenExperimental
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.services.common.BaseService
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

    /**
     * Check all available services for [mRID].
     *
     * @param mRID The [IdentifiedObject] with this [mRID] to retrieve.
     * @return The [IdentifiedObject], or null if a matching [IdentifiedObject] could not be found in any service.
     * @throws ClassCastException if the [IdentifiedObject] exists but is not of the specified type [T].
     */
    @OptIn(ZepbenExperimental::class)
    inline operator fun <reified T : IdentifiedObject> get(mRID: String?): T? {
        return networkService.get(T::class, mRID)
            ?: diagramService.get(T::class, mRID)
            ?: customerService.get(T::class, mRID)
            ?: customerDiagramService.get(T::class, mRID)
    }

    /**
     * Retrieve the service that contains a particular [IdentifiedObject] with the given [mRID].
     *
     * @param mRID The [IdentifiedObject] with this [mRID] to look for.
     * @return The relevant service, or null if a matching [IdentifiedObject] could not be found in any service.
     */
    @OptIn(ZepbenExperimental::class)
    fun which(mRID: String): BaseService? {
        return networkService.takeIf { it.contains(mRID) } ?: diagramService.takeIf { it.contains(mRID) } ?: customerService.takeIf { it.contains(mRID) } ?: customerDiagramService.takeIf { it.contains(mRID) }
    }
}
