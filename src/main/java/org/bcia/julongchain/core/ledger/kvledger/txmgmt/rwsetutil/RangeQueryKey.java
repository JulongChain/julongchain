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
package org.bcia.julongchain.core.ledger.kvledger.txmgmt.rwsetutil;

/**
 * 封装范围查询键
 *
 * @author sunzongyu
 * @date 2018/04/16
 * @company Dingxuan
 */
public class RangeQueryKey {
    private String startKey;
    private String endKey;
    private boolean itrExhausted;

    public RangeQueryKey(){}

    public RangeQueryKey(String startKey, String endKey, boolean itrExhausted){
        this.startKey = startKey;
        this.endKey = endKey;
        this.itrExhausted = itrExhausted;
    }

    public String getEndKey() {
        return endKey;
    }

    public void setEndKey(String endKey) {
        this.endKey = endKey;
    }

    public boolean isItrExhausted() {
        return itrExhausted;
    }

    public void setItrExhausted(boolean itrExhausted) {
        this.itrExhausted = itrExhausted;
    }

    public String getStartKey() {

        return startKey;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }

    @Override
    public boolean equals(Object obj) {
        RangeQueryKey key;
        try {
            key = (RangeQueryKey) obj;
        } catch (Exception e) {
            return false;
        }
        if (startKey == null){
            if(key.getStartKey() != null){
                return false;
            }
        } else {
            if(!startKey.equals(key.getStartKey())){
                return false;
            }
        }
        if (endKey == null) {
            if(key.getEndKey() != null){
                return false;
            }
        } else {
            if (!endKey.equals(key.getEndKey())) {
                return false;
            }
        }
        if (itrExhausted != key.isItrExhausted()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (startKey + endKey + (itrExhausted ? "true" : "false")).hashCode();
    }
}
