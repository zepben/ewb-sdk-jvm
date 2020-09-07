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

import com.zepben.annotations.EverythingIsNonnullByDefault;
import com.zepben.cimbend.cim.iec61968.common.PositionPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by robertocomp on 3/01/2017.
 * <p> This class holds the methods needed to parse the lngLatPath field and the lngLat field.
 * The lngLatPath field holds a list of {@link PositionPoint}.
 * The lngLat field is an instance of {@link PositionPoint}</p>
 */
@EverythingIsNonnullByDefault
public final class PositionPointParser {

    /**
     * <p>Converts a list of {@link PositionPoint} into a string the represents a lngLatPath of latitude and longitude.</p>
     *
     * @param path a list of {@link PositionPoint} to be translated into a parsable string.
     * @return returns a string that represents the input list of {@link PositionPoint}
     */
    @Nullable
    static public String toString(@Nullable List<PositionPoint> path) {
        if (path == null || path.isEmpty())
            return null;

        StringBuilder pathStringBuilder = new StringBuilder();

        for (PositionPoint coordinate : path) {
            pathStringBuilder
                .append(coordinate.getXPosition())
                .append(",")
                .append(coordinate.getYPosition())
                .append(";");
        }
        return pathStringBuilder.toString();
    }

    /**
     * <p>Converts a string of a lngLatPath of latitude and longitude to a list of {@link PositionPoint}.</p>
     * <p>The format of the lngLatPath string should follow the format:
     * "%f,%f;%f,%f;"
     * "," works as a separator for the longitude and latitude fields of {@link PositionPoint}.
     * ";" works as a separator between the elements of the list of {@link PositionPoint}.
     * example : "longitude1,latitude1;longitude2,latitude2;"</p>
     *
     * @param pathString a string representing a list of {@link PositionPoint} as described in the description.
     * @return returns a list of {@link PositionPoint} as represented by the input string. Returns null if the input string was not parsable for any reason.
     */
    @Nullable
    static List<PositionPoint> toPath(@Nullable String pathString) {
        return process(pathString, 2, Integer.MAX_VALUE);
    }

    /**
     * <p>Converts a string of longitude and latitude to a {@link PositionPoint}.</p>
     * <p>The format of the longitude and latitude string should follow the format:
     * "%f,%f"
     * "," works as a separator for the longitude and latitude fields of {@link PositionPoint}.
     * example : "longitude,latitude"</p>
     *
     * @param lngLatString a string representing a {@link PositionPoint} as described in the description.
     * @return returns a list of {@link PositionPoint} with a single entry as represented by the input string. Returns null if the input string was not parsable for any reason.
     */
    @Nullable
    static List<PositionPoint> toSingle(@Nullable String lngLatString) {
        return process(lngLatString, 1, 1);
    }

    @Nullable
    private static List<PositionPoint> process(@Nullable String pathString, int minCoords, int maxCoords) {
        if ((pathString == null) || pathString.equals(""))
            return Collections.emptyList();

        String[] coordinates = pathString.split(";", 0);
        if ((coordinates.length == 0) || coordinates[0].equals(""))
            return Collections.emptyList();

        if ((coordinates.length < minCoords) || (coordinates.length > maxCoords))
            return null;

        ArrayList<PositionPoint> path = new ArrayList<>();
        try {
            for (String coordinate : coordinates) {
                String[] coordinateStrings = coordinate.split(",", 0);

                if (coordinateStrings.length == 2)
                    path.add(new PositionPoint(Double.parseDouble(coordinateStrings[0]), Double.parseDouble(coordinateStrings[1])));
                else
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return Collections.unmodifiableList(path);
    }

    private PositionPointParser() {
        // Prevent instantiation.
    }

}
