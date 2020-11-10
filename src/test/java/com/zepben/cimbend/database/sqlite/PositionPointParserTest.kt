/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite

import com.zepben.cimbend.cim.iec61968.common.PositionPoint
import com.zepben.cimbend.database.sqlite.PositionPointParser.toPath
import com.zepben.cimbend.database.sqlite.PositionPointParser.toSingle
import com.zepben.cimbend.database.sqlite.PositionPointParser.toString
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by robertocomp on 4/01/2017.
 *
 * Tests the [PositionPointParserTest] methods.
 */
class PositionPointParserTest {

    @Test
    fun parseTest() {
        val testPath = ArrayList<PositionPoint>()
        val pathLength = 10

        for (i in 0 until pathLength) {
            testPath.add(PositionPoint(i.toDouble(), (pathLength - i).toDouble()))
        }

        val testingString = toString(testPath)
        var parsedPath: List<PositionPoint>? = toPath(testingString)

        // Testing parsing a List<PositionPoint>
        Assert.assertThat(parsedPath!!.size, Matchers.equalTo(testPath.size))
        for (i in 0 until pathLength) {
            Assert.assertThat(parsedPath[i].xPosition, Matchers.closeTo(testPath[i].xPosition, 0.00001))
            Assert.assertThat(parsedPath[i].yPosition, Matchers.closeTo(testPath[i].yPosition, 0.00001))
        }

        // Testing emptyLists
        Assert.assertThat(toString(ArrayList()), Matchers.equalTo(null))

        // Testing parsing PositionPoint
        val positionPoint = PositionPoint(1.0, 0.1)
        val lngLatString: String?
        lngLatString = toString(listOf(positionPoint))

        Assert.assertThat(lngLatString, Matchers.equalTo("1.0,0.1;"))
        parsedPath = toSingle(lngLatString)
        Assert.assertThat(parsedPath!!.size, Matchers.equalTo(1))
        Assert.assertThat(parsedPath[0].xPosition, Matchers.closeTo(positionPoint.xPosition, 0.00001))
        Assert.assertThat(parsedPath[0].yPosition, Matchers.closeTo(positionPoint.yPosition, 0.00001))

        // Testing input of unparsable strings.
        Assert.assertThat(toPath("1,2,7;3,4;5,6;"), Matchers.nullValue())
        Assert.assertThat(toPath("1,2,7;3,4;5;"), Matchers.nullValue())
        Assert.assertThat(toPath("1.0,2.0;3.3,5.6;A,7.7;"), Matchers.nullValue())
        Assert.assertThat(toSingle("1,2,3"), Matchers.nullValue())
        Assert.assertThat(toSingle("1"), Matchers.nullValue())
        Assert.assertThat(toSingle("1,A"), Matchers.nullValue())

        // Testing black strings
        Assert.assertThat(toPath(""), Matchers.empty())
        Assert.assertThat(toSingle(""), Matchers.empty())
    }

}
