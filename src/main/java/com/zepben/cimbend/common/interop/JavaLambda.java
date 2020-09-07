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
