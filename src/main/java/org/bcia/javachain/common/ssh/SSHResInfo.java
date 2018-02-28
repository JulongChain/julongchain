/**
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
package org.bcia.javachain.common.ssh;

import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

/**
 * Gossip测试节点，第二个节点
 *
 * @author wanliangbing
 * @date 2018-02-28
 * @company Dingxuan
 */
public class SSHResInfo {

    private static JavaChainLog log = JavaChainLogFactory.getLog(SSHResInfo.class);

    private int exitStuts;//返回状态码 （在linux中可以通过 echo $? 可知每步执行令执行的状态码）
    private String outRes;//标准正确输出流内容
    private String errRes;//标准错误输出流内容

    public SSHResInfo(int exitStuts, String outRes, String errRes) {
        super();
        this.exitStuts = exitStuts;
        this.outRes = outRes;
        this.errRes = errRes;
    }

    public SSHResInfo() {
        super();
    }

    public int getExitStuts() {
        return exitStuts;
    }

    public void setExitStuts(int exitStuts) {
        this.exitStuts = exitStuts;
    }

    public String getOutRes() {
        return outRes;
    }

    public void setOutRes(String outRes) {
        this.outRes = outRes;
    }

    public String getErrRes() {
        return errRes;
    }

    public void setErrRes(String errRes) {
        this.errRes = errRes;
    }

    /**当exitStuts=0 && errRes="" &&outREs=""返回true
     * @return
     */
    public boolean isEmptySuccess(){
        if(this.getExitStuts()==0 && "".equals(this.getErrRes())&& "".equals(this.getOutRes())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SSHResInfo [exitStuts=" + exitStuts + ", outRes=" + outRes + ", errRes=" + errRes + "]";
    }

    public void clear(){
        exitStuts=0;
        outRes=errRes=null;
    }
}
