package org.bcia.julongchain.core.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.junit.Assert;
import org.junit.Test;

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
        String dockerFilePath = "src/main/java/org/bcia/julongchain/images/baseos/test.in";
        String tag = "test-image1";
        DockerUtil.buildImage(
                dockerFilePath, tag);
        List<String> list = DockerUtil.listImages(tag);
        Assert.assertEquals(list.get(0), tag);
    }

    @Test
    public void testListContainer() {
        List<String> strings = DockerUtil.listContainers("test");
        System.out.println(strings);
    }

    @Test
    public void testStartContainer() {
        DockerUtil.startContainer("f62387bd60db495cf40aa84ebe831a023d4a6030010c903dbbc5fe5c7a883529");
    }

    @Test
    public void testCreateContainer() {
        String imageName = "testImage";
        List<String> imageTags = DockerUtil.listImages(imageName);
        String imageId = imageTags.get(0);
        String name = "testContainer";
        DockerUtil.createContainer(imageId, name, "/bin/bash");
        List<String> containers = DockerUtil.listContainers(name);
        Assert.assertEquals(containers.get(0), name);
    }

    @Test
    public void testListImages() {
        String imageName = "test-image1:latest";
        List<String> list = DockerUtil.listImages(imageName);
        System.out.println(list.toString());
        Assert.assertNotNull(list);
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(list.get(0), imageName);
    }


}
