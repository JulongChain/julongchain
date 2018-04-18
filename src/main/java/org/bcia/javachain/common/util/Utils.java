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
package org.bcia.javachain.common.util;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;

import java.util.Calendar;
import java.util.Date;

/**
 * 类描述
 *
 * @author sunianle
 * @date 3/27/18
 * @company Dingxuan
 */
public class Utils {
    public static Timestamp createUtcTimeStamp(){
        Timestamp timeStamp= Timestamps.fromMillis(new Date().getTime());
        return timeStamp;
    }

    public static String getTestGroupID(){
        return "TEST_GROUP_ID";
    }

    public static byte[] appendBytes(byte[] bytes1,byte[] bytes2){
        int size1= bytes1.length;
        int size2=bytes2.length;
        int size=size1+size2;
        byte[] results=new byte[size];
        int i=0;
        for(byte bt:bytes1){
            results[i]=bt;
            i++;
        }
        for(byte bt:bytes2){
            results[i]=bt;
            i++;
        }
        return results;
    }
}
