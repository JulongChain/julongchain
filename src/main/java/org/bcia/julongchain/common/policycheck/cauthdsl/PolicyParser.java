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

package org.bcia.julongchain.common.policycheck.cauthdsl;
import com.google.protobuf.ByteString;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policycheck.bean.Context;
import org.bcia.julongchain.common.util.proto.ProtoUtils;
import org.bcia.julongchain.protos.common.MspPrincipal;
import org.bcia.julongchain.protos.common.Policies;

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
    String regex = "^([[:alnum:]]+)([.])(member|admin)$";
    String regexErr = "^No parameter '([^']+)' found[.]$";
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
                log.error("Unexpected type %s",args[i].getClass());
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
                log.error("Unexpected type %s",args[i].getClass());
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
                log.error("Unexpected type %s",args[i].getClass());
                return null;
            }
        }
        return toret+")";
    }
    public Policies.SignaturePolicy secondPass(Object ...args){
        if(args.length<3){
            log.info("At least 3 arguments expected, got %d",args.length);
        }
        Context context = new Context();
        if(args[0] instanceof Context){
            context = (Context)args[0];
        }else{
            log.error("Unrecognized type, expected the context, got %s",args[0].getClass());
            return null;
        }
        int t;
        if(args[1] instanceof Float){
            t = (int)args[1];
        }
        else{
            log.error("Unrecognized type, expected the context, got %s",args[1].getClass());
            return null;
        }
        int n = args.length-1;
        if(t>n){
            log.error("Invalid t-out-of-n predicate, t %d, n %d", t, n);
            return null;
        }
        List<Policies.SignaturePolicy> policies = new ArrayList<Policies.SignaturePolicy>();
        for (int i=2;i<args.length;i++){
            if(args[i] instanceof String){
                Pattern pattern = Pattern.compile(regex);
                Matcher matchPattern = pattern.matcher((CharSequence) args[i]);
                List<String> subm = new ArrayList<String>();
                while(matchPattern.find()){
                    subm.add(matchPattern.group());
                }
                if(subm == null || subm.size() != 1 || subm.get(0).length() != 4){
                    log.error("Error parsing principal %s",t);
                    return null;
                }
               MspPrincipal.MSPRole.MSPRoleType role = null;
                if(subm.get(3) == "member"){
                    role = MspPrincipal.MSPRole.MSPRoleType.MEMBER;
                }else{
                    role = MspPrincipal.MSPRole.MSPRoleType.ADMIN;
                }
                MspPrincipal.MSPPrincipal.Builder builder = MspPrincipal.MSPPrincipal.newBuilder();
                builder.setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE);
                MspPrincipal.MSPRole.Builder roleBuilder = MspPrincipal.MSPRole.newBuilder();
                //build MspRole
                roleBuilder.setRole(role);
                roleBuilder.setMspIdentifier(subm.get(1));
                roleBuilder.build();
                builder.setPrincipal(ByteString.copyFrom(ProtoUtils.marshalOrPanic(roleBuilder.build())));
                MspPrincipal.MSPPrincipal mspPrincipal = builder.build();
                context.getPrincipals().add(mspPrincipal);
                context.setPrincipals(context.getPrincipals());
                Policies.SignaturePolicy dapolicy = CAuthDslBuilder.signedBy(context.getIDNum());
                policies.add(dapolicy);
                int num = context.getIDNum();
                num++;
                context.setIDNum(num);
            }
            if(args[i] instanceof Policies.SignaturePolicy){
                policies.add((Policies.SignaturePolicy) args[i]);
            }else{
                log.error("Unrecognized type, expected a principal or a policy, got %s",args[i]);
            }

        }
        Policies.SignaturePolicy[] policys = new Policies.SignaturePolicy[policies.size()];
        for(int i=0;i<policys.length;i++){
            policys[i] = policies.get(i);
        }
        return CAuthDslBuilder.nOutOf(t,policys);
    }
    public Policies.SignaturePolicyEnvelope fromString(String policy){

        return null;
    }


}
