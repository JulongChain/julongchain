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
package org.bcia.julongchain.core.aclmgmt.resources;

/**
 * resources used for ACL checks. Note that some of the checks
 * such as LSCC_INSTALL are "peer wide" (current access checks in peer are
 * based on local MSP). These are not currently covered by resource or default
 * ACLProviders
 *
 * @author sunianle
 * @date 4/3/18
 * @company Dingxuan
 */
public class Resources {
    public static  final String PROPOSE = "PROPOSE";

    //LSSC resources
    public static  final String LSSC_INSTALL="LSSC.INSTALL";
    public static  final String LSSC_DEPLOY="LSSC.DEPLOY";
    public static  final String LSSC_UPGRADE="LSSC.UPGRADE";
    public static  final String LSSC_GETSCINFO="LSSC.GETSCINFO";
    public static  final String LSSC_GETDEPSPEC="LSSC.GETDEPSPEC";
    public static  final String LSSC_GETSCDATA="LSSC.GETSCDATA";
    public static  final String LSSC_GETCHAINCODES="LSSC.GETCHAINCODES";
    public static  final String LSSC_GETINSTALLEDCHAINCODES="LSSC.GETINSTALLEDCHAINCODES";

    //QSSC resources
    public static  final String QSSC_GetChainInfo="QSSC.GetChainInfo";
    public static  final String QSSC_GetBlockByNumber="QSSC.GetBlockByNumber";
    public static  final String QSSC_GetBlockByHash="QSSC.GetBlockByHash";
    public static  final String QSSC_GetTransactionByID="QSSC.GetTransactionByID";
    public static  final String QSSC_GetBlockByTxID="QSSC.GetBlockByTxID";

    //CSSC resources
    public static  final String CSSC_JoinChain= "CSSC.JoinChain";
    public static  final String CSSC_GetConfigBlock= "CSSC.GetConfigBlock";
    public static  final String CSSC_GetChannels="CSSC.GetChannels";
    public static  final String CSSC_GetConfigTree="CSSC.GetConfigTree";
    public static  final String CSSC_SimulateConfigTreeUpdate="CSSC.SimulateConfigTreeUpdate";

    //Chaincode-to-Chaincode call
    public static  final String SC2SC="SC2SC";

    //Events
    public static  final String BLOCKEVENT="BLOCKEVENT";
    public static  final String FILTEREDBLOCKEVENT="FILTEREDBLOCKEVENT";
}
