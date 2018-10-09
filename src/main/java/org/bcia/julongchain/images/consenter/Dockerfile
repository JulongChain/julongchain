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
FROM julongchain/julongchain-baseimage
MAINTAINER Julongchain "qkl@dxct.org"
COPY julongchain.jar /root/julongchain
COPY config/ /root/julongchain/config
COPY msp/ /root/julongchain/msp
COPY lib/ /root/julongchain/lib
COPY libsdtsmjni.so /lib
COPY libsdtsm.so /lib
ENV LANG C.UTF-8
WORKDIR /root/julongchain
CMD java -jar /root/julongchain/julongchain.jar consenter start
