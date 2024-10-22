/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.SetCurrentStatesStatus
import io.mockk.mockk

class UpdateNetworkStateServiceTest {
    private val onSetCurrentStates = mockk<(events: List<CurrentStateEvent>) -> SetCurrentStatesStatus>()
    private val service = UpdateNetworkStateService(onSetCurrentStates)


}
