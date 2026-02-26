import { defineConfig } from 'vitest/config';
// @ts-expect-error Cannot find module
import angular from '@angular/build/testing';

export default defineConfig({
  plugins: [angular()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: [],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      exclude: [
        'node_modules/',
        'src/test.ts',
      ]
    }
  },
  resolve: {
    alias: {
      '@core': '/src/app/core',
      '@pages': '/src/app/pages',
      '@shared': '/src/app/shared'
    }
  }
});
