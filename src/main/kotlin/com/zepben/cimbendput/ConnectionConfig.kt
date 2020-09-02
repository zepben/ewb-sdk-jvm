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

package com.zepben.cimbendput

/**
 * Configuration for connecting to a gRPC Producer server.
 *
 * @property host The host of the producer server
 * @property port The port the producer server is listening on
 * @property certChainFilePath
 * @property privateKeyFilePath
 * @property trustCertCollectionFilePath
 */
data class ConnectionConfig(
    val host: String,
    val port: Int,
    val certChainFilePath: String? = null,
    val privateKeyFilePath: String? = null,
    val trustCertCollectionFilePath: String? = null)
