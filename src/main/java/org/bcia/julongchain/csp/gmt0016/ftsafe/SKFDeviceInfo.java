/**
 * Copyright Feitian. All Rights Reserved.
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
package org.bcia.julongchain.csp.gmt0016.ftsafe;

/**
 * Class description
 *
 * @author
 * @date 7/4/18
 * @company FEITIAN
 */
public class SKFDeviceInfo {

    public class Version{

        private byte major;
        private byte minor;

        Version(byte major, byte minor){
            this.major = major;
            this.minor = minor;
        }

        public byte getMajor() {return major;}
        public byte getMinor() {return minor;}

    }

    private byte[] version;
    private byte[] manufacturer;
    private byte[] issuer;
    private byte[] label;
    private byte[] serialnumber;
    private byte[] hwversion;
    private byte[] firmwareversion;
    private long algsyscap;
    private long algasymcap;
    private long alghashcap;
    private long devauthalgid;
    private long totalspace;
    private long freespace;
    private long maxeccbuffersize;
    private long maxbuffersize;
    private byte[] reserved;

    public SKFDeviceInfo() {
        this.version = new byte[2];
        this.hwversion = new byte[2];
        this.firmwareversion = new byte[2];

        this.manufacturer = new byte[64];
        this.issuer = new byte[64];
        this.label = new byte[32];
        this.serialnumber = new byte[32];
        this.reserved = new byte[64];

        this.algsyscap = 0L;
        this.algasymcap = 0L;
        this.alghashcap = 0L;
        this.devauthalgid = 0L;
        this.totalspace = 0L;
        this.freespace = 0L;
        this.maxeccbuffersize = 0L;
        this.maxbuffersize = 0L;
    }

    public SKFDeviceInfo(byte[] ver, byte[] manufacturer, byte[] issuer,
                         byte[] label, byte[] serialnumber, byte[] hwversion, byte[] firmwareversion,
                         long algsyscap, long algasymcap, long alghashcap, long devauthalgid,
                         long totalspace, long freespace, long maxeccbuffersize,
                         long maxbuffersize, byte[] reserved)
    {

        this.version = new byte[ver.length];
        System.arraycopy(ver, 0, this.version, 0, ver.length);

        this.hwversion = new byte[hwversion.length];
        System.arraycopy(hwversion, 0, this.hwversion, 0, hwversion.length);

        this.firmwareversion = new byte[firmwareversion.length];
        System.arraycopy(firmwareversion, 0, this.firmwareversion, 0, firmwareversion.length);

        this.manufacturer = new byte[manufacturer.length];
        System.arraycopy(manufacturer, 0, this.manufacturer, 0, manufacturer.length);

        this.issuer = new byte[issuer.length];
        System.arraycopy(issuer, 0, this.issuer, 0, issuer.length);

        this.label = new byte[label.length];
        System.arraycopy(label, 0, this.label, 0, label.length);

        this.serialnumber = new byte[serialnumber.length];
        System.arraycopy(serialnumber, 0, this.serialnumber, 0, serialnumber.length);

        this.reserved = new byte[reserved.length];
        System.arraycopy(reserved, 0, this.reserved, 0, reserved.length);

        this.algsyscap = algsyscap;
        this.algasymcap = algasymcap;
        this.alghashcap = alghashcap;
        this.devauthalgid = devauthalgid;
        this.totalspace = totalspace;
        this.freespace = freespace;
        this.maxeccbuffersize = maxeccbuffersize;
        this.maxbuffersize = maxbuffersize;

    }

    public byte[] getSerialnumber() {
        return serialnumber;
    }

    public byte[] getLabel() {
        return label;
    }

    public byte[] getIssuer() {
        return issuer;
    }

    public byte[] getManufacturer() {
        return manufacturer;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public Version getVersion() {
        return new Version(version[0], version[1]);
    }

    public Version getHWVersion() {
        return new Version(hwversion[0], hwversion[1]);
    }

    public Version getFWVersion() {
        return new Version(firmwareversion[0], firmwareversion[1]);
    }

    public long getAlgsyscap() {
        return algsyscap;
    }

    public long getFreespace() {
        return freespace;
    }

    public long getAlgasymcap() {
        return algasymcap;
    }

    public long getAlghashcap() {
        return alghashcap;
    }

    public long getDevauthalgid() {
        return devauthalgid;
    }

    public long getTotalspace() {
        return totalspace;
    }

    public long getMaxeccbuffersize() {
        return maxeccbuffersize;
    }

    public long getMaxbuffersize() {
        return maxbuffersize;
    }
}
