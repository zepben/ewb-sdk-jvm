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
