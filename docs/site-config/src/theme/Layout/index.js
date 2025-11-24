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
