/**
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
package org.bcia.javachain.common.ssh;

import com.jcraft.jsch.*;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * SSH帮助类
 *
 * @author wanliangbing
 * @date 2018-02-28
 * @company Dingxuan
 */
public class SshHelper {
    private static JavaChainLog logger = JavaChainLogFactory.getLog(SshHelper.class);

    private String charset = Charset.defaultCharset().toString();
    private Session session;

    public SshHelper(String host, Integer port, String user, String password) throws JSchException {
        connect(host, port, user, password);
    }

    /**
     * 连接远程服务器
     *
     * @param host 主机
     * @param port 端口
     * @param user 用户名
     * @param password 密码
     * @return
     * @throws JSchException
     */
    private Session connect(String host, Integer port, String user, String password) throws JSchException{
        try {
            JSch jsch = new JSch();
            if(port != null){
                session = jsch.getSession(user, host, port.intValue());
            }else{
                session = jsch.getSession(user, host);
            }
            session.setPassword(password);
            //设置第一次登陆的时候提示，可选值:(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            //30秒连接超时
            session.connect(5000);
        } catch (JSchException e) {
            logger.error("SFTPUitl 获取连接发生错误",e);
            throw e;
        }
        return session;
    }

    /**
     * 发送命令
     *
     * @param command 命令
     * @return
     * @throws Exception
     */
    public SshResInfo sendCmd(String command) throws Exception{
        return sendCmd(command, 200);
    }
    /**
     * 执行命令，返回执行结果
     * @param command 命令
     * @param delay 估计shell命令执行时间
     * @return String 执行命令后的返回
     * @throws IOException
     * @throws JSchException
     */
    public SshResInfo sendCmd(String command, int delay) throws Exception{
        int minDelay = 50;
        if(delay < minDelay){
            delay = 50;
        }
        SshResInfo result = null;
        //读数据缓存
        byte[] tmp = new byte[1024];
        //执行SSH返回的结果
        StringBuffer strBuffer = new StringBuffer();
        StringBuffer errResult=new StringBuffer();

        Channel channel = session.openChannel("exec");
        ChannelExec ssh = (ChannelExec) channel;
        //返回的结果可能是标准信息,也可能是错误信息,所以两种输出都要获取
        //一般情况下只会有一种输出.
        //但并不是说错误信息就是执行命令出错的信息,如获得远程java JDK版本就以
        //ErrStream来获得.
        InputStream stdStream = ssh.getInputStream();
        InputStream errStream = ssh.getErrStream();

        ssh.setCommand(command);
        ssh.connect();

        try {
            //开始获得SSH命令的结果
            while(true){
                //获得错误输出
                while(errStream.available() > 0){
                    int i = errStream.read(tmp, 0, 1024);

                    if(i < 0) {
                        break;
                    }
                    errResult.append(new String(tmp, 0, i));
                }

                //获得标准输出
                while(stdStream.available() > 0){
                    int i = stdStream.read(tmp, 0, 1024);

                    if(i < 0) {
                        break;
                    }
                    strBuffer.append(new String(tmp, 0, i));
                }
                if(ssh.isClosed()){
                    int code = ssh.getExitStatus();
                    logger.info("exit-status: " + code);
                    result = new SshResInfo(code, strBuffer.toString(), errResult.toString());
                    break;
                }
                try
                {
                    Thread.sleep(delay);
                }
                catch(Exception ee)
                {
                    logger.error(ee.getMessage(),ee);
                }
            }
        } finally {
            channel.disconnect();
        }

        return result;
    }

    private String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    /**
     * 删除远程服务器中的文件或文件夹
     *
     * @param remoteFile 文件名称
     * @return
     */
    public boolean deleteRemoteFIleOrDir(String remoteFile){
        ChannelSftp channel=null;
        try {
            channel=(ChannelSftp) session.openChannel("sftp");
            channel.connect();
            SftpATTRS sftpATTRS= channel.lstat(remoteFile);
            if(sftpATTRS.isDir()){
                //目录
                logger.debug("remote File:dir");
                channel.rmdir(remoteFile);
                return true;
            }else if(sftpATTRS.isReg()){
                //文件
                logger.debug("remote File:file");
                channel.rm(remoteFile);
                return true;
            }else{
                logger.debug("remote File:unkown");
                return false;
            }
        }catch (JSchException e) {
            if(channel!=null){
                channel.disconnect();
                session.disconnect();
            }
            logger.error("error",e);
            return  false;
        } catch (SftpException e) {
            logger.info("meg"+e.getMessage());
            logger.error("SftpException",e);
            return false;
        }
    }

    /**
     * 检查远程服务器是否存在文件或文件夹
     *
     * @param remoteFile
     * @return
     */
    public boolean detectedFileExist(String remoteFile) {

        ChannelSftp channel=null;
        try {
            channel=(ChannelSftp) session.openChannel("sftp");
            channel.connect();
            SftpATTRS sftpATTRS= channel.lstat(remoteFile);
            if(sftpATTRS.isDir()||sftpATTRS.isReg()){
                //目录 和文件
                logger.info("remote File:dir");
                return true;
            }else{
                logger.info("remote File:unkown");
                return false;
            }
        }catch (JSchException e) {
            if(channel!=null){
                channel.disconnect();
                session.disconnect();
            }
            return  false;
        } catch (SftpException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * 关闭连接
     */
    public void close(){
        if(session.isConnected()){
            session.disconnect();
        }
    }

}
