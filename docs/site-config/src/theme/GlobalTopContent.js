/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import React from 'react';

function GlobalTopContent() {
  return (
    <div style={{ backgroundColor: 'lightyellow', padding: '10px', textAlign: 'center' }}>
      🚨 This is an important announcement! 🚨
      <br/>
      This documentation site is no longer actively maintained. 
      <br/>
      For up-to-date documentation, see the new site at the <a href="https://zepben.github.io/evolve/docs/replacethis/">EWB JVM SDK</a> page. 
    </div>
  );
}

export default GlobalTopContent;
