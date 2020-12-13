/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common.translator

import com.zepben.evolve.cim.iec61968.common.Document
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.common.OrganisationRole
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.Resolvers
import com.zepben.evolve.services.common.extensions.internEmpty
import com.zepben.protobuf.cim.iec61968.common.Document as PBDocument
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
import com.zepben.protobuf.cim.iec61968.common.OrganisationRole as PBOrganisationRole
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject as PBIdentifiedObject

/************ IEC61968 COMMON ************/
fun toCim(pb: PBDocument, cim: Document, baseService: BaseService): Document =
    cim.apply {
        title = pb.title.internEmpty()
        createdDateTime = pb.createdDateTime.toInstant()
        authorName = pb.authorName.internEmpty()
        type = pb.type.internEmpty()
        status = pb.status.internEmpty()
        comment = pb.comment.internEmpty()
        toCim(pb.io, this, baseService)
    }

fun toCim(pb: PBOrganisation, baseService: BaseService): Organisation =
    Organisation(pb.mRID()).apply {
        toCim(pb.io, this, baseService)
    }

fun toCim(pb: PBOrganisationRole, cim: OrganisationRole, baseService: BaseService): OrganisationRole =
    cim.apply {
        baseService.resolveOrDeferReference(Resolvers.organisation(this), pb.organisationMRID)
        toCim(pb.io, this, baseService)
    }

/************ IEC61970 CORE ************/
@Suppress("UNUSED_PARAMETER")
fun toCim(pb: PBIdentifiedObject, cim: IdentifiedObject, baseService: BaseService): IdentifiedObject =
    cim.apply {
        name = pb.name.internEmpty()
        description = pb.description.internEmpty()
        numDiagramObjects = pb.numDiagramObjects
    }


/************ Class for Java friendly usage ************/

abstract class BaseProtoToCim(private val baseService: BaseService)
