/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.diagram

import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.database.sqlite.common.BaseServiceWriter
import com.zepben.evolve.services.diagram.DiagramService

class DiagramServiceWriter(
    service: DiagramService,
    writer: DiagramCIMWriter,
    hasCommon: (String) -> Boolean,
    addCommon: (String) -> Boolean
) : BaseServiceWriter<DiagramService, DiagramCIMWriter>(service, writer, hasCommon, addCommon) {

    override fun doSave(): Boolean {
        var status = true

        status = status and saveEach<DiagramObject>(writer::save)
        status = status and saveEach<Diagram>(writer::save)

        return status
    }

}
