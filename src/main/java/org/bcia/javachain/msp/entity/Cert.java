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
package org.bcia.javachain.msp.entity;

import org.bouncycastle.jcajce.provider.asymmetric.X509;

import java.io.FileInputStream;
import java.security.cert.*;


/**
 * @author zhangmingyang
 * @Date: 2018/3/14
 * @company Dingxuan
 */
public class Cert {

    //    byte[] raw;
//    byte[] RawTBSCertificate;
//    byte[] RawSubjectPublicKeyInfo;
//    byte[] RawSubject;
//
//    byte[] RawIssuer;
//    byte[] Signature;
//
////    SignatureAlgorithm SignatureAlgorithm
////
////    PublicKeyAlgorithm PublicKeyAlgorithm
////    PublicKey          interface{}
////
////    Version             int
////    SerialNumber        *big.Int
////    Issuer              pkix.Name
////    Subject             pkix.Name
////    NotBefore, NotAfter time.Time // Validity bounds.
////    KeyUsage            KeyUsage
//
//    // Extensions contains raw X.509 extensions. When parsing certificates,
//    // this can be used to extract non-critical extensions that are not
//    // parsed by this package. When marshaling certificates, the Extensions
//    // field is ignored, see ExtraExtensions.
//    pkix.Extension[] Extensions;
//
//    // ExtraExtensions contains extensions to be copied, raw, into any
//    // marshaled certificates. Values override any extensions that would
//    // otherwise be produced based on the other fields. The ExtraExtensions
//    // field is not populated when parsing certificates, see Extensions.
//    ExtraExtensions []pkix.Extension
//
//    // UnhandledCriticalExtensions contains a list of extension IDs that
//    // were not (fully) processed when parsing. Verify will fail if this
//    // slice is non-empty, unless verification is delegated to an OS
//    // library which understands all the critical extensions.
//    //
//    // Users can access these extensions using Extensions and can remove
//    // elements from this slice if they believe that they have been
//    // handled.
//    UnhandledCriticalExtensions []asn1.ObjectIdentifier
//
//    ExtKeyUsage        []ExtKeyUsage           // Sequence of extended key usages.
//    UnknownExtKeyUsage []asn1.ObjectIdentifier // Encountered extended key usages unknown to this package.
//
//    // BasicConstraintsValid indicates whether IsCA, MaxPathLen,
//    // and MaxPathLenZero are valid.
//    BasicConstraintsValid bool
//    IsCA                  bool
//
//    // MaxPathLen and MaxPathLenZero indicate the presence and
//    // value of the BasicConstraints' "pathLenConstraint".
//    //
//    // When parsing a certificate, a positive non-zero MaxPathLen
//    // means that the field was specified, -1 means it was unset,
//    // and MaxPathLenZero being true mean that the field was
//    // explicitly set to zero. The case of MaxPathLen==0 with MaxPathLenZero==false
//    // should be treated equivalent to -1 (unset).
//    //
//    // When generating a certificate, an unset pathLenConstraint
//    // can be requested with either MaxPathLen == -1 or using the
//    // zero value for both MaxPathLen and MaxPathLenZero.
//    MaxPathLen int
//    // MaxPathLenZero indicates that BasicConstraintsValid==true
//    // and MaxPathLen==0 should be interpreted as an actual
//    // maximum path length of zero. Otherwise, that combination is
//    // interpreted as MaxPathLen not being set.
//    MaxPathLenZero bool
//
//    SubjectKeyId   []byte
//    AuthorityKeyId []byte
//
//    // RFC 5280, 4.2.2.1 (Authority Information Access)
//    OCSPServer            []string
//    IssuingCertificateURL []string
//
//    // Subject Alternate Name values
//    DNSNames       []string
//    EmailAddresses []string
//    IPAddresses    []net.IP
//
//    // Name constraints
//    PermittedDNSDomainsCritical bool // if true then the name constraints are marked critical.
//    PermittedDNSDomains         []string
//    ExcludedDNSDomains          []string
//
//    // CRL Distribution Points
//    CRLDistributionPoints []string
//
//    PolicyIdentifiers []asn1.ObjectIdentifier

}

