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
package org.bcia.julongchain.core.ledger.util;

import com.google.protobuf.ByteString;
import org.bcia.julongchain.protos.node.TransactionPackage;

/**
 * 交易验证器标志
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class TxValidationFlags {
    private int[] flags;

    public TxValidationFlags(int len){
        this.flags = new int[len];
    }

    public static TxValidationFlags fromByteString(ByteString bs) {
        byte[] bytes = bs.toByteArray();
        TxValidationFlags flags = new TxValidationFlags(bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            flags.setFlag(i, TransactionPackage.TxValidationCode.forNumber((int) bytes[i]));
        }
        return flags;
    }

    public void setFlag(int txIndex, TransactionPackage.TxValidationCode flag) {
        flags[txIndex] = flag.getNumber();
    }

    public TransactionPackage.TxValidationCode flag(int txIndex) {
        return TransactionPackage.TxValidationCode.forNumber(flags[txIndex]);
    }

    public boolean isValid(int txIndex){
        return isSetTo(txIndex, TransactionPackage.TxValidationCode.VALID);
    }

    public boolean isInValid(int txIndex){
        return !isValid(txIndex);
    }

    public boolean isSetTo(int txIndex, TransactionPackage.TxValidationCode flag){
        return flags.length <= txIndex || flags[txIndex] == flag.getNumber();
    }

    public int length(){
        return flags.length;
    }

    public ByteString toByteString(){
        byte[] bytes = new byte[flags.length];
        for (int i = 0; i < flags.length; i++) {
            bytes[i] = (byte) flags[i];
        }
        return ByteString.copyFrom(bytes);
    }
}
