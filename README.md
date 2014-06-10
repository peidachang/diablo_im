# 一个基于netty实现的NIO消息通信组件

------

这是自己写的第一个关于NIO方面的组件，来到新单位后，由于项目紧张被赶驴上架去折腾这样一个东西，很多东西都还是不懂只能写到哪算哪了。

### 一 主要依赖的包
* netty-all NIO的基础框架
* tair-client  作为进程间的通信
* fastjson json的转化工具
* guava 辅助的工具包
* slf4j-api、logback-classic、logback-core 日志组件
* commons-lang、commons-collections 
* commons-codec 序列化工具包

------

### 二 如何启动

因为外部依赖了tair，所以需要一个集中式的缓存，这个版本是采用了tair。
>服务端启动

```java
   public static void main(String[] args) throws Exception {
        LogUtils.initLogback();
        ServerFacade wsServerFacade = new ServerFacade();
        wsServerFacade.init();
    }
```

>客户端启动

```java
public static void main(String[] args) {
        LogUtils.initLogback();

        MessageVo messageVo = new MessageVo();
        messageVo.setType(RequestTypeEnum.MESSAGE_TRANSFER.getType());
        messageVo.setFromUserId("13588701563");
        messageVo.setToUserId("13588701564");
        messageVo.setMsg("hehe11");
        WSClientFacade.getInstance().withDefaultTairManager(new     DefaultTairManager()).send(messageVo);//DefaultTairManager为tair中代码
    }
```

------

### 三 辅助的压力测试工具
tsung，一个很强大的压测工具，最新版本支持对于websocket的压测
具体的使用：[使用介绍](http://www.inter12.org/archives/867)

------

### 四 参考资料
1. http://netty.io/wiki/index.html



