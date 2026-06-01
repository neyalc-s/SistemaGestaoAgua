#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_DIR="/tmp/sistemagestaoagua-vscode-run"
CP_SRC="$ROOT_DIR/src"
CP_LIBS="$ROOT_DIR/lib/ojdbc11.jar:$ROOT_DIR/lib/flatlaf-3.6.jar:$ROOT_DIR/lib/ikonli-core-12.3.1.jar:$ROOT_DIR/lib/ikonli-swing-12.3.1.jar:$ROOT_DIR/lib/ikonli-fontawesome5-pack-12.3.1.jar:$ROOT_DIR/lib/ikonli-materialdesign2-pack-12.3.1.jar"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Usa -print0/xargs -0 para suportar caminhos com espacos.
find "$ROOT_DIR/src" -name '*.java' -print0 \
  | xargs -0 javac -d "$OUT_DIR" -cp "$CP_SRC:$CP_LIBS"

java -cp "$OUT_DIR:$CP_LIBS" App.Executable
