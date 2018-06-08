/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.consenter.common.server;

import org.bcia.julongchain.common.deliver.IHandler;
import org.bcia.julongchain.consenter.common.multigroup.Registrar;

/**
 * @author zhangmingyang
 * @Date: 2018/6/4
 * @company Dingxuan
 */
public class Server {
 private Registrar registrar;
 private IHandler deliverHandle;
 private org.bcia.julongchain.consenter.common.server.IHandler broadcastHandler;

}
