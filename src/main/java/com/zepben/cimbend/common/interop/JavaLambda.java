/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.common.interop;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EverythingIsNonnullByDefault
public class JavaLambda {

    public static <T> Function1<T, Unit> unit(Consumer<T> callable) {
        return t -> {
            callable.accept(t);
            return Unit.INSTANCE;
        };
    }

    public static <T, U> Function2<T, U, Unit> unit(BiConsumer<T, U> callable) {
        return (t, u) -> {
            callable.accept(t, u);
            return Unit.INSTANCE;
        };
    }

}
