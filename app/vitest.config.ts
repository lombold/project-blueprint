import { defineConfig } from 'vitest/config';
import { fileURLToPath, URL } from 'node:url';

export default defineConfig({
  test: {
    coverage: {
      exclude: ['node_modules/'],
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
    },
    environment: 'jsdom',
    globals: true,
    server: {
      deps: {
        inline: ['@ionic/angular', '@ionic/core'],
      },
    },
  },
  resolve: {
    alias: [
      {
        find: /^@ionic\/core\/components$/,
        replacement: fileURLToPath(
          new URL(
            './node_modules/@ionic/core/components/index.js',
            import.meta.url,
          ),
        ),
      },
    ],
  },
});
