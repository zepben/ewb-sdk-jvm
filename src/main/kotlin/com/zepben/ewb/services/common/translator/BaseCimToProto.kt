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

/**
 * Convert the [Document] into its protobuf counterpart.
 *
 * @param cim The [Document] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Document, pb: PBDocument.Builder): PBDocument.Builder =
    pb.apply {
        cim.title?.also { titleSet = it } ?: run { titleNull = NullValue.NULL_VALUE }
        cim.createdDateTime?.toTimestamp()?.let { createdDateTimeSet = it } ?: run { createdDateTimeNull = NullValue.NULL_VALUE }
        cim.type?.also { typeSet = it } ?: run { typeNull = NullValue.NULL_VALUE }
        cim.status?.also { statusSet = it } ?: run { statusNull = NullValue.NULL_VALUE }
        cim.comment?.also { commentSet = it } ?: run { commentNull = NullValue.NULL_VALUE }
        cim.authorName?.also { authorNameSet = it } ?: run { authorNameNull = NullValue.NULL_VALUE }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [Organisation] into its protobuf counterpart.
 *
 * @param cim The [Organisation] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Organisation, pb: PBOrganisation.Builder): PBOrganisation.Builder =
    pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [OrganisationRole] into its protobuf counterpart.
 *
 * @param cim The [OrganisationRole] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: OrganisationRole, pb: PBOrganisationRole.Builder): PBOrganisationRole.Builder =
    pb.apply {
        cim.organisation?.mRID?.let { organisationMRID = it } ?: clearOrganisationMRID()
        toPb(cim, ioBuilder)
    }

/**
 * An extension for converting any [Organisation] into its protobuf counterpart.
 */
fun Organisation.toPb(): PBOrganisation = toPb(this, PBOrganisation.newBuilder()).build()

// ######################
// # IEC61970 Base Core #
// ######################

/**
 * Convert the [IdentifiedObject] into its protobuf counterpart.
 *
 * @param cim The [IdentifiedObject] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: IdentifiedObject, pb: PBIdentifiedObject.Builder): PBIdentifiedObject.Builder =
    pb.apply {
        mrid = cim.mRID
        cim.name?.also { nameSet = it } ?: run { nameNull = NullValue.NULL_VALUE }
        cim.description?.also { descriptionSet = it } ?: run { descriptionNull = NullValue.NULL_VALUE }
        cim.numDiagramObjects?.also { numDiagramObjectsSet = it } ?: run { numDiagramObjectsNull = NullValue.NULL_VALUE }
        cim.names.forEach { name -> addNamesBuilder().also { toPb(name, it) } }
    }

/**
 * Convert the [Name] into its protobuf counterpart.
 *
 * @param cim The [Name] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Name, pb: PBName.Builder): PBName.Builder =
    pb.apply {
        name = cim.name
        type = cim.type.name
    }

/**
 * Convert the [NameType] into its protobuf counterpart.
 *
 * @param cim The [NameType] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: NameType, pb: PBNameType.Builder): PBNameType.Builder =
    pb.apply {
        name = cim.name
        cim.description?.also { descriptionSet = it } ?: run { descriptionNull = NullValue.NULL_VALUE }
    }

/**
 * An extension for converting any [NameType] into its protobuf counterpart.
 */
fun NameType.toPb(): PBNameType = toPb(this, PBNameType.newBuilder()).build()

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * The base helper class for Java friendly convertion from CIM objects to their protobuf counterparts.
 */
abstract class BaseCimToProto {

    // ###################
    // # IEC61968 Common #
    // ###################

    /**
     * Convert the [Organisation] into its protobuf counterpart.
     *
     * @param organisation The [Organisation] to convert.
     * @return The protobuf form of [organisation].
     */
    fun toPb(organisation: Organisation): PBOrganisation = organisation.toPb()

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Convert the [NameType] into its protobuf counterpart.
     *
     * @param nameType The [NameType] to convert.
     * @return The protobuf form of [nameType].
     */
    fun toPb(nameType: NameType): PBNameType = nameType.toPb()

}
