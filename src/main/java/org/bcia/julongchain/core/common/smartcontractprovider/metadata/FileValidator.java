/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.julongchain.core.common.smartcontractprovider.metadata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.bcia.julongchain.common.exception.ValidateException;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;

import java.util.Map;

/**
 * 用于处理特定metadata
 *
 * @author sunzongyu
 * @date 2018/05/16
 * @company Dingxuan
 */
public class FileValidator {

    private static final JavaChainLog log = JavaChainLogFactory.getLog(FileValidator.class);

    public FileValidator(){

    }

    public void handle(String fileName, byte[] fileBytes) throws ValidateException {
        couchdbIndexFileValidator(fileName, fileBytes);
    }


    /**
     * couchdbIndexFileValidator implements fileValidator
     */
    private static void couchdbIndexFileValidator(String fileName, byte[] fileBytes) throws ValidateException {
        String ext = fileName.substring(fileName.length() - 5);
        String json = ".json";
        if(!json.equals(ext)){
            throw new ValidateException("Index metadata file " + fileName + " does not have a .json extension");
        }
        Map<String, Object> indexDefinition = isJSON(fileBytes);
        if(indexDefinition == null){
            throw new ValidateException("Index metadata file " + fileName + " is not a valid index definition");
        }
        validateIndexJSON(indexDefinition);
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
    private static Map<String, Object> isJSON(byte[] s){
        return (Map<String, Object>) JSON.parse(s);
    }

    /**
     * 校验Json数据
     * @param indexDefinition
     * @throws ValidateException
     */
    private static void validateIndexJSON(Map<String,Object> indexDefinition)throws ValidateException {
        //标记key是否包含
        boolean indexIncluded = false;

        for(Map.Entry<String, Object> entry : indexDefinition.entrySet()){
            String jsonKey = entry.getKey();
            Object jsonValue  = entry.getValue();
            switch (jsonKey){
                case "index":
                    if(!(jsonValue instanceof Map)){
                        throw new ValidateException("Invalid entry, \"index\" must be a json");
                    }
                    processIndexMap((Map<String, Object>) jsonValue);
                    indexIncluded = true;
                    break;
                case "ddoc":
                    if(!(jsonValue instanceof String)){
                        throw new ValidateException("Invalid entry, \"ddoc\" must be a String");
                    }
                    log.debug("Found index object: \"{}\":\"{}\"", jsonKey, jsonValue);
                    break;
                case "type":
                    if(!"json".equals(jsonValue)){
                        throw new ValidateException("Index type must be json");
                    }
                    log.debug("Found index object: \"{}\":\"{}\"", jsonKey, jsonValue);
                    break;
                case "name":
                    if(!(jsonValue instanceof String)){
                        throw new ValidateException("Invalid entry, \"name\" must be a String");
                    }
                    log.debug("Found index objecg: \"{}\":\"{}\"", jsonKey, jsonValue);
                    break;
                default:
                    throw new ValidateException("Invalid Entry. Entry " + jsonKey);
            }
        }

        if(!indexIncluded){
            throw new ValidateException("Index definition must include a \"fields\" definition");
        }
    }

    /**
     * processIndexMap processes an interface map and wraps field names or traverses
     * the next level of the json query
     * @param jsonFragment
     * @throws ValidateException
     */
    private static void processIndexMap(Map<String,Object> jsonFragment)throws ValidateException {
        for(Map.Entry<String, Object> entry : jsonFragment.entrySet()){
            String jsonKey = entry.getKey();
            Object jsonValue  = entry.getValue();
            switch (jsonKey){
                case "fields":
                    if(jsonValue instanceof JSONArray){
                        for(Object itemValue : (JSONArray) jsonValue){
                            if(itemValue instanceof String){
                                log.debug("Found index field name: \"{}\"", itemValue);
                            } else if(itemValue instanceof Map){
                                validateFieldMap((Map) itemValue);
                            } else {
                                throw new ValidateException("Unexpected JSON type");
                            }
                        }
                    }
                    break;
                case "partial_filter_selector":
                    //TODO - add support for partial filter selector, for now nothing to do
                    break;
                default:
                    throw new ValidateException("Invalid entry. Entry " + jsonKey);
            }
        }
    }

    /**
     * validateFieldMap validates the list of field objects
     * @param jsonFragment
     * @throws ValidateException
     */
    private static void validateFieldMap(Map<String,Object> jsonFragment)throws ValidateException{
        for(Map.Entry<String, Object> entry : jsonFragment.entrySet()) {
            String jsonKey = entry.getKey();
            Object jsonValue = entry.getValue();
            if(jsonValue instanceof String){
                if(!"asc".equals(((String) jsonValue).toLowerCase()) && !"desc".equals(((String) jsonValue).toLowerCase())){
                    throw new ValidateException("Sort must be either \"asc\" or \"desc\". \"" + jsonValue + "\" was found");
                }
                log.debug("Found index objecg: \"{}\":\"{}\"", jsonKey, jsonValue);
            } else {
                throw new ValidateException("Invalid field definition, fields must be in the form \"fieldname\":\"sort\"");
            }
        }
    }
}
