/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

/**
 * An overcurrent protective device with a circuit opening fusible part that is heated and severed by the passage of
 * overcurrent through it. A fuse is considered a switching device because it breaks current.
 */
class Fuse @JvmOverloads constructor(mRID: String = "") : Switch(mRID)
