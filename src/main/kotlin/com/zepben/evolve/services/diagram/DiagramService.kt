/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.diagram

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.services.common.BaseService
import kotlin.reflect.KClass

private val supportedClasses: Set<KClass<out IdentifiedObject>> = setOf(
    Diagram::class,
    DiagramObject::class
)

/**
 * Maintains an in-memory model of diagrams.
 */
class DiagramService : BaseService("diagram") {

    private val diagramObjectsByDiagramMRID: MutableMap<String, MutableMap<String, DiagramObject>> = mutableMapOf()
    private val diagramObjectsByIdentifiedObjectMRID: MutableMap<String, MutableMap<String, DiagramObject>> = mutableMapOf()

    private val diagramObjectIndexes = listOf(diagramObjectsByIdentifiedObjectMRID, diagramObjectsByDiagramMRID)

    /**
     * Get [DiagramObject]'s from the service associated with the given mRID.
     *
     * [DiagramObject]'s are indexed by its [DiagramObject.mRID], its [DiagramObject.diagram]'s mRID,
     * and its [DiagramObject.identifiedObjectMRID]'s mRID (if present).
     *
     * If you request a [DiagramObject] by its mRID you will receive a List with a single entry, otherwise
     * the list will contain as many [DiagramObject]'s as have been recorded against the provided mRID.
     * @return A list of [DiagramObject]'s associated with [mRID].
     */
    fun getDiagramObjects(mRID: String): List<DiagramObject> {
        val diagramObject = get<DiagramObject>(mRID)
        if (diagramObject != null)
            return listOf(diagramObject)

        for (map in diagramObjectIndexes)
            if (mRID in map)
                return map[mRID]!!.values.toList()
        return emptyList()
    }

    fun add(diagram: Diagram): Boolean = super.add(diagram)
    fun remove(diagram: Diagram): Boolean = super.remove(diagram)

    /**
     * Associate a [DiagramObject] with this service.
     *
     * The [DiagramObject] will be indexed by its [Diagram] and its [IdentifiedObject] (if present). If you change the
     * diagram or identified object referenced by the diagram object after it's been added to the service, you will
     * need to remove and re-add the diagram object for it to be indexed correctly.
     *
     * @return true if the [DiagramObject] was successfully associated with the service.
     */
    fun add(diagramObject: DiagramObject): Boolean = super.add(diagramObject) && addIndex(diagramObject)

    /**
     * Disassociate a [DiagramObject] with the service. This will remove all indexing of the [DiagramObject] and it
     * will no longer be able to be found via the service.
     *
     * @param diagramObject The [DiagramObject] to disassociate with this service.
     * @return true if the [DiagramObject] was removed successfully.
     */
    fun remove(diagramObject: DiagramObject): Boolean = super.remove(diagramObject) && removeIndex(diagramObject)

   /**
     * Index a [DiagramObject] against its associated [Diagram] and [IdentifiedObject].
     *
     * @return true if the index was updated.
     */
    private fun addIndex(diagramObject: DiagramObject): Boolean {
        diagramObject.diagram?.mRID?.let {
            diagramObjectsByDiagramMRID.computeIfAbsent(it) {
                mutableMapOf()
            }[diagramObject.mRID] = diagramObject
        }

        diagramObject.identifiedObjectMRID?.let {
            diagramObjectsByIdentifiedObjectMRID.computeIfAbsent(it) {
                mutableMapOf()
            }[diagramObject.mRID] = diagramObject
        }

        return true
    }

    /**
     * Remove the indexes of a [DiagramObject].
     *
     * @return true if the index was updated.
     */
    private fun removeIndex(diagramObject: DiagramObject): Boolean {
        diagramObject.diagram?.mRID?.let {
            val diagramMap = diagramObjectsByDiagramMRID[it]
            diagramMap?.remove(diagramObject.mRID)
            if (diagramMap?.isEmpty() == true)
                diagramObjectsByDiagramMRID.remove(it)
        }

        diagramObject.identifiedObjectMRID?.let {
            val identifiedObjectMap = diagramObjectsByIdentifiedObjectMRID[it]
            identifiedObjectMap?.remove(diagramObject.mRID)
            if (identifiedObjectMap?.isEmpty() == true)
                diagramObjectsByIdentifiedObjectMRID.remove(it)
        }

        return true
    }
}
