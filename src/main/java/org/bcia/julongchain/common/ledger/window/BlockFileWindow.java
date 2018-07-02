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
package org.bcia.julongchain.common.ledger.window;

import org.bcia.julongchain.common.ledger.blkstorage.fsblkstorage.BlockFileReader;
import org.bcia.julongchain.core.ledger.ledgerconfig.LedgerConfig;
import org.bcia.julongchain.core.ledger.util.Util;
import org.bcia.julongchain.protos.common.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于展示BlockFile文件
 *
 * @author sunzongyu
 * @date 2018/06/28
 * @company Dingxuan
 */
public class BlockFileWindow {
	public static void main(String[] args) throws Exception {
		//账本ID
		String ledgerID = "myGroup";
		//区块文件号
		long blockFileNum = 0;

		List<Common.Block> blocks = new BlockFileWindow().getBlocks(ledgerID, blockFileNum);
		blocks.forEach(System.out::println);
	}

	private List<Common.Block> getBlocks(String ledgerID, long blockFileNum) throws Exception {
		String filePath = getBlockFile(ledgerID, blockFileNum);
		System.out.println("区块文件路径：" + filePath);
		return getBlocks(filePath);
	}

	private List<Common.Block> getBlocks(String filePath) throws Exception {
		List<Common.Block> list = new ArrayList<>();
		File file = new File(filePath);
		int len = 0;
		int i = 0;
		while (len < file.length()) {
			BlockFileReader reader = new BlockFileReader(filePath);
			byte[] blockLen = reader.read(len, 8);
			len += 8;
			long l = Util.bytesToLong(blockLen, 0, 8);
			System.out.println("block" + i++ + " length: " + l);
			byte[] blockBytes = reader.read(len, l);
			len += l;
			Common.Block block = Common.Block.parseFrom(blockBytes);
			list.add(block);
		}
		return list;
	}

	private String getBlockFile(String ledgerID, long blockNum) {
		return getBlockDir(ledgerID) + "/blockfile_" + String.format("%06d", blockNum);
	}

	private String getBlockDir(String ledgerID) {
		return LedgerConfig.getChainsPath() + "/chains/" + ledgerID;
	}
}
