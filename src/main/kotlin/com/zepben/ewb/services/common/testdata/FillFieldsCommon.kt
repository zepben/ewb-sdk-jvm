/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.testdata

import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.services.common.BaseService
import java.time.Instant

//
// NOTE: The following extensions are named differently on purpose. If they are called `fillFields` you can not import them
//       correctly, and you get skipping of sublevels in the service specific use.
//

// ###################
// # IEC61968 Common #
// ###################

fun Document.fillFieldsCommon(service: BaseService, includeRuntime: Boolean = true): Document {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    title = "title"
    createdDateTime = Instant.now()
    authorName = "authorName"
    type = "type"
    status = "status"
    comment = "comment"

    return this
}

fun Organisation.fillFieldsCommon(service: BaseService, includeRuntime: Boolean = true): Organisation {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun OrganisationRole.fillFieldsCommon(service: BaseService, includeRuntime: Boolean = true): OrganisationRole {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    organisation = Organisation().also {
        assert(service.tryAdd(it)) { "Initial tryAdd should return true" }
    }

    return this
}

// ######################
// # IEC61970 Base Core #
// ######################

//
// Note: `includeRuntime` is here to force consistency across all other methods and prevent the need for rework if runtime
//       is ever required at a level that was not previously used.
//
fun IdentifiedObject.fillFieldsCommon(service: BaseService, @Suppress("UNUSED_PARAMETER") includeRuntime: Boolean = true): IdentifiedObject {
    name = "1"
    description = "the description"
    numDiagramObjects = 2

    for (i in 0..1) {
        val nameType = service.getNameType("name_type $i")
            ?: NameType("name_type $i")
                .apply { description = "name_type_${i}_description" }
                .also { service.addNameType(it) }
        addName(nameType, "name_$i")
    }

    return this
}
