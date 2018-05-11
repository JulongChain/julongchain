/**
 * Copyright Dingxuan. All Rights Reserved.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.core.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.google.common.collect.Sets;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用docker-java client连接和运行docker命令, docker要以dockerd -H tcp://0.0.0.0:2375方式运行,
 * 参考：https://github.com/docker-java/docker-java/wiki
 *
 * @author wanliangbing
 * @date 2018/5/11
 * @company Dingxuan
 */
public class DockerUtil {

  private static JavaChainLog logger = JavaChainLogFactory.getLog(DockerUtil.class);

  /** docker host ip */
  private static final String DOCKER_HOST_IP = "192.168.1.211";
  /** docker host port */
  private static final String DOCKER_HOST_PORT = "2375";

  private static DockerClient getDockerClient() {
    DockerClient dockerClient =
        DockerClientBuilder.getInstance("tcp://" + DOCKER_HOST_IP + ":" + DOCKER_HOST_PORT).build();
    return dockerClient;
  }

  private static void closeDockerClient(DockerClient dockerClient) {
    try {
      dockerClient.close();
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }

  /**
   * 创建Docker镜像
   *
   * @param dockerFilePath dockerFile文件路径
   * @param tag 镜像的标签
   * @return
   */
  public static String buildImage(String dockerFilePath, String tag) {
    DockerClient dockerClient = getDockerClient();

    BuildImageResultCallback callback =
        new BuildImageResultCallback() {
          @Override
          public void onNext(BuildResponseItem item) {
            logger.info(item.toString());
            super.onNext(item);
          }
        };

    String imageId =
        dockerClient
            .buildImageCmd()
            .withDockerfilePath(dockerFilePath)
            .withTags(Sets.newHashSet(tag))
            .exec(callback)
            .awaitImageId();

    closeDockerClient(dockerClient);

    logger.info("build image success, imageId:" + imageId);

    return imageId;
  }

  /**
   * 查找镜像
   *
   * @param imageName 镜像名称
   * @return
   */
  public static List<String> searchImages(String imageName) {
    DockerClient dockerClient = getDockerClient();

    List<SearchItem> searchImageItemList = dockerClient.searchImagesCmd(imageName).exec();

    List<String> searchImageNameList = new ArrayList<String>();

    for (SearchItem searchItem : searchImageItemList) {
      searchImageNameList.add(searchItem.getName());
    }

    closeDockerClient(dockerClient);

    return searchImageNameList;
  }

  public static void createContainer(String imageId) {}

  public static void main(String[] args) {
    List<String> list = searchImages("ubuntu");
    for (String s : list) {
      System.out.println(s);
    }
  }
}
