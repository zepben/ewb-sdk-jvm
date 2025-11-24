/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

import React from 'react';
import OriginalLayout from '@theme-original/Layout';
import GlobalTopContent from '@theme/GlobalTopContent';

function Layout(props) {
  return (
    <div>
      <GlobalTopContent />
      <OriginalLayout {...props}>
        {props.children}
      </OriginalLayout>
    </div>
  );
}

export default Layout;
