/** @type {import('dependency-cruiser').IConfiguration} */
module.exports = {
  forbidden: [
    {
      name: 'no-circular',
      comment: 'Prevent circular dependencies across app code.',
      severity: 'error',
      from: { path: '^src/app/' },
      to: { circular: true },
    },
    {
      name: 'not-to-dev-dep',
      comment: 'Production app code must not depend on devDependencies.',
      severity: 'error',
      from: { path: '^src/app/' },
      to: { dependencyTypes: ['npm-dev'] },
    },
    {
      name: 'not-to-spec',
      comment: 'Production files must not import test specs.',
      severity: 'error',
      from: { path: '^src/app/', pathNot: '\\.spec\\.ts$' },
      to: { path: '\\.spec\\.ts$' },
    },
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
    {
      name: 'api-barrel-only',
      comment: 'Outside core/api, import API client only through core/api barrel.',
      severity: 'error',
      from: { path: '^src/app/', pathNot: '^src/app/core/api/' },
      to: {
        path: '^src/app/core/api/',
        pathNot: '^src/app/core/api/index\\.ts$',
      },
    },
    {
      name: 'no-orphans',
      comment: 'All app modules should be part of the dependency graph.',
      severity: 'error',
      from: {
        orphan: true,
        path: '^src/app/',
        pathNot: '(^src/app/app\\.ts$|^src/app/app\\.config\\.ts$|^src/app/app\\.routes\\.ts$)',
      },
      to: {},
    },
  ],
  options: {
    tsConfig: {
      fileName: './tsconfig.app.json',
    },
    exclude: '\\.spec\\.ts$',
  },
};
