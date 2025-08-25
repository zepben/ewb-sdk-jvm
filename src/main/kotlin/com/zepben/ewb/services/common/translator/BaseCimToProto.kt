/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.translator

import com.google.protobuf.NullValue
import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Name
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.protobuf.cim.iec61968.common.Document as PBDocument
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
import com.zepben.protobuf.cim.iec61968.common.OrganisationRole as PBOrganisationRole
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject as PBIdentifiedObject
import com.zepben.protobuf.cim.iec61970.base.core.Name as PBName
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType

// ###################
// # IEC61968 Common #
// ###################

fun toPb(cim: Document, pb: PBDocument.Builder): PBDocument.Builder =
    pb.apply {
        cim.title?.also { titleSet = it } ?: run { titleNull = NullValue.NULL_VALUE }
        cim.createdDateTime.toTimestamp()?.let { createdDateTime = it } ?: clearCreatedDateTime()
        cim.type?.also { typeSet = it } ?: run { typeNull = NullValue.NULL_VALUE }
        cim.status?.also { statusSet = it } ?: run { statusNull = NullValue.NULL_VALUE }
        cim.comment?.also { commentSet = it } ?: run { commentNull = NullValue.NULL_VALUE }
        cim.authorName?.also { authorNameSet = it } ?: run { authorNameNull = NullValue.NULL_VALUE }
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

// ######################
// # IEC61970 Base Core #
// ######################

fun toPb(cim: IdentifiedObject, pb: PBIdentifiedObject.Builder): PBIdentifiedObject.Builder =
    pb.apply {
        mrid = cim.mRID
        cim.name?.also { nameSet = it } ?: run { nameNull = NullValue.NULL_VALUE }
        cim.description?.also { descriptionSet = it } ?: run { descriptionNull = NullValue.NULL_VALUE }
        cim.numDiagramObjects?.also { numDiagramObjectsSet = it } ?: run { numDiagramObjectsNull = NullValue.NULL_VALUE }
        cim.names.forEach { name -> addNamesBuilder().also { toPb(name, it) } }
    }

fun toPb(cim: Name, pb: PBName.Builder): PBName.Builder =
    pb.apply {
        name = cim.name
        type = cim.type.name
    }

fun toPb(cim: NameType, pb: PBNameType.Builder): PBNameType.Builder =
    pb.apply {
        name = cim.name
        cim.description?.also { descriptionSet = it } ?: run { descriptionNull = NullValue.NULL_VALUE }
    }

fun NameType.toPb(): PBNameType = toPb(this, PBNameType.newBuilder()).build()

// #################################
// # Class for Java friendly usage #
// #################################

abstract class BaseCimToProto {

    // ###################
    // # IEC61968 Common #
    // ###################

    fun toPb(organisation: Organisation): PBOrganisation = organisation.toPb()

    // ######################
    // # IEC61970 Base Core #
    // ######################

    fun toPb(nameType: NameType): PBNameType = nameType.toPb()

}
