package org.bcia.julongchain.core.container.dockercontroller;

import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.exception.VMException;
import org.bcia.julongchain.core.container.DockerUtil;
import org.bcia.julongchain.core.smartcontract.node.SmartContractRunningUtil;
import org.bcia.julongchain.node.entity.NodeServer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * test DockerVM
 *
 * @author wanliangbing
 * @date 18-9-4
 * @company Dingxuan
 */
public class DockerVMTest {

    @Test
    public void deploy() throws VMException {
        String smartContractId = "mycc";
        String version = "1.0";
        DockerVM dockerVM = new DockerVM();
        dockerVM.setSmartContractId(smartContractId);
        dockerVM.setVersion(version);
        String imageName = dockerVM.getImageName();
        String containerName = dockerVM.getContainerName();
        Assert.assertTrue(DockerUtil.listImages(imageName).size() == 0);
        Assert.assertTrue(DockerUtil.listContainers(containerName).size() == 0);
        dockerVM.deploy();
        Assert.assertTrue(DockerUtil.listImages(imageName).size() > 0);
        Assert.assertTrue(DockerUtil.listContainers(containerName).size() > 0);
    }

    @Test
    public void start() throws VMException {
        String smartContractId = "mycc";
        String version = "1.0";
        DockerVM dockerVM = new DockerVM();
        dockerVM.setSmartContractId(smartContractId);
        dockerVM.setVersion(version);
        Assert.assertTrue(DockerUtil.listImages(dockerVM.getImageName()).size() > 0);
        String containerName = dockerVM.getContainerName();
        Assert.assertTrue(DockerUtil.listContainers(containerName).size() > 0);
        String containerStatus = DockerUtil.getContainerStatus(containerName);
        Assert.assertTrue(StringUtils.isNotEmpty(containerStatus));
        Assert.assertEquals(containerStatus, "Created");
        dockerVM.start();
        SmartContractRunningUtil.addStreamObserver(smartContractId, null);
        containerStatus = DockerUtil.getContainerStatus(containerName);
        Assert.assertTrue(StringUtils.isNotEmpty(containerStatus));
        Assert.assertEquals(containerStatus, "Running");
    }

    @Test
    public void stop() throws VMException {
        String smartContractId = "mycc";
        String version = "1.0";
        DockerVM dockerVM = new DockerVM();
        dockerVM.setSmartContractId(smartContractId);
        dockerVM.setVersion(version);
        Assert.assertTrue(DockerUtil.listImages(dockerVM.getImageName()).size() > 0);
        String containerName = dockerVM.getContainerName();
        Assert.assertTrue(DockerUtil.listContainers(containerName).size() > 0);
        String containerStatus = DockerUtil.getContainerStatus(containerName);
        Assert.assertTrue(StringUtils.isNotEmpty(containerStatus));
        Assert.assertEquals(containerStatus, "Running");
        dockerVM.start();
        containerStatus = DockerUtil.getContainerStatus(containerName);
        Assert.assertTrue(StringUtils.isNotEmpty(containerStatus));
        Assert.assertEquals(containerStatus, "Exited");
    }

}