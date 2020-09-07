/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.common.translator

import com.zepben.cimbend.cim.iec61968.common.Document
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.common.OrganisationRole
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.common.Resolvers
import com.zepben.cimbend.common.extensions.internEmpty
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
