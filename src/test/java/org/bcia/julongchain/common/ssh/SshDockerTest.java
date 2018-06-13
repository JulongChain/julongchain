package org.bcia.julongchain.common.ssh;

import org.junit.Test;

public class SshDockerTest {

    @Test
    public void executeCommand() {
        String command = "docker build -t docker.bcia.julongchain.com:5000/jdk.ubuntu /root/docker/jdk.ubuntu";
        SshDocker.executeCommand("192.168.246.130",22,"root","000000",command);
    }
}