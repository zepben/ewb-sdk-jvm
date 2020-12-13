/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.common.interop;

import com.zepben.annotations.EverythingIsNonnullByDefault;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

@EverythingIsNonnullByDefault
public class JavaClass {

    public static <T> KClass<T> toKClass(Class<T> clazz) {
        return JvmClassMappingKt.getKotlinClass(clazz);
    }

}
