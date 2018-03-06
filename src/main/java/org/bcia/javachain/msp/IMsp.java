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
package org.bcia.javachain.msp;

import org.bcia.javachain.msp.entity.IdentityIdentifier;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.msp.Identities;
import org.bcia.javachain.protos.msp.MspConfigPackage;

import java.util.Map;

/**
 * @author zhangmingyang
 * @Date: 2018/3/6
 * @company Dingxuan
 */
public interface IMsp {

    public interface  Identity{
        /**
         * ExpiresAt() time.Time 未定义
         */
       /**
        *   为定义该方法   定义实体类 IdentityIdentifier来实现  GetIdentifier() *IdentityIdentifier
       	 */
        public  String GetMSPIdentifier();
        public void Validate();
        /**
         * 未实现该方法 GetOrganizationalUnits() []*OUIdentifier 之后实现OUIdentifier的实体
         */
        public void Verify(byte[] msg,byte[] sig);
        public byte[] Serialize();
        public void SatisfiesPrincipal(MspPrincipal principal);
    }

    public interface SigningIdentity{

      public byte[] Sign(byte[] msg);
      public Identity GetPublicVersion();
    }

    public  interface MSPManager{
      public  void Setup();
      public Map<String,MSP> GetMSPs();
      public interface MSP{
            public  interface IdentityDeserializer{
                public   Identity DeserializeIdentity(byte[] serializedIdentity);
                public  void IsWellFormed(Identities.SerializedIdentity identity);
            }
            public void Setup(MspConfigPackage.MSPConfig config);
            public int GetVersion();
           /**
            * const常量,之后需要确认
            */
            public int GetType();
            public String GetIdentifier();
            public SigningIdentity GetSigningIdentity(IdentityIdentifier identityIdentifier);
            public SigningIdentity GetDefaultSigningIdentity();
            public byte[][] GetTLSRootCerts();
            public byte[][] GetTLSIntermediateCerts();
            public void Validate(Identity id);
            public void SatisfiesPrincipal(Identity id, MspPrincipal.MSPPrincipal principal);
        }

    }
}
