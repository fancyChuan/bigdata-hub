## ZooKeeper内部原理

#### 节点类型
- 持久节点、临时节点
- 有序节点、无序节点

> 在分布式系统中，顺序号可以被用于为所有的事件进行全局排序，这样客户端可以通过顺序号来推断事件的顺序

#### 监听器原理
![image](img/监听器原理.png)

#### Paxos算法(拓展)
这是一种基于消息传递且具有高度容错特性的一致性算法

分布式系统中的节点通信存在两种模型：
- 共享内存（Shared memory）
- 消息传递（Messages passing）
    - 可能存在的错误场景：进程可能会慢、被杀死或者重启，消息可能会延迟、丢失、重复

```
Paxos算法流程中的每条消息描述如下：
1.	Prepare: Proposer生成全局唯一且递增的Proposal ID (可使用时间戳加Server ID)，向所有Acceptors发送Prepare请求，这里无需携带提案内容，只携带Proposal ID即可。
2.	Promise: Acceptors收到Prepare请求后，做出“两个承诺，一个应答”。
两个承诺：
a.	不再接受Proposal ID小于等于（注意：这里是<= ）当前请求的Prepare请求。
b.	不再接受Proposal ID小于（注意：这里是< ）当前请求的Propose请求。
一个应答：
c.	不违背以前做出的承诺下，回复已经Accept过的提案中Proposal ID最大的那个提案的Value和Proposal ID，没有则返回空值。
3.	Propose: Proposer 收到多数Acceptors的Promise应答后，从应答中选择Proposal ID最大的提案的Value，作为本次要发起的提案。如果所有应答的提案Value均为空值，则可以自己随意决定提案Value。然后携带当前Proposal ID，向所有Acceptors发送Propose请求。
4.	Accept: Acceptor收到Propose请求后，在不违背自己之前做出的承诺下，接受并持久化当前Proposal ID和提案Value。
5.	Learn: Proposer收到多数Acceptors的Accept后，决议形成，将形成的决议发送给所有Learners。

```

