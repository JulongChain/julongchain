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
package org.bcia.javachain.core.node;

import org.apache.commons.lang3.StringUtils;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 类描述
 *
 * @author
 * @date 2018/3/15
 * @company Dingxuan
 */
public class NodeConfiguration {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeConfiguration.class);

    public void cacheConfiguration() {

    }






//        // getLocalAddress returns the address:port the local peer is operating on.  Affected by env:peer.addressAutoDetect
//        getLocalAddress:=func() (string, error){
//            peerAddress:=viper.GetString("peer.address")
//            if peerAddress == "" {
//                return "",fmt.Errorf("peer.address isn't set")
//            }
//            host, port, err :=net.SplitHostPort(peerAddress)
//            if err != nil {
//                return "",errors.Errorf("peer.address isn't in host:port format: %s", peerAddress)
//            }
//
//            autoDetectedIPAndPort:=net.JoinHostPort(GetLocalIP(), port)
//            peerLogger.Info("Auto-detected peer address:", autoDetectedIPAndPort)
//            // If host is the IPv4 address "0.0.0.0" or the IPv6 address "::",
//            // then fallback to auto-detected address
//            if ip:=net.ParseIP(host);
//            ip != nil && ip.IsUnspecified() {
//                peerLogger.Info("Host is", host, ", falling back to auto-detected address:", autoDetectedIPAndPort)
//                return autoDetectedIPAndPort,nil
//            }
//
//            if viper.GetBool("peer.addressAutoDetect") {
//                peerLogger.Info("Auto-detect flag is set, returning", autoDetectedIPAndPort)
//                return autoDetectedIPAndPort,nil
//            }
//            peerLogger.Info("Returning", peerAddress)
//            return peerAddress,nil
//
//        }
//
//        // getPeerEndpoint returns the PeerEndpoint for this Peer instance.  Affected by env:peer.addressAutoDetect
//        getPeerEndpoint:=func() ( * pb.PeerEndpoint, error){
//            var peerAddress string
//            peerAddress, err :=getLocalAddress()
//            if err != nil {
//                return nil,err
//            }
//            return &pb.PeerEndpoint {
//                Id: &pb.PeerID {
//                    Name:
//                    viper.GetString("peer.id")
//                },Address:
//                peerAddress
//            },nil
//        }
//
//        localAddress, localAddressError = getLocalAddress()
//        peerEndpoint, _ = getPeerEndpoint()
//
//        configurationCached = true
//
//        if localAddressError != nil {
//            return localAddressError
//        }
//        return
//    }



//    public String getLocalAddress() {
//        //TODO:会获取到
//        NodeConfig.Node node = new NodeConfig.Node();
//
//        //获取地址(host+port)
//        String address = node.getAddress();
//        if(StringUtils.isBlank(address)){
//            log.warn("Missing node.address in node.yaml");
//            return null;
//        }
//
//        String[] str = address.split(":");
//        if(str.length != 2){
//            log.warn("Wrong node.address: " + address);
//            return null;
//        }
//
//        String host = str[0];
//        String port = str[1];
//
//
//
//
//
//        autoDetectedIPAndPort := net.JoinHostPort(GetLocalIP(), port)
//        peerLogger.Info("Auto-detected peer address:", autoDetectedIPAndPort)
//        // If host is the IPv4 address "0.0.0.0" or the IPv6 address "::",
//        // then fallback to auto-detected address
//        if ip := net.ParseIP(host); ip != nil && ip.IsUnspecified() {
//            peerLogger.Info("Host is", host, ", falling back to auto-detected address:", autoDetectedIPAndPort)
//            return autoDetectedIPAndPort, nil
//        }
//
//        if viper.GetBool("peer.addressAutoDetect") {
//            peerLogger.Info("Auto-detect flag is set, returning", autoDetectedIPAndPort)
//            return autoDetectedIPAndPort, nil
//        }
//        peerLogger.Info("Returning", peerAddress)
//        return peerAddress, nil
//
//    }

    /**
     * 获取本地IP地址
     *
     * @return
     */
    public static String getLocalIP() {
        String localIp = null;
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                ip = (InetAddress) ni.getInetAddresses().nextElement();
                //localIp = ip.getHostAddress();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                        && ip.getHostAddress().indexOf(":") == -1) {
                    localIp = ip.getHostAddress();
                    break;
                } else {
                    ip = null;
                }
            }
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        }

        return localIp;
    }
}