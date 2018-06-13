/**
 * Copyright DingXuan. All Rights Reserved.
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
package org.bcia.julongchain.consenter.common.broadcast.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;

import javax.jms.*;

/**
 * @author zhangmingyang
 * @Date: 2018/3/13
 * @company Dingxuan
 */
public class Sender {
  //  private static final int SEND_NUMBER = 5;

//    public static void main(String[] args) {
//        // ConnectionFactory ：连接工厂，JMS 用它创建连接
//        ConnectionFactory connectionFactory; // Connection ：JMS 客户端到JMS
//        // Provider 的连接
//        Connection connection = null;
//        // Session： 一个发送或接收消息的线程
//        Session session;
//        // Destination ：消息的目的地;消息发送给谁.
//        Destination destination;
//        // MessageProducer：消息发送者
//        MessageProducer producer; // TextMessage message;
//        // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
//        connectionFactory = new ActiveMQConnectionFactory(
//                ActiveMQConnection.DEFAULT_USER,
//                ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.1.107:61616");
//        try { // 构造从工厂得到连接对象
//            connection = connectionFactory.createConnection();
//            // 启动
//            connection.start();
//            // 获取操作连接
//            session = connection.createSession(Boolean.TRUE,
//                    Session.AUTO_ACKNOWLEDGE);
//            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
//            destination = session.createQueue("FirstQueue");
//            // 得到消息生成者【发送者】
//            producer = session.createProducer(destination);
//            // 设置不持久化，此处学习，实际根据项目决定
//            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//            // 构造消息，此处写死，项目就是参数，或者方法获取
//         Common.Envelope envelope=Common.Envelope.newBuilder().setPayload(ByteString.copyFrom("aabbccdd".getBytes())).build();
//
//            //Map<String, Object> toEncryptMap=new HashMap<String, Object>();
//       //     toEncryptMap.put("id",123);
//        //    toEncryptMap.put("value",envelope);
//            //toEncryptMap.put("biz","123");
//            byte[] data=envelope.toByteArray();
//            sendMessage(session, producer,data);
//            session.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (null != connection) {
//                    connection.close();
//                }
//            } catch (Throwable ignore) {
//            }
//        }
//    }
//
//    public static void sendMessage(Session session, MessageProducer producer,Object env)
//            throws Exception {
////        for (int i = 1; i <= SEND_NUMBER; i++) {
//            //session.createBytesMessage();
//            //BytesMessage message=session.createBytesMessage();
//         ActiveMQObjectMessage message = (ActiveMQObjectMessage) session.createObjectMessage();
//      // byte[] data=  message.setObject((Serializable) env);
//        //message.writeBytes(envelope.toByteArray());
//            //message.readBytes();
//           // TextMessage message = session.createTextMessage("ActiveMq 发送的消息");
//
//            // 发送消息到目的地方
//
//            System.out.println("发送消息：" + "ActiveMq 发送的消息" +message);
//            producer.send(message);
//        }
    //}


    public static  void sendMessage(byte[] data){
        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;
        Destination destination;
        MessageProducer producer;
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.1.107:61616");
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("FirstQueue");
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            ActiveMQObjectMessage message = (ActiveMQObjectMessage) session.createObjectMessage();
            message.setObject(data);
            // 发送消息到目的地方
            producer.send(message);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection) {
                    connection.close();
                }
            } catch (Throwable ignore) {
            }
        }
    }
}
