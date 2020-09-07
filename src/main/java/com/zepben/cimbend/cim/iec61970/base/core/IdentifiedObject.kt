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
package com.zepben.cimbend.cim.iec61970.base.core

import java.util.*

/**
 * This is a root class to provide common identification for all classes needing identification and naming attributes.
 *
 * @property mRID Master resource identifier issued by a model authority. The mRID is unique within an exchange context. Global uniqueness
 *                is easily achieved by using a UUID,  as specified in RFC 4122, for the mRID. The use of UUID is strongly recommended.
 *                For CIMXML data files in RDF syntax conforming to IEC 61970-552 Edition 1, the mRID is mapped to rdf:ID or rdf:about attributes
 *                that identify CIM object elements.
 * @property name is any free human readable and possibly non unique text naming the object.
 * @property description a free human readable text describing or naming the object. It may be non unique and may not correlate to a naming hierarchy.
 * @property numDiagramObjects Number of DiagramObject's known to associate with this [IdentifiedObject]
 */
abstract class IdentifiedObject(mRID: String = "") {

    val mRID: String = if (mRID.isEmpty()) UUID.randomUUID().toString() else mRID
    var name: String = ""
    var description: String = ""
    var numDiagramObjects: Int = 0

    /**
     * @return True if this [IdentifiedObject] has at least 1 DiagramObject associated with it, false otherwise.
     */
    val hasDiagramObjects: Boolean
        get() = numDiagramObjects > 0

    override fun toString(): String {
        return javaClass.simpleName + "{" +
            "id='" + mRID + '\'' +
            '}'
    }
}
