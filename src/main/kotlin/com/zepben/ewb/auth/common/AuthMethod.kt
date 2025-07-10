/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.auth.common

/**
 * An enum class that represents the different authentication methods that could be returned from the server's
 * ewb/config/auth endpoint.
 */
enum class AuthMethod {
    NONE,
    SELF,
    OAUTH,
    AUTH0,
    ENTRAID
}
