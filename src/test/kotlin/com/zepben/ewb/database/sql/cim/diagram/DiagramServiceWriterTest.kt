/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.diagram

import com.zepben.ewb.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DiagramServiceWriterTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val diagramService = DiagramService()
    private val cimWriter = mockk<DiagramCimWriter> { every { write(any<Diagram>()) } returns true }
    private val diagramServiceWriter = DiagramServiceWriter(mockk(), cimWriter)

    //
    // NOTE: We don't do an exhaustive test of saving objects as this is done via the schema test.
    //

    @Test
    internal fun `passes objects through to the cim writer`() {
        val diagram = Diagram(generateId()).also { diagramService.add(it) }

        // NOTE: the write method will fail due to the relaxed mock returning false for all write operations,
        //       but a `write` should still be attempted on every object
        diagramServiceWriter.write(diagramService)

        verify(exactly = 1) { cimWriter.write(diagram) }
    }

}
