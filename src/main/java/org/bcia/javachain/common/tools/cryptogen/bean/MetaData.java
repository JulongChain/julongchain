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
package org.bcia.javachain.common.tools.cryptogen.bean;


/**
 * @author chenhao, yegangcheng
 * @date 2018/4/3
 * @company Excelsecu
 */
public class MetaData {
    public static String PROGRAM_NAME = "cryptogen";
    public static String mVersion = "";

    public static String getVersionInfo() {
        if (mVersion.equals("")) {
            mVersion = "development build";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(PROGRAM_NAME)
                .append(":\n Version: ")
                .append(mVersion)
                .append("\n Java version: ")
                .append(System.getProperty("java.version"))
                .append("\n OS/Arch: ")
                .append(System.getProperty("os.name"))
                .append("/")
                .append(System.getProperty("os.arch"));
        return builder.toString();
    }
}
