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
package org.bcia.julongchain.core.common.smartcontractprovider.metadata;

import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证器,用于验证某些输入是否合法
 * TODO 验证器验证couchdb，暂时无法实现
 *
 * @author sunianle, sunzongyu
 * @date 5/10/18
 * @company Dingxuan
 */
public class Validators {
    private static JavaChainLog log = JavaChainLogFactory.getLog(Validators.class);

    private static Map<String, FileValidator> fileValidators = new HashMap<>();

    /**
     * ValidateMetadataFile checks that metadata files are valid
     * according to the validation rules of the metadata directory (metadataType)
     */
    public static void validateMetadataFile(String fileName, byte[] fileBytes, String metadataType) throws ValidateException {
        if (fileValidators.containsKey(metadataType)) {
            throw new ValidateException("Metadata not supported in directory " + metadataType);
        }
        FileValidator fileValidator = fileValidators.get(metadataType);
        fileValidator.handle(fileName, fileBytes);
    }

}
