#!/bin/bash
#  SHOGun, https://terrestris.github.io/shogun/
#
#  Copyright © 2020-present terrestris GmbH & Co. KG
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0.txt
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
URL="https://localhost/"
TIMEOUT=240
seconds=0

echo 'Waiting up to' $TIMEOUT 'seconds for HTTP 200 from' $URL 
until [ "$seconds" -gt "$TIMEOUT" ] || $(curl --insecure --location --output /dev/null --silent --max-time $TIMEOUT --head --fail $URL); do
  sleep 5
  seconds=$((seconds+5))
done

if [ "$seconds" -lt "$TIMEOUT" ]; then
  echo 'OK'
else
  echo "ERROR: Timed out wating for HTTP 200 from" $URL >&2
  exit 1
fi
