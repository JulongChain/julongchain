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
package org.bcia.javachain.common.ledger.util.leveldbhelper;

import java.util.Iterator;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/8
 * @company Dingxuan
 */
public class DB {

    /** Open opens the underlying db
     *
     */
    public void open() {

    }

    /** Close closes the underlying db
     *
     */
    public void close() {

    }

    /** Get returns the value for the given key
     *
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        return null;
    }

    /** Put saves the key/value
     *
     * @param key
     * @param value
     * @param sync
     */
    public void put(byte[] key, byte[] value, Boolean sync) {
        return;
    }

    /** Delete deletes the given key
     *
     * @param key
     * @param sync
     */
    public void delete(byte[] key, Boolean sync) {
        return;
    }

    /** GetIterator returns an iterator over key-value store. The iterator should be released after the use.
     * The resultset contains all the keys that are present in the db between the startKey (inclusive) and the endKey (exclusive).
     * A nil startKey represents the first available key and a nil endKey represent a logical key after the last available key
     */
    public Iterator getIterator(byte[] startKey, byte[] endKey) {
        return null;
    }

    /** WriteBatch writes a batch
     *
     * @param datchbatch
     * @param bool
     */
    public void writeBatch(byte[] datchbatch, Boolean bool) {
        return;
    }
}
