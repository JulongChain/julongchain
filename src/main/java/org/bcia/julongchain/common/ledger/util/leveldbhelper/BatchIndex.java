/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.common.ledger.util.leveldbhelper;

/**
 * 索引更新包
 *
 * @author sunzongyu
 * @date 2018/04/13
 * @company Dingxuan
 */
public class BatchIndex {
    private KeyType keyType;
    private int keyPos;
    private int kenLen;
    private int valuePos;
    private int valueLen;

    public BatchIndex() {
    }

    public BatchIndex(KeyType keyType, int keyPos, int kenLen, int valuePos, int valueLen) {
        this.keyType = keyType;
        this.keyPos = keyPos;
        this.kenLen = kenLen;
        this.valuePos = valuePos;
        this.valueLen = valueLen;
    }

    public byte[] k(byte[] data){
        byte[] result = new byte[kenLen];
        System.arraycopy(data, keyPos, result, 0 , keyPos + kenLen);
        return result;
    }

    public byte[] v(byte data){
        byte[] result = new byte[valueLen];
        System.arraycopy(data, valuePos, result, 0 , valuePos + valueLen);
        return result;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public int getKeyPos() {
        return keyPos;
    }

    public void setKeyPos(int keyPos) {
        this.keyPos = keyPos;
    }

    public int getKenLen() {
        return kenLen;
    }

    public void setKenLen(int kenLen) {
        this.kenLen = kenLen;
    }

    public int getValuePos() {
        return valuePos;
    }

    public void setValuePos(int valuePos) {
        this.valuePos = valuePos;
    }

    public int getValueLen() {
        return valueLen;
    }

    public void setValueLen(int valueLen) {
        this.valueLen = valueLen;
    }
}
