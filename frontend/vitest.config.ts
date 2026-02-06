import { defineConfig } from 'vitest/config';
import angular from '@angular/build/testing';
import { buildAngularTestingEnvironment } from '@angular/build/testing';

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
      '@': '/src'
    }
  }
});
