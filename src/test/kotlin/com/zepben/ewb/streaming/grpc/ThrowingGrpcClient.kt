/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.grpc

import com.zepben.protobuf.nc.NetworkConsumerGrpc

class ThrowingGrpcClient(val ex: Throwable = RuntimeException(), override val stub: NetworkConsumerGrpc.NetworkConsumerStub) :
    GrpcClient<NetworkConsumerGrpc.NetworkConsumerStub>() {

    fun throwViaSafeTryRpc(): GrpcResult<Unit> = tryRpc { throw ex }

}
