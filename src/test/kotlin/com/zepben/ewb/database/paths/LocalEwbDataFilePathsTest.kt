/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.paths

import com.zepben.testutils.exception.ExpectException.Companion.expect
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate

class LocalEwbDataFilePathsTest {

    private val today = LocalDate.now()
    private val baseDir = Paths.get("/some/path/to/ewb/data")

    private val createDirectories = mockk<(Path) -> Path>().also { every { it(any()) } answers { firstArg() } }
    private val isDirectory = mockk<(Path) -> Boolean>().also { every { it(any()) } returns true }
    private val exists = mockk<(Path) -> Boolean>().also { every { it(any()) } returns true }
    private val listFiles = mockk<(Path) -> Iterator<Path>>().also { every { it(any()) } answers { descendants.iterator() } }
    private val descendants = mutableListOf<Path>()

    private val ewbPaths = LocalEwbDataFilePaths(baseDir, createPath = false, createDirectories, isDirectory, exists, listFiles)

    @Test
    internal fun `constructor coverage`() {
        // Coverage of default values being used by primary constructor.
        LocalEwbDataFilePaths(Paths.get("."))

        // Coverage of secondary constructor.
        LocalEwbDataFilePaths(".")
        LocalEwbDataFilePaths(".", createPath = true)
    }

    @Test
    internal fun `validates directory is valid at construction`() {
        verifySequence {
            isDirectory(baseDir)
        }
        confirmVerified(createDirectories, isDirectory, exists)

        every { isDirectory(any()) } returns false

        expect { LocalEwbDataFilePaths(baseDir, createPath = false, createDirectories, isDirectory, exists) }
            .toThrow<IllegalArgumentException>()
            .withMessage("baseDir must be a directory")
    }

    @Test
    internal fun `creates missing root directory at construction if requested`() {
        // Clear the calls from the member variable call to the constructor.
        clearMocks(isDirectory, answers = false)

        LocalEwbDataFilePaths(baseDir, createPath = true, createDirectories, isDirectory, exists)

        verifySequence {
            createDirectories(baseDir)
            isDirectory(baseDir)
        }
        confirmVerified(createDirectories, isDirectory, exists)
    }

    @Test
    internal fun `formats paths`() {
        DatabaseType.entries.forEach { type ->
            if (type.perDate)
                assertThat(ewbPaths.resolve(type, today), equalTo(baseDir.datedPath(today, type.fileDescriptor)))
            else {
                expect { ewbPaths.resolve(type, today) }
                    .toThrow<IllegalArgumentException>()
                    .withMessage("type must have its perDate set to true to use this method.")
            }
        }

        DatabaseType.entries.forEach { type ->
            if (!type.perDate)
                assertThat(ewbPaths.resolve(type), equalTo(baseDir.resolve(type.fileDescriptor + ".sqlite")))
            else {
                expect { ewbPaths.resolve(type) }
                    .toThrow<IllegalArgumentException>()
                    .withMessage("type must have its perDate set to false to use this method.")
            }
        }
    }

    @Test
    internal fun `creates date directories if they dont exist`() {
        val dateDir = baseDir.resolve(today.toString())

        assertThat(ewbPaths.createDirectories(today), equalTo(dateDir))

        confirmVerified(createDirectories)
        every { exists(any()) } returns false

        assertThat(ewbPaths.createDirectories(today), equalTo(dateDir))

        verifySequence { createDirectories(dateDir) }
    }

    @Test
    internal fun `finds specified date if it exists`() {
        // Files for today.
        DatabaseType.entries.filter { it.perDate }.forEach {
            descendants.add(Path.of(today.toString(), "${today}-${it.fileDescriptor}.sqlite"))
        }
        validateClosest(today)

        // Should return null without checking a file as they are not date based, even if the file exists.
        validateClosest(DatabaseType.ENERGY_READINGS_INDEX, null)
        validateClosest(DatabaseType.LOAD_AGGREGATOR_METERS_BY_DATE, null)
        validateClosest(DatabaseType.WEATHER_READING, null)
        validateClosest(DatabaseType.RESULTS_CACHE, null)
    }

    @Test
    internal fun `finds previous date if it exists and today is missing`() {
        // NOTE: We want to use two days ago rather than yesterday to make sure it searches more than one day.
        val twoDaysAgo = today.minusDays(2)

        // Files for 2 days ago.
        DatabaseType.entries.filter { it.perDate }.forEach {
            descendants.add(Path.of(twoDaysAgo.toString(), "${twoDaysAgo}-${it.fileDescriptor}.sqlite"))
        }

        validateClosest(twoDaysAgo)
    }

    @Test
    internal fun `doesn't find files outside the search window`() {
        val elevenDaysAgo = today.minusDays(11)

        // Only find files for 11 days ago (outside the 10-day search).
        every { exists(any()) } returns false
        every { exists(match { it.toString().contains(elevenDaysAgo.toString()) }) } returns true

        validateClosest(null)
    }

    @Test
    internal fun `can search forwards in time`() {
        // NOTE: We want to use two days from now rather than tomorrow to make sure it searches more than one day. We also use
        //       three days ago to make sure it is searching outwards from the date, not into the past then the future.
        val twoDaysFromNow = today.plusDays(2)
        val threeDaysAgo = today.minusDays(3)

        // Files for 2 days from now and 3 days ago.
        DatabaseType.entries.filter { it.perDate }.forEach {
            descendants.add(Path.of(twoDaysFromNow.toString(), "${twoDaysFromNow}-${it.fileDescriptor}.sqlite"))
            descendants.add(Path.of(threeDaysAgo.toString(), "${threeDaysAgo}-${it.fileDescriptor}.sqlite"))
        }

        validateClosest(twoDaysFromNow, searchForwards = true)
    }

    @Test
    internal fun `closest date using default parameters`() {
        val tomorrow = today.plusDays(1)
        val twoDaysAgo = today.minusDays(2)

        // Files for tomorrow and 2 days ago.
        DatabaseType.entries.filter { it.perDate }.forEach {
            descendants.add(Path.of(twoDaysAgo.toString(), "${twoDaysAgo}-${it.fileDescriptor}.sqlite"))
            descendants.add(Path.of(tomorrow.toString(), "${tomorrow}-${it.fileDescriptor}.sqlite"))
        }

        // Should find two days ago as it doesn't search forward by default.
        assertThat(ewbPaths.findClosest(DatabaseType.NETWORK_MODEL), equalTo(twoDaysAgo))
    }

    @Test
    internal fun `getAvailableDatesFor accepts date types`() {
        DatabaseType.entries.filter { it.perDate }.forEach {
            `validate getAvailableDatesFor for date type`(it)
        }
    }

    @Test
    internal fun `getAvailableDatesFor throws on non-date type`() {
        DatabaseType.entries.filter { !it.perDate }.forEach {
            expect { `validate getAvailableDatesFor for date type`(it) }
                .toThrow<IllegalStateException>()
                .withMessage("INTERNAL ERROR: Should only be calling `getAvailableDatesFor` for `perDate` files, which should all be covered above, so go ahead and add it.")
        }
    }

    @Test
    internal fun `getAvailableDatesFor() sorts the returned dates`() {
        val directories = listOf(
            Path.of("2001-02-03", "network-model.sqlite"),
            Path.of("2032-05-07", "network-model.sqlite"),
            Path.of("2009-05-09", "network-model.sqlite"),
            Path.of("2009-05-08", "network-model.sqlite")
        )

        every { listFiles(baseDir) } answers { directories.iterator() }

        assertThat(
            ewbPaths.getAvailableDatesFor(DatabaseType.NETWORK_MODEL), equalTo(
                listOf(
                    LocalDate.parse("2001-02-03"),
                    LocalDate.parse("2009-05-08"),
                    LocalDate.parse("2009-05-09"),
                    LocalDate.parse("2032-05-07"),
                )
            )
        )
    }

    @Test
    internal fun enumerateDescendants() {
        descendants.addAll(
            listOf(
                Path.of(today.toString(), "${today}-network-model.sqlite"),
                Path.of(today.toString(), "${today}-customer.sqlite"),
                Path.of("results-cache.sqlite"),
                Path.of("weather-readings.sqlite")
            )
        )

        val result = ewbPaths.enumerateDescendants()
        assertThat(result.asSequence().count(), equalTo(descendants.size))
        ewbPaths.enumerateDescendants().forEach {
            assertThat("$it - all listed files should have been found in the results.", descendants.contains(it))
        }
    }

    @Test
    internal fun resolveDatabase() {
        val path = "2333-11-22"

        assertThat(ewbPaths.resolveDatabase(Paths.get(path)), equalTo(baseDir.resolve(path)))
    }

    @Test
    internal fun `resolves variant databases`() {
        fun DatabaseType.toVariantPath(variant: String) =
            baseDir.resolve(today.toString()).resolve(EwbDataFilePaths.VARIANTS_PATH).resolve(variant).resolve("$today-$fileDescriptor.sqlite")

        DatabaseType.entries.forEach { type ->
            if (type.perDate) {
                assertThat(ewbPaths.resolve(type, today, "my-variant1"), equalTo(type.toVariantPath("my-variant1")))
                assertThat(ewbPaths.resolve(type, today, "my-variant2"), equalTo(type.toVariantPath("my-variant2")))
            } else {
                expect { ewbPaths.resolve(type, today, "my-variant") }
                    .toThrow<IllegalArgumentException>()
                    .withMessage("type must have its perDate set to true to use this method.")
            }
        }
    }

    @Test
    internal fun `can request variants for a day`() {
        val yesterday = today.minusDays(1)

        descendants += listOf(
            Path.of(yesterday.toString(), EwbDataFilePaths.VARIANTS_PATH, "my-variant-1"),
            Path.of(yesterday.toString(), EwbDataFilePaths.VARIANTS_PATH, "my-variant-2"),

            Path.of(today.toString(), EwbDataFilePaths.VARIANTS_PATH, "my-variant-2"),
            Path.of(today.toString(), EwbDataFilePaths.VARIANTS_PATH, "my-variant-3"),
        )

        assertThat(ewbPaths.getAvailableVariantsFor(yesterday), contains("my-variant-1", "my-variant-2"))

        // No date will default to today.
        assertThat(ewbPaths.getAvailableVariantsFor(), contains("my-variant-2", "my-variant-3"))

        // Should return an empty list if there are no variants for the specified date.
        assertThat(ewbPaths.getAvailableVariantsFor(today.minusDays(2)), empty())
    }

    @Test
    internal fun `variant databases don't count for the exists check for find nearest`() {
        val t1 = today.minusDays(1)
        val t2 = today.minusDays(2)

        DatabaseType.entries.filter { it.perDate }.forEach {
            descendants.add(Path.of(t1.toString(), EwbDataFilePaths.VARIANTS_PATH, "my-variant", "${t1}-${it.fileDescriptor}.sqlite"))
        }

        DatabaseType.entries.filter { it.perDate }.forEach {
            assertThat(ewbPaths.findClosest(it, maxDaysToSearch = 3), nullValue())
        }

        DatabaseType.entries.filter { it.perDate }.forEach {
            descendants.add(Path.of(t2.toString(), "${t2}-${it.fileDescriptor}.sqlite"))
        }

        DatabaseType.entries.filter { it.perDate }.forEach {
            assertThat(ewbPaths.findClosest(it, maxDaysToSearch = 3), equalTo(t2))
        }
    }

    @Test
    internal fun `only folders under variants are included`() {
        val yesterday = today.minusDays(1)

        descendants.add(Path.of(yesterday.toString(), "not-variant", "my-variant-1"))

        assertThat(ewbPaths.getAvailableVariantsFor(yesterday), empty())
    }

    private fun validateClosest(expectedDate: LocalDate?, searchForwards: Boolean = false) {
        DatabaseType.entries.filter { it.perDate }.forEach {
            validateClosest(it, expectedDate, searchForwards)
        }
    }

    private fun validateClosest(type: DatabaseType, expectedDate: LocalDate?, searchForwards: Boolean = false) {
        assertThat(ewbPaths.findClosest(type, 10, today, searchForwards), equalTo(expectedDate))
    }

    private fun `validate getAvailableDatesFor for date type`(dbType: DatabaseType) {
        clearMocks(isDirectory, exists, listFiles, answers = false)

        val usableDirectories = listOf("2001-02-03", "2001-02-04", "2011-03-09")
        val emptyDirectories = listOf("2111-11-11", "2222-12-14")
        val nonDateDirectories = listOf("other_data", "2002-02-04-backup", "backup-2011-03-09")
        val nonDirectoryFiles = listOf("config.json", "other", "run.sh", "1234-11-22")

        val otherPaths = (emptyDirectories + nonDateDirectories + nonDirectoryFiles)

        every { listFiles(baseDir) } answers {
            (usableDirectories.map { Paths.get(baseDir.toString(), it, "${dbType.fileDescriptor}.sqlite") } +
                otherPaths.map { Paths.get(baseDir.toString(), it) }).iterator()
        }

        assertThat(ewbPaths.getAvailableDatesFor(dbType), equalTo(usableDirectories.map { LocalDate.parse(it) }))

        verify { listFiles(baseDir) }
    }

    private fun Path.datedPath(date: LocalDate, name: String): Path =
        resolve(date.toString()).resolve("$date-$name.sqlite")

}
