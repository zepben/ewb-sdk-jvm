/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

/**
 * The Name class provides the means to define any number of human readable names for an object. A name is **not** to be used for defining inter-object
 * relationships. For inter-object relationships instead use the object identification 'mRID'.
 *
 * @property name Any free text that name the object.
 * @property type Type of this name.
 * @property identifiedObject Identified object that this name designates.
 */
data class Name(val name: String, val type: NameType, val identifiedObject: IdentifiedObject)
