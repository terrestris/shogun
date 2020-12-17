#!/bin/bash

URL="http://localhost:8080/shogun-boot/"
TIMEOUT=120
seconds=0

echo 'Waiting up to' $TIMEOUT 'seconds for HTTP 200 from' $URL 
until [ "$seconds" -gt "$TIMEOUT" ] || $(curl --output /dev/null --silent --max-time $TIMEOUT --head --fail $URL); do
  sleep 5
  seconds=$((seconds+5))
done

if [ "$seconds" -lt "$TIMEOUT" ]; then
  echo 'OK'
else
  echo "ERROR: Timed out wating for HTTP 200 from" $URL >&2
  exit 1
fi
