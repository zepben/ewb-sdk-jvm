/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common

import com.zepben.evolve.cim.iec61968.common.*
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.utils.ServiceComparatorValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

internal abstract class BaseServiceComparatorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    protected abstract val comparatorValidator: ServiceComparatorValidator<out BaseService, out BaseServiceComparator>

    protected fun compareIdentifiedObject(newIdObj: (mRID: String) -> IdentifiedObject) {
        comparatorValidator.validateCompare(newIdObj("mRID"), newIdObj("mRID"))
        comparatorValidator.validateProperty(IdentifiedObject::name, newIdObj, { "name" }, { "diff name" })
        comparatorValidator.validateProperty(IdentifiedObject::description, newIdObj, { "description" }, { "other description" })
        comparatorValidator.validateProperty(IdentifiedObject::numDiagramObjects, newIdObj, { 0 }, { 1 })

        comparatorValidator.validateCollection(
            IdentifiedObject::names,
            IdentifiedObject::addName,
            newIdObj,
            { NameType("type1") },
            { NameType("type2") },
            { "name1" },
            { "name2" }
        )
    }

    protected fun compareDocument(newDocument: (mRID: String) -> Document) {
        compareIdentifiedObject(newDocument)

        comparatorValidator.validateProperty(Document::title, newDocument, { "title" }, { "diff title" })
        comparatorValidator.validateProperty(Document::createdDateTime, newDocument, { Instant.MIN }, { Instant.MAX })
        comparatorValidator.validateProperty(Document::authorName, newDocument, { "authorName" }, { "diff authorName" })
        comparatorValidator.validateProperty(Document::type, newDocument, { "type" }, { "diff type" })
        comparatorValidator.validateProperty(Document::status, newDocument, { "status" }, { "diff status" })
        comparatorValidator.validateProperty(Document::comment, newDocument, { "comment" }, { "diff comment" })
    }

    @Test
    internal fun testCompareLocation() {
        compareIdentifiedObject { Location(it) }

        comparatorValidator.validateProperty(
            Location::mainAddress,
            { Location(it) },
            { StreetAddress(townDetail = TownDetail("town", "state")) },
            { StreetAddress(townDetail = TownDetail("other", "state")) })
        comparatorValidator.validateIndexedCollection(
            Location::points,
            Location::addPoint,
            { Location(it) },
            { PositionPoint(1.0, 2.0) },
            { PositionPoint(3.0, 4.0) })
    }

    protected fun compareOrganisationRole(newOrganisationRole: (mRID: String) -> OrganisationRole) {
        compareIdentifiedObject(newOrganisationRole)

        comparatorValidator.validateProperty(OrganisationRole::organisation, newOrganisationRole, { Organisation("org1") }, { Organisation("org2") })
    }

    @Test
    internal fun testCompareOrganisation() {
        compareIdentifiedObject { Organisation(it) }
    }

    @Test
    internal fun testValidateService() {
        val iter = comparatorValidator.newService().supportedKClasses.iterator()
        val clazz1: KClass<out IdentifiedObject> = iter.next()
        val clazz2: KClass<out IdentifiedObject> = iter.next()

        val subject = clazz1.primaryConstructor?.call("mRID")?.apply { name = "the name" }
            ?: throw Error("expected primary constructor")
        val match = clazz1.primaryConstructor?.call("mRID")?.apply { name = "the name" }
            ?: throw Error("expected primary constructor")
        val diffMRID = clazz1.primaryConstructor?.call("diff mRID")?.apply { name = "the name" }
            ?: throw Error("expected primary constructor")
        val diffClass = clazz2.primaryConstructor?.call("mRID")?.apply { name = "the name" }
            ?: throw Error("expected primary constructor")
        val diffName = clazz1.primaryConstructor?.call("mRID")?.apply { name = "diff name" }
            ?: throw Error("expected primary constructor")
        comparatorValidator.validateServiceOf(subject, match)
        comparatorValidator.validateServiceOf(subject, diffMRID, expectMissingFromTarget = subject, expectMissingFromSource = diffMRID)
        comparatorValidator.validateServiceOf(subject, diffClass, expectMissingFromTarget = subject, expectMissingFromSource = diffClass)
        comparatorValidator.validateServiceOf(
            subject, diffName,
            expectModification = ObjectDifference(subject, diffName).apply { differences["name"] = ValueDifference(subject.name, diffName.name) })
    }

    @Test
    internal fun testValidateNameTypes() {
        val subject = NameType("type").apply { description = "desc"; getOrAddName("name", Junction("id")) }
        val match = NameType("type").apply { description = "desc"; getOrAddName("name", Junction("id")) }
        val diffName = NameType("diff type").apply { description = "desc"; getOrAddName("name", Junction("id")) }
        val diffDescription = NameType("type").apply { description = "diff desc"; getOrAddName("name", Junction("id")) }
        val diffNameName = NameType("type").apply { description = "desc"; getOrAddName("diff name", Junction("id")) }
        val diffNameIdentifiedObject = NameType("type").apply { description = "desc"; getOrAddName("name", Junction("diff id")) }

        comparatorValidator.validateNameTypes(subject, match)
        comparatorValidator.validateNameTypes(subject, diffName, expectMissingFromTarget = subject, expectMissingFromSource = diffName)
        comparatorValidator.validateNameTypes(
            subject,
            diffDescription,
            expectModification = ObjectDifference(subject, diffDescription).apply {
                differences["description"] = ValueDifference(subject.description, diffDescription.description)
            })
        comparatorValidator.validateNameTypes(
            subject,
            diffNameName,
            expectModification = ObjectDifference(subject, diffNameName).apply {
                differences["names"] =
                    CollectionDifference(missingFromTarget = subject.names.toMutableList(), missingFromSource = diffNameName.names.toMutableList())
            })
        comparatorValidator.validateNameTypes(
            subject,
            diffNameIdentifiedObject,
            expectModification = ObjectDifference(subject, diffNameIdentifiedObject).apply {
                differences["names"] =
                    CollectionDifference(missingFromTarget = subject.names.toMutableList(), missingFromSource = diffNameIdentifiedObject.names.toMutableList())
            })


        val sourceType = NameType("type").apply {
            description = "desc"
            getOrAddName("name", Junction("id"))
        }

        val targetType = NameType("type").apply {
            description = "desc"
            getOrAddName("name", Junction("id"))
        }

        comparatorValidator.validateNameTypes(sourceType, targetType)
    }

}
