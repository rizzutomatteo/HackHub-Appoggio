#!/usr/bin/env bash
# Renderer LOCALE alternativo (fallback senza Docker) per i diagrammi PlantUML.
# Rende tutti i .puml di iterazione*/ in PNG + SVG usando la plantuml.jar locale
# (scripts/lib/plantuml.jar) e il Java di sistema.
#
# ATTENZIONE — differenza di layout rispetto al render canonico:
#   Il render CANONICO del progetto e' ./scripts/render-diagrams.sh (container
#   plantuml/plantuml, Docker). Questo script e' un FALLBACK quando Docker non
#   c'e'. La plantuml.jar standalone usa un "proxy" ELK interno (autonomo, NON
#   delega a Eclipse ELK): il layout puo' risultare piu' affollato / con piu'
#   incroci rispetto al render Docker. Usalo come ANTEPRIMA; per il deliverable
#   pulito ri-renderizza con Docker e rifinisci in Visual Paradigm.
#
# Requisiti: Java 17+ (ELK e' compilato per Java 17) e scripts/lib/plantuml.jar.
# Uso:  ./scripts/render-diagrams-local.sh [file.puml ...]
#       senza argomenti rende tutti i .puml di iterazione*/.
set -euo pipefail

cd "$(dirname "$0")/.."

JAR="scripts/lib/plantuml.jar"
if [ ! -f "$JAR" ]; then
  echo "Manca $JAR. Scaricalo da https://github.com/plantuml/plantuml/releases" >&2
  exit 1
fi
if ! command -v java >/dev/null 2>&1; then
  echo "Java non trovato. Installa OpenJDK 17 (es. sudo apt-get install openjdk-17-jre-headless)." >&2
  exit 1
fi

if [ "$#" -gt 0 ]; then
  PUML=("$@")
else
  mapfile -t PUML < <(find iterazione* -name '*.puml' | sort)
fi
if [ "${#PUML[@]}" -eq 0 ]; then
  echo "Nessun file .puml da renderizzare."
  exit 0
fi

echo "Diagrammi da renderizzare (renderer LOCALE, non canonico): ${#PUML[@]}"
for fmt in png svg; do
  java -DPLANTUML_LIMIT_SIZE=16384 -jar "$JAR" -t"$fmt" "${PUML[@]}"
done

echo "Fatto: PNG e SVG generati accanto ai .puml (anteprima locale)."
