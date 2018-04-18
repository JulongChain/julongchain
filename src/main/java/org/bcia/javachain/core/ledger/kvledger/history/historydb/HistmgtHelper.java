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
package org.bcia.javachain.core.ledger.kvledger.history.historydb;

import org.apache.commons.lang3.ArrayUtils;
import org.bcia.javachain.core.ledger.util.Util;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/08
 * @company Dingxuan
 */
public class HistmgtHelper {
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};

    public static byte[] constructCompositeHistoryKey(String ns, String key, long blocNum, long tranNum){
        byte[] compositeKey = ns.getBytes();
        ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        ArrayUtils.addAll(compositeKey, key.getBytes());
        ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        ArrayUtils.addAll(compositeKey, Util.longToBytes(blocNum, 8));
        ArrayUtils.addAll(compositeKey, COMPOSITE_KEY_SEP);
        ArrayUtils.addAll(compositeKey, Util.longToBytes(tranNum, 8));
        return compositeKey;
    }
}
