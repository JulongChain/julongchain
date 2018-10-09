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
FROM docker.julongchain.org:5000/julongchain-baseimage:0.8.3-alpha
MAINTAINER Julongchain "qkl@dxct.org"
COPY pom.xml /root/julongchain
COPY src/ /root/julongchain/src
WORKDIR /root/julongchain
ENV CORE_NODE_ADDRESS #core_node_address#
ENV LANG C.UTF-8
RUN mvn package
RUN rm -rf /root/julongchain/pom.xml
RUN rm -rf /root/julongchain/src
CMD java -jar /root/julongchain/target/julongchain-smartcontract-java-jar-with-dependencies.jar -i #smart_contract_id -a #core_node_address_and_port