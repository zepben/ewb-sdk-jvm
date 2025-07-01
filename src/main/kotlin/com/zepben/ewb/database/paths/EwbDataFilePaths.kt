/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.paths

import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import kotlin.io.path.name

/**
 * Provides paths to all the various data files / folders used by EWB.
 */
interface EwbDataFilePaths {

    /**
     * Resolves the [Path] to the database file for the specified [DatabaseType] that has
     * [DatabaseType.perDate] set to true and the specified [LocalDate].
     *
     * @param type The [DatabaseType] to use for the database [Path].
     * @param date The [LocalDate] to use for the database [Path].
     * @return The [Path] to the [DatabaseType] database file.
     */
    fun resolve(type: DatabaseType, date: LocalDate): Path {
        require(type.perDate) { "type must have its perDate set to true to use this method." }
        return resolveDatabase(date.toDatedPath(type))
    }

    /**
     * Resolves the [Path] to the database file for the specified [DatabaseType] that has
     * [DatabaseType.perDate] set to false.
     *
     * @param type The [DatabaseType] to use for the database [Path].
     * @return The [Path] to the [DatabaseType] database file.
     */
    fun resolve(type: DatabaseType): Path {
        require(!type.perDate) { "type must have its perDate set to false to use this method." }
        return resolveDatabase(Paths.get(type.databaseName))
    }

    /**
     * Create the directories required to have a valid path for the specified date.
     *
     * @param date The [LocalDate] required in the path.
     * @return The [Path] to the directory for the [date].
     */
    @Throws(IOException::class)
    fun createDirectories(date: LocalDate): Path

    /**
     * Find the closest date with a usable database of the specified type.
     *
     * @param type The type of database to search for.
     * @param maxDaysToSearch The maximum number of days to search for a valid database.
     * @param date The target date. Defaults to today.
     * @param searchForwards Indicates the search should also look forwards in time from [date] for a valid file. Defaults to reverse search only.
     *
     * @return The closest [LocalDate] to [date] with a valid database of [type] within the search parameters, or null if no valid database was found.
     */
    fun findClosest(type: DatabaseType, maxDaysToSearch: Int = 999999, date: LocalDate = LocalDate.now(), searchForwards: Boolean = false): LocalDate? {
        // We do not want to return a date for non date based files.
        if (!type.perDate)
            return null

        val descendants = enumerateDescendants().asSequence().toList()
        if (checkExists(descendants, type, date))
            return date

        var offset = 1L
        while (offset <= maxDaysToSearch) {
            val previousDate = date.minusDays(offset)
            if (checkExists(descendants, type, previousDate))
                return previousDate

            if (searchForwards) {
                val forwardDate = date.plusDays(offset)
                if (checkExists(descendants, type, forwardDate))
                    return forwardDate
            }
            ++offset
        }

        return null
    }

    /**
     * Find available databases specified by [DatabaseType] in data path.
     *
     * @param type The type of database to search for.
     *
     * @return list of [LocalDate]'s for which this specified [DatabaseType] databases exist in the data path.
     */
    fun getAvailableDatesFor(type: DatabaseType): List<LocalDate> {
        if (!type.perDate)
            throw IllegalStateException("INTERNAL ERROR: Should only be calling `getAvailableDatesFor` for `perDate` files, which should all be covered above, so go ahead and add it.")

        val descendants = enumerateDescendants().asSequence().toList()
        return descendants
            .filter { it.name.endsWith(type.databaseName) }
            .mapNotNull { it.parent.runCatching { LocalDate.parse(name) }.getOrNull() }
            .sorted()
            .toList()
    }

    /**
     * Lists the child items of source location.
     *
     * @return collection of child items.
     */
    fun enumerateDescendants(): Iterator<Path>

    /**
     * Resolves the database in the specified source [Path].
     *
     * @param path [Path] to the source database file.
     * @return [Path] to the local database file.
     */
    fun resolveDatabase(path: Path): Path

    /**
     * Check if a database [Path] of the specified [DatabaseType] and [LocalDate] exists.
     *
     * @param descendants A list of [Path] representing the descendant paths.
     * @param type The type of database to search for.
     * @param date The date to check.
     *
     * @return True if a database of the specified [type] and [date] exits in the date path.
     */
    private fun checkExists(descendants: List<Path>, type: DatabaseType, date: LocalDate): Boolean =
        descendants.any { cp -> cp.endsWith(date.toDatedPath(type)) }

    private fun LocalDate.toDatedPath(type: DatabaseType): Path =
        toString().let { dateStr -> Paths.get(dateStr).resolve("$dateStr-${type.databaseName}") }

    private val DatabaseType.databaseName: String get() = "$fileDescriptor.sqlite"

}
