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
     * [DatabaseType.perDate] set to true and the specified [LocalDate], within the [variant], for the specified [variantContents].
     *
     * ChangeSet content is split into two separate databases for each supported [DatabaseType], with targets of ObjectCreations and ObjectModifications going
     * to one database, and targets of ObjectDeletions and ObjectReverseModifications going to another. This is to avoid conflicting IDs between the two
     * databases.
     * The ChangeSet and it's associated ObjectCreation, ObjectDeletion, and ObjectModifications will be contained in a single [DatabaseType.VARIANT] database.
     *
     * @param type The [DatabaseType] to use for the database [Path].
     * @param date The [LocalDate] to use for the database [Path].
     * @param variant The name of the variant containing the database.
     * @param variantContents The relevant content for the desired [type].
     * @return The [Path] to the [DatabaseType] database file for the [variant].
     */
    fun resolve(type: DatabaseType, date: LocalDate, variant: String, variantContents: VariantContents): Path {
        require(type.perDate) { "type must have its perDate set to true to use this method." }
        require(variantContents.types.contains(type)) { "type must be compatible with variantContents. Compatible options for ${variantContents.name}: ${variantContents.types.joinToString(",")}" }
        return resolveDatabase(date.toDatedVariantPath(type, variant, variantContents))
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
     * Find available variants for the specified [date] in data path.
     *
     * @param date The target date. Defaults to today.
     *
     * @return list of variant names that exist in the data path for the specified [date].
     */
    fun getAvailableVariantsFor(date: LocalDate = LocalDate.now()): List<String> {
        return enumerateDescendants("$date/$VARIANTS_PATH")
            .asSequence()
            .filter { it.parent?.parent?.fileName.toString() == VARIANTS_PATH }
            .map { it.parent?.fileName.toString() }
            .sorted()
            .toList()
    }

    /**
     * Lists the child items of source location.
     *
     * @return collection of child items.
     */
    fun enumerateDescendants(prefix: String? = null): Iterator<Path>

    /**
     * Resolves the database in the specified source [Path].
     *
     * @param path [Path] to the source database file.
     * @return [Path] to the local database file.
     */
    fun resolveDatabase(path: Path): Path

    /**
     * Generate a file path for a given date and type.
     *
     * @param type The [DatabaseType] to use for the database [Path].
     * @param date The [LocalDate] to use for the database [Path].
     */
    fun getDatedPath(type: DatabaseType, date: LocalDate): Path = date.toDatedPath(type)

    /**
     * Generate a file path for a given date, variant, and the contents of that variant.
     *
     * @param type The [DatabaseType] to use for the database [Path].
     * @param date The [LocalDate] to use for the database [Path].
     * @param variant The name of the variant containing the database.
     * @param variantContents The relevant content for the desired [type].
     * @return The [Path] to the [DatabaseType] database file for the [variant].
     */
    fun getDatedVariantPath(type: DatabaseType, date: LocalDate, variant: String, variantContents: VariantContents): Path =
        date.toDatedVariantPath(type, variant, variantContents)

    /**
     * Parses a dated variant [path] into its constituent components.
     *
     * This is the inverse of [getDatedVariantPath].
     *
     * Expected path formats:
     * - `{date}/variants/{variant}/{subDirectory}/{date}-{databaseName}` (when [VariantContents.subDirectory] is non-empty)
     * - `{date}/variants/{variant}/{date}-{databaseName}` (when [VariantContents.subDirectory] is empty)
     *
     * @param path The path to parse.
     * @return A [DatedVariantPathComponents] containing the extracted [DatabaseType], [LocalDate], variant name, and [VariantContents].
     * @throws IllegalArgumentException If the path does not match the expected format.
     */
    fun parseDatedVariantPath(path: Path): DatedVariantPathComponents {
        val pathComponents = path.toList()

        val (variant, variantContents, fileName) = when (pathComponents.size) {
            4 -> Triple(pathComponents[2], VariantContents.CHANGESET, pathComponents[3].toString())
            5 -> {
                val subDir = pathComponents[3].toString()
                Triple(
                    pathComponents[2],
                    VariantContents.entries.firstOrNull { it.subDirectory == subDir }
                        ?: throw IllegalArgumentException("Invalid path. There is no `VariantContent` for the sub directory `$subDir`."),
                    pathComponents[4].toString())
            }

            else -> throw IllegalArgumentException("Invalid path. Make sure the path is correct by using `getDatedVariantPath`.")
        }
        val dateStr = pathComponents[0].toString()
        val type = variantContents.types.firstOrNull { fileName == "$dateStr-${it.databaseName}" }
            ?: throw IllegalArgumentException("Invalid path. There is no `DatabaseType` for the file name `$fileName`.")

        return DatedVariantPathComponents(
            type = type,
            date = LocalDate.parse(dateStr),
            variant = variant.toString(),
            variantContents = variantContents,
        )
    }

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

    private fun LocalDate.toDatedVariantPath(type: DatabaseType, variant: String, variantContents: VariantContents): Path =
        toString().let { dateStr -> Paths.get(dateStr).resolve(VARIANTS_PATH).resolve(variant).resolve(variantContents.subDirectory).resolve("$dateStr-${type.databaseName}") }

    private val DatabaseType.databaseName: String get() = "$fileDescriptor.sqlite"

    companion object {

        /**
         * The folder containing the variants. Will be placed under the dated folder alongside the network model database.
         */
        const val VARIANTS_PATH: String = "variants"

    }

}
