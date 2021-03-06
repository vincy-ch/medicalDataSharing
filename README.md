# medicalDataSharing
代码是关于基于fabric区块链的医疗数据共享系统

# 一、运行步骤
以阿里云环境，介绍代码具体使用：

0. 首先要把fabric1.4环境装好，具体可以见[这个博客](https://blog.csdn.net/weixin_42787605/article/details/121772885)
1. 进入阿里云后，下载本代码到某文件夹下 我是放在了 ~/go/src下面
2. 每次重新启动阿里云服务器都需要注释/etc/resolv.conf文件里的这句话，否则网络会启动失败

```bash
vi /etc/resolv.conf
# 注释掉下面这句话
options timeout:2 attempts:3 rotate single-request-reopen
```
3. 清理环境，为防止之前启动的docker容器和网络影响这次运行，首先要删除之前的容器和网络(**要记得每次重新启动网络时都需要进行这一步**)
```bash
# 进入目录medical-data
cd medicalDataSharing/fabric/scripts/fabric-samples/medical-data
# 查看容器
docker ps
# 清理容器 
docker rm -f $(docker ps -aq) # 或者直接调用  ./stopFabric.sh
# 查看网络
docker network ls
# 清理网络
docker network prune
# 然后输入y
```

4. 进入目录medical-data，执行启动脚本

```bash
./startFabric.sh java
```
这个脚本主要包括：
  1. 使用first-network网络的bysh.sh脚本：创建peer、orderer、ca等各个容器+启动first-network网络+创建通道+把节点加入通道
  2. 在节点上安装链码+链码初始化
参数java表示我们使用的链码使用java语言写的，这个参数决定了链码的存放目录，这个链码只有java版本的
更具体的内容可以直接看脚本内容


5. 启动区块链网络的时候，可以通过下面这个命令查看所有容器的日志，最好是另外打开一个窗口看日志

进入目录commercial-paper/organization/magnetocorp/configuration/cli，输入下面的命令，可以同时汇总并监视各个容器的日志

```bash
cd go/src/medicalDataSharing/fabric/scripts/fabric-samples/commercial-paper/organization/magnetocorp/configuration/cli/
./monitordocker.sh net_byfn
```
6. 当出现下面这段话后，说明区块链网络启动成功，而且链码安装并初始化成功
<img src="https://img-blog.csdnimg.cn/b0af6e759e58431dad44b78d18904186.png" width="80%">



7. 启动服务端

```bash
cd java
mvn test
```
下面这样就是服务端启动成功了

<img src="https://img-blog.csdnimg.cn/50ec16276e1d4ea1a9b3bcb0cb0ae0f6.png" width="80%">

[这个网址](http://39.96.201.238:5984/_utils/)可以看到数据库，最下面就是我们新建的表，链码在初始化的时候，创建了100个用户
<img src="https://img-blog.csdnimg.cn/b6972f4de69146979f0e5f9623a4b7e4.png" width="80%">


8. http服务端启动成功之后，可以通过客户端直接访问服务端

访问参数可以看medicalDataSharing\fabric\scripts\fabric-samples\medical-data\java\src\main\java\org\example\HttpServer.java文件里对参数的解析

访问方法有两种，第一种是直接通过[输入url](http://post.jsonin.com/)访问，如下图：
<img src="https://img-blog.csdnimg.cn/4146a10cbd6040909e968646a3bf159f.png" width="80%">

第二种是通过代码访问，具体代码在medicalDataSharing\fabric\scripts\fabric-samples\medical-data\java\src\main\java\org\example\HttpClient.java，把url替换成自己阿里云的url就可以了，直接运行main函数，下图是运行了创建用户的方法，返回了用户的id是100


<img src="https://img-blog.csdnimg.cn/fa2180a4d9b64050b525e36e74bf36aa.png" width="80%">

# 二、功能介绍
分为两部分功能，主要是通过fabric实现的区块链
# 三、代码介绍
代码是关于基于fabric区块链的医疗数据共享系统，系统中主要有两个模块，加密魔模块和区块链模块：


<img src="https://img-blog.csdnimg.cn/6ec200f78c554b06b1c8cf04d1a599b8.png" width="80%">

## 1. 区块链模块介绍
区块链有分为两个组织，共识机制采用的是kafka


<img src="https://img-blog.csdnimg.cn/f4b81f6a249c49479e6db73d356145ad.png" width="50%">

## 2. 加密模块介绍
加密使用的是cp-abe，是在[此人](http://github.com/junwei-wang)源码的基础之上，加了三个功能：
1. 对文件分级解密
2. 将属性授权中心拆分成两个，避免密钥托管，两个中心交互生成用户私钥，其实应该这两个授权中心放在两个节点上，但为了方便，我把它还是放在一个节点上
3. 在密钥生成过程中，加入用户生成的随机数，授权中心返回给用户的是伪密钥，只有用户知道随机数，可以把它恢复成真实密钥
