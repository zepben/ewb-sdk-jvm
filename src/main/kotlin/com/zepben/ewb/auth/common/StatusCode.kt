/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.common

enum class StatusCode(val code: Int) {
    // Successful
    OK(200),

    // Token was malformed
    MALFORMED_TOKEN(400),

    // Failed to authenticate
    UNAUTHENTICATED(403),

    // Failed to authenticate, token didn't have required claims
    PERMISSION_DENIED(403),

    // Resource/service not found
    NOT_FOUND(404),

    // All other errors
    UNKNOWN(500);

}
