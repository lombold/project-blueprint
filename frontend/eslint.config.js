import js from '@eslint/js';
import { configs as angularConfigs } from '@angular-eslint/eslint-plugin';
import { configs as angularTemplateConfigs } from '@angular-eslint/eslint-plugin-template';
import tseslint from 'typescript-eslint';

export default [
  {
    files: ['**/*.ts'],
    extends: [
      js.configs.recommended,
      ...tseslint.configs.recommended,
      ...angularConfigs.recommended,
    ],
    rules: {
      '@angular-eslint/directive-selector': [
        'error',
        { type: 'attribute', prefix: 'app', style: 'camelCase' }
      ],
      '@angular-eslint/component-selector': [
        'error',
        { type: 'element', prefix: 'app', style: 'kebab-case' }
      ],
    },
  },
  {
    files: ['**/*.html'],
    extends: [
      ...angularTemplateConfigs.recommended,
    ],
  },
];
