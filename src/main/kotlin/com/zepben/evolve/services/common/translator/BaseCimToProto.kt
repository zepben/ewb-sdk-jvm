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
import com.zepben.evolve.cim.iec61970.base.core.Name
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.protobuf.cim.iec61968.common.Document as PBDocument
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
import com.zepben.protobuf.cim.iec61968.common.OrganisationRole as PBOrganisationRole
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject as PBIdentifiedObject
import com.zepben.protobuf.cim.iec61970.base.core.Name as PBName
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType

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

fun Organisation.toPb(): PBOrganisation = toPb(this, PBOrganisation.newBuilder()).build()

/************ IEC61970 CORE ************/

fun toPb(cim: IdentifiedObject, pb: PBIdentifiedObject.Builder): PBIdentifiedObject.Builder =
    pb.apply {
        pb.mrid = cim.mRID
        pb.name = cim.name
        pb.description = cim.description
        pb.numDiagramObjects = cim.numDiagramObjects
        cim.names.forEach { name -> addNamesBuilder().also { toPb(name, it) } }
    }

fun toPb(cim: Name, pb: PBName.Builder): PBName.Builder =
    pb.apply {
        pb.name = cim.name
        pb.type = cim.type.name
    }

fun toPb(cim: NameType, pb: PBNameType.Builder): PBNameType.Builder =
    pb.apply {
        pb.name = cim.name
        pb.description = cim.description
    }

fun NameType.toPb(): PBNameType = toPb(this, PBNameType.newBuilder()).build()

/************ Class for Java friendly usage ************/

abstract class BaseCimToProto {

    // IEC61968 COMMON
    fun toPb(organisation: Organisation): PBOrganisation = organisation.toPb()

    // IEC61970 CORE
    fun toPb(nameType: NameType): PBNameType = nameType.toPb()

}
