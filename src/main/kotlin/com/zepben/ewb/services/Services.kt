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
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.variant.VariantService

/**
 * A convenience class for storing the data supported by the SDK.
 *
 * @property networkService A [NetworkService].
 * @property diagramService A [DiagramService].
 * @property customerService A [CustomerService].
 * @property customerDiagramService A [DiagramService].
 * @property variantService A [VariantService].
 */
open class Services @ZepbenExperimental constructor(
    val networkService: NetworkService = NetworkService(),
    val diagramService: DiagramService = DiagramService(),
    val customerService: CustomerService = CustomerService(),
    val variantService: VariantService = VariantService(),
    // TODO: Create task to implement customerDiagramService in the database reading, at which point ZepbenExperimental can be removed.
    @property:ZepbenExperimental open val customerDiagramService: DiagramService,    // NOTE: Do not use unless you know better. Talk to Anthony/Kurt.
) {

    @OptIn(ZepbenExperimental::class)
    constructor(
        networkService: NetworkService = NetworkService(),
        diagramService: DiagramService = DiagramService(),
        customerService: CustomerService = CustomerService(),
        variantService: VariantService = VariantService(),
    ) : this(networkService, diagramService, customerService, variantService, DiagramService())

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
     * Accessor of the [variantService] to allow for destructuring.
     */
    open operator fun component4(): VariantService = variantService

    /**
     * Accessor of the [customerDiagramService] to allow for destructuring.
     */
    @OptIn(ZepbenExperimental::class)
    open operator fun component5(): DiagramService = customerDiagramService

    /**
     * Retrieve all services that contain an [IdentifiedObject] with the given [mRID].
     *
     * @param mRID The [IdentifiedObject] with this [mRID] to look for.
     * @return The services containing a matching [IdentifiedObject], or an empty list if it could not be found in any service.
     */
    @OptIn(ZepbenExperimental::class)
    fun whichAll(mRID: String): List<BaseService> = listOfNotNull(
        networkService.takeIf { it.contains(mRID) },
        diagramService.takeIf { it.contains(mRID) },
        customerService.takeIf { it.contains(mRID) },
        customerDiagramService.takeIf { it.contains(mRID) },
        variantService.takeIf { it.contains(mRID) },
    )
}
