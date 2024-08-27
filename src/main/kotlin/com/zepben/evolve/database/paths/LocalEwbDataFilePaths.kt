/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.paths

import java.nio.file.FileVisitOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate


// LocalFileSystemEwbDataFilePaths("/data/ewb")
class LocalEwbDataFilePaths  @JvmOverloads constructor(
    private val baseDir: Path,
    createPath: Boolean = false,
    private val createDirectories: (Path) -> Path = { Files.createDirectories(it) },
    isDirectory: (Path) -> Boolean = { Files.isDirectory(it) },
    private val exists: (Path) -> Boolean = { Files.exists(it) },
    private val listFiles: (Path) -> Iterator<Path> = { Files.walk(it, 2, FileVisitOption.FOLLOW_LINKS).iterator() }
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
}
