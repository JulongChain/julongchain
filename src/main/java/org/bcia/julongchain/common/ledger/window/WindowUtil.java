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

/**
 * 工具类
 *
 * @author sunzongyu
 * @date 2018/06/28
 * @company Dingxuan
 */
public class WindowUtil {
	public static void soutByte(byte[] bytes, int length) {
		int i = 0;
		for (byte b : bytes) {
			System.out.print(b + " ");
			if (i++ % length == length - 1) {
				System.out.println();
			}
		}
		System.out.println();
	}
}
