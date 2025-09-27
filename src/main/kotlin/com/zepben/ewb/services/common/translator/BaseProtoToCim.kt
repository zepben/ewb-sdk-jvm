/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.translator

import com.zepben.ewb.cim.iec61968.common.Document
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.common.OrganisationRole
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.Resolvers
import com.zepben.protobuf.cim.iec61968.common.Document as PBDocument
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
import com.zepben.protobuf.cim.iec61968.common.OrganisationRole as PBOrganisationRole
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject as PBIdentifiedObject
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType

//
// NOTE: Do not add base level extensions here, add them to the overriding services directly otherwise you will have import issues.
//

// ###################
// # IEC61968 Common #
// ###################

/**
 * Convert the protobuf [PBDocument] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDocument] to convert.
 * @param cim The CIM [Document] to populate.
 * @param baseService The [BaseService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Document].
 */
fun toCim(pb: PBDocument, cim: Document, baseService: BaseService): Document =
    cim.apply {
        title = pb.titleSet.takeUnless { pb.hasTitleNull() }
        createdDateTime = pb.createdDateTimeSet.takeUnless { pb.hasCreatedDateTimeNull() }?.toInstant()
        authorName = pb.authorNameSet.takeUnless { pb.hasAuthorNameNull() }
        type = pb.typeSet.takeUnless { pb.hasTypeNull() }
        status = pb.statusSet.takeUnless { pb.hasStatusNull() }
        comment = pb.commentSet.takeUnless { pb.hasCommentNull() }
        toCim(pb.io, this, baseService)
    }

/**
 * Convert the protobuf [PBOrganisation] into its CIM counterpart.
 *
 * @param pb The protobuf [PBOrganisation] to convert.
 * @param baseService The [BaseService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Organisation].
 */
fun toCim(pb: PBOrganisation, baseService: BaseService): Organisation =
    Organisation(pb.mRID()).apply {
        toCim(pb.io, this, baseService)
    }

/**
 * Convert the protobuf [PBOrganisationRole] into its CIM counterpart.
 *
 * @param pb The protobuf [PBOrganisationRole] to convert.
 * @param cim The CIM [OrganisationRole] to populate.
 * @param baseService The [BaseService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [OrganisationRole].
 */
fun toCim(pb: PBOrganisationRole, cim: OrganisationRole, baseService: BaseService): OrganisationRole =
    cim.apply {
        baseService.resolveOrDeferReference(Resolvers.organisation(this), pb.organisationMRID)
        toCim(pb.io, this, baseService)
    }

// ######################
// # IEC61970 Base Core #
// ######################

/**
 * Convert the protobuf [PBIdentifiedObject] into its CIM counterpart.
 *
 * @param pb The protobuf [PBIdentifiedObject] to convert.
 * @param cim The CIM [IdentifiedObject] to populate.
 * @param baseService The [BaseService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [IdentifiedObject].
 */
fun toCim(pb: PBIdentifiedObject, cim: IdentifiedObject, baseService: BaseService): IdentifiedObject =
    cim.apply {
        name = pb.nameSet.takeUnless { pb.hasNameNull() }
        description = pb.descriptionSet.takeUnless { pb.hasDescriptionNull() }
        numDiagramObjects = pb.numDiagramObjectsSet.takeUnless { pb.hasNumDiagramObjectsNull() }
        pb.namesList.forEach { entry ->
            val nameType = baseService.getNameType(entry.type) ?: NameType(entry.type).also { baseService.addNameType(it) }
            this.addName(nameType, entry.name)
        }
    }

//
// NOTE: We always update the description in case the name type was created from the name embedded in an IdentifiedObject.
//
/**
 * Convert the protobuf [PBNameType] into its CIM counterpart.
 *
 * @param pb The protobuf [PBNameType] to convert.
 * @param baseService The [BaseService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [NameType].
 */
fun toCim(pb: PBNameType, baseService: BaseService): NameType =
    (baseService.getNameType(pb.name) ?: NameType(pb.name).also { baseService.addNameType(it) })
        .apply { description = pb.descriptionSet.takeUnless { pb.hasDescriptionNull() } }

/**
 * The base helper class for Java friendly convertion from protobuf objects to their CIM counterparts.
 */
abstract class BaseProtoToCim
