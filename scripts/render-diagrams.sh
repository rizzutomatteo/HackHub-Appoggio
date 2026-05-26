#!/usr/bin/env bash
# Rende tutti i diagrammi PlantUML (.puml) di iterazione1/ in PNG + SVG usando il
# container ufficiale plantuml/plantuml (Java 17 + ELK + Graphviz gia' dentro).
#
# Requisito: SOLO Docker. Non serve installare Java/Graphviz/ELK in locale.
# (Necessario perche' il layout ELK e' compilato per Java 17: i renderer locali
#  con Java piu' vecchio falliscono con UnsupportedClassVersionError.)
#
# Uso:  ./scripts/render-diagrams.sh
# Le immagini sono generate accanto ai rispettivi .puml.
set -euo pipefail

cd "$(dirname "$0")/.."

IMG="plantuml/plantuml"

mapfile -t PUML < <(find iterazione1 -name '*.puml' | sort)
if [ "${#PUML[@]}" -eq 0 ]; then
  echo "Nessun file .puml trovato in iterazione1/."
  exit 0
fi

echo "Diagrammi da renderizzare: ${#PUML[@]}"
for fmt in png svg; do
  docker run --rm --user "$(id -u):$(id -g)" \
    -v "$PWD":/data -w /data "$IMG" -t"$fmt" "${PUML[@]}"
done

echo "Fatto: PNG e SVG generati accanto ai .puml."
