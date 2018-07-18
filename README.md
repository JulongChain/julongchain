## 聚龙链（JulongChain）平台
聚龙链平台是一个基于Java的开源联盟链区块链平台。


## 链接
[Jira任务管理系统](http://jira.bcia.net.cn:8082/)

[Gitlab代码库管理系统](http://gitlab.bcia.net.cn:6060/)

## 如何编译
使用IntelliJ IDEA开发环境。

使用maven进行编译。

JDK版本为1.8。

## 当前版本
当前版本为0.8。

## 开源说明 <a name="license"></a>

聚龙链平台使用Apache License, Version 2.0 (Apache-2.0), located in the [LICENSE](LICENSE) file.

##命令行支持

启动Consenter服务  <br/>
<i>  java -jar julongchain.jar consenter start  </i>

启动Node服务  <br/>
<i>  java -jar julongchain.jar node start  </i>

查询Node服务状态  <br/>
<i>  java -jar julongchain.jar node status  </i>

创建群组  <br/>
<i>  java -jar julongchain.jar group create -c 127.0.0.1:7050 -g myGroup  </i><br/>
  参数说明：  <br/> 
     -c 共识节点地址  <br/>
     -g 群组名称     <br/>

加入群组  <br/>
<i>  java -jar julongchain.jar group join -t 127.0.0.1:7051 -b /opt/BCIA/JavaChain/myGroup.block  </i><br/>
  参数说明：  <br/>
      -t 要加入群组的目标节点地址 <br/>
      -b 创世区块保存的文件地址  <br/>
   
列出已加入的群组  <br/>
<i>  java -jar julongchain.jar group list -t 127.0.0.1:7051  </i><br/>
  参数说明：  <br/>
      -t 要查询的目标节点地址 <br/>

安装智能合约  <br/>
<i>  java -jar julongchain.jar contract install -t 127.0.0.1:7051 -n mycc -v 1.0 -p /root/julongchain/mycc_src  </i><br/>
  参数说明：  <br/>
   -t 要安装智能合约的目标节点地址 <br/>
   -n 智能合约名称      <br/>
   -v 智能合约版本      <br/>
   -p 智能合约源码路径   <br/>
   
实例化智能合约  <br/>
<i>  java -jar julongchain.jar contract instantiate -t 127.0.0.1:7051 -c 127.0.0.1:7050 -g myGroup -n mycc -v 1.0  -i 
"{'args':['init','a','100','b','200']}" -P "OR	('Org1MSP.member','Org2MSP.member')"  </i><br/>
  参数说明：  <br/>
   -t 要实例化智能合约的目标节点地址 <br/>
   -c 共识节点地址             <br/>
   -g 群组名称                <br/>
   -n 智能合约名称             <br/>
   -v 智能合约版本             <br/>
   -i 智能合约init方法入参      <br/>
   -P 背书策略                 <br/>
   
调用智能合约  <br/>
<i>  java -jar julongchain.jar contract invoke -t 127.0.0.1:7051 -c 127.0.0.1:7050 -g myGroup -n mycc -i "{'args':['invoke','a','b',
'10']}"  </i><br/>
  参数说明：  <br/>
   -t 要执行智能合约的目标节点地址 <br/>
   -c 共识节点地址               <br/>
   -g 群组名称                  <br/>
   -n 智能合约名称               <br/>
   -i 智能合约invoke方法入参   <br/>
   
##Docker Daemon启动
```
service docker stop
dockerd -H tcp://0.0.0.0:2375
```


