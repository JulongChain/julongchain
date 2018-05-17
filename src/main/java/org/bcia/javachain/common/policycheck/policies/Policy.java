/**
 * Copyright Aisino. All Rights Reserved.
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

package org.bcia.javachain.common.policycheck.policies;

/**
 * 类描述
 *
 * @author yuanjun
 * @date 14/05/18
 * @company Aisino
 */
public class Policy {
    public static final String PathSeparator = "/";

    // ChannelPrefix is used in the path of standard channel policy managers
    public static final String ChannelPrefix = "Channel";

    // ApplicationPrefix is used in the path of standard application policy paths
    public static final String ApplicationPrefix = "Application";

    // OrdererPrefix is used in the path of standard orderer policy paths
    public static final String OrdererPrefix = "Orderer";

    // ChannelReaders is the label for the channel's readers policy (encompassing both orderer and application readers)
    public static final String ChannelReaders = PathSeparator + ChannelPrefix + PathSeparator + "Readers";

    // ChannelWriters is the label for the channel's writers policy (encompassing both orderer and application writers)
    public static final String ChannelWriters = PathSeparator + ChannelPrefix + PathSeparator + "Writers";

    // ChannelApplicationReaders is the label for the channel's application readers policy
    public static final String ChannelApplicationReaders = PathSeparator + ChannelPrefix + PathSeparator + ApplicationPrefix + PathSeparator + "Readers";

    // ChannelApplicationWriters is the label for the channel's application writers policy
    public static final String ChannelApplicationWriters = PathSeparator + ChannelPrefix + PathSeparator + ApplicationPrefix + PathSeparator + "Writers";

    // ChannelApplicationAdmins is the label for the channel's application admin policy
    public static final String ChannelApplicationAdmins = PathSeparator + ChannelPrefix + PathSeparator + ApplicationPrefix + PathSeparator + "Admins";

    // BlockValidation is the label for the policy which should validate the block signatures for the channel
    public static final String BlockValidation = PathSeparator + ChannelPrefix + PathSeparator + OrdererPrefix + PathSeparator + "BlockValidation";
}
