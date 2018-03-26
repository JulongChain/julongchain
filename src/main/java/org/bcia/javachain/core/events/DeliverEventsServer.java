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
package org.bcia.javachain.core.events;

import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.node.EventsPackage;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/3/21
 * @company Dingxuan
 */
public class DeliverEventsServer implements IDeliverEventsServer {
    private IDeliverHandler deliverHandler;

    @Override
    public EventsPackage.DeliverResponse deliverFiltered(Common.Envelope envelope) {
        return deliverHandler.handle(envelope);
    }

    @Override
    public EventsPackage.DeliverResponse deliver(Common.Envelope envelope) {
        return deliverHandler.handle(envelope);
    }
}


//    DeliverFiltered(Common.Envelope envelope) error {
//        logger.Debugf("Starting new DeliverFiltered handler")
//        defer dumpStacktraceOnPanic()
//        srvSupport := &deliverFilteredBlockSupport{
//            Deliver_DeliverFilteredServer: srv,
//        }
//        // getting policy checker based on resources.FILTEREDBLOCKEVENT resource name
//        return s.dh.Handle(deliver.NewDeliverServer(srvSupport, s.policyCheckerProvider(resources.FILTEREDBLOCKEVENT), s.sendProducer(srv)))
//    }
//
//    // Deliver sends a stream of blocks to a client after commitment
//    func (s *server) Deliver(srv peer.Deliver_DeliverServer) error {
//        logger.Debugf("Starting new Deliver handler")
//        defer dumpStacktraceOnPanic()
//        srvSupport := &deliverBlockSupport{
//            Deliver_DeliverServer: srv,
//        }
//        // getting policy checker based on resources.BLOCKEVENT resource name
//        return s.dh.Handle(deliver.NewDeliverServer(srvSupport, s.policyCheckerProvider(resources.BLOCKEVENT), s.sendProducer(srv)))
//    }
//
//    func (s *server) sendProducer(srv peer.Deliver_DeliverFilteredServer) func(msg proto.Message) error {
//        return func(msg proto.Message) error {
//            response, ok := msg.(*peer.DeliverResponse)
//            if !ok {
//                logger.Errorf("received wrong response type, expected response type peer.DeliverResponse")
//                return errors.New("expected response type peer.DeliverResponse")
//            }
//            return srv.Send(response)
//        }
//    }
//}
