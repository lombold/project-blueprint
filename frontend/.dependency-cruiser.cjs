/** @type {import('dependency-cruiser').IConfiguration} */
module.exports = {
  forbidden: [
    {
      name: 'core-not-to-pages',
      comment: 'Core must not depend on route-level pages.',
      severity: 'error',
      from: { path: '^src/app/core/' },
      to: { path: '^src/app/pages/' },
    },
    {
      name: 'shared-not-to-core',
      comment: 'Shared must stay framework-agnostic and avoid core wiring.',
      severity: 'error',
      from: { path: '^src/app/shared/' },
      to: { path: '^src/app/core/' },
    },
    {
      name: 'shared-not-to-pages',
      comment: 'Shared must not depend on route-level pages.',
      severity: 'error',
      from: { path: '^src/app/shared/' },
      to: { path: '^src/app/pages/' },
    },
    {
      name: 'pages-feature-isolation',
      comment: 'One page feature must not import another page feature directly.',
      severity: 'error',
      from: { path: '^src/app/pages/([^/]+)/' },
      to: {
        path: '^src/app/pages/',
        pathNot: '^src/app/pages/$1/',
      },
    },
  ],
  options: {
    tsConfig: {
      fileName: './tsconfig.app.json',
    },
    exclude: '\\.spec\\.ts$',
  },
};
