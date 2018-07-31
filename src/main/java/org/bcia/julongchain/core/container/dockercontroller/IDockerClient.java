/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.julongchain.core.container.dockercontroller;

/**
 * dockerClient represents a docker client
 *
 * @author wanliangbing
 * @date 2018/4/2
 * @company Dingxuan
 */
public interface IDockerClient {

    /** CreateContainer creates a docker container, returns an error in case
     *
    of failure
     */
    String createContainer(String opts);

    /** UploadToContainer uploads a tar archive to be extracted to a path in the
     * filesystem of the container.
     * @param id
     * @param opts
     */
    void uploadToContainer(String id, String opts);
    /** StartContainer starts a docker container, returns an error in case of
     * failure
     */
    void startContainer(String id, String cfg);
    /** AttachToContainer attaches to a docker container, returns an error in
     * case of failure
     */
    void attachToContainer(String opts);
    /** BuildImage builds an image from a tarball's url or a Dockerfile in the
     * inputstream, returns an error in case of failure
    */
    void buildImage(String opts);

    /** RemoveImageExtended removes a docker image by its name or ID, returns an
     * error in case of failure
     * @param id
     * @param opts
     */
    void removeImageExtended(String id, String opts);
    /** StopContainer stops a docker container, killing it after the given
     * timeout (in seconds). Returns an error in case of failure
     */
    void stopContainer(String id, Long timeout);

    /** KillContainer sends a signal to a docker container, returns an error in
     * case of failure
     * @param opts
     */
    void KillContainer(String opts);
    /** RemoveContainer removes a docker container, returns an error in case
     * of failure
     */
    void removeContainer(String opts);

}
