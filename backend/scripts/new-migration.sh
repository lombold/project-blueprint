#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <description>"
  echo "Example: $0 add-user-status"
  exit 1
fi

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
backend_dir="$(cd "${script_dir}/.." && pwd)"
migration_dir="${backend_dir}/src/main/resources/db/migration/incremental"

description="$(echo "$1" \
  | tr '[:upper:]' '[:lower:]' \
  | sed -E 's/[^a-z0-9]+/_/g; s/^_+//; s/_+$//')"

if [[ -z "${description}" ]]; then
  echo "Description must contain at least one letter or digit."
  exit 1
fi

version="$(date -u +%Y%m%d%H%M%S)"
file="${migration_dir}/V${version}__${description}.sql"

if [[ -e "${file}" ]]; then
  echo "Migration already exists: ${file}"
  exit 1
fi

mkdir -p "${migration_dir}"
cat > "${file}" <<EOF
-- Forward-only migration: ${description}
-- Keep this file immutable after it has been committed or applied anywhere.
-- Mirror the schema change represented in ../current/R__current_schema.sql.

EOF

echo "Created ${file}"
echo "Next: add forward-only SQL, then run 'mvn test' from backend/."
