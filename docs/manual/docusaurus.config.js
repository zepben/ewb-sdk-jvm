const path = require("path");
const versions = require("./versions.json");

module.exports = {
  title: "Evolve SDK (JVM)",
  tagline: "",
  url: "https://zepben.github.io/evolve/sdk-jvm/",
  baseUrl: "/evolve/sdk/",
  onBrokenLinks: "throw",
  favicon: "img/favicon.ico",
  organizationName: "zepben",
  projectName: "evolve-sdk-jvm",
  themeConfig: {
    colorMode: {
      defaultMode: "light",
      disableSwitch: false,
      respectPrefersColorScheme: true,
    },
    navbar: {
      logo: {
        alt: "Zepben",
        src: "img/logo.svg",
        srcDark: "img/logo-dark.svg",
        href: "https://www.zepben.com/",
      },
      items: [
        {
          type: "docsVersionDropdown",
          position: "right",
        },
        {
          to: "/",
          activeBasePath: "docs",
          label: "Docs",
          position: "left",
        },
        {
          href: "https://github.com/zepben/evolve-sdk-jvm/",
          label: "GitHub",
          position: "right",
          "aria-label": "GitHub repository",
        },
      ],
    },
    footer: {
      style: "dark",
      links: [],
      copyright: `Copyright © ${new Date().getFullYear()} Zeppelin Bend Pty. Ltd.`,
    },
    googleAnalytics: {
      trackingID: "TRACKING_ID",
      anonymizeIP: false,
    },
    algolia: {
      apiKey: "API_KEY",
      indexName: "INDEX_NAME",
      searchParameters: {},
    },
    prism: {
      additionalLanguages: ['kotlin'],
    },
  },
  presets: [
    [
      "@zepben/docusaurus-preset",
      {
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve("./sidebars.js"),
          editUrl: "https://github.com/zepben",
        },
        theme: {
          customCss: require.resolve("./src/css/custom.css"),
        },
      },
    ],
  ],
};
