/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:JvmName("NetworkModelProjectServiceUtils")

package com.zepben.ewb.services.variant

import com.zepben.ewb.cim.extensions.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProject
import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.AnnotatedProjectDependency
import com.zepben.ewb.cim.iec61970.infiec61970.infpart303.networkmodelprojects.NetworkModelProjectStage
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.services.network.NetworkService

/**
 * A function that provides an exhaustive `when` style statement for all [Identifiable] leaf types supported by
 * the [VariantService]. If the provided [identifiable] is not supported by the service the [isOther] handler
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
 * @param identifiable The identified object to handle.
 */
inline fun <R> whenVariantIdentifiedObject(
    identifiable: Identifiable,
    isNetworkModelProject: (NetworkModelProject) -> R,
    isNetworkModelProjectStage: (NetworkModelProjectStage) -> R,
    isAnnotatedProjectDependency: (AnnotatedProjectDependency) -> R,
    isChangeSet: (ChangeSet) -> R,
    isObjectCreation: (ObjectCreation) -> R,
    isObjectDeletion: (ObjectDeletion) -> R,
    isObjectModification: (ObjectModification) -> R,
    isObjectReverseModification: (ObjectReverseModification) -> R,
    isOther: (Any) -> R = { obj: Any ->
        throw IllegalArgumentException("Identified object type ${obj::class} is not supported by the network model project service")
    }
): R = when (identifiable) {
    is NetworkModelProject -> isNetworkModelProject(identifiable)
    is NetworkModelProjectStage -> isNetworkModelProjectStage(identifiable)
    is AnnotatedProjectDependency -> isAnnotatedProjectDependency(identifiable)
    is ChangeSet -> isChangeSet(identifiable)
    is ObjectCreation -> isObjectCreation(identifiable)
    is ObjectDeletion -> isObjectDeletion(identifiable)
    is ObjectModification -> isObjectModification(identifiable)
    is ObjectReverseModification -> isObjectReverseModification(identifiable)
    else -> isOther(identifiable)
}
