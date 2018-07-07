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
    public static List<Policies.SignaturePolicy> policies = new ArrayList<Policies.SignaturePolicy>();
    static  String regex = "^([[:alnum:]]+)([.])(member|admin)$";
    String regexErr = "^No parameter '([^']+)' found[.]$";

    public static Policies.SignaturePolicyEnvelope fromString(String policy) throws PolicyException {

       String res = checkPolicyStr(policy);
       PolicyNode node = checkPolicyNode(res);
       Context ctx = new Context(0,new ArrayList<MspPrincipal.MSPPrincipal>());
       List<PolicyNode> policyNodes = queryNode(node);

       //getSignaturePolicy(policyNodes.get(1),ctx);
       for(PolicyNode policyNode : policyNodes){
           getSignaturePolicy(policyNode,ctx);
       }
        Policies.SignaturePolicy[] policys = new Policies.SignaturePolicy[policies.size()];
        for(int i=0;i<policys.length;i++){
            policys[i] = policies.get(i);
        }
        Policies.SignaturePolicy signaturePolicy = CAuthDslBuilder.nOutOf(1,policys);
       Policies.SignaturePolicyEnvelope.Builder builder = Policies.SignaturePolicyEnvelope.newBuilder();
       builder.setVersion(0);
       builder.setRule(signaturePolicy);
       for(int i=0;i<context.getPrincipals().size();i++){
           // builder.setIdentities(i,context.getPrincipals().get(i));
            builder.addIdentities(context.getPrincipals().get(i));
        }
       return builder.build();
    }
    public static List<PolicyNode> queryNode(PolicyNode node){
        List<PolicyNode> policyNodes = new ArrayList<PolicyNode>();
        if("outof".equals(node.getMsg())){
            for(int i=0;i<node.sons.size();i++){
                if("outof".equals(node.sons.get(i).msg.toString())){
                    policyNodes.add(node.sons.get(i));
                }else{
                    continue;
                }
            }
        }
        return policyNodes;
    }

    public static void getSignaturePolicy(PolicyNode node, Context ctx) throws PolicyException {

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
        //List<Policies.SignaturePolicy> policies = new ArrayList<Policies.SignaturePolicy>();
        for(int i=2;i<node.sons.size();i++){
            String str = node.sons.get(i).getMsg().toString();
            str = str.replaceAll("\'","");
            String[] strs = str.split("\\.");
            MspPrincipal.MSPRole.MSPRoleType role = null;
            if("member".equals(strs[strs.length-1])){
                role = MspPrincipal.MSPRole.MSPRoleType.MEMBER;
            }if("admin".equals(strs[strs.length-1])){
                role = MspPrincipal.MSPRole.MSPRoleType.ADMIN;
            }else{
                String msg=String.format("Unrecognized type, expected a principal or a policy, got %s",node.sons.get(i).getMsg());
            }
            MspPrincipal.MSPPrincipal.Builder builder = MspPrincipal.MSPPrincipal.newBuilder();
            builder.setPrincipalClassification(MspPrincipal.MSPPrincipal.Classification.ROLE);
            MspPrincipal.MSPRole.Builder roleBuilder = MspPrincipal.MSPRole.newBuilder();
            //build MspRole
            roleBuilder.setRole(role);
            roleBuilder.setMspIdentifier(node.sons.get(i).getMsg());
            roleBuilder.build();
            builder.setPrincipal(ByteString.copyFrom(ProtoUtils.marshalOrPanic(roleBuilder.build())));
            MspPrincipal.MSPPrincipal mspPrincipal = builder.build();
            ctx.getPrincipals().add(mspPrincipal);
            ctx.setPrincipals(ctx.getPrincipals());
            Policies.SignaturePolicy dapolicy = CAuthDslBuilder.signedBy(ctx.getIDNum());
            policies.add(dapolicy);
            int num = ctx.getIDNum();
            num++;
            ctx.setIDNum(num);
            context = ctx;
        }


       /* Policies.SignaturePolicy[] policys = new Policies.SignaturePolicy[policies.size()];
        for(int i=0;i<policys.length;i++){
            policys[i] = policies.get(i);
        }
        return CAuthDslBuilder.nOutOf(t,policys);*/
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
        System.out.print(str);

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
