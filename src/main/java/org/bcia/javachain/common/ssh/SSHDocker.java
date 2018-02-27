package org.bcia.javachain.common.ssh;

import com.jcraft.jsch.JSchException;

public class SSHDocker {

    public static void executeCommand(String host,Integer port,String username,String password,String command){
        try {
            SSHHelper helper = new SSHHelper(host, port, username, password);
            try {
                SSHResInfo resInfo =helper.sendCmd(command);
                System.out.println(resInfo.toString());
                helper.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

}
