/**
 * Copyright Dingxuan. All Rights Reserved.
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
package org.bcia.javachain.common.ledger.util;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/9
 * @company Dingxuan
 */
public class IoUtil {

    /** CreateDirIfMissing creates a dir for dirPath if not already exists. If the dir is empty it returns true
     *
     * @param dirPath
     * @return
     */
    public Boolean createDirIfMissing(String dirPath) {
        return Boolean.FALSE;
    }

    /** DirEmpty returns true if the dir at dirPath is empty
     *
     * @param dirPath
     * @return
     */
    public Boolean DirEmpty(String dirPath){
        return Boolean.FALSE;
    }

    /** FileExists checks whether the given file exists.
     * If the file exists, this method also returns the size of the file.
     */
    public Boolean fileExists(String filePath) {
       return Boolean.FALSE;
    }

    /** ListSubdirs returns the subdirectories
     *
     * @param dirPath
     * @return
     */
    public String[] listSubdirs(String dirPath) {
        return null;
    }

    public void logDirStatus(String msg, String dirPath) {
        return;
    }

}
