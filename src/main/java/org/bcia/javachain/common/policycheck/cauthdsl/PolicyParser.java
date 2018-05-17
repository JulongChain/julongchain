/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.javachain.common.policycheck.cauthdsl;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.policies.SignaturePolicy;
import org.bcia.javachain.common.policycheck.bean.Context;
import org.bcia.javachain.common.policycheck.bean.SignaturePolicyEnvelope;
import org.bcia.javachain.msp.mgmt.Msp;
import org.bcia.javachain.protos.common.MspPrincipal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;
/**
 * 类描述
 * 签名策略脚本解析
 * @author yuanjun
 * @date 26/04/18
 * @company Aisino
 */
public class PolicyParser {
    private static JavaChainLog log = JavaChainLogFactory.getLog(PolicyParser.class);
    String regex="";
    String regexErr="";
    public Object and(Object ...args){

        String toret = "outof("+args.length;
        for(int i=0;i<args.length;i++){
            toret+=",";
            if(args[i] instanceof String){
                if(Pattern.matches(regex,args[i].toString())){
                    toret += "'" + args[i].toString() + "'";
                }else{
                    toret +=args[i].toString();
                }
            }else{
                log.info("Unexpected type "+args[i].getClass());
                return null;
            }
        }
        return toret+")";
    }

    public Object or(Object ...args){
        String toret = "outof(1";
        for(int i=0;i<args.length;i++){
            toret+=",";
            if(args[i] instanceof String){
                if(Pattern.matches(regex,args[i].toString())){
                    toret += "'" + args[i].toString() + "'";
                }else{
                    toret += args[i].toString();
                }
            }else{
                log.info("Unexpected type "+args[i].getClass());
                return null;
            }
        }
        return toret+")";
    }
    public Object firstPass(Object ...args){
        String toret = "outof(ID";
        for(int i=0;i<args.length;i++){
            toret+=",";
            if(args[i] instanceof String){
                if(Pattern.matches(regex,args[i].toString())){
                    toret += "'" + args[i].toString() + "'";
                }else{
                    toret += args[i].toString();
                }
            }
            if(args[i] instanceof Float){
                toret += (int)args[i];
            }
            else{
                log.info("Unexpected type "+args[i].getClass());
                return null;
            }
        }
        return toret+")";
    }
    public Object secondPass(Object ...args){
        if(args.length<3){
            log.info("At least 3 arguments expected, got"+args.length);
        }
        Context context = new Context();
        if(args[0] instanceof Context){
            context = (Context)args[0];
        }else{
            log.info("Unrecognized type, expected the context, got"+args[0].getClass());
            return null;
        }
        int t;
        if(args[1] instanceof Float){
            t = (int)args[1];
        }
        else{
            log.info("Unrecognized type, expected the context, got"+args[1].getClass());
            return null;
        }
        int n = args.length-1;
        if(t>n){
            log.info("Invalid"+t+"-out-of-"+n+" predicate");
            return null;
        }
        List<SignaturePolicy > policies = new ArrayList<SignaturePolicy>();
        for (int i=2;i<args.length;i++){
            if(args[i] instanceof String){
                /**
                 * subm := regex.FindAllStringSubmatch(t, -1)
                 if subm == nil || len(subm) != 1 || len(subm[0]) != 4 {
                 return nil, fmt.Errorf("Error parsing principal %s", t)
                 }
                 */
            }

        }
        return "";
    }
    public SignaturePolicyEnvelope fromString(String policy){
        return null;
    }

    public Context newContext(){
        //return &context{IDNum: 0, principals: make([]*msp.MSPPrincipal, 0)}
        Context context = new Context();
        context.setIDNum(0);
        MspPrincipal[] principals = new MspPrincipal[0];
        context.setPrincipals(principals);
        return context;

    }
}
