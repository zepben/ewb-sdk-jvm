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

package com.zepben.cimbend.database.sqlite;

import com.zepben.cimbend.cim.iec61968.common.PositionPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by robertocomp on 4/01/2017.
 * <p>
 * Tests the {@link PositionPointParserTest} methods.
 * </p>
 */
@SuppressWarnings("ConstantConditions")
public class PositionPointParserTest {

    @Test
    public void parseTest() {

        ArrayList<PositionPoint> testPath = new ArrayList<>();
        int pathLength = 10;

        for (int i = 0; i < pathLength; i++) {
            testPath.add(new PositionPoint(i, (pathLength - i)));
        }

        String testingString = PositionPointParser.toString(testPath);
        List<PositionPoint> parsedPath = new ArrayList<>(PositionPointParser.toPath(testingString));

        // Testing parsing a List<PositionPoint>
        assertThat(parsedPath.size(), equalTo(testPath.size()));
        for (int i = 0; i < pathLength; i++) {
            assertThat(parsedPath.get(i).getXPosition(), closeTo(testPath.get(i).getXPosition(), 0.00001));
            assertThat(parsedPath.get(i).getYPosition(), closeTo(testPath.get(i).getYPosition(), 0.00001));
        }

        // Testing emptyLists
        assertThat(PositionPointParser.toString(new ArrayList<>()), equalTo(null));

        // Testing parsing PositionPoint
        PositionPoint positionPoint = new PositionPoint(1.0, 0.1);
        String lngLatString;
        lngLatString = PositionPointParser.toString(Collections.singletonList(positionPoint));

        assertThat(lngLatString, equalTo("1.0,0.1;"));
        parsedPath = PositionPointParser.toSingle(lngLatString);
        assertThat(parsedPath.size(), equalTo(1));
        assertThat(parsedPath.get(0).getXPosition(), closeTo(positionPoint.getXPosition(), 0.00001));
        assertThat(parsedPath.get(0).getYPosition(), closeTo(positionPoint.getYPosition(), 0.00001));

        // Testing input of unparsable strings.
        assertThat(PositionPointParser.toPath("1,2,7;3,4;5,6;"), nullValue());
        assertThat(PositionPointParser.toPath("1,2,7;3,4;5;"), nullValue());
        assertThat(PositionPointParser.toPath("1.0,2.0;3.3,5.6;A,7.7;"), nullValue());
        assertThat(PositionPointParser.toSingle("1,2,3"), nullValue());
        assertThat(PositionPointParser.toSingle("1"), nullValue());
        assertThat(PositionPointParser.toSingle("1,A"), nullValue());

        // Testing black strings
        assertThat(PositionPointParser.toPath(""), empty());
        assertThat(PositionPointParser.toSingle(""), empty());
    }

}
