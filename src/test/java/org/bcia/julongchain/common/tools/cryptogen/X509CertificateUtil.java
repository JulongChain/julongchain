/**
 * Copyright BCIA. All Rights Reserved.
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

package org.bcia.julongchain.common.tools.cryptogen;

import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.common.tools.cryptogen.bean.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenhao, liuxifeng
 * @date 2018/4/9
 * @company Excelsecu
 */
public class X509CertificateUtil {


    private static final String C = "C";
    private static final String EMAIL_ADDRESS = "EMAILADDRESS";
    private static final String CN = "CN";
    private static final String OU = "OU";
    private static final String O = "O";
    private static final String ST = "ST";
    private static final String L = "L";
    private static final String STREET = "STREET";
    private static final String POSTAL_CODE = "POSTAL_CODE";


    public static Subject getSubject(String subjectDN) {

        Subject subject = new Subject();

        String[] subjects = subjectDN.replaceAll(", ", ",").split(",");

        for (String s : subjects) {

            String[] attributes = s.split("=");
            String attr = attributes[0];
            String value = attributes[1];
            switch (attr) {
                case CN:
                    subject.setCommonName(value);
                    break;
                case C:
                    List<String> cList = subject.getCountry();
                    if (cList == null) {
                        cList = new ArrayList<>();
                    }
                    cList.add(value);
                    subject.setCountry(cList);
                    break;
                case EMAIL_ADDRESS:
                    subject.setEmailAddress(value);
                    break;
                case OU:
                    List<String> OUList = subject.getOrganizationalUnit();
                    if (OUList == null) {
                        OUList = new ArrayList<>();
                    }
                    OUList.add(value);
                    subject.setOrganizationalUnit(OUList);
                    break;
                case O:
                    List<String> OList = subject.getOrganization();
                    if (OList == null) {
                        OList = new ArrayList<>();
                    }
                    OList.add(value);
                    subject.setOrganization(OList);
                    break;
                case ST:
                    List<String> stateList = subject.getStateOrProvince();
                    if (stateList == null) {
                        stateList = new ArrayList<>();
                    }
                    stateList.add(value);
                    subject.setStateOrProvince(stateList);
                    break;
                case L:
                    List<String> localityList = subject.getLocality();
                    if (localityList == null) {
                        localityList = new ArrayList<>();
                    }
                    localityList.add(value);
                    subject.setLocality(localityList);
                    break;
                case STREET:
                    List<String> streetList = subject.getStreetAddress();
                    if (streetList == null) {
                        streetList = new ArrayList<>();
                    }
                    streetList.add(value);
                    subject.setStreetAddress(streetList);
                    break;
                case POSTAL_CODE:
                    List<String> postalCodeList = subject.getPostalCode();
                    if (postalCodeList == null) {
                        postalCodeList = new ArrayList<>();
                    }
                    postalCodeList.add(value);
                    subject.setPostalCode(postalCodeList);
                    break;
                default:
                    JavaChainLogFactory.getLog().error("Invalid keyword " + attr);
                    break;
            }

        }

        return subject;

    }

    public static int[] getOID(List<String> oids) {

        List<Integer> oidList = new ArrayList<>();
        for (String oidsString : oids) {

            String[] oidStringArray = oidsString.split("\\.");
            for (String s : oidStringArray) {
                oidList.add(Integer.valueOf(s));
            }
        }

        int[] oid = new int[oidList.size()];
        for (int i = 0; i < oidList.size(); i++) {
            oid[i] = oidList.get(i);
        }
        return oid;
    }
}
