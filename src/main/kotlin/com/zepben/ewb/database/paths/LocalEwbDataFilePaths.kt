/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.paths

import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate


/**
 * Provides paths to all the various data files / folders in the local file system used by EWB.
 *
 * @param baseDir The root directory of the EWB data structure.
 * @param createPath Create the root directory (and any missing parent folders) if it does not exist.
 * @param createDirectories Function for directory creation.
 * @param isDirectory Function to determine if the supplied path is a directory .
 * @param exists Function to determine if the supplied path exists.
 * @param listFiles Function for listing directories and files under the supplied path.
 */
class LocalEwbDataFilePaths @JvmOverloads constructor(
    private val baseDir: Path,
    createPath: Boolean = false,
    private val createDirectories: (Path) -> Path = { Files.createDirectories(it) },
    isDirectory: (Path) -> Boolean = { Files.isDirectory(it) },
    private val exists: (Path) -> Boolean = { Files.exists(it) },
    private val listFiles: (Path) -> Iterator<Path> = { Files.walk(it, MAX_DEPTH, FileVisitOption.FOLLOW_LINKS).iterator() }
) : EwbDataFilePaths {

    init {
        if (createPath)
            createDirectories(baseDir)

        require(isDirectory(baseDir)) { "baseDir must be a directory" }
    }

    @JvmOverloads
    constructor(baseDir: String, createPath: Boolean = false) : this(Paths.get(baseDir), createPath)

    override fun createDirectories(date: LocalDate): Path {
        val datePath = baseDir.resolve(date.toString())
        return if (exists(datePath))
            datePath
        else
            createDirectories(datePath)
    }

    override fun enumerateDescendants(): Iterator<Path> =
        listFiles(baseDir)

    override fun resolveDatabase(path: Path): Path =
        baseDir.resolve(path)

    private companion object {

        // The maximum depth to follow links. A depth of 4 is used to allow finding:
        // 1. The top level date directory.
        // 2. The base service databases and the variants sub folder.
        // 3. The variant folders.
        // 4. The variant service databases.
        const val MAX_DEPTH = 4

    }

}
