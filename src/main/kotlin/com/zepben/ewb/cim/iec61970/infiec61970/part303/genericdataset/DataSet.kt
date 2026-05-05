/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.cim.iec61970.base.core.Identifiable

/**
 * A generic container of a version of instance data. The mRID can be used in an audit trail, not
 * in reusable script intended to work with new versions of data. A dataset could be serialized
 * multiple times and in multiple technologies, yet retain the same identity.
 *
 * @property mRID Master resource identifier issued by a model authority. The mRID is unique within an exchange
 *                context. Global uniqueness is easily achieved by using a UUID, as specified in RFC 4122, for
 *                the mRID. The use of UUID is strongly recommended. For CIMXML data files in RDF syntax conforming
 *                to IEC 61970-552 Edition 1, the mRID is mapped to rdf:ID or rdf:about attributes that identify
 *                CIM object elements.
 * @property name is any free human-readable and possibly non-unique text naming the object.
 * @property description a free human-readable text describing or naming the object. It may be non-unique and may not correlate to a naming hierarchy.
 */
abstract class DataSet(override val mRID: String): Identifiable {

    var name: String? = null
    var description: String? = null

    /**
     * Printable version of the object including the type, name and mRID.
     */
    override fun typeNameAndMRID(): String = if (name.isNullOrBlank()) "${javaClass.simpleName} $mRID" else "${javaClass.simpleName} $name [$mRID]"


    /**
     * Printable version of the object including its name and mRID.
     */
    override fun nameAndMRID(): String = if (name.isNullOrBlank()) mRID else "'$name' [$mRID]"

}
