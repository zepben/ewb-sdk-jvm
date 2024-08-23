/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.paths

import java.io.IOException
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

    @Throws(IOException::class)
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

// AwsS3EwbDataFilePaths("s3://zepben-ap-southeast-2-ewb-storage20240820032035111200000001")
//class S3EwbDataFilePaths(private val s3Url: S3Obj,
//                            private val localDir: Path,
//                            createPath: Boolean = false,
//                            private val createDirectories: (Path) -> Path = { Files.createDirectories(it) },
//                            private val isDirectory: (Path) -> Boolean = { Files.isDirectory(it) },
//                            private val exists: (Path) -> Boolean = { Files.exists(it) },
//                            private val listFiles: (Path) -> Iterator<Path> = { Files.list(it).iterator() }) : EwbDataFilePaths {
//
//    init {
//        if (createPath)
//            createDirectories(localDir)
//
//        require(isDirectory(localDir)) { "localDir must be a directory" }
//    }
//
//    private val s3Url = URI.create(s3Url)
//    override fun enumerateChildren(): Sequence<String> {
//        // s3 ls path
//        TODO("Not yet implemented")
//    }
//
//    override fun locationExists(path: Path): Boolean {
//        // check if path is valid in s3
//        TODO("Not yet implemented")
//    }
//
//    override fun resolveDatabase(path: Path): Path {
//        // Download from S3 + path to tmpDir + path
//        // Return tmpDir + path
//        TODO("Not yet implemented")
//    }
//}
//
//
//class S3EwbUploader(s3Url: String, localDir: Path, ewbLocalFilePaths: LocalEwbDataFilePaths = LocalEwbDataFilePaths(localDir, true)) : EwbDataFilePaths by ewbLocalFilePaths{
//    fun upload(dbType: DatabaseType, date: LocalDate){
//        // upload resolve(dbType, date) to s3Url + date.toDatedPath(dbType.fileDescriptor)
//    }
//
//    fun upload(dbType: DatabaseType){
//        // upload resolve(dbType) to s3Url + Paths.get(dbType.fileDescriptor + ".sqlite")
//    }
//}
