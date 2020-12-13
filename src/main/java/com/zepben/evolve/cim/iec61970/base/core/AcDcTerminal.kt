/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

/**
 * An electrical connection point (AC or DC) to a piece of conducting equipment. Terminals are connected at physical
 * connection points called connectivity nodes.
 */
abstract class AcDcTerminal(mRID: String = "") : IdentifiedObject(mRID)
