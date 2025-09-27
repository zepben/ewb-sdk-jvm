/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61968.common

import com.zepben.ewb.cim.extensions.ZBEX

/**
 * [ZBEX] The method to use to make contact with a person or company.
 */
@ZBEX
enum class ContactMethodType {

    /**
     * [ZBEX] Unknown contact method type.
     */
    @ZBEX
    UNKNOWN,

    /**
     * [ZBEX] Contact via email using the primary email address.
     */
    @ZBEX
    EMAIL,

    /**
     * [ZBEX] Contact by call using the primary phone number.
     */
    @ZBEX
    CALL,

    /**
     * [ZBEX] Letter by post to the contact address is the method of contact.
     */
    @ZBEX
    LETTER,

}
