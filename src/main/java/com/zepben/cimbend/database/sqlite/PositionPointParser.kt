/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite

import com.zepben.cimbend.cim.iec61968.common.PositionPoint

/**
 * Created by robertocomp on 3/01/2017.
 *
 * This class holds the methods needed to parse the lngLatPath field and the lngLat field.
 * The lngLatPath field holds a list of [PositionPoint].
 * The lngLat field is an instance of [PositionPoint]
 */
object PositionPointParser {

    /**
     * Converts a list of [PositionPoint] into a string the represents a lngLatPath of latitude and longitude.
     *
     * @param path a list of [PositionPoint] to be translated into a parsable string.
     * @return returns a string that represents the input list of [PositionPoint]
     */
    fun toString(path: List<PositionPoint>?): String? {
        if (path == null || path.isEmpty())
            return null

        val pathStringBuilder = StringBuilder()

        for ((xPosition, yPosition) in path) {
            pathStringBuilder
                .append(xPosition)
                .append(",")
                .append(yPosition)
                .append(";")
        }
        return pathStringBuilder.toString()
    }

    /**
     * Converts a string of a lngLatPath of latitude and longitude to a list of [PositionPoint].
     *
     * The format of the lngLatPath string should follow the format:
     * "%f,%f;%f,%f;"
     * "," works as a separator for the longitude and latitude fields of [PositionPoint].
     * ";" works as a separator between the elements of the list of [PositionPoint].
     * example : "longitude1,latitude1;longitude2,latitude2;"
     *
     * @param pathString a string representing a list of [PositionPoint] as described in the description.
     * @return returns a list of [PositionPoint] as represented by the input string. Returns null if the input string was not parsable for any reason.
     */
    fun toPath(pathString: String?): List<PositionPoint>? = process(pathString, 2, Int.MAX_VALUE)

    /**
     * Converts a string of longitude and latitude to a [PositionPoint].
     *
     * The format of the longitude and latitude string should follow the format:
     * "%f,%f"
     * "," works as a separator for the longitude and latitude fields of [PositionPoint].
     * example : "longitude,latitude"
     *
     * @param lngLatString a string representing a [PositionPoint] as described in the description.
     * @return returns a list of [PositionPoint] with a single entry as represented by the input string. Returns null if the input string was not parsable for any reason.
     */
    fun toSingle(lngLatString: String?): List<PositionPoint>? = process(lngLatString, 1, 1)

    private fun process(pathString: String?, minCoords: Int, maxCoords: Int): List<PositionPoint>? {
        if (pathString == null || pathString == "")
            return emptyList()

        val coordinates = pathString.split(";").dropLastWhile { it.isEmpty() }.toTypedArray()

        if (coordinates.isEmpty() || coordinates[0] == "")
            return emptyList()

        if (coordinates.size < minCoords || coordinates.size > maxCoords)
            return null

        val path = mutableListOf<PositionPoint>()
        try {
            for (coordinate in coordinates) {
                val coordinateStrings = coordinate.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()

                if (coordinateStrings.size == 2)
                    path.add(PositionPoint(coordinateStrings[0].toDouble(), coordinateStrings[1].toDouble()))
                else
                    return null
            }
        } catch (e: NumberFormatException) {
            return null
        }

        return path
    }

}
