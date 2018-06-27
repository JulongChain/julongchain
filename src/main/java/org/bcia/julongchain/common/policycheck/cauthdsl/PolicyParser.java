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
import org.bcia.julongchain.common.exception.PolicyException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.policycheck.bean.Context;
import org.bcia.julongchain.common.policycheck.bean.PolicyNode;
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
    public static Context context;
    static  String regex = "^([[:alnum:]]+)([.])(member|admin)$";
    String regexErr = "^No parameter '([^']+)' found[.]$";

    public static Policies.SignaturePolicyEnvelope fromString(String policy) throws PolicyException {

       String res = checkPolicyStr(policy);
       PolicyNode node = checkPolicyNode(res);
       Context ctx = new Context(0,new ArrayList<MspPrincipal.MSPPrincipal>());
       Policies.SignaturePolicy signaturePolicy = getSignaturePolicy(node,ctx);
        Policies.SignaturePolicyEnvelope.Builder builder = Policies.SignaturePolicyEnvelope.newBuilder();
        builder.setVersion(0);
        builder.setRule(signaturePolicy);
        for(int i=0;i<context.getPrincipals().size();i++){
            builder.setIdentities(i,context.getPrincipals().get(i));
        }
       return builder.build();
    }


    public static Policies.SignaturePolicy getSignaturePolicy(PolicyNode node, Context ctx) throws PolicyException {
        for(int i=0;i<node.sons.size();i++){
            if("outof".equals(node.getMsg())){
                getSignaturePolicy(node.sons.get(i),ctx);
            }
        }
        if(node.sons.size() < 3 ){
            String msg=String.format("At least 3 arguments expected, got %d",node.sons.size());
            throw new PolicyException(msg);
        }
        int t ;
        try {
            t = Integer.parseInt(node.sons.get(1).getMsg());
        }catch (Exception e){
            String msg=String.format("Unrecognized type, expected a number, got %s",node.sons.get(1).getMsg());
            throw new PolicyException(msg);
        }
        int n = node.sons.size()-1;
        if(t>n){
            String msg=String.format("Invalid t-out-of-n predicate, t %d, n %d",t,n);
            throw new PolicyException(msg);
        }
        List<Policies.SignaturePolicy> policies = new ArrayList<Policies.SignaturePolicy>();
        for(int i=2;i<node.sons.size();i++){
            Pattern p=Pattern.compile(regex);
            Matcher m=p.matcher(node.sons.get(i).getMsg());
            if(m.matches()){
                Pattern pattern = Pattern.compile(regex);
                Matcher matchPattern = pattern.matcher((CharSequence) node.sons.get(i));
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
                ctx.getPrincipals().add(mspPrincipal);
                ctx.setPrincipals(ctx.getPrincipals());
                Policies.SignaturePolicy dapolicy = CAuthDslBuilder.signedBy(context.getIDNum());
                policies.add(dapolicy);
                int num = ctx.getIDNum();
                num++;
                ctx.setIDNum(num);
                context = ctx;
            }else{
                String msg=String.format("Unrecognized type, expected a principal or a policy, got %s",node.sons.get(i).getMsg());
            }
        }
        Policies.SignaturePolicy[] policys = new Policies.SignaturePolicy[policies.size()];
        for(int i=0;i<policys.length;i++){
            policys[i] = policies.get(i);
        }
        return CAuthDslBuilder.nOutOf(t,policys);
    }

    public static String checkPolicyStr(String policy) {
        PolicyNode todo = new PolicyNode();
        PolicyNode first = new PolicyNode();
        todo = first;
        for (char c : policy.toCharArray()) {
            PolicyNode tmp = new PolicyNode();
            switch (c) {
                case '(':
                    todo.sons.add(tmp);
                    tmp.parent = todo;
                    todo = tmp;
                    break;
                case ',':
                    todo.parent.sons.add(tmp);
                    tmp.parent = todo.parent;
                    todo = tmp;
                    break;
                case ')':
                    todo = todo.parent;
                    break;
                case ' ':
                    break;
                default:
                    todo.msg.append(c);
                    break;
            }
        }
        String str =first.n2str();
        System.out.print(first.n2str());

        return str;
    }
    public static PolicyNode checkPolicyNode(String policy) {
        PolicyNode todo = new PolicyNode();
        PolicyNode first = new PolicyNode();
        todo = first;
        for (char c : policy.toCharArray()) {
            PolicyNode tmp = new PolicyNode();
            switch (c) {
                case '(':
                    todo.sons.add(tmp);
                    tmp.parent = todo;
                    todo = tmp;
                    break;
                case ',':
                    todo.parent.sons.add(tmp);
                    tmp.parent = todo.parent;
                    todo = tmp;
                    break;
                case ')':
                    todo = todo.parent;
                    break;
                case ' ':
                    break;
                default:
                    todo.msg.append(c);
                    break;
            }
        }


        return first;
    }



}
