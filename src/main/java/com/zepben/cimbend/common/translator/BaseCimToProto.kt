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
import com.zepben.protobuf.cim.iec61968.common.Document as PBDocument
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
import com.zepben.protobuf.cim.iec61968.common.OrganisationRole as PBOrganisationRole
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject as PBIdentifiedObject

/************ IEC61968 COMMON ************/
fun toPb(cim: Document, pb: PBDocument.Builder): PBDocument.Builder =
    pb.apply {
        title = cim.title
        cim.createdDateTime.toTimestamp()?.let { createdDateTime = it } ?: clearCreatedDateTime()
        type = cim.type
        status = cim.status
        comment = cim.comment
        authorName = cim.authorName
        toPb(cim, ioBuilder)
    }

fun toPb(cim: Organisation, pb: PBOrganisation.Builder): PBOrganisation.Builder =
    pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: OrganisationRole, pb: PBOrganisationRole.Builder): PBOrganisationRole.Builder =
    pb.apply {
        cim.organisation?.mRID?.let { organisationMRID = it } ?: clearOrganisationMRID()
        toPb(cim, ioBuilder)
    }

/************ IEC61970 CORE ************/
fun toPb(cim: IdentifiedObject, pb: PBIdentifiedObject.Builder): PBIdentifiedObject.Builder =
    pb.apply {
        pb.mrid = cim.mRID
        pb.name = cim.name
        pb.description = cim.description
        pb.numDiagramObjects = cim.numDiagramObjects
    }

/************ Extensions ************/

fun Organisation.toPb(): PBOrganisation = toPb(this, PBOrganisation.newBuilder()).build()

/************ Class for Java friendly usage ************/

abstract class BaseCimToProto {

    fun toPb(organisation: Organisation): PBOrganisation = organisation.toPb()

}
