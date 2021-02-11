/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.translator;

import com.zepben.evolve.streaming.grpc.ConnectionConfig;
import com.zepben.evolve.streaming.grpc.GrpcChannel;
import com.zepben.evolve.streaming.grpc.GrpcChannelFactory;

public class testjava {

    testjava(){
        try (GrpcChannel x = GrpcChannelFactory.create(new ConnectionConfig("localhost", 50052))){
        }
    }
}
