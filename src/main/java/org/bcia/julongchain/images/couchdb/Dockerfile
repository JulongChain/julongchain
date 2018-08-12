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

# Add CouchDB user account
RUN groupadd -r couchdb && useradd -d /opt/couchdb -g couchdb couchdb

RUN apt-get update -y && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    erlang-nox \
    erlang-reltool \
    haproxy \
    libicu5. \
    libmozjs185-1.0 \
    openssl \
    cmake \
    apt-transport-https \
    gcc \
    g++ \
    erlang-dev \
    libcurl4-openssl-dev \
    libicu-dev \
    libmozjs185-dev \
    make \
  && rm -rf /var/lib/apt/lists/*

# Grab su-exec and tini
RUN set -x \
    && git clone https://github.com/ncopa/su-exec /tmp/su-exec/ \
    && cd /tmp/su-exec \
    && make all \
    && cp su-exec /usr/bin/ \
    && git clone https://github.com/krallin/tini/ /tmp/tini/ \
    && cd /tmp/tini/ \
    && git checkout v0.14.0 \
    && cmake . && make \
    && cp tini tini-static /usr/local/bin/ \
    # Clean up su-exec and tini
    && rm -rf /tmp/tini \
    && rm -rf /tmp/su-exec

ARG COUCHDB_VERSION=2.0.0

# Download dev dependencies
RUN set -x \
 && npm install -g grunt-cli \
 # Acquire CouchDB source code
 && cd /usr/src && mkdir couchdb \
 && curl -fSL https://dist.apache.org/repos/dist/release/couchdb/source/$COUCHDB_VERSION/apache-couchdb-$COUCHDB_VERSION.tar.gz -o couchdb.tar.gz \
 && tar -xzf couchdb.tar.gz -C couchdb --strip-components=1 \
 && cd couchdb \
 # Build the release and install into /opt
 && ./configure --disable-docs \
 && make release \
 && mv /usr/src/couchdb/rel/couchdb /opt/ \
 # Cleanup build detritus
 && rm -rf /var/lib/apt/lists/* /usr/lib/node_modules /usr/src/couchdb*

# Add configuration
COPY payload/local.ini /opt/couchdb/etc/local.d/
COPY payload/vm.args /opt/couchdb/etc/
COPY payload/docker-entrypoint.sh /

# Setup directories and permissions
RUN chmod +x /docker-entrypoint.sh \
 && mkdir /opt/couchdb/data /opt/couchdb/etc/default.d \
 && chown -R couchdb:couchdb /opt/couchdb/

WORKDIR /opt/couchdb
EXPOSE 5984 4369 9100

VOLUME ["/opt/couchdb/data"]

ENTRYPOINT ["tini", "--", "/docker-entrypoint.sh"]
CMD ["/opt/couchdb/bin/couchdb"]
