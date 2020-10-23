/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.grpc

class ThrowingGrpcClient(val ex: Throwable = RuntimeException()) : GrpcClient() {

    fun throwViaSafeTryRpc(): GrpcResult<Unit> {
        return tryRpc { throw ex }
    }

}
