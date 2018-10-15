## 聚龙链（JulongChain）平台
聚龙链平台是一个基于Java的开源联盟链区块链平台。


## 链接
[Jira任务管理系统](http://jira.bcia.net.cn:8082/)

[Gitlab代码库管理系统](http://gitlab.bcia.net.cn:6060/)

## 如何编译
编译环境：  <br/>
具体的环境配置请见相关文档  <br/>
集成开发环境：IntelliJ IDEA 2017.3.3  <br/>
JAVA 开发环境：JAVA JDK 1.8.0_151  <br/>
Maven依赖管理工具：Maven 3.5.2  <br/>
代码版本管理工具：GitLab  <br/>

编译步骤：  <br/>
1.从GitLab下载项目源码：打开IntelliJ IDEA,选择菜单File => New => Project from Version Control => Git  <br/>
  设置好文件夹和路径,输入Git Repository URL点后面的Test按钮测试链接成功后,点击Clone开始下载源码  <br/>
  Git Repository URL:ssh://git@gitlab.bcia.net.cn:13622/bcia/julongchain.git  <br/>
  
2.添加框架支持：项目名字右键 => Add Framework Support,Java EE version选择Java EE 8,勾选Maven选项  <br/>

3.设置Project环境：选择菜单File => Project Structure… => Project,Project SDK选择已安装的JDK 1.8  <br/>
  Project language level 修改为8  <br/>
  
4.Maven导入依赖包：pom.xml右键 => Maven => Reimport  <br/>

5.编译：在Maven Projects中展开julongchain => Lifecycle,选择双击compile编译  <br/>
<br/>



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
<i>  java -jar julongchain.jar group join -t 127.0.0.1:7051 -b /opt/BCIA/JulongChain/myGroup.block  </i><br/>
  参数说明：  <br/>
      -t 要加入群组的目标节点地址 <br/>
      -b 创世区块保存的文件地址  <br/>
   
列出已加入的群组  <br/>
<i>  java -jar julongchain.jar group list -t 127.0.0.1:7051  </i><br/>
  参数说明：  <br/>
      -t 要查询的目标节点地址 <br/>

查询当前群组链信息<br/>
         <i>  java -jar julongchain.jar group info -t 127.0.0.1:7051 -g myGroup  </i><br/>
   参数说明：  <br/>
      -t 要查询的目标节点地址 <br/>
      -g 群组名称           <br/>

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

查询智能合约（不产生新的区块）  <br/>
<i>  java -jar julongchain.jar contract query -t 127.0.0.1:7051 -g myGroup -n mycc -i "{'args':['query','a']}"  </i><br/>
  参数说明：  <br/>
   -t 要查询智能合约的目标节点地址 <br/>
   -g 群组名称                  <br/>
   -n 智能合约名称               <br/>
   -i 智能合约invoke方法入参      <br/>
123
