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
package org.bcia.javachain.core.ledger.util;

import com.google.protobuf.ByteString;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.protos.node.TransactionPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class TxValidationFlags {
    List<ByteString> flags;

    public TxValidationFlags(List<ByteString> list){
        this.flags = list;
    }

    public void setFlag(int txIndex, TransactionPackage.TxValidationCode flag) throws LedgerException {
        try {
            byte[] b = new byte[1];
            b[0] = (byte) flag.getNumber();
            flags.add(txIndex, ByteString.copyFrom(b));
        } catch (Exception e) {
            throw new LedgerException(e);
        }
    }

    public TransactionPackage.TxValidationCode flag(int txIndex) throws LedgerException{
        try {
            return TransactionPackage.TxValidationCode.forNumber(flags.get(txIndex).byteAt(0));
        } catch (Exception e) {
            throw new LedgerException("Got error when flag txIndex in flag()");
        }
    }

    public boolean isValid(int txIndex){
        return isSetTo(txIndex, TransactionPackage.TxValidationCode.VALID);
    }

    public boolean isInValid(int txIndex){
        return !isValid(txIndex);
    }

    public boolean isSetTo(int txIndex, TransactionPackage.TxValidationCode flag){
        return flag == null || flag.getNumber() == flags.get(txIndex).toByteArray()[0];
    }
}
