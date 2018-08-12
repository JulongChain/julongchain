/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.ledger;

/**
 * 丢失的私有数据
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class MissingPrivateData {

    private String txId;
    private Integer seqInBlock;
    private String namespace;
    private String collection;

    public MissingPrivateData(String txId, Integer seqInBlock, String namespace, String collection) {
        this.txId = txId;
        this.seqInBlock = seqInBlock;
        this.namespace = namespace;
        this.collection = collection;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public Integer getSeqInBlock() {
        return seqInBlock;
    }

    public void setSeqInBlock(Integer seqInBlock) {
        this.seqInBlock = seqInBlock;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
