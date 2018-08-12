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

ENV SCALA_VERSION=2.11 \
    KAFKA_VERSION=0.9.0.1 \
    KAFKA_DOWNLOAD_SHA1=FC9ED9B663DD608486A1E56197D318C41813D326

RUN curl -fsSL "http://archive.apache.org/dist/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" -o kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz \
    && echo "${KAFKA_DOWNLOAD_SHA1}  kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" | sha1sum -c - \
    && tar xfz kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt \
    && mv /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION" /opt/kafka \
    && rm kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz

ADD payload/kafka-run-class.sh /opt/kafka/bin/kafka-run-class.sh

ADD payload/docker-entrypoint.sh /docker-entrypoint.sh

EXPOSE 9092
EXPOSE 9093

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["/opt/kafka/bin/kafka-server-start.sh"]
