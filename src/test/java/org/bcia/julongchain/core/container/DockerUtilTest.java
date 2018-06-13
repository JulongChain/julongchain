package org.bcia.julongchain.core.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.command.LogContainerResultCallback;
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
  public void buildBaseImage() {
    DockerUtil.buildImage(
        "src/main/java/org/bcia/julongchain/images/baseos/Dockerfile.in", "julongchain-baseimage");
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
