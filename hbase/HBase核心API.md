## HBase核心API


#### Connection对象
- Connection代表对集群的连接对象，封装了与实际服务器的低级别单独连接以及与zookeeper的连接。
- Connection可以通过ConnectionFactory类实例化。
- Connection的生命周期由调用者管理，使用完毕后需要执行close()以释放资源。
- Connection是线程安全的，多个Table和Admin可以共用同一个Connection对象。因此一个客户端只需要实例化一个连接即可。
反之，Table和Admin不是线程安全的！因此不建议并缓存或池化这两种对象
- Connection的实例化是重量级的，而Table和Admin对象的创建是轻量级的

> 注意：一个客户端只需要实例化一个连接，并不意味着是单例模式。也就是说，在客户端如果实例化两次，会创建两个Connection对象出来