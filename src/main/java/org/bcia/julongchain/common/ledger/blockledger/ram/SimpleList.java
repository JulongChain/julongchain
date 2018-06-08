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
package org.bcia.julongchain.common.ledger.blockledger.ram;

import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.protos.common.Common;

/**
 * Block链表
 *
 * @author sunzongyu
 * @date 2018/04/28
 * @company Dingxuan
 */
public class SimpleList {
    private static final JavaChainLog logger = JavaChainLogFactory.getLog(SimpleList.class);

    private SimpleList next;
    private Common.Block block;

    public SimpleList(){}

    public SimpleList(SimpleList next, Common.Block block){
        this.next = next;
        this.block = block;
    }

    public SimpleList getNext() {
        return next;
    }

    public void setNext(SimpleList next) {
        this.next = next;
    }

    public Common.Block getBlock() {
        return block;
    }

    public void setBlock(Common.Block block) {
        this.block = block;
    }

//    public Object getLock() {
//        return lock;
//    }
}
