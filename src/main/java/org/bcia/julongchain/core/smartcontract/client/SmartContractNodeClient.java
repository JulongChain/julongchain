/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.smartcontract.client;

import com.google.protobuf.ByteString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.smartcontract.shim.SmartContractBase;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/28
 * @company Dingxuan
 */
public class SmartContractNodeClient extends SmartContractBase {

  private static Log logger = LogFactory.getLog(SmartContractNodeClient.class);

  @Override
  public SmartContractResponse init(ISmartContractStub stub) {
    logger.info("init");
    return newSuccessResponse();
  }

  @Override
  public SmartContractResponse invoke(ISmartContractStub stub) {
    logger.info("invoke");

    String value1 = ByteString.copyFrom(stub.getState("aaa")).toStringUtf8();

    logger.info("-------------------- value1: " + value1);

    String a = "bbb";
    stub.putState("aaa", a.getBytes());
    return newSuccessResponse();
  }

  public static void main(String[] args) {
    String[] bytes = new String[] {"", ""};
    // bytes[0] = "-a 127.0.0.1:7051";
    bytes[0] = "-iMyChaincode1";
    SmartContractNodeClient test = new SmartContractNodeClient();
    test.start(bytes);
  }

  @Override
  public String getSmartContractStrDescription() {
    return "MySmartContract";
  }
}
