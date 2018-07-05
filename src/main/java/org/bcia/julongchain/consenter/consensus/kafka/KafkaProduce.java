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
package org.bcia.julongchain.consenter.consensus.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.bcia.julongchain.consenter.util.Constant;
import org.bcia.julongchain.consenter.util.YamlLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * 类描述
 *
 * @author yangdong
 * @date 2018/3/19
 * @company Shudun
 */

public class KafkaProduce {
    private final Producer<String, String> producer;
   // public final static String TOPIC = "test2";



    Map map=(HashMap) YamlLoader.readYamlFile(Constant.ORDERER_CONFIG).get(Constant.KAFKA);



    private KafkaProduce(){
        StringBuffer sbBrokerHost=new StringBuffer();
        StringBuffer sbZookeeperHost=new StringBuffer();

        Map serversMap= (HashMap)map.get(Constant.SERVER);
        for(int i=1;i<=serversMap.size();i++){
            String index="brokerHost"+i;
            StringBuffer host=new StringBuffer((String)serversMap.get(index));
            sbBrokerHost=sbBrokerHost.append(host);
        }

        Map zookeeperMap= (HashMap)map.get(Constant.ZOOKEEPER);
        for(int i=1;i<=zookeeperMap.size();i++){
            String index="ZKHost"+i;
            StringBuffer host=new StringBuffer((String)zookeeperMap.get(index));
            sbZookeeperHost=sbZookeeperHost.append(host);
        }

        Properties props = new Properties();

        // 此处配置的是kafka的broker地址:端口列表
        // props.put("metadata.broker.list", "192.168.128.129:9092");
        props.put("zookeeper.connect", sbZookeeperHost.toString());//声明zk
        props.put("metadata.broker.list",sbBrokerHost.toString());// 声明kafka broker
        //配置value的序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        //配置key的序列化类
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");

        //request.required.acks
        //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
        //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
        //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
        props.put("request.required.acks","-1");

        producer = new Producer<String, String>(new ProducerConfig(props));
    }


    //发送消息到kafka服务端
    public void send(ProducerMessage message){
        String key = String.valueOf(message.getKey());
        String dataStr=new String(message.getValue());
        producer.send(new KeyedMessage<String, String>(message.getTopic(),key ,dataStr));
    }




    /*public void produce() {
        int messageNo = 1;
        final int COUNT = 101;

        int messageCount = 0;
        while (messageNo < COUNT) {
            String key = String.valueOf(messageNo);
            String data = "Hello kafka message luis :" + key;
            producer.send(new KeyedMessage<String, String>(TOPIC, key ,data));

            System.out.println(data);
            messageNo ++;
            messageCount++;
        }

        System.out.println("Producer端一共产生了" + messageCount + "条消息！");
    }*/

  /*  public static void main( String[] args )
    {
        new KafkaProduce().produce();
    }*/
  public static void main(String[] args) {
      KafkaProduce kk=new KafkaProduce();
  }
}
