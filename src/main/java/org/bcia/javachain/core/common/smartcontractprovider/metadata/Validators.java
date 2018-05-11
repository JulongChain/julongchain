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
package org.bcia.javachain.core.common.smartcontractprovider.metadata;

import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.util.Map;

/**
 * 验证器,用于验证某些输入是否合法
 *
 * @author sunianle
 * @date 5/10/18
 * @company Dingxuan
 */
public class Validators {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Validators.class);
    /**
     * ValidateMetadataFile checks that metadata files are valid
     * according to the validation rules of the metadata directory (metadataType)
     * @param srcPath
     * @param metadataType
     * @throws ValidateException
     */
    public static void validateMetadataFile(String srcPath,String metadataType) throws ValidateException {

    }

    /**
     *
     * @param srcPath
     * @throws ValidateException
     */
    public static void validateCouchdbIndexFile(String srcPath)throws ValidateException {

    }

    /**
     * isJSON tests a string to determine if it can be parsed as valid JSON
     * @param s
     * @return
     */
    public static boolean isJSON(byte[] s){
        return true;
    }

    /**
     *
     * @param indexDefinition
     * @throws ValidateException
     */
    public static void validateIndexJSON(Map<String,Object> indexDefinition)throws ValidateException {

    }

    /**
     *
     * @param jsonFragment
     * @throws ValidateException
     */
    public static void processIndexMap(Map<String,Object> jsonFragment)throws ValidateException {

    }

    /**
     *
     * @param jsonFragment
     * @throws ValidateException
     */
    public static void validateFieldMap(Map<String,Object> jsonFragment)throws ValidateException{

    }
}
