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
package org.bcia.julongchain.core.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.bcia.julongchain.common.log.JavaChainLog;
import org.bcia.julongchain.common.log.JavaChainLogFactory;
import org.bcia.julongchain.core.node.NodeConfigFactory;

import java.io.File;
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

  public static DockerClient getDockerClient() {
    // tcp://localhost:2375
    String endpoint = NodeConfigFactory.getNodeConfig().getVm().getEndpoint();
    DockerClient dockerClient = DockerClientBuilder.getInstance(endpoint).build();
    return dockerClient;
  }

  public static void closeDockerClient(DockerClient dockerClient) {
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
            .withDockerfile(new File(dockerFilePath))
            .withTags(Sets.newHashSet(tag))
            .exec(callback)
            .awaitImageId();

    closeDockerClient(dockerClient);

    logger.info("build image success, imageId:" + imageId);

    return imageId;
  }

  /**
   * 从docker hub中查找镜像
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

  /**
   * list images
   *
   * @param imageName 镜像名称
   * @return
   */
  public static List<String> listImages(String imageName) {
    List<String> imageNameList = new ArrayList<String>();
    DockerClient dockerClient = getDockerClient();
    List<Image> imageList = dockerClient.listImagesCmd().exec();
    for (Image image : imageList) {
      String imageTag = image.getRepoTags()[0];
      if (StringUtils.isEmpty(imageName) || StringUtils.contains(imageTag, imageName)) {
        imageNameList.add(imageTag);
      }
    }
    closeDockerClient(dockerClient);
    return imageNameList;
  }

  /**
   * list containers
   *
   * @param name 容器名称
   * @return
   */
  public static List<String> listContainers(String name) {
    // Show only containers with the passed status (created|restarting|running|paused|exited).
    List<String> result = new ArrayList<String>();
    DockerClient dockerClient = getDockerClient();
    List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).exec();
    for (Container container : containerList) {
      if (StringUtils.isEmpty(name) || StringUtils.contains(container.getNames()[0], name)) {
        result.add(container.getId());
      }
    }
    closeDockerClient(dockerClient);
    return result;
  }

  public static String createContainer(String imageId, String containerName, String... cmdStr) {
    String containerId =
        getDockerClient()
            .createContainerCmd(imageId)
            .withName(containerName)
            .withCmd(cmdStr)
            .exec()
            .getId();
    logger.info("container ID:" + containerId);
    return containerId;
  }

  public static void startContainer(String containerId) {
    logger.info("start container, ID:" + containerId);
    getDockerClient().startContainerCmd(containerId).exec();
  }

  public static void stopContainer(String containerId) {
    getDockerClient().stopContainerCmd(containerId).exec();
  }
}
