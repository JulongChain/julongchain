package org.bcia.julongchain.common.deliver;

import io.grpc.stub.StreamObserver;
import org.bcia.julongchain.common.util.FileUtils;
import org.bcia.julongchain.node.common.client.DeliverClient;
import org.bcia.julongchain.node.common.client.IDeliverClient;
import org.bcia.julongchain.protos.common.Common;
import org.bcia.julongchain.protos.consenter.Ab;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * deliver服务处理测试
 *
 * @author zhangmingyang
 * @date 2018/07/03
 * @company Dingxuan
 */
public class DeliverHandlerTest {

    @Test
    public void delivrBlocks() {
        String ip="localhost";
        int port=7050;
        String groupId="myGroup";
        System.out.println(("getGenesisBlock begin"));
        IDeliverClient deliverClient = new DeliverClient(ip, port);
        deliverClient.getSpecifiedBlock(groupId, 0L, new StreamObserver<Ab.DeliverResponse>() {
            @Override
            public void onNext(Ab.DeliverResponse value) {
                System.out.println(("Deliver onNext"));
                deliverClient.close();

                if (value.hasBlock()) {
                    Common.Block block = value.getBlock();
                    try {
                        FileUtils.writeFileBytes(groupId + ".block", block.toByteArray());

                        File file = new File(groupId + ".block");
                        System.out.println(("file is generated2-----$" + file.getCanonicalPath()));
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                } else {
                    System.out.println(("Deliver status:" + value.getStatus().getNumber()));
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                deliverClient.close();
            }

            @Override
            public void onCompleted() {
                System.out.println(("Deliver onCompleted"));
                deliverClient.close();
            }
        });
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}