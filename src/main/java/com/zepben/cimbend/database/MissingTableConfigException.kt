/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;

@EverythingIsNonnullByDefault
@SuppressWarnings("unused")
public class MissingTableConfigException extends RuntimeException {

    public MissingTableConfigException() {
    }

    public MissingTableConfigException(String message) {
        super(message);
    }

    public MissingTableConfigException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public MissingTableConfigException(@Nullable Throwable cause) {
        super(cause);
    }

    public MissingTableConfigException(String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
