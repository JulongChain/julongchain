/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.smartcontract.client;

import org.bcia.julongchain.common.exception.SmartContractException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.util.CommConstant;
import org.bcia.julongchain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.julongchain.core.smartcontract.shim.SmartContractBase;
import org.bcia.julongchain.core.ssc.cssc.CSSC;
import org.bcia.julongchain.core.ssc.essc.ESSC;
import org.bcia.julongchain.core.ssc.lssc.LSSC;
import org.bcia.julongchain.core.ssc.qssc.QSSC;
import org.bcia.julongchain.core.ssc.vssc.VSSC;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 智能客户端
 *
 * @author wanliangbing
 * @date 2018/4/17
 * @company Dingxuan
 */
public class SmartContractSupportClient extends SmartContractBase {

  private static JavaChainLog logger = JavaChainLogFactory.getLog(SmartContractSupportClient.class);
  private static Map<String, String> map = new HashMap<String, String>();

  static {
    map.put(CommConstant.ESSC, ESSC.class.getName());
    map.put(CommConstant.LSSC, LSSC.class.getName());
    map.put(CommConstant.CSSC, CSSC.class.getName());
    map.put(CommConstant.QSSC, QSSC.class.getName());
    map.put(CommConstant.VSSC, VSSC.class.getName());
    // map.put("mycc", SmartContractSupportClient.class.getName());
  }

  @Override
  public SmartContractResponse init(ISmartContractStub stub) {
    logger.info("SmartContractSupportClient");
    return newSuccessResponse();
  }

  @Override
  public SmartContractResponse invoke(ISmartContractStub stub) {
    logger.info("SmartContractSupportClient invoke " + stub.getTxId());
    /** stub.getState("key1"); stub.getState("key2"); stub.getState("key3"); */
    stub.putState("putKey1", "putValue1".getBytes());
    /**
     * stub.putState("putKey2", "putValue2".getBytes()); stub.putState("putKey3",
     * "putValue3".getBytes());
     */
    return newSuccessResponse();
  }

  @Override
  public String getSmartContractStrDescription() {
    return null;
  }

  public static void launch(String smartContractId) throws SmartContractException {
    try {
      logger.info(String.format("launch smartContract[%s]", smartContractId));
      String[] args = new String[] {"", ""};
      args[0] = "-i" + smartContractId;
      String smartContractClassName = map.get(smartContractId);
      Class<?> clz = Class.forName(smartContractClassName);
      Constructor<?> constructor = clz.getDeclaredConstructor();
      SmartContractBase smartContract = (SmartContractBase) constructor.newInstance();
      smartContract.start(args);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new SmartContractException(e);
    }
  }

  public static Boolean checkSystemSmartContract(String smartContractId) {
    return map.get(smartContractId) != null;
  }

  public static void main(String[] args) throws Exception {
    // launch(CommConstant.ESSC);
    launch(CommConstant.QSSC);
    while (true) {}
  }
}
