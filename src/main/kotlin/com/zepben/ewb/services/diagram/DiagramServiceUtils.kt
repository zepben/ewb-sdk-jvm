/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:JvmName("DiagramServiceUtils")

package com.zepben.ewb.services.diagram

import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.ewb.services.customer.CustomerService

/**
 * A function that provides an exhaustive `when` style statement for all [IdentifiedObject] leaf types supported by
 * the [DiagramService]. If the provided [identifiedObject] is not supported by the service the [isOther] handler
 * is invoked which by default will throw an [IllegalArgumentException]
 *
 * By using this function, you acknowledge that if any new types are added to the customer service, and thus this
 * function, it will cause a compilation error when updating to the new version. This should reduce errors due to
 * missed handling of new types introduced to the model. As this is intended behaviour it generally will not be
 * considered a breaking change in terms of semantic versioning of this library.
 *
 * If it is not critical that all types within the service are always handled, it is recommended to use a typical
 * `when` statement (Kotlin) or if-else branch (Java) and update new cases as required without breaking your code.
 *
 * @param identifiedObject The identified object to handle.
 * @param isDiagram Handler when the [identifiedObject] is a [Diagram]
 * @param isDiagramObject Handler when the [identifiedObject] is a [DiagramObject]
 * @param isOther Handler when the [identifiedObject] is not supported by the [CustomerService].
 */
@JvmOverloads
inline fun <R> whenDiagramServiceObject(
    identifiedObject: Identifiable,
    isDiagram: (Diagram) -> R,
    isDiagramObject: (DiagramObject) -> R,
    isOther: (Identifiable) -> R = { idObj: Identifiable ->
        throw IllegalArgumentException("Identified object type ${idObj::class} is not supported by the diagram service")
    }
): R = when (identifiedObject) {
    is Diagram -> isDiagram(identifiedObject)
    is DiagramObject -> isDiagramObject(identifiedObject)
    else -> isOther(identifiedObject)
}
