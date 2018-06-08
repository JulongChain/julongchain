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
package org.bcia.julongchain.events.consumer;

import org.bcia.julongchain.protos.node.EventsPackage;
import org.bouncycastle.asn1.x509.Certificate;

import java.sql.Timestamp;
import java.util.List;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/05/20
 * @company Dingxuan
 */
public class RegistrationConfig {
    private List<EventsPackage.Interest> InterestedEvents;
    private Timestamp timestamp;
    private Certificate x509Certificate;

    public List<EventsPackage.Interest> getInterestedEvents() {
        return InterestedEvents;
    }

    public void setInterestedEvents(List<EventsPackage.Interest> interestedEvents) {
        InterestedEvents = interestedEvents;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Certificate getX509Certificate() {
        return x509Certificate;
    }

    public void setX509Certificate(Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
    }
}
