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
package org.bcia.julongchain.core.ledger.customtx;

import org.bcia.julongchain.protos.common.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * 包装交易处理器
 * 用于处理未背书交易
 * 于账本初始化时创建并初始化
 *
 * @author sunzongyu
 * @date 2018/04/09
 * @company Dingxuan
 */
public class CustomTx {
    private static Map<Common.HeaderType, IProcessor> processors = new HashMap<>();

    public static Map<Common.HeaderType, IProcessor> getProcessors() {
        return processors;
    }

    public static void initialize(Map<Common.HeaderType, IProcessor> processors){
		if (processors != null) {
			CustomTx.processors = processors;
		}
    }

    public static IProcessor getProcessor(Common.HeaderType txType){
        return processors.get(txType);
    }
}
