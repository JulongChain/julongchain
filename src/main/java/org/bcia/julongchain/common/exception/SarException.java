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
package org.bcia.julongchain.common.exception;

/**
 * GM/T-0016 API error.
 *
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class SarException extends JavaChainException {

    public static final int SAR_OK = 0x00000000;
    public static final int SAR_FAIL = 0x0A000001;
    public static final int SAR_UNKNOWNERR=0x0A000002;
    public static final int SAR_NOTSUPPORTYETERR=0x0A000003;
    public static final int SAR_FILEERR=0x0A000004;
    public static final int SAR_INVALIDHANDLEERR = 0x0A000005;
    public static final int SAR_INVALIDPARAMERR=0x0A000006;
    public static final int SAR_READFILEER=0x0A000007;
    public static final int SAR_WRITEFILEERR=0x0A000008;
    public static final int SAR_NAMELENERR=0x0A000009;
    public static final int SAR_KEYUSAGEERR=0x0A00000A;
    public static final int SAR_MODULUSLENERR=0x0A00000B;
    public static final int SAR_NOTINITIALIZEERR=0x0A0000C;
    public static final int SAR_OBJERR=0x0A00000D;
    public static final int SAR_MEMORYERR=0x0A00000E;
    public static final int SAR_TIMEOUTERR=0x0A00000F;

    public static final int SAR_INDATALENERR=0x0A000010;
    public static final int SAR_INDATAERR=0x0A000011;
    public static final int SAR_GENRANDERR=0x0A000012;
    public static final int SAR_HASHOBJERR=0x0A0000013;
    public static final int SAR_HASHERR=0x0A000014;
    public static final int SAR_GENRSAKEYERR=0x0A000015;
    public static final int SAR_RSAMODULUSLENERR=0x0A000016;
    public static final int SAR_CSPIMPRTPUBKEYERR=0x0A000017;
    public static final int SAR_RSAENCERR=0x0A000018;
    public static final int SAR_RSADECERR=0x0A000019;
    public static final int SAR_HASHOTEQUALERR=0x0A00001A;
    public static final int SAR_KEYNOTFOUNTERR=0x0A00001B;
    public static final int SAR_CERTNOTFOUNTERR=0x0A00001C;
    public static final int SAR_NOTEXPORTERR=0x0A00001D;
    public static final int SAR_DECRYPTPADERR=0x0A00001E;
    public static final int SAR_MACLENERR=0x0A00001F;

    public static final int SAR_BUFFER_TOO_SMALL=0x0A000020;
    public static final int SAR_KEYINFOTYPEERR=0x0A000021;
    public static final int SAR_NOT_EVENTERR=0x0A000022;
    public static final int SAR_DEVICE_REMOVED=0x0A000023;
    public static final int SAR_PIN_INCORRECT =0x0A000024;
    public static final int SAR_PIN_LOCKED = 0x0A000025;
    public static final int SAR_PIN_INVALID = 0x0A000026;
    public static final int SAR_PIN_LEN_RANGE = 0x0A000027;
    public static final int SAR_USER_ALREADY_LOGGED_IN = 0x0A000028;
    public static final int SAR_USER_PIN_NOT_INITIALIZED = 0x0A000029;
    public static final int SAR_USER_TYPE_INVALID = 0x0A00002A;
    public static final int SAR_APPLICATION_NAME_INVALID = 0x0A00002B;
    public static final int SAR_APPLICATION_EXISTS = 0x0A00002C;
    public static final int SAR_USER_NOT_LOGGED_IN = 0x0A00002D;
    public static final int SAR_APPLICATION_NOT_EXIST = 0x0A00002E;
    public static final int SAR_FILE_ALREADY_EXIST = 0x0A00002F;

    public static final int SAR_NO_ROOM = 0x0A000030;
    public static final int SAR_FILE_NOT_EXIST = 0x0A000031;
    public static final int SAR_REACH_MAX_CONTAINER_COUNT = 0x0A000032;

    private static final String MODULE_NAME = "[Sar]";
    private int errorCode;

    public SarException(int errorCode) {
        super(MODULE_NAME + String.format("gm0016 error, code 0x%08X", errorCode));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
