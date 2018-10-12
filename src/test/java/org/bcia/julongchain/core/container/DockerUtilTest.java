package org.bcia.julongchain.core.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.List;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/05/29
 * @company Dingxuan
 */
public class DockerUtilTest {

    @Test
    public void testCreateImages() {
        String dockerFilePath = "src/main/java/org/bcia/julongchain/images/baseos/test";
        String tag = "test-image:latest";
        DockerUtil.buildImage(dockerFilePath, tag);
        List<String> list = DockerUtil.listImages(tag);
        Assert.assertEquals(list.get(0), tag);
        DockerUtil.removeImage(tag);
    }

    @Test
    public void testListContainer() {
        String imageId = "test-image:latest";
        String containerId = "test-container";
        List<String> list = DockerUtil.listImages(imageId);
        if (CollectionUtils.isNotEmpty(list)) {
            DockerUtil.removeImage(imageId);
        }
        String dockerFilePath = "src/main/java/org/bcia/julongchain/images/baseos/test";
        DockerUtil.buildImage(dockerFilePath, imageId);
        DockerUtil.createContainer(imageId, containerId);
        List<String> strings = DockerUtil.listContainers(containerId);
        Assert.assertTrue(strings.size() > 0);
        DockerUtil.removeContainer(containerId);
        DockerUtil.removeImage(imageId);
    }

    @Test
    public void testStartContainer() {
        String imageId = "test-image:latest";
        String containerId = "test-container";
        List<String> list = DockerUtil.listImages(imageId);
        if (CollectionUtils.isNotEmpty(list)) {
            DockerUtil.removeImage(imageId);
        }
        String dockerFilePath = "src/main/java/org/bcia/julongchain/images/baseos/test";
        DockerUtil.buildImage(dockerFilePath, imageId);
        DockerUtil.createContainer(imageId, containerId);
        List<String> strings = DockerUtil.listContainers(containerId);
        Assert.assertTrue(strings.size() > 0);
        DockerUtil.startContainer(containerId);
        String containerStatus = DockerUtil.getContainerStatus(containerId);
        Assert.assertTrue(StringUtils.contains(containerStatus, "Up"));
        DockerUtil.stopContainer(containerId);
        DockerUtil.removeContainer(containerId);
        DockerUtil.removeImage(imageId);
    }

    @Test
    public void testCreateContainer() {
        String imageId = "test-image:latest";
        String containerId = "test-container";
        List<String> list = DockerUtil.listImages(imageId);
        if (CollectionUtils.isNotEmpty(list)) {
            DockerUtil.removeImage(imageId);
        }
        String dockerFilePath = "src/main/java/org/bcia/julongchain/images/baseos/test";
        DockerUtil.buildImage(dockerFilePath, imageId);
        DockerUtil.createContainer(imageId, containerId);
        List<String> strings = DockerUtil.listContainers(containerId);
        Assert.assertTrue(strings.size() > 0);
        DockerUtil.removeContainer(containerId);
        DockerUtil.removeImage(imageId);
    }

    @Test
    public void testListImages() {
        String imageId = "test-image:latest";
        List<String> list = DockerUtil.listImages(imageId);
        if (CollectionUtils.isNotEmpty(list)) {
            DockerUtil.removeImage(imageId);
        }
        String dockerFilePath = "src/main/java/org/bcia/julongchain/images/baseos/test";
        DockerUtil.buildImage(dockerFilePath, imageId);
        List<String> list1 = DockerUtil.listImages(imageId);
        DockerUtil.removeImage(imageId);
        Assert.assertNotNull(list1);
        Assert.assertEquals(list1.size(), 1);
        Assert.assertEquals(list1.get(0), imageId);
    }

}
