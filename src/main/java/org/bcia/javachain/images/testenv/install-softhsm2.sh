#!/bin/bash
#
#  Copyright Dingxuan. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -e

ARCH=`uname -m`

if [ $ARCH = "s390x" ]; then
  echo "deb http://ftp.us.debian.org/debian sid main" >> /etc/apt/sources.list
fi

# Install softhsm2 package
apt-get update
apt-get -y install softhsm2

# Create tokens directory
mkdir -p /var/lib/softhsm/tokens/

#Initialize token
softhsm2-util --init-token --slot 0 --label "ForFabric" --so-pin 1234 --pin 98765432
