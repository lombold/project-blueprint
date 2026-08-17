/** @type {import('dependency-cruiser').IConfiguration} */
module.exports = {
  forbidden: [
    {
      name: "no-circular",
      severity: "error",
      from: { path: "^src/app/" },
      to: { circular: true },
    },
    {
      name: "core-not-to-pages",
      severity: "error",
      from: { path: "^src/app/core/" },
      to: { path: "^src/app/pages/" },
    },
    {
      name: "shared-not-to-core-or-pages",
      severity: "error",
      from: { path: "^src/app/shared/" },
      to: { path: "^src/app/(core|pages)/" },
    },
    {
      name: "pages-feature-isolation",
      severity: "error",
      from: { path: "^src/app/pages/([^/]+)/" },
      to: {
        path: "^src/app/pages/",
        pathNot: "^src/app/pages/$1/",
      },
    },
    {
      name: "api-barrel-only",
      severity: "error",
      from: { path: "^src/app/", pathNot: "^src/app/core/api/" },
      to: {
        path: "^src/app/core/api/",
        pathNot: "^src/app/core/api/index\\.ts$",
      },
    },
  ],
  options: {
    exclude: "\\.spec\\.ts$",
    tsConfig: { fileName: "./tsconfig.app.json" },
  },
};
