package org.bcia.javachain.core.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.bcia.javachain.common.localmsp.impl.LocalSignerTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/05/29
 * @company Dingxuan
 */
public class DockerUtilTest {

  @Test
  public void buildBaseImage() {
    DockerUtil.buildImage(
        "src/main/java/org/bcia/javachain/images/baseos/Dockerfile.in", "javachain-baseimage");
  }

  @Test
  public void logC() {
    List<String> list = DockerUtil.listContainers("");
    System.out.println(list.toString());

    DockerClient dockerClient = DockerUtil.getDockerClient();
    LogContainerResultCallback exec = dockerClient.logContainerCmd(list.get(0)).withTailAll().exec(new
        LogContainerResultCallback());

  }

}
