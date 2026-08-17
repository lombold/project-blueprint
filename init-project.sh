#!/usr/bin/env bash
#
# init-project.sh — Clone the blueprint and create a new project from it.
#
# Usage:
#   ./init-project.sh <kebab-case-name> <github-owner> --app-id <reverse-dns-id> [--target-dir <path>]
#
# Example:
#   ./init-project.sh my-cool-app lombold --app-id ch.lombold.mycoolapp
#   ./init-project.sh my-cool-app lombold --app-id ch.lombold.mycoolapp --target-dir ~/Projects/my-cool-app
#
# What it does:
#   1. Clones the blueprint repo (full history preserved)
#   2. Derives all case variants from the kebab-case name
#   3. Replaces all references in file contents
#   4. Renames files and directories
#   5. Regenerates OpenAPI clients for the renamed contract
#   6. Replaces template placeholders in README.md
#   7. Re-points the git origin to the new GitHub repo
#   8. Creates the GitHub repo and pushes
#
# Prerequisites:
#   - git, gh (GitHub CLI, authenticated), Java 25, Maven, sed, find
#
set -euo pipefail

# ─── Colors ──────────────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# ─── Helpers ─────────────────────────────────────────────────────────────────

die() { echo -e "${RED}ERROR: $*${NC}" >&2; exit 1; }
info() { echo -e "${CYAN}→ $*${NC}"; }
success() { echo -e "${GREEN}✓ $*${NC}"; }
warn() { echo -e "${YELLOW}⚠ $*${NC}"; }

usage() {
  echo "Usage: $0 <kebab-case-name> <github-owner> --app-id <reverse-dns-id> [--target-dir <path>]"
  echo ""
  echo "  <kebab-case-name>  New project name in kebab-case (e.g., my-cool-app)"
  echo "  <github-owner>     GitHub user or org (e.g., lombold)"
  echo "  --app-id           Native application ID (e.g., ch.lombold.mycoolapp)"
  echo "  --target-dir       Where to create the project (default: ./<name>)"
}

# Convert kebab-case to camelCase:  my-cool-app → myCoolApp
to_camel() {
  local result=""
  IFS='-' read -ra parts <<< "$1"
  for i in "${!parts[@]}"; do
    local part="${parts[$i]}"
    if [[ $i -eq 0 ]]; then
      result="$part"
    else
      result="${result}$(echo "${part:0:1}" | tr '[:lower:]' '[:upper:]')${part:1}"
    fi
  done
  echo "$result"
}

# Convert kebab-case to PascalCase:  my-cool-app → MyCoolApp
to_pascal() {
  local result=""
  IFS='-' read -ra parts <<< "$1"
  for part in "${parts[@]}"; do
    result="${result}$(echo "${part:0:1}" | tr '[:lower:]' '[:upper:]')${part:1}"
  done
  echo "$result"
}

# Convert kebab-case to lowercase (no separators):  my-cool-app → mycoolapp
to_lower() {
  echo "$1" | tr -d '-'
}

# ─── Validate inputs ────────────────────────────────────────────────────────

BLUEPRINT_REPO="lombold/project-blueprint"

NEW_NAME="${1:-}"
GITHUB_OWNER="${2:-}"
TARGET_DIR=""
APP_ID=""

if [[ "$NEW_NAME" == "-h" || "$NEW_NAME" == "--help" ]]; then
  usage
  exit 0
fi

if [[ -z "$NEW_NAME" || -z "$GITHUB_OWNER" ]]; then
  usage
  exit 1
fi

shift 2
while [[ $# -gt 0 ]]; do
  case "$1" in
    --app-id)
      [[ $# -ge 2 ]] || die "--app-id requires a value"
      APP_ID="$2"
      shift 2
      ;;
    --target-dir)
      [[ $# -ge 2 ]] || die "--target-dir requires a value"
      TARGET_DIR="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      die "Unknown option: $1"
      ;;
  esac
done

# Validate kebab-case format
if [[ ! "$NEW_NAME" =~ ^[a-z][a-z0-9]*(-[a-z0-9]+)*$ ]]; then
  die "Project name must be kebab-case (e.g., my-cool-app). Got: '$NEW_NAME'"
fi

if [[ -z "$APP_ID" ]]; then
  die "--app-id is required (e.g., ch.lombold.mycoolapp)"
fi

if [[ ! "$APP_ID" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  die "App ID must be a lowercase reverse-DNS identifier (e.g., ch.lombold.mycoolapp). Got: '$APP_ID'"
fi

# ─── Derive all case variants ───────────────────────────────────────────────

OLD_KEBAB="project-name"
OLD_CAMEL="projectName"
OLD_PASCAL="ProjectName"
OLD_LOWER="projectname"
OLD_APP_ID="com.example.projectname"

NEW_KEBAB="$NEW_NAME"
NEW_CAMEL=$(to_camel "$NEW_NAME")
NEW_PASCAL=$(to_pascal "$NEW_NAME")
NEW_LOWER=$(to_lower "$NEW_NAME")
OLD_APP_PATH=$(tr '.' '/' <<< "$OLD_APP_ID")
NEW_APP_PATH=$(tr '.' '/' <<< "$APP_ID")

TARGET_DIR="${TARGET_DIR:-$(pwd)/$NEW_KEBAB}"

echo ""
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║                   Project Initialization                     ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""
echo "  Blueprint:     ${BLUEPRINT_REPO}"
echo "  New repo:      ${GITHUB_OWNER}/${NEW_KEBAB}"
echo "  Target dir:    ${TARGET_DIR}"
echo "  App ID:        ${OLD_APP_ID}  →  ${APP_ID}"
echo ""
echo "  Case variants:"
echo "    kebab:       ${OLD_KEBAB}  →  ${NEW_KEBAB}"
echo "    camelCase:   ${OLD_CAMEL}  →  ${NEW_CAMEL}"
echo "    PascalCase:  ${OLD_PASCAL}  →  ${NEW_PASCAL}"
echo "    lowercase:   ${OLD_LOWER}  →  ${NEW_LOWER}"
echo ""

read -rp "Proceed? [y/N] " confirm
[[ "$confirm" =~ ^[Yy]$ ]] || { echo "Aborted."; exit 0; }

# ─── Step 1: Clone the blueprint ────────────────────────────────────────────

info "Cloning blueprint (full history)..."

if [[ -d "$TARGET_DIR" ]]; then
  die "Target directory already exists: $TARGET_DIR"
fi

git clone "git@github.com:${BLUEPRINT_REPO}.git" "$TARGET_DIR"
cd "$TARGET_DIR"

success "Cloned to $TARGET_DIR"

# ─── Step 2: Replace file contents ──────────────────────────────────────────
#
# Order matters: replace more specific patterns first to avoid partial matches.
# e.g., "projectName" before "projectname", since the latter is a substring.
#
# We skip .git/, binary files, and the init script itself.
#

info "Replacing references in file contents..."

# Detect sed flavor (macOS vs GNU)
if sed --version >/dev/null 2>&1; then
  SED_INPLACE=(sed -i)
else
  SED_INPLACE=(sed -i '')
fi

# Find all text files, excluding .git and the init script
find_text_files() {
  find . \
    -not -path './.git/*' \
    -not -path './backend/target/*' \
    -not -path './frontend/node_modules/*' \
    -not -path './frontend/dist/*' \
    -not -path './frontend/.angular/*' \
    -not -path './frontend/coverage/*' \
    -not -path './app/node_modules/*' \
    -not -path './app/www/*' \
    -not -path './app/.angular/*' \
    -not -path './app/coverage/*' \
    -not -path './app/android/.gradle/*' \
    -not -path './app/android/app/build/*' \
    -not -path './app/android/app/src/main/assets/public/*' \
    -not -path './app/ios/App/App/public/*' \
    -not -name 'init-project.sh' \
    -type f \
    -exec grep -lI '' {} +
}

# Replace in all text files. Runs replacements in order.
replace_in_files() {
  local old="$1"
  local new="$2"
  local count=0

  while IFS= read -r file; do
    if grep -qF "$old" "$file" 2>/dev/null; then
      "${SED_INPLACE[@]}" "s|${old}|${new}|g" "$file"
      ((count++)) || true
    fi
  done < <(find_text_files)

  if [[ $count -gt 0 ]]; then
    echo "    ${old} → ${new}  (${count} files)"
  fi
}

# Replace template placeholders in README.md FIRST (before generic replacements
# would transform the inner text and leave the curly braces behind)
if [[ -f "README.md" ]]; then
  "${SED_INPLACE[@]}" "s|{{projectname}}|${NEW_LOWER}|g" README.md
  "${SED_INPLACE[@]}" "s|{{ProjectName}}|${NEW_PASCAL}|g" README.md
  "${SED_INPLACE[@]}" "s|{{project-name}}|${NEW_KEBAB}|g" README.md
  echo "    {{...}} placeholders in README.md"
fi

# Replace content patterns (order: most specific first)
replace_in_files "$OLD_APP_ID" "$APP_ID"
replace_in_files "$OLD_CAMEL"  "$NEW_CAMEL"
replace_in_files "$OLD_PASCAL" "$NEW_PASCAL"
replace_in_files "$OLD_LOWER"  "$NEW_LOWER"
replace_in_files "$OLD_KEBAB"  "$NEW_KEBAB"

success "File contents replaced"

# ─── Step 3: Rename files and directories ────────────────────────────────────
#
# Rename directories first (deepest first to avoid path breakage),
# then rename files.
#

info "Renaming Android package directories..."

for source_set in main test androidTest; do
  old_package_dir="app/android/app/src/${source_set}/java/${OLD_APP_PATH}"
  new_package_dir="app/android/app/src/${source_set}/java/${NEW_APP_PATH}"

  if [[ -d "$old_package_dir" ]]; then
    mkdir -p "$(dirname "$new_package_dir")"
    mv "$old_package_dir" "$new_package_dir"
    echo "    $old_package_dir → $new_package_dir"
  fi
done

success "Android package directories renamed"

info "Renaming directories..."

# Rename directories containing old names (deepest first)
# We loop until no more renames are needed (handles nested renames)
renamed=true
while $renamed; do
  renamed=false
  while IFS= read -r dir; do
    dirname=$(basename "$dir")
    parent=$(dirname "$dir")

    new_dirname="$dirname"
    new_dirname="${new_dirname//$OLD_LOWER/$NEW_LOWER}"
    new_dirname="${new_dirname//$OLD_KEBAB/$NEW_KEBAB}"
    new_dirname="${new_dirname//$OLD_PASCAL/$NEW_PASCAL}"
    new_dirname="${new_dirname//$OLD_CAMEL/$NEW_CAMEL}"

    if [[ "$dirname" != "$new_dirname" ]]; then
      mv "$dir" "$parent/$new_dirname"
      echo "    $dir → $parent/$new_dirname"
      renamed=true
    fi
  done < <(find . -not -path './.git/*' -not -path './backend/target/*' -not -path './frontend/node_modules/*' -not -path './frontend/dist/*' -not -path './frontend/.angular/*' -not -path './frontend/coverage/*' -not -path './app/node_modules/*' -not -path './app/www/*' -not -path './app/.angular/*' -not -path './app/coverage/*' -not -path './app/android/.gradle/*' -not -path './app/android/app/build/*' -not -path './app/android/app/src/main/assets/public/*' -not -path './app/ios/App/App/public/*' -type d -depth | grep -E "(${OLD_LOWER}|${OLD_KEBAB}|${OLD_PASCAL}|${OLD_CAMEL})" || true)
done

success "Directories renamed"

info "Renaming files..."

find . -not -path './.git/*' -not -path './backend/target/*' -not -path './frontend/node_modules/*' -not -path './frontend/dist/*' -not -path './frontend/.angular/*' -not -path './frontend/coverage/*' -not -path './app/node_modules/*' -not -path './app/www/*' -not -path './app/.angular/*' -not -path './app/coverage/*' -not -path './app/android/.gradle/*' -not -path './app/android/app/build/*' -not -path './app/android/app/src/main/assets/public/*' -not -path './app/ios/App/App/public/*' -type f | while IFS= read -r file; do
  filename=$(basename "$file")
  parent=$(dirname "$file")

  new_filename="$filename"
  new_filename="${new_filename//$OLD_LOWER/$NEW_LOWER}"
  new_filename="${new_filename//$OLD_KEBAB/$NEW_KEBAB}"
  new_filename="${new_filename//$OLD_PASCAL/$NEW_PASCAL}"
  new_filename="${new_filename//$OLD_CAMEL/$NEW_CAMEL}"

  if [[ "$filename" != "$new_filename" ]]; then
    mv "$file" "$parent/$new_filename"
    echo "    $file → $parent/$new_filename"
  fi
done

success "Files renamed"

# ─── Step 4: Regenerate API clients ─────────────────────────────────────────

info "Regenerating OpenAPI clients for the renamed contract..."

(
  cd backend
  mvn spotless:apply generate-sources
)

success "OpenAPI clients regenerated"

# ─── Step 5: Commit the rename ───────────────────────────────────────────────

info "Committing rename changes..."

git add -A
if git diff --cached --quiet; then
  success "No changes to commit (blueprint was already clean)"
else
  git commit -m "chore: initialize project as ${NEW_KEBAB}

Renamed from blueprint template (${BLUEPRINT_REPO}).
All placeholder references updated to ${NEW_KEBAB}."
  success "Rename committed"
fi

# ─── Step 6: Update git remote ──────────────────────────────────────────────

info "Updating git remote..."

git remote set-url origin "git@github.com:${GITHUB_OWNER}/${NEW_KEBAB}.git"

success "Remote set to ${GITHUB_OWNER}/${NEW_KEBAB}"

# ─── Step 7: Create GitHub repo and push ─────────────────────────────────────

info "Creating GitHub repository..."

if gh repo view "${GITHUB_OWNER}/${NEW_KEBAB}" >/dev/null 2>&1; then
  warn "Repository ${GITHUB_OWNER}/${NEW_KEBAB} already exists, skipping creation"
  git push -u origin main
else
  gh repo create "${GITHUB_OWNER}/${NEW_KEBAB}" --private --source=. --push
fi

success "Pushed to ${GITHUB_OWNER}/${NEW_KEBAB}"

# ─── Done ────────────────────────────────────────────────────────────────────

echo ""
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║                         All done!                            ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo ""
echo "  Repository:  https://github.com/${GITHUB_OWNER}/${NEW_KEBAB}"
echo "  Local path:  ${TARGET_DIR}"
echo ""
echo "  Next steps:"
echo "    cd ${TARGET_DIR}"
echo "    # Backend:  cd backend && mvn spring-boot:run"
echo "    # Frontend: cd frontend && bun install && bun run start"
echo "    # App:      cd app && bun install && bun run start"
echo "    # Native:   cd app && bun run sync"
echo ""
echo "  Remember to set up the blueprint sync variable (optional):"
echo "    gh variable set BLUEPRINT_REPO --body '${BLUEPRINT_REPO}' --repo ${GITHUB_OWNER}/${NEW_KEBAB}"
echo ""
