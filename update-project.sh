#!/usr/bin/env bash

set -euo pipefail

git fetch blueprint main
git checkout -B chore/sync-blueprint
git merge blueprint/main || true

read -p "Have you resolved all conflicts? (y/n) " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]; then
    git add .
    git commit -m "chore: sync with blueprint main" || true
    git checkout main
    git merge chore/sync-blueprint

    echo "All done! Don't forget to push your changes."
else
    git merge --abort
    echo "Run this script again."
fi
exit 0