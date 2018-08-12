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
package org.bcia.julongchain.csp.gmt0016.excelsecu.bean;

/**
 * @author chenhao
 * @date 2018/4/2
 * @company Excelsecu
 */
public class DeviceInfo {


    private Version version;

    private String manufacturer;

    private String issuer;

    private String label;

    private String serialNumber;

    private Version hwVersion;

    private Version firmwareVersion;

    private long algSymCap;

    private long algHashCap;

    private long algAsymCap;

    private long devAuthAlgID;

    private long totalSpace;

    private long freeSpace;

    private long maxECCBufferSize;

    private long maxBufferSize;

    private byte[] reserved = new byte[64];

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Version getHwVersion() {
        return hwVersion;
    }

    public void setHwVersion(Version hwVersion) {
        this.hwVersion = hwVersion;
    }

    public Version getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(Version firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public long getAlgSymCap() {
        return algSymCap;
    }

    public void setAlgSymCap(long algSymCap) {
        this.algSymCap = algSymCap;
    }

    public long getAlgHashCap() {
        return algHashCap;
    }

    public void setAlgHashCap(long algHashCap) {
        this.algHashCap = algHashCap;
    }

    public long getAlgAsymCap() {
        return algAsymCap;
    }

    public void setAlgAsymCap(long algAsymCap) {
        this.algAsymCap = algAsymCap;
    }

    public long getDevAuthAlgID() {
        return devAuthAlgID;
    }

    public void setDevAuthAlgID(long devAuthAlgID) {
        this.devAuthAlgID = devAuthAlgID;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
    }

    public long getMaxECCBufferSize() {
        return maxECCBufferSize;
    }

    public void setMaxECCBufferSize(long maxECCBufferSize) {
        this.maxECCBufferSize = maxECCBufferSize;
    }

    public long getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(long maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }
}
