### oozie常用变量

常量表示形式 | 含义说明
--- | ---
${coord:minutes(int n)|返回日期时间：从一开始，周期执行n分钟
${coord:hours(int n)|返回日期时间：从一开始，周期执行n * 60分钟
${coord:days(int n)|返回日期时间：从一开始，周期执行n * 24 * 60分钟
${coord:months(int n)|返回日期时间：从一开始，周期执行n * M * 24 * 60分钟（M表示一个月的天数）
${coord:endOfDays(int n)|返回日期时间：从当天的最晚时间（即下一天）开始，周期执行n * 24 * 60分钟
${coord:endOfMonths(1)|返回日期时间：从当月的最晚时间开始（即下个月初），周期执行n * 24 * 60分钟
${coord:current(int n)|返回日期时间：从一个Coordinator动作（Action）创建时开始计算，第n个dataset实例执行时间
${coord:dataIn(String name)|在输入事件（input-events）中，解析dataset实例包含的所有的URI
${coord:dataOut(String name)|在输出事件（output-events）中，解析dataset实例包含的所有的URI
${coord:offset(int n, String timeUnit)|表示时间偏移，如果一个Coordinator动作创建时间为T，n为正数表示向时刻T之后偏移，n为负数向向时刻T之前偏移，timeUnit表示时间单位（选项有MINUTE、HOUR、DAY、MONTH、YEAR）
${coord:hoursInDay(int n)|指定的第n天的小时数，n>0表示向后数第n天的小时数，n=0表示当天小时数，n<0表示向前数第n天的小时数
${coord:daysInMonth(int n)|指定的第n个月的天数，n>0表示向后数第n个月的天数，n=0表示当月的天数，n<0表示向前数第n个月的天数
${coord:tzOffset()|ataset对应的时区与Coordinator Job的时区所差的分钟数
${coord:latest(int n)|最近以来，当前可以用的第n个dataset实例
${coord:future(int n, int limit)|当前时间之后的dataset实例，n>=0，当n=0时表示立即可用的dataset实例，limit表示dataset实例的个数
${coord:nominalTime()|nominal时间等于Coordinator Job启动时间，加上多个Coordinator Job的频率所得到的日期时间。例如：start=”2009-01-01T24:00Z”，end=”2009-12-31T24:00Z”，frequency=”${coord:days(1)}”，frequency=”${coord:days(1)}，则nominal时间为：2009-01-02T00:00Z、2009-01-03T00:00Z、2009-01-04T00:00Z、…、2010-01-01T00:00Z
${coord:actualTime()|Coordinator动作的实际创建时间。例如：start=”2011-05-01T24:00Z”，end=”2011-12-31T24:00Z”，frequency=”${coord:days(1)}”，则实际时间为：2011-05-01，2011-05-02，2011-05-03，…，2011-12-31
${coord:user()|启动当前Coordinator Job的用户名称
${coord:dateOffset(String baseDate, int instance, String timeUnit)|计算新的日期时间的公式：newDate = baseDate + instance * timeUnit，如：baseDate=’2009-01-01T00:00Z’，instance=’2′，timeUnit=’MONTH’，则计算得到的新的日期时间为’2009-03-01T00:00Z’。
${coord:formatTime(String timeStamp, String format)|格式化时间字符串，format指定模式


### Oozie配置

[oozie重要配置详解（生产）](https://blog.csdn.net/weixin_39478115/article/details/78879082)

完整配置如下：

```
< property >
    < name > oozie.output.compression.codec </ name >
    < value > gz </ value >
    < description >
        要使用的压缩编解码器的名称。
        编解码器的实现类需要通过另一个属性oozie.compression.codecs指定。
        你可以为oozie.compression.codecs指定一个逗号分隔的'Codec_name'='Codec_class'列表
        编解码器类实现了org.apache.oozie.compression.CompressionCodec接口。
        如果没有指定oozie.compression.codecs，则默认使用gz编解码器实现。
    </ description >
</ property >

< property >
    < name > oozie.external_monitoring.enable </ name >
    < value > false </ value >
    < description >
        如果需要将oozie功能指标展示给metrics-server后端，请将其设置为true
        如果设置为true，则必须指定以下属性：oozie.metrics.server.name，
        oozie.metrics.host，oozie.metrics.prefix，oozie.metrics.report.interval.sec，oozie.metrics.port
    </ description >
</ property >

< property >
    < name > oozie.external_monitoring.type </ name >
    < value > graphite </ value >
    < description >
        我们要发送指标的服务器的名称将是石墨或神经节。
    </ description >
</ property >

< property >
    < name > oozie.external_monitoring.address </ name >
    < value > http：// localhost：2020 </ value >
</ property >

< property >
    < name > oozie.external_monitoring.metricPrefix </ name >
    < value > oozie </ value >
</ property >

< property >
    < name > oozie.external_monitoring.reporterIntervalSecs </ name >
    < value > 60 </ value >
</ property >

< property >
    < name > oozie.jmx_monitoring.enable </ name >
    < value > false </ value >
    < description >
        如果需要通过JMX接口暴露oozie功能指标，请将其设置为true。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.uber.jar.enable </ name >
    < value > false </ value >
    < description >
        如果为true，则启用oozie.mapreduce.uber.jar mapreduce工作流配置属性，该属性用于指定
        在HDFS中的超级罐子。用uber jar提交工作流至少需要Hadoop 2.2.0或1.2.0。如果为false，则为工作流程
        其中指定oozie.mapreduce.uber.jar配置属性将失败。
    </ description >
</ property >

< property >
    < name > oozie.processing.timezone </ name >
    < 值 > UTC </ 值 >
    < description >
        Oozie服务器时区。有效值是UTC和GMT（+/-）####，例如“GMT + 0530”将是印度
        时区。Oozie Coordinator / Bundle所有日期解析和生成的日期将在指定的时间内完成
        时区。正常情况下，“UTC”的默认值不能改变。如果因为任何原因
        GMT（+/-）####时区不会观察到DST变化。
    </ description >
</ property >

<！ - Base Oozie URL：<SCHEME>：// <HOST>：<PORT> / <CONTEXT> - >

< property >
    < name > oozie.base.url </ name >
    < value > http：// $ {oozie.http.hostname}：$ {oozie.http.port} / oozie </ value >
    < description >
         基本Oozie网址。
    </ description >
</ property >

<！ -服务- >

< property >
    < name > oozie.system.id </ name >
    < value > oozie  -  $ {user.name} </ value >
    < description >
        Oozie系统ID。
    </ description >
</ property >

< property >
    < name > oozie.systemmode </ name >
    < 值 >正常</ 值 >
    < description >
        Oozie启动时的系统模式。
    </ description >
</ property >

< property >
    < name > oozie.delete.runtime.dir.on.shutdown </ name >
    < value > true </ value >
    < description >
        如果运行时目录应该在Oozie关闭后保留。
    </ description >
</ property >

< property >
    < name > oozie.services </ name >
    < 值 >
        org.apache.oozie.service.SchedulerService，
        org.apache.oozie.service.InstrumentationService，
        org.apache.oozie.service.MemoryLocksService，
        org.apache.oozie.service.UUIDService，
        org.apache.oozie.service.ELService，
        org.apache.oozie.service.AuthorizationService，
        org.apache.oozie.service.UserGroupInformationService，
        org.apache.oozie.service.HadoopAccessorService，
        org.apache.oozie.service.JobsConcurrencyService，
        org.apache.oozie.service.URIHandlerService，
        org.apache.oozie.service.DagXLogInfoService，
        org.apache.oozie.service.SchemaService，
        org.apache.oozie.service.LiteWorkflowAppService，
        org.apache.oozie.service.JPAService，
        org.apache.oozie.service.StoreService，
        org.apache.oozie.service.SLAStoreService，
        org.apache.oozie.service.DBLiteWorkflowStoreService，
        org.apache.oozie.service.CallbackService，
        org.apache.oozie.service.ActionService，
        org.apache.oozie.service.ShareLibService，
        org.apache.oozie.service.CallableQueueService，
        org.apache.oozie.service.ActionCheckerService，
        org.apache.oozie.service.RecoveryService，
        org.apache.oozie.service.PurgeService，
        org.apache.oozie.service.CoordinatorEngineService，
        org.apache.oozie.service.BundleEngineService，
        org.apache.oozie.service.DagEngineService，
        org.apache.oozie.service.CoordMaterializeTriggerService，
        org.apache.oozie.service.StatusTransitService，
        org.apache.oozie.service.PauseTransitService，
        org.apache.oozie.service.GroupsService，
        org.apache.oozie.service.ProxyUserService，
        org.apache.oozie.service.XLogStreamingService，
        org.apache.oozie.service.JvmPauseMonitorService，
        org.apache.oozie.service.SparkConfigurationService，
        org.apache.oozie.service.SchemaCheckerService
    </ 值 >
    < description >
        所有服务将由Oozie Services单身人士创建和管理。
        类名必须用逗号分隔。
    </ description >
</ property >

< property >
    < name > oozie.services.ext </ name >
    < value > </ value >
    < description >
        用自定义实现来添加/替换'oozie.services'中定义的服务。
        类名必须用逗号分隔。
    </ description >
</ property >

< property >
    < name > oozie.service.XLogStreamingService.buffer.len </ name >
    < value > 4096 </ value >
    < 描述 > 4K缓冲器用于渐进流日志
    </ description >
</ property >
< property >
    < name > oozie.service.XLogStreamingService.error.buffer.len </ name >
    < value > 2048 </ value >
    < 描述 > 2K缓冲器用于流式传输错误日志
        逐步
    </ description >
</ property >

< property >
    < name > oozie.service.XLogStreamingService.audit.buffer.len </ name >
    < value > 3 </ value >
    < description >传输审计日志的行数
        逐步
    </ description >
</ property >
<！ - HCatAccessorService - > < property > < name > oozie.service.HCatAccessorService.jmsconnections </ name > < 值 > 默认= java.naming.factory.initial的＃org.apache.activemq.jndi.ActiveMQInitialContextFactory; java.naming.provider.url的＃TCP：//本地主机：61616; connectionFactoryNames＃ConnectionFactory的 </ 值 > < description > 指定端点映射到JMS配置属性。一般来说，终点 标识HCatalog服务器URL。如果没有提到端点，则使用“default” 在查询中。如果某些JMS属性未定义，系统将使用该属性 定义了jndi.properties。从应用程序类路径中检索jndi.properties文件。 还可以提供映射规则，将Hcatalog服务器映射到相应的JMS提供程序。 hcat：// $ {1} $ {2} .server.com：8020 = java.naming.factory.initial的＃Dummy.Factory; java.naming.provider.url的＃TCP：//broker.$ {2} ：61616 </ description > </ property >

<！ - HCatAccessorService - > < property > < name > oozie.service.HCatAccessorService.jms.use.canonical.hostname </ name > < value > false </ value > < description >从HCat服务器发布的JMS消息通常包含HCat服务器的规范主机名 在独立模式下或在HA设置中多个节点的情况下VIP的规范名称。使用此设置 在协调器依赖关系的HCat URI中翻译用户指定的HCat服务器主机名或其别名 到其规范名称，以便它们可以与JMS依赖项可用性通知完全匹配。 </ description > </ property >

<！ - TopicService - >
< property > < name > oozie.service.JMSTopicService.topic.name </ name > < 值 > 默认值= $ {用户名} </ 值 > < description > 主题选项是$ {用户名}或$ {jobId}或固定的字符串，可以指定为默认值或a 特定的工作类型。 例如，要为工作流，协调器和捆绑包拥有固定的字符串主题， 以下面的逗号分隔格式指定：{jobtype1} = {some_string1}，{jobtype2} = {some_string2} 工作类型可以是工作流，协调员或捆绑。 例如，下面定义了工作流作业，工作流操作，协调员作业，协调员操作， 捆绑工作和捆绑行动 工作流程=工作流程， 协调员=协调， 管束=束 对于没有定义主题的作业，默认主题将是$ {username} </ description > </ property >

<！ - JMS Producer连接- >
< property >
    < name > oozie.jms.producer.connection.properties </ name >
    < value > java.naming.factory.initial＃org.apache.activemq.jndi.ActiveMQInitialContextFactory; java.naming.provider.url＃tcp：// localhost：61616; connectionFactoryNames＃ConnectionFactory </ value >
</ property >
<！ - JMSAccessorService - > < property > < name > oozie.service.JMSAccessorService.connectioncontext.impl </ name > < 值 > org.apache.oozie.jms.DefaultConnectionContext </ 值 > < description > 指定连接上下文实现 </ description > </ property >

<！ - ConfigurationService - >

< property >
    < name > oozie.service.ConfigurationService.ignore.system.properties </ name >
    < 值 >
        oozie.service.AuthorizationService.security.enabled
    </ 值 >
    < description >
        指定“oozie。*”属性不能通过Java系统属性重写。
        属性名称必须用逗号隔开。
    </ description >
</ property >

< property >
    < name > oozie.service.ConfigurationService.verify.available.properties </ name >
    < value > true </ value >
    < description >
        指定是否启用可用配置检查。
    </ description >
</ property >

<！ - SchedulerService - >

< property >
    < name > oozie.service.SchedulerService.threads </ name >
    < value > 10 </ value >
    < description >
        SchedulerService用于运行deamon任务的线程数。
        如果超时，则预定的守护程序任务将排队等待，直到线程可用。
    </ description >
</ property >

<！ -   AuthorizationService - >

< property >
    < name > oozie.service.AuthorizationService.authorization.enabled </ name >
    < value > false </ value >
    < description >
        指定是否启用安全性（用户名/管理员角色）。
        如果禁用，任何用户都可以管理Oozie系统并管理任何工作。
    </ description >
</ property >

< property >
    < name > oozie.service.AuthorizationService.default.group.as.acl </ name >
    < value > false </ value >
    < description >
        在用户的默认组是作业的ACL的情况下启用旧的行为。
    </ description >
</ property >

< property >
    < name > oozie.service.AuthorizationService.system.info.authorized.users </ name >
    < value > </ value >
    < description >
        以逗号分隔的授权用于Web服务调用的用户列表获取系统配置。
    </ description >
</ property >

<！ - InstrumentationService - >

< property >
    < name > oozie.service.InstrumentationService.logging.interval </ name >
    < value > 60 </ value >
    < description >
        InstrumentationService应记录仪表的时间间隔（以秒为单位）。
        如果设置为0，则不会记录检测数据。
    </ description >
</ property >

<！ - PurgeService - >
< property >
    < name > oozie.service.PurgeService.older.than </ name >
    < value > 30 </ value >
    < description >
        已完成的工作流作业比此值旧，将由PurgeService清除。
    </ description >
</ property >

< property >
    < name > oozie.service.PurgeService.coord.older.than </ name >
    < value > 7 </ value >
    < description >
        以天为单位的早于此值的已完成的协调员作业将由PurgeService清除。
    </ description >
</ property >

< property >
    < name > oozie.service.PurgeService.bundle.older.than </ name >
    < value > 7 </ value >
    < description >
        以天为单位的早于此值的已完成捆绑包作业将由PurgeService清除。
    </ description >
</ property >

< property >
    < name > oozie.service.PurgeService.purge.old.coord.action </ name >
    < value > false </ value >
    < description >
        是否清除已完成的工作流程及其相应的协调员操作
        如果已完成的工作流作业比该值早，则长时间运行协调员作业
        在oozie.service.PurgeService.older.than中指定。
    </ description >
</ property >

< property >
    < name > oozie.service.PurgeService.purge.limit </ name >
    < value > 100 </ value >
    < description >
        已完成操作清除 - 将每个清除限制为此值
    </ description >
</ property >

< property >
    < name > oozie.service.PurgeService.purge.interval </ name >
    < value > 3600 </ value >
    < description >
        清洗服务将运行的时间间隔，以秒为单位。
    </ description >
</ property >

< property >
    < name > oozie.service.PurgeService.enable.command.line </ name >
    < value > true </ value >
    < description >
        启用/禁用oozie admin purge命令。默认情况下，它被启用。
    </ description >
</ property >

<！ - RecoveryService - >

< property >
    < name > oozie.service.RecoveryService.wf.actions.older.than </ name >
    < value > 120 </ value >
    < description >
        有资格排队恢复的操作的年龄，以秒为单位。
    </ description >
</ property >

< property >
    < name > oozie.service.RecoveryService.wf.actions.created.time.interval </ name >
    < value > 7 </ value >
    < description >
    创建有资格排队恢复的操作的时间段。
    </ description >
</ property >

< property >
    < name > oozie.service.RecoveryService.callable.batch.size </ name >
    < value > 10 </ value >
    < description >
        这个值决定了可以一起调用的可调用的数量
        由单个线程执行。
    </ description >
</ property >

< property >
    < name > oozie.service.RecoveryService.push.dependency.interval </ name >
    < value > 200 </ value >
    < description >
        此值确定推送丢失相关性命令排队的延迟
        在恢复服务
    </ description >
</ property >

< property >
    < name > oozie.service.RecoveryService.interval </ name >
    < value > 60 </ value >
    < description >
        RecoverService运行的时间间隔，以秒为单位。
    </ description >
</ property >

< property >
    < name > oozie.service.RecoveryService.coord.older.than </ name >
    < value > 600 </ value >
    < description >
        协调员有资格排队恢复的工作或行动的年龄，以秒为单位。
    </ description >
</ property >

< property >
    < name > oozie.service.RecoveryService.bundle.older.than </ name >
    < value > 600 </ value >
    < description >
        在数秒钟内有资格排队恢复的捆绑作业的年龄。
    </ description >
</ property >

<！ - CallableQueueService - >

< property >
    < name > oozie.service.CallableQueueService.queue.size </ name >
    < value > 10000 </ value >
    < description >最大可调用队列大小</ description >
</ property >

< property >
    < name > oozie.service.CallableQueueService.threads </ name >
    < value > 10 </ value >
    < description >用于执行可调用的线程的数量</ description >
</ property >

< property >
    < name > oozie.service.CallableQueueService.callable.concurrency </ name >
    < value > 3 </ value >
    < description >
        给定可调用类型的最大并发性。
        每个命令都是可调用的类型（提交，启动，运行，信号，作业，作业，挂起，恢复等）。
        每个动作类型都是可调用类型（Map-Reduce，Pig，SSH，FS，子工作流等）。
        所有使用动作执行者的命令（action-start，action-end，action-kill和action-check）都会使用
        作为可调用类型的动作类型。
    </ description >
</ property >

< property >
    < name > oozie.service.CallableQueueService.callable.next.eligible </ name >
    < value > true </ value >
    < description >
        如果为true，那么当队列中的可调用已经达到最大并发时，
        Oozie不断地找到下一个还没有达到最大并发的。
    </ description >
</ property >

< property >
    < name > oozie.service.CallableQueueService.InterruptMapMaxSize </ name >
    < value > 500 </ value >
    < description >
        中断映射的最大大小，如果超过大小，中断元素将不会被插入到映射中。
    </ description >
</ property >

< property >
    < name > oozie.service.CallableQueueService.InterruptTypes </ name >
    < value > kill，resume，suspend，bundle_kill，bundle_resume，bundle_suspend，coord_kill，coord_change，coord_resume，coord_suspend </ value >
    < description >
        获取被认为是中断类型的XCommand类型
    </ description >
</ property >

<！ -   CoordMaterializeTriggerService - >

< property >
    < name > oozie.service.CoordMaterializeTriggerService.lookup.interval
    </ name >
    < value > 300 </ value >
    < description >协调器作业查找间隔（以秒为单位）。
    </ description >
</ property >

<！ -如果您需要CoordMaterializeTriggerService的不同调度间隔，则启用此功能。
默认情况下，它将使用查找间隔作为调度间隔
<属性>
    <名称> oozie.service.CoordMaterializeTriggerService.scheduling.interval
    </名称>
    <值> 300 </值>
    <description> CoordMaterializeTriggerService将运行的频率</ description>
</属性>
 - >

< property >
    < name > oozie.service.CoordMaterializeTriggerService.materialization.window
    </ name >
    < value > 3600 </ value >
    < description >协调器作业查找命令实现了每个
        为此下一个“窗口”持续时间作业
    </ description >
</ property >

< property >
    < name > oozie.service.CoordMaterializeTriggerService.callable.batch.size </ name >
    < value > 10 </ value >
    < description >
        这个值决定了可以一起调用的可调用的数量
        由单个线程执行。
    </ description >
</ property >

< property >
    < name > oozie.service.CoordMaterializeTriggerService.materialization.system.limit </ name >
    < value > 50 </ value >
    < description >
        此值确定在给定时间要实现的协调员作业的数量。
    </ description >
</ property >

< property >
    < name > oozie.service.coord.normal.default.timeout
    </ name >
    < value > 120 </ value >
    < description >正常作业的协调器操作输入检查的默认超时（分钟）。
        -1表示无限超时</ description >
</ property >

< property >
    < name > oozie.service.coord.default.max.timeout
    </ name >
    < value > 86400 </ value >
    < description >协调器操作输入检查的默认最大超时（以分钟为单位）。86400 = 60天
    </ description >
</ property >

< property >
    < name > oozie.service.coord.input.check.requeue.interval
    </ name >
    < value > 60000 </ value >
    < description >协调器数据输入检查的命令重新排队间隔（以毫秒为单位）。
    </ description >
</ property >

< property >
    < name > oozie.service.coord.input.check.requeue.interval.additional.delay </ name >
    < value > 0 </ value >
    < description >该值（以秒为单位）将被添加到oozie.service.coord.input.check.requeue.interval和结果值
将是等待很长时间而没有任何输入的动作的时间间隔。 </ description > </ property >

< property >
    < name > oozie.service.coord.push.check.requeue.interval
    </ name >
    < value > 600000 </ value >
    < 说明 >用于推依赖关系命令重新队列间隔（毫秒）。
    </ description >
</ property >

< property >
    < name > oozie.service.coord.default.concurrency
    </ name >
    < value > 1 </ value >
    < description >协调器作业的缺省并发性，用于确定最大动作的数量
    同时执行。-1表示无限并发。</ description >
</ property >

< property >
    < name > oozie.service.coord.default.throttle
    </ name >
    < value > 12 </ value >
    < description >协调器作业的默认油门，以确定最大动作应该达到多少
     同时处于WAITING状态。</ description >
</ property >

< property >
    < name > oozie.service.coord.materialization.throttling.factor
    </ name >
    < value > 0.05 </ value >
    < description >确定在任何时候单个作业的WAITING状态应该有多少个最大动作。该值通过计算
     这个因子是整个队列的大小。</ description >
</ property >

< property >
    < name > oozie.service.coord.check.maximum.frequency </ name >
    < value > true </ value >
    < description >
        如果属实，Oozie会拒绝任何频率超过5分钟的协调员。不建议禁用
        这个检查或提交协调员频率超过5分钟：这样做可能会导致意外的行为和
        额外的系统压力。
    </ description >
</ property >

<！ - ELService - >
<！ -   ELService支持的组列表- >
< property >
    < name > oozie.service.ELService.groups </ name >
    < value > job-submit，工作流，wf-sla-submit，coord-job-submit-freq，coord-job-submit-nofuncs，coord-job-submit-data，coord-job-submit-instances，coord-sla -submit，坐标行动创建，坐标行动创建-研究所，坐标-SLA创建，坐标行动启动，坐标在职等待超时，捆绑提交，坐标作业提交-初始实例</ 值 >
    < description >不同EL服务的组列表</ description >
</ property >

< property >
    < name > oozie.service.ELService.constants.job-submit </ name >
    < 值 >
    </ 值 >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.job-submit </ name >
    < 值 >
    </ 值 >
    < description >
      EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.constants.job-submit </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以添加扩展，而不必包含所有内置的扩展。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.job-submit </ name >
    < value > </ value >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以添加扩展，而不必包含所有内置的扩展。
    </ description >
</ property >
<！ -工作流特定- > < property > < name > oozie.service.ELService.constants.workflow </ name > < 值 > KB = org.apache.oozie.util.ELConstantsFunctions＃KB， MB = org.apache.oozie.util.ELConstantsFunctions＃MB， GB = org.apache.oozie.util.ELConstantsFunctions＃国标， TB = org.apache.oozie.util.ELConstantsFunctions＃TB， PB = org.apache.oozie.util.ELConstantsFunctions＃PB， RECORDS = org.apache.oozie.action.hadoop.HadoopELFunctions＃RECORDS， MAP_IN = org.apache.oozie.action.hadoop.HadoopELFunctions＃MAP_IN， MAP_OUT = org.apache.oozie.action.hadoop.HadoopELFunctions＃MAP_OUT， REDUCE_IN = org.apache.oozie.action.hadoop.HadoopELFunctions＃REDUCE_IN， REDUCE_OUT = org.apache.oozie.action.hadoop.HadoopELFunctions＃REDUCE_OUT， 组= org.apache.oozie.action.hadoop.HadoopELFunctions＃组 </ 值 > < description > EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。 </ description > </ property >

< property >
    < name > oozie.service.ELService.ext.constants.workflow </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.workflow </ name >
    < 值 >
        firstNotNull = org.apache.oozie.util.ELConstantsFunctions＃firstNotNull，
        的concat = org.apache.oozie.util.ELConstantsFunctions＃concat，则
        的replaceAll = org.apache.oozie.util.ELConstantsFunctions＃的replaceAll，
        appendAll = org.apache.oozie.util.ELConstantsFunctions＃appendAll，
        修剪= org.apache.oozie.util.ELConstantsFunctions＃修剪，
        时间戳= org.apache.oozie.util.ELConstantsFunctions＃时间戳，
        的URLEncode = org.apache.oozie.util.ELConstantsFunctions＃URLEncode的，
        toJsonStr = org.apache.oozie.util.ELConstantsFunctions＃toJsonStr，
        toPropertiesStr = org.apache.oozie.util.ELConstantsFunctions＃toPropertiesStr，
        toConfigurationStr = org.apache.oozie.util.ELConstantsFunctions＃toConfigurationStr，
        WF：ID = org.apache.oozie.DagELFunctions＃wf_id，
        WF：名称= org.apache.oozie.DagELFunctions＃wf_name，
        WF：APPPATH = org.apache.oozie.DagELFunctions＃wf_appPath，
        WF：CONF = org.apache.oozie.DagELFunctions＃wf_conf，
        WF：用户= org.apache.oozie.DagELFunctions＃wf_user，
        WF：组= org.apache.oozie.DagELFunctions＃wf_group，
        WF：回调= org.apache.oozie.DagELFunctions＃wf_callback，
        WF：过渡= org.apache.oozie.DagELFunctions＃wf_transition，
        WF：lastErrorNode = org.apache.oozie.DagELFunctions＃wf_lastErrorNode，
        WF：错误码= org.apache.oozie.DagELFunctions＃wf_errorCode，
        WF：的errorMessage = org.apache.oozie.DagELFunctions＃wf_errorMessage，
        WF：运行= org.apache.oozie.DagELFunctions＃wf_run，
        WF：actionData = org.apache.oozie.DagELFunctions＃wf_actionData，
        WF：actionExternalId = org.apache.oozie.DagELFunctions＃wf_actionExternalId，
        WF：actionTrackerUri = org.apache.oozie.DagELFunctions＃wf_actionTrackerUri，
        WF：actionExternalStatus = org.apache.oozie.DagELFunctions＃wf_actionExternalStatus，
        hadoop的：计数器= org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_counters，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf，
        FS：存在= org.apache.oozie.action.hadoop.FsELFunctions＃fs_exists，
        FS：ISDIR = org.apache.oozie.action.hadoop.FsELFunctions＃fs_isDir，
        FS：dirSize = org.apache.oozie.action.hadoop.FsELFunctions＃fs_dirSize，
        FS：文件大小= org.apache.oozie.action.hadoop.FsELFunctions＃fs_fileSize，
        FS：BLOCKSIZE = org.apache.oozie.action.hadoop.FsELFunctions＃fs_blockSize，
        hcat：存在= org.apache.oozie.coord.HCatELFunctions＃hcat_exists
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.WorkflowAppService.WorkflowDefinitionMaxLength </ name >
    < value > 100000 </ value >
    < description >
        工作流定义的最大长度（以字节为单位）
        如果长度超过给定的最大值，则会报告错误
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.workflow </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

<！ -在工作流作业提交过程中解决SLA信息- >
< property >
    < name > oozie.service.ELService.constants.wf-sla-submit </ name >
    < 值 >
        MINUTES = org.apache.oozie.util.ELConstantsFunctions＃SUBMIT_MINUTES，
        HOURS = org.apache.oozie.util.ELConstantsFunctions＃SUBMIT_HOURS，
        天= org.apache.oozie.util.ELConstantsFunctions＃SUBMIT_DAYS
        </ 值 >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.constants.wf-sla-submit </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.wf-sla-submit </ name >
    < value > </ value >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >
< property >
    < name > oozie.service.ELService.ext.functions.wf-sla-submit </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >
<！ -协调员细节- > l <！ -作业提交期间的阶段1解析- > <！ - EL Evalautor设置，以解决主要是射频标签- > < property > < name > oozie.service.ELService.constants.coord-job-submit-freq </ name > < value > </ value > < description > EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。 </ description > </ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-job-submit-freq </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-job-submit-freq </ name >
    < 值 >
        坐标：天= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_days，
        坐标：月= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_months，
        坐标：小时= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_hours，
        坐标：分钟= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_minutes，
        坐标：endOfDays = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_endOfDays，
        坐标：endOfMonths = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_endOfMonths，
        坐标：endOfWeeks = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_endOfWeeks，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-job-submit-initial-instance </ name >
    < 值 >
        $ {oozie.service.ELService.functions.coord作业提交 -  nofuncs}，
        坐标：dateOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_dateOffset，
        坐标：dateTzOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_dateTzOffset
    </ 值 >
    < description >
        用于协调作业的EL函数提交初始实例，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-job-submit-freq </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.constants.coord-job-wait-timeout </ name >
    < value > </ value >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-job-wait-timeout </ name >
    < value > </ value >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以添加扩展，而不必包含所有内置的扩展。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-job-wait-timeout </ name >
    < 值 >
        坐标：天= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_days，
        坐标：月= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_months，
        坐标：小时= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_hours，
        坐标：分钟= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_minutes，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-job-wait-timeout </ name >
    < value > </ value >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以添加扩展，而不必包含所有内置的扩展。
    </ description >
</ property >
<！ - EL Evalautor设置主要是解决所有的常量/变量-没有EL函数解析- > < property > < name > oozie.service.ELService.constants.coord-job-submit-nofuncs </ name > < 值 > 分= org.apache.oozie.coord.CoordELConstants＃SUBMIT_MINUTE， 小时= org.apache.oozie.coord.CoordELConstants＃SUBMIT_HOUR， DAY = org.apache.oozie.coord.CoordELConstants＃SUBMIT_DAY， 月= org.apache.oozie.coord.CoordELConstants＃SUBMIT_MONTH， YEAR = org.apache.oozie.coord.CoordELConstants＃SUBMIT_YEAR </ 值 > < description > EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。 </ description > </ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-job-submit-nofuncs </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-job-submit-nofuncs </ name >
    < 值 >
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-job-submit-nofuncs </ name >
    < value > </ value >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >
<！ - EL Evalautor设置到** **检查情况/启动实例/终端实例是否有效 没有EL功能将被解决- > < property > < name > oozie.service.ELService.constants.coord-job-submit-instances </ name > < value > </ value > < description > EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。 </ description > </ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-job-submit-instances </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-job-submit-instances </ name >
    < 值 >
        坐标：hoursInDay = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_hoursInDay_echo，
        坐标：daysInMonth = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_daysInMonth_echo，
        坐标：tzOffset = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_tzOffset_echo，
        坐标：电流= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_current_echo，
        坐标：currentRange = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_currentRange_echo，
        坐标：偏移量= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_offset_echo，
        坐标：最新= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_latest_echo，
        坐标：latestRange = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_latestRange_echo，
        坐标：未来= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_future_echo，
        坐标：futureRange = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_futureRange_echo，
        坐标：formatTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_formatTime_echo，
        坐标：epochTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_epochTime_echo，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        坐标：绝对= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_absolute_echo，
        坐标：endOfMonths = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_endOfMonths_echo，
        坐标：endOfWeeks = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_endOfWeeks_echo，
        坐标：endOfDays = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_endOfDays_echo，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf，
        坐标：dateOffset = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dateOffset_echo，
        坐标：dateTzOffset = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dateTzOffset_echo
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-job-submit-instances </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >
<！ - EL Evalautor设置到** **检查是否数据输入和数据出有效 没有EL功能将被解决- >

< property >
    < name > oozie.service.ELService.constants.coord-job-submit-data </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-job-submit-data </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-job-submit-data </ name >
    < 值 >
        坐标：DATAIN = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dataIn_echo，
        坐标：DATAOUT = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dataOut_echo，
        坐标：nominalTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_nominalTime_echo_wrap，
        坐标：actualTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_actualTime_echo_wrap，
        坐标：dateOffset = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dateOffset_echo，
        坐标：dateTzOffset = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dateTzOffset_echo，
        坐标：formatTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_formatTime_echo，
        坐标：epochTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_epochTime_echo，
        坐标：actionId = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_actionId_echo，
        坐标：名称= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_name_echo，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        坐标：databaseIn = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_databaseIn_echo，
        坐标：databaseOut = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_databaseOut_echo，
        坐标：tableIn = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_tableIn_echo，
        坐标：tableOut = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_tableOut_echo，
        坐标：dataInPartitionFilter = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataInPartitionFilter_echo，
        坐标：dataInPartitionMin = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataInPartitionMin_echo，
        坐标：dataInPartitionMax = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataInPartitionMax_echo，
        坐标：dataInPartitions = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataInPartitions_echo，
        坐标：dataOutPartitions = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataOutPartitions_echo，
        坐标：dataOutPartitionValue = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataOutPartitionValue_echo，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-job-submit-data </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

<！ -协调员作业提交过程中解决SLA信息- >
< property >
    < name > oozie.service.ELService.constants.coord-sla-submit </ name >
    < 值 >
        MINUTES = org.apache.oozie.coord.CoordELConstants＃SUBMIT_MINUTES，
        HOURS = org.apache.oozie.coord.CoordELConstants＃SUBMIT_HOURS，
        天= org.apache.oozie.coord.CoordELConstants＃SUBMIT_DAYS
        </ 值 >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-sla-submit </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.bundle-submit </ name >
    < value > bundle：conf = org.apache.oozie.bundle.BundleELFunctions＃bundle_conf </ value >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-sla-submit </ name >
    < 值 >
        坐标：DATAOUT = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dataOut_echo，
        坐标：nominalTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_nominalTime_echo_fixed，
        坐标：actualTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_actualTime_echo_wrap，
        坐标：dateOffset = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dateOffset_echo，
        坐标：dateTzOffset = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_dateTzOffset_echo，
        坐标：formatTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_formatTime_echo，
        坐标：epochTime = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_epochTime_echo，
        坐标：actionId = org.apache.oozie.coord.CoordELFunctions＃ph1_coord_actionId_echo，
        坐标：名称= org.apache.oozie.coord.CoordELFunctions＃ph1_coord_name_echo，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        坐标：databaseOut = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_databaseOut_echo，
        坐标：tableOut = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_tableOut_echo，
        坐标：dataOutPartitions = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataOutPartitions_echo，
        坐标：dataOutPartitionValue = org.apache.oozie.coord.HCatELFunctions＃ph1_coord_dataOutPartitionValue_echo，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >
< property >
    < name > oozie.service.ELService.ext.functions.coord-sla-submit </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >
<！ - 为协调者创建动作- > < property > < name > oozie.service.ELService.constants.coord-action-create </ name > < 值 > </ 值 > < description > EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。 </ description > </ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-action-create </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-action-create </ name >
    < 值 >
        坐标：hoursInDay = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_hoursInDay，
        坐标：daysInMonth = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_daysInMonth，
        坐标：tzOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_tzOffset，
        坐标：电流= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_current，
        坐标：currentRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_currentRange，
        坐标：偏移量= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_offset，
        坐标：最新= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_latest_echo，
        坐标：latestRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_latestRange_echo，
        坐标：未来= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_future_echo，
        坐标：futureRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_futureRange_echo，
        坐标：actionId = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_actionId，
        坐标：名称= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_name，
        坐标：formatTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_formatTime，
        坐标：epochTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_epochTime，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        坐标：绝对= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_absolute_echo，
        坐标：endOfMonths = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_endOfMonths_echo，
        坐标：endOfWeeks = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_endOfWeeks_echo，
        坐标：endOfDays = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_endOfDays_echo，
        坐标：absoluteRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_absolute_range，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-action-create </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >
<！ - 协调器的动作创建用于仅评估实例编号，如$ {current（daysInMonth（））}。电流将被回显- > < property > < name > oozie.service.ELService.constants.coord-action-create-inst </ name > < 值 > </ 值 > < description > EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。 </ description > </ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-action-create-inst </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-action-create-inst </ name >
    < 值 >
        坐标：hoursInDay = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_hoursInDay，
        坐标：daysInMonth = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_daysInMonth，
        坐标：tzOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_tzOffset，
        坐标：电流= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_current_echo，
        坐标：currentRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_currentRange_echo，
        坐标：偏移量= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_offset_echo，
        坐标：最新= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_latest_echo，
        坐标：latestRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_latestRange_echo，
        坐标：未来= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_future_echo，
        坐标：futureRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_futureRange_echo，
        坐标：formatTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_formatTime，
        坐标：epochTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_epochTime，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        坐标：绝对= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_absolute_echo，
        坐标：absoluteRange = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_absolute_range，
        坐标：endOfMonths = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_endOfMonths_echo，
        坐标：endOfWeeks = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_endOfWeeks_echo，
        坐标：endOfDays = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_endOfDays_echo，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf，
        坐标：dateOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_dateOffset，
        坐标：dateTzOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_dateTzOffset
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-action-create-inst </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

    <！ -行为产生/实现过程中解决SLA信息- >
< property >
    < name > oozie.service.ELService.constants.coord-sla-create </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-sla-create </ name >
    < 值 >
        MINUTES = org.apache.oozie.coord.CoordELConstants＃SUBMIT_MINUTES，
        HOURS = org.apache.oozie.coord.CoordELConstants＃SUBMIT_HOURS，
        DAYS = org.apache.oozie.coord.CoordELConstants＃SUBMIT_DAYS </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-sla-create </ name >
    < 值 >
        坐标：DATAOUT = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_dataOut，
        坐标：nominalTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_nominalTime，
        坐标：actualTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_actualTime，
        坐标：dateOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_dateOffset，
        坐标：dateTzOffset = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_dateTzOffset，
        坐标：formatTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_formatTime，
        坐标：epochTime = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_epochTime，
        坐标：actionId = org.apache.oozie.coord.CoordELFunctions＃ph2_coord_actionId，
        坐标：名称= org.apache.oozie.coord.CoordELFunctions＃ph2_coord_name，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        坐标：databaseOut = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_databaseOut，
        坐标：tableOut = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_tableOut，
        坐标：dataOutPartitions = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataOutPartitions，
        坐标：dataOutPartitionValue = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataOutPartitionValue，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >
< property >
    < name > oozie.service.ELService.ext.functions.coord-sla-create </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >
<！ - 动作开始协调员- > < property > < name > oozie.service.ELService.constants.coord-action-start </ name > < 值 > </ 值 > < description > EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。 </ description > </ property >

< property >
    < name > oozie.service.ELService.ext.constants.coord-action-start </ name >
    < value > </ value >
    < description >
        EL常量声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃CONSTANT。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.functions.coord-action-start </ name >
    < 值 >
        坐标：hoursInDay = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_hoursInDay，
        坐标：daysInMonth = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_daysInMonth，
        坐标：tzOffset = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_tzOffset，
        坐标：最新= org.apache.oozie.coord.CoordELFunctions＃ph3_coord_latest，
        坐标：latestRange = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_latestRange，
        坐标：未来= org.apache.oozie.coord.CoordELFunctions＃ph3_coord_future，
        坐标：futureRange = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_futureRange，
        坐标：DATAIN = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_dataIn，
        坐标：DATAOUT = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_dataOut，
        坐标：nominalTime = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_nominalTime，
        坐标：actualTime = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_actualTime，
        坐标：dateOffset = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_dateOffset，
        坐标：dateTzOffset = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_dateTzOffset，
        坐标：formatTime = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_formatTime，
        坐标：epochTime = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_epochTime，
        坐标：actionId = org.apache.oozie.coord.CoordELFunctions＃ph3_coord_actionId，
        坐标：名称= org.apache.oozie.coord.CoordELFunctions＃ph3_coord_name，
        坐标：CONF = org.apache.oozie.coord.CoordELFunctions＃coord_conf，
        坐标：用户= org.apache.oozie.coord.CoordELFunctions＃coord_user，
        坐标：databaseIn = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_databaseIn，
        坐标：databaseOut = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_databaseOut，
        坐标：tableIn = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_tableIn，
        坐标：tableOut = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_tableOut，
        坐标：dataInPartitionFilter = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataInPartitionFilter，
        坐标：dataInPartitionMin = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataInPartitionMin，
        坐标：dataInPartitionMax = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataInPartitionMax，
        坐标：dataInPartitions = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataInPartitions，
        坐标：dataOutPartitions = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataOutPartitions，
        坐标：dataOutPartitionValue = org.apache.oozie.coord.HCatELFunctions＃ph3_coord_dataOutPartitionValue，
        的hadoop：CONF = org.apache.oozie.action.hadoop.HadoopELFunctions＃hadoop_conf
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.ext.functions.coord-action-start </ name >
    < 值 >
    </ 值 >
    < description >
        EL函数声明，以逗号分隔，格式为[PREFIX：] NAME = CLASS＃METHOD。
        这个属性是一个方便的属性，可以在不需要的情况下向内置的执行程序添加扩展
        包括所有内置的。
    </ description >
</ property >

< property >
    < name > oozie.service.ELService.latest-el.use-current-time </ name >
    < value > false </ value >
    < description >
        确定是否使用当前时间来确定最新的依赖关系或动作创建时间。
        这是为了与旧的oozie行为向后兼容。
    </ description >
</ property >

<！ - UUIDService - >

< property >
    < name > oozie.service.UUIDService.generator </ name >
    < value >计数器</ value >
    < description >
        随机：生成的UUID将是随机字符串。
        计数器：产生的UUID将会是一个以系统启动时间为后缀的计数器。
    </ description >
</ property >

<！ - DBLiteWorkflowStoreService - >

< property >
    < name > oozie.service.DBLiteWorkflowStoreService.status.metrics.collection.interval </ name >
    < value > 5 </ value >
    < description >工作流程状态指标收集时间间隔（分钟）</ description >
</ property >

< property >
    < name > oozie.service.DBLiteWorkflowStoreService.status.metrics.window </ name >
    < value > 3600 </ value >
    < description >
        工作流程状态指标收集窗口在几秒钟内。工作流程状态将针对该窗口进行检测。
    </ description >
</ property >

DBLiteWorkflowStoreService使用的<！ - DB模式信息- >

< property >
    < name > oozie.db.schema.name </ name >
    < value > oozie </ value >
    < description >
        Oozie数据库名称
    </ description >
</ property >

<！ -数据库导入CLI：批量大小- >

< property >
    < name > oozie.db.import.batch.size </ name >
    < value > 1000 </ value >
    < description >
        Oozie DB导入CLI工具在单个事务中导入了多少实体以避免OutOfMemoryErrors。
    </ description >
</ property >
<！ - StoreService - >

< property >
    < name > oozie.service.JPAService.create.db.schema </ name >
    < value > false </ value >
    < description >
        创建Oozie数据库。

        如果设置为true，则创建数据库模式（如果不存在）。如果数据库模式存在是一个NOP。
        如果设置为false，则不会创建数据库模式。如果数据库模式不存在，则启动失败。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.validate.db.connection </ name >
    < value > true </ value >
    < description >
        验证数据库连接池中的数据库连接。
        如果'oozie.service.JPAService.create.db.schema'属性设置为true，则忽略此属性。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.validate.db.connection.eviction.interval </ name >
    < value > 300000 </ value >
    < description >
        验证数据库连接池中的数据库连接。
        当验证数据库连接“TestWhileIdle”为true时，休眠的毫秒数
         在空闲对象逐出线程的运行之间。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.validate.db.connection.eviction.num </ name >
    < value > 10 </ value >
    < description >
        验证数据库连接池中的数据库连接。
        当验证数据库连接“TestWhileIdle”为真时，要检查的对象的数量
        空闲对象逐出线程的每次运行。
    </ description >
</ property >


< property >
    < name > oozie.service.JPAService.connection.data.source </ name >
    < value > org.apache.oozie.util.db.BasicDataSourceWrapper </ value >
    < description >
        用于连接池的DataSource。如果你想要的财产
        openJpa.connectionProperties =“DriverClassName = ...”具有实际效果，请将其设置为
        org.apache.oozie.util.db.BasicDataSourceWrapper。
        DBCP错误（https://issues.apache.org/jira/browse/DBCP-333）会阻止其他JDBC驱动程序
        设置为在使用自定义类加载器时具有实际效果。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.connection.properties </ name >
    < value > </ value >
    < description >
        数据源连接属性。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.jdbc.driver </ name >
    < value > org.apache.derby.jdbc.EmbeddedDriver </ value >
    < description >
        JDBC驱动程序类。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.jdbc.url </ name >
    < value > jdbc：derby：$ {oozie.data.dir} / $ {oozie.db.schema.name} -db; create = true </ value >
    < description >
        JDBC URL。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.jdbc.username </ name >
    < 值 > sa </ 值 >
    < description >
        数据库用户名。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.jdbc.password </ name >
    < value > </ value >
    < description >
        DB用户密码。

        重要提示：如果密码为空，则留下一个空格字符串，服务将修剪该值，
                   如果为空配置假定它是NULL。

        重要提示：如果StoreServicePasswordService处于活动状态，则会使用中给出的值重置此值
                   控制台。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.pool.max.active.conn </ name >
    < value > 10 </ value >
    < description >
         最大连接数。
    </ description >
</ property >

< property >
    < name > oozie.service.JPAService.openjpa.BrokerImpl </ name >
    < value >非终结</ value >
    < description >
      默认的OpenJPAEntityManager实现在实例完成时自动关闭。
      这样可以防止开发人员无法明确关闭时可能发生的意外资源泄漏
      EntityManagers完成后，但它也引发了一个可扩展性的瓶颈，因为JVM必须
      在实例创建期间执行同步，因为终结器线程将有更多的实例进行监视。
      为避免这种开销，请将openjpa.BrokerImpl配置属性设置为非终结。
      要使用默认实现将其设置为空白。
   </ description >
</ property >

< property >
    < name > oozie.service.JPAService.retry.initial-wait-time.ms </ name >
    < value > 100 </ value >
    < description >
      第一次失败的数据库操作和重新尝试的操作之间的初始等待时间（以毫秒为单位）。这段等待
      每次重试时间加倍。
   </ description >
</ property >

< property >
    < name > oozie.service.JPAService.retry.maximum-wait-time.ms </ name >
    < value > 30000 </ value >
    < description >
      数据库重试尝试之间的最长等待时间。
   </ description >
</ property >

< property >
    < name > oozie.service.JPAService.retry.max-retries </ name >
    < value > 10 </ value >
    < description >
      失败的数据库操作的最大重试次数。
   </ description >
</ property >
<！ - SchemaService - >

< property >
    < name > oozie.service.SchemaService.wf.schemas </ name >
    < 值 >
        Oozie的-共1.0.xsd，
        Oozie的工作流-0.1.xsd，Oozie的工作流-0.2.xsd，Oozie的工作流-0.2.5.xsd，Oozie的工作流-0.3.xsd，Oozie的工作流-0.4.xsd，
        Oozie的工作流-0.4.5.xsd，Oozie的工作流-0.5.xsd，Oozie的工作流-1.0.xsd，
        壳行动0.1.xsd，壳行动0.2.xsd，壳行动0.3.xsd，壳行动1.0.xsd，
        电子邮件行动0.1.xsd，电子邮件行动0.2.xsd，
        蜂房行动0.2.xsd，蜂房行动0.3.xsd，蜂房行动0.4.xsd，蜂房行动0.5.xsd，蜂房行动0.6.xsd，蜂房行动1.0.xsd，
        sqoop行动-0.2.xsd，sqoop行动-0.3.xsd，sqoop行动-0.4.xsd，sqoop行动，1.0.xsd，
        SSH-行动0.1.xsd，SSH-行动0.2.xsd，
        DistCp使用-行动0.1.xsd，DistCp使用-行动0.2.xsd，
        Oozie的-SLA-0.1.xsd，Oozie的-SLA-0.2.xsd，
        hive2行动-0.1.xsd，hive2行动-0.2.xsd，hive2行动，1.0.xsd，
        火花动作0.1.xsd，火花动作0.2.xsd，火花动作1.0.xsd
    </ 值 >
    < description >
        工作流模式列表（以逗号分隔）。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaService.wf.ext.schemas </ name >
    < value > </ value >
    < description >
        工作流附加模式列表（以逗号分隔）。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaService.coord.schemas </ name >
    < 值 >
        Oozie的协调员-0.1.xsd，Oozie的协调员-0.2.xsd，Oozie的协调员-0.3.xsd，Oozie的协调员-0.4.xsd，
        Oozie的协调员-0.5.xsd，Oozie的-SLA-0.1.xsd，Oozie的-SLA-0.2.xsd
    </ 值 >
    < description >
        协调员模式列表（以逗号分隔）。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaService.coord.ext.schemas </ name >
    < value > </ value >
    < description >
        协调员的附加模式清单（以逗号分隔）。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaService.bundle.schemas </ name >
    < 值 >
        Oozie的束-0.1.xsd，Oozie的束-0.2.xsd
    </ 值 >
    < description >
        软件包模式列表（以逗号分隔）。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaService.bundle.ext.schemas </ name >
    < value > </ value >
    < description >
        软件包附加模式列表（以逗号分隔）。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaService.sla.schemas </ name >
    < 值 >
        GMS-Oozie的-SLA-0.1.xsd，Oozie的-SLA-0.2.xsd
    </ 值 >
    < description >
        GMS SLA的语义验证模式列表（用逗号分隔）。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaService.sla.ext.schemas </ name >
    < value > </ value >
    < description >
        GMS SLA的语义验证附加模式列表（用逗号分隔）。
    </ description >
</ property >

<！ - CallbackService - >

< property >
    < name > oozie.service.CallbackService.base.url </ name >
    < value > $ {oozie.base.url} / callback </ value >
    < description >
         ActionExecutors使用的基本回调URL。
    </ description >
</ property >

< property >
    < name > oozie.service.CallbackService.early.requeue.max.retries </ name >
    < value > 5 </ value >
    < description >
        如果Oozie太早接收到一个回调（当动作处于PREP状态时），它会多次重新执行命令
        给予行动时间过渡到运行。
    </ description >
</ property >

<！ - CallbackServlet - >

< property >
    < name > oozie.servlet.CallbackServlet.max.data.len </ name >
    < value > 2048 </ value >
    < description >
        动作完成数据输出的最大字符数。
    </ description >
</ property >

<！ -外部统计- >

< property >
    < name > oozie.external.stats.max.size </ name >
    < value > -1 </ value >
    < description >
        操作统计的最大大小（以字节为单位）。-1意味着无限的价值。
    </ description >
</ property >

<！ - JobCommand - >

< property >
    < name > oozie.JobCommand.job.console.url </ name >
    < value > $ {oozie.base.url}？job = </ value >
    < description >
         工作流作业的基本控制台URL。
    </ description >
</ property >


<！ - ActionService - >

< property >
    < name > oozie.service.ActionService.executor.classes </ name >
    < 值 >
        org.apache.oozie.action.decision.DecisionActionExecutor，
        org.apache.oozie.action.hadoop.JavaActionExecutor，
        org.apache.oozie.action.hadoop.FsActionExecutor，
        org.apache.oozie.action.hadoop.MapReduceActionExecutor，
        org.apache.oozie.action.hadoop.PigActionExecutor，
        org.apache.oozie.action.hadoop.HiveActionExecutor，
        org.apache.oozie.action.hadoop.ShellActionExecutor，
        org.apache.oozie.action.hadoop.SqoopActionExecutor，
        org.apache.oozie.action.hadoop.DistcpActionExecutor，
        org.apache.oozie.action.hadoop.Hive2ActionExecutor，
        org.apache.oozie.action.ssh.SshActionExecutor，
        org.apache.oozie.action.oozie.SubWorkflowActionExecutor，
        org.apache.oozie.action.email.EmailActionExecutor，
        org.apache.oozie.action.hadoop.SparkActionExecutor
    </ 值 >
    < description >
        ActionExecutors类的列表（用逗号分隔）。
        只有具有关联执行程序的操作类型才能在工作流中使用。
    </ description >
</ property >

< property >
    < name > oozie.service.ActionService.executor.ext.classes </ name >
    < value > </ value >
    < description >
        ActionExecutors扩展类列表（用逗号分隔）。只有与关联的动作类型
        执行者可以在工作流中使用。这个属性是一个方便的属性来添加扩展到内置的
        在执行者，而不必包括所有内置的。
    </ description >
</ property >

<！ - ActionCheckerService - >

< property >
    < name > oozie.service.ActionCheckerService.action.check.interval </ name >
    < value > 60 </ value >
    < description >
        ActionCheckService将运行的频率。
    </ description >
</ property >

 < property >
    < name > oozie.service.ActionCheckerService.action.check.delay </ name >
    < value > 600 </ value >
    < description >
        ActionCheck之间的时间，以秒为单位。
    </ description >
</ property >

< property >
    < name > oozie.service.ActionCheckerService.callable.batch.size </ name >
    < value > 10 </ value >
    < description >
        这个值决定了一起被分配的动作的数量
        由单个线程执行。
    </ description >
</ property >

<！ - StatusTransitService - >
< property >
    < name > oozie.service.StatusTransitService.statusTransit.interval </ name >
    < value > 60 </ value >
    < description >
        StatusTransitService将运行的频率（以秒为单位）。
    </ description >
</ property >

< property >
    < name > oozie.service.StatusTransitService.backward.support.for.coord.status </ name >
    < value > false </ value >
    < description >
        如果协调员作业使用“uri：oozie：coordinator：0.1”提交，并希望保持Oozie 2.x状态转移，则为true。
        如果设置为真，
        1.协调员工作的成功意味着完成工作。
        2.协调员工作中没有DONEWITHERROR状态
        3.协调员工作中没有PAUSED或PREPPAUSED状态
        4. PREPSUSPENDED在协调员工作中变为暂停
    </ description >
</ property >

< property >
    < name > oozie.service.StatusTransitService.backward.support.for.states.without.error </ name >
    < value > true </ value >
    < description >
        真的，如果你想保持Oozie 3.2状态过境。
        将其更改为Oozie 4.x版本为false。
        如果设置为真，
        没有像RUNNINGWITHERROR，SUSPENDEDWITHERROR和PAUSEDWITHERROR的状态
        协调员和捆绑
    </ description >
</ property >

<！ - PauseTransitService - >
< property >
    < name > oozie.service.PauseTransitService.PauseTransit.interval </ name >
    < value > 60 </ value >
    < description >
        PauseTransitService将运行的频率（以秒为单位）。
    </ description >
</ property >

<！ - LauncherAMUtils - >
< property >
    < name > oozie.action.max.output.data </ name >
    < value > 2048 </ value >
    < description >
        输出数据的最大字符数。
    </ description >
</ property >

< property >
    < name > oozie.action.fs.glob.max </ name >
    < value > 50000 </ value >
    < description >
        最大数量的globbed文件。
    </ description >
</ property >

<！ - JavaActionExecutor - >
<！ -这对于Java的动作执行者的子类（例如map-reduce，pig，hive，java等）是很常见的- >

< property >
    < name > oozie.action.launcher.am.restart.kill.childjobs </ name >
    < value > true </ value >
    < description >
        在RM重启，AM恢复时，由于RM非工作保持恢复，可能会发生多个启动器作业实例
        由于崩溃或AM网络连接丢失。这也可能导致旧AM尝试的孤儿工作
        导致冲突运行。这会使用YARN应用程序标记杀死之前尝试的子作业。
    </ description >
</ property >

< property >
    < name > oozie.action.spark.setup.hadoop.conf.dir </ name >
    < value > false </ value >
    < description >
        Oozie action.xml（oozie.action.conf.xml）包含所有的hadoop配置和用户提供的配置。
        该属性将允许用户将Hadoop action.xml复制为hadoop * -site配置文件。好处是，
        用户不需要将这些文件管理成spark sharelib。如果用户想要管理hadoop配置
        本身，应该禁用它。
    </ description >
</ property >

< property >
    < name > oozie.action.shell.setup.hadoop.conf.dir </ name >
    < value > false </ value >
    < description >
        Shell操作通常用于运行依赖于HADOOP_CONF_DIR的程序（例如，hive，beeline，sqoop等）。同
        YARN，HADOO_CONF_DIR被设置为NodeManager的Hadoop的* -site.xml文件的副本，这可能是有问题的，因为
        （一）他们是为了NM的意思，不一定是客户，（二）他们将没有任何配置Oozie，或
        用户通过Oozie，套。当此属性设置为true时，Shell操作将准备* -site.xml文件
        基于正确的配置并将HADOOP_CONF_DIR设置为指向它。设置为false将使Oozie离开
        单独的HADOOP_CONF_DIR。这也可以通过将其置于Shell Action的配置中而在Action级别进行设置
        部分，这也具有先决条件。也就是说，建议尽可能使用适当的操作类型。
    </ description >
</ property >

< property >
    < name > oozie.action.shell.setup.hadoop.conf.dir.write.log4j.properties </ name >
    < value > true </ value >
    < description >
        切换以控制是否应将log4j.properties文件写入准备好的配置目录中
        oozie.action.shell.setup.hadoop.conf.dir已启用。这用于使用命令来控制log4j的日志记录行为
        在shell动作脚本中运行，并确保日志记录不会影响输出数据捕获如果泄露到标准输出。
        写入文件的内容由oozie.action.shell.setup.hadoop.conf.dir.log4j.content的值确定。
    </ description >
</ property >

< property >
    < name > oozie.action.shell.setup.hadoop.conf.dir.log4j.content </ name >
    < 值 >
        log4j.rootLogger = INFO，控制台
        log4j.appender.console = org.apache.log4j.ConsoleAppender
        log4j.appender.console.target = System.err的
        log4j.appender.console.layout = org.apache.log4j.PatternLayout
        log4j.appender.console.layout.ConversionPattern =％d {yy / MM / dd HH：mm：ss}％p％c {2}：％m％n
    </ 值 >
    < description >
        将值写入log4j.properties文件下的config目录下创建的
        oozie.action.shell.setup.hadoop.conf.dir和oozie.action.shell.setup.hadoop.conf.dir.write.log4j.properties
        属性都启用。这些值必须按照Log4J的预期格式正确换行。
        阅读这个属性时，尾随和前面的空格将被修剪。
        这用于使用在shell动作脚本中运行的命令来控制log4j的日志行为。
    </ description >
</ property >

< property >
    < name > oozie.action.launcher.yarn.timeline-service.enabled </ name >
    < value > false </ value >
    < description >
        为启动器作业启用/禁用获取ATS授权令牌
        对于所有操作类型，YARN / Hadoop 2.6（在Hadoop 1中不起作用）默认情况下，如果tez-site.xml存在
        分布式缓存。
        这可以通过设置在每个操作的基础上被覆盖
        oozie.launcher.yarn.timeline-service.enabled在工作流的操作配置部分。
    </ description >
</ property >

< property >
    < name > oozie.action.pig.log.expandedscript </ name >
    < value > true </ value >
    < description >
        将展开的猪脚本记录在启动程序stdout日志中
    </ description >
</ property >

< property >
    < name > oozie.action.rootlogger.log.level </ name >
    < value > INFO </ value >
    < description >
        根记录器的记录级别
    </ description >
</ property >

<！ - HadoopActionExecutor - >
<！ -这对于map-reduce和pig - >的子类动作执行者是很常见的

< property >
    < name > oozie.action.retries.max </ name >
    < value > 3 </ value >
    < description >
       失败时执行操作的重试次数
    </ description >
</ property >

< property >
    < name > oozie.action.retry.interval </ name >
    < value > 10 </ value >
    < description >
        在失败的情况下重试动作的时间间隔
    </ description >
</ property >

< property >
    < name > oozie.action.retry.policy </ name >
    < value > periodic </ value >
    < description >
        重试失败时的操作策略。可能的值是周期性/指数性的
    </ description >
</ property >

<！ - SshActionExecutor - >

< property >
    < name > oozie.action.ssh.delete.remote.tmp.dir </ name >
    < value > true </ value >
    < description >
        如果设置为true，则会在执行ssh动作结束时删除临时目录。
    </ description >
</ property >

< property >
    < name > oozie.action.ssh.http.command </ name >
    < value > curl </ value >
    < description >
        用于回调oozie的命令通常是'curl'或'wget'。
        该命令必须在USER @ HOST box shell的PATH环境变量中可用。
    </ description >
</ property >

< property >
    < name > oozie.action.ssh.http.command.post.options </ name >
    < value >  -  data-binary @＃stdout --request POST --header“content-type：text / plain”</ value >
    < description >
        回调命令POST选项。
        当ssh动作被捕获时使用。
    </ description >
</ property >

< property >
    < name > oozie.action.ssh.allow.user.at.host </ name >
    < value > true </ value >
    < description >
        指定是否允许或将要替换由ssh动作指定的用户
        由工作用户
    </ description >
</ property >

<！ - SubworkflowActionExecutor - >

< property >
    < name > oozie.action.subworkflow.max.depth </ name >
    < value > 50 </ value >
    < description >
        子工作流的最大深度。例如，如果设置为3，则工作流可以启动subwf1，它可以启动subwf2，
        可以启动subwf3; 但是如果subwf3试图启动subwf4，那么该操作将失败。这有助于预防
        错误的工作流程从开始无限递归子工作流程。
    </ description >
</ property >

<！ - HadoopAccessorService - >

< property >
    < name > oozie.service.HadoopAccessorService.kerberos.enabled </ name >
    < value > false </ value >
    < description >
        指示是否将Oozie配置为使用Kerberos。
    </ description >
</ property >

< property >
    < name > local.realm </ name >
    < 值 > LOCALHOST </ 值 >
    < description >
        Kerberos领域由Oozie和Hadoop使用。使用“local.realm”与Hadoop配置保持一致
    </ description >
</ property >

< property >
    < name > oozie.service.HadoopAccessorService.keytab.file </ name >
    < value > $ {user.home} /oozie.keytab </ value >
    < description >
        Oozie用户密钥表文件的位置。
    </ description >
</ property >

< property >
    < name > oozie.service.HadoopAccessorService.kerberos.principal </ name >
    < value > $ {user.name} / localhost @ $ {local.realm} </ value >
    < description >
        Kerberos Oozie服务的负责人。
    </ description >
</ property >

< property >
    < name > oozie.service.HadoopAccessorService.jobTracker.whitelist </ name >
    < value > </ value >
    < description >
        白名单Oozie服务的工作跟踪器。
    </ description >
</ property >

< property >
    < name > oozie.service.HadoopAccessorService.nameNode.whitelist </ name >
    < value > </ value >
    < description >
        白名单Oozie服务的工作跟踪器。
    </ description >
</ property >

< property >
    < name > oozie.service.HadoopAccessorService.hadoop.configurations </ name >
    < value > * = hadoop-conf </ value >
    < description >
        逗号分隔的AUTHORITY = HADOOP_CONF_DIR，其中AUTHORITY是HOST：PORT
        Hadoop服务（JobTracker，YARN，HDFS）。通配符“*”的配置是
        在没有完全匹配权威的情况下使用。HADOOP_CONF_DIR包含
        相关的Hadoop * -site.xml文件。如果路径是相对的被查看
        Oozie配置目录; 尽管路径可以是绝对的（即指向
        到本地文件系统中的Hadoop客户机conf /目录。
    </ description >
</ property >


< property >
    < name > oozie.service.HadoopAccessorService.action.configurations </ name >
    < value > * = action-conf </ value >
    < description >
        逗号分隔的AUTHORITY = ACTION_CONF_DIR，其中AUTHORITY是HOST：PORT
        Hadoop MapReduce服务（JobTracker，YARN）。通配符“*”的配置是
        在没有完全匹配权威的情况下使用。ACTION_CONF_DIR可能包含
        ACTION.xml文件，其中ACTION是动作类型（'java'，'map-reduce'，'pig'，
        'hive'，'sqoop'等）。如果ACTION.xml文件存在，将使用其属性
        作为操作的默认属性。如果路径是相对的被查看
        Oozie配置目录; 尽管路径可以是绝对的（即指向
        到本地文件系统中的Hadoop客户机conf /目录。
    </ description >
</ property >

< property >
    < name > oozie.service.HadoopAccessorService.action.configurations.load.default.resources </ name >
    < value > true </ value >
    < description >
        true表示hadoop的默认和站点xml文件（core-default，core-site，
        hdfs-default，hdfs-site，mapred-default，mapred-site，yarn-default，yarn-site）
        在Oozie服务器上被解析成actionConf。false表示网站的xml文件是
        没有加载到服务器上，而是加载到启动器节点上。
        这只对pig和hive动作来处理加载这些文件
        自动从启动任务的类路径。它默认为true。
    </ description >
</ property >

<！ -凭证- >
< property >
    < name > oozie.credentials.credentialclasses </ name >
    < value > </ value >
    < description >
        CredentialsProvider的凭证类映射列表
    </ description >
</ property >
< property >
    < name > oozie.credentials.skip </ name >
    < value > false </ value >
    < description >
        这决定了Oozie是否应该跳过从凭据提供者获取凭证。这可以覆盖在一个
        工作级别或行动级别。
    </ description >
</ property >

< property >
    < name > oozie.actions.main.classnames </ name >
    < value > distcp = org.apache.hadoop.tools.DistCp </ value >
    < description >
        Action类的类名映射列表
    </ description >
</ property >

< property >
    < name > oozie.service.WorkflowAppService.system.libpath </ name >
    < value > / user / $ {user.name} / share / lib </ value >
    < description >
        用于工作流程应用程序的系统库路径。
        如果工作属性设置，则将此路径添加到工作流应用程序
        属性'oozie.use.system.libpath'为true。
    </ description >
</ property >

< property >
    < name > oozie.command.default.lock.timeout </ name >
    < value > 5000 </ value >
    < description >
        用于获取对实体的排他锁定的命令的默认超时（以毫秒为单位）。
    </ description >
</ property >

< property >
    < name > oozie.command.default.requeue.delay </ name >
    < value > 10000 </ value >
    < description >
        缺省时间（以毫秒为单位）用于延迟执行的命令。
    </ description >
</ property >
<！ - LiteWorkflowStoreService，工作流操作自动重试- >

< property >
    < name > oozie.service.LiteWorkflowStoreService.user.retry.max </ name >
    < value > 3 </ value >
    < description >
        工作流操作的自动重试最大数量默认为3。
    </ description >
</ property >

< property >
    < name > oozie.service.LiteWorkflowStoreService.user.retry.inteval </ name >
    < value > 10 </ value >
    < description >
        工作流操作的自动重试时间间隔为分钟，默认值为10分钟。
    </ description >
</ property >

< property >
    < name > oozie.service.LiteWorkflowStoreService.user.retry.policy </ name >
    < value > periodic </ value >
    < description >
        工作流操作的自动重试策略。可能的值是周期性的或指数性的，周期性是默认值。
    </ description >
</ property >

< property >
    < name > oozie.service.LiteWorkflowStoreService.user.retry.error.code </ name >
    < 值 > JA008，JA009，JA017，JA018，JA019，FS009，FS008，FS014 </ 值 >
    < description >
        针对这些指定的错误代码处理工作流操作的自动重试时间间隔：
        在fs操作中使用chmod时，FS009，FS008是文件存在错误。
        FS014是fs action中的权限错误
        JA018是工作流程map-reduce操作中的输出目录存在错误。
        JA019在执行distcp操作时出错。
        JA017是作业执行者中不存在的错误。
        JA008在动作执行器中是FileNotFoundException。
        JA009在动作执行器中是IOException。
        ALL是行为执行者中的任何一种错误。
    </ description >
</ property >

< property >
    < name > oozie.service.LiteWorkflowStoreService.user.retry.error.code.ext </ name >
    < value > </ value >
    < description >
        针对这些指定的额外错误代码处理工作流操作的自动重试时间间隔：
        ALL是行为执行者中的任何一种错误。
    </ description >
</ property >

< property >
    < name > oozie.service.LiteWorkflowStoreService.node.def.version </ name >
    < value > _oozie_inst_v_2 </ value >
    < description >
        NodeDef默认版本，_oozie_inst_v_0，_oozie_inst_v_1或_oozie_inst_v_2
    </ description >
</ property >

<！ - Oozie认证- >

< property >
    < name > oozie.authentication.type </ name >
    < value >简单</ value >
    < description >
        定义用于Oozie HTTP端点的认证。
        支持的值是：simple | kerberos | ＃AUTHENTICATION_HANDLER_CLASSNAME＃
    </ description >
</ property >
< property >
    < name > oozie.server.authentication.type </ name >
    < value > $ {oozie.authentication.type} </ value >
    < description >
        定义用于Oozie服务器通过HTTP与其他Oozie服务器通信的身份验证。
        支持的值是：simple | kerberos | ＃AUTHENTICATOR_CLASSNAME＃
    </ description >
</ property >

< property >
    < name > oozie.server.connection.timeout.seconds </ name >
    < value > 180 </ value >
    < description >
        定义用于Oozie服务器通过HTTP与其他Oozie服务器通信的连接超时。默认是3分钟。
    </ description >
</ property >

< property >
    < name > oozie.authentication.token.validity </ name >
    < value > 36000 </ value >
    < description >
        指示验证令牌有效之前（以秒为单位）有效
        被更新。
    </ description >
</ property >

< property >
  < name > oozie.authentication.cookie.domain </ name >
  < value > </ value >
  < description >
    用于存储身份验证令牌的HTTP cookie的域。
    为了验证跨多个主机正确工作
    该域必须正确设置。
  </ description >
</ property >

< property >
    < name > oozie.authentication.simple.anonymous.allowed </ name >
    < value > true </ value >
    < description >
        指示使用“简单”身份验证时是否允许匿名请求。
    </ description >
</ property >

< property >
    < name > oozie.authentication.kerberos.principal </ name >
    < value > HTTP / localhost @ $ {local.realm} </ value >
    < description >
        指示用于HTTP端点的Kerberos主体。
        根据Kerberos HTTP SPNEGO规范，主体必须以“HTTP /”开头。
    </ description >
</ property >

< property >
    < name > oozie.authentication.kerberos.keytab </ name >
    < value > $ {oozie.service.HadoopAccessorService.keytab.file} </ value >
    < description >
        keytab文件的位置与主体的凭据。
        引用Oozie用于Hadoop的Kerberos凭据的keytab文件。
    </ description >
</ property >

< property >
    < name > oozie.authentication.kerberos.name.rules </ name >
    < 值 >默认</ 值 >
    < description >
        kerberos的名字规则是解决kerberos的主要名字，参考Hadoop的
        KerberosName获取更多细节。
    </ description >
</ property >

<！ - Coordinator“NONE”执行顺序默认时间公差- >
< property >
    < name > oozie.coord.execution.none.tolerance </ name >
    < value > 1 </ value >
    < description >
        标准时间之后的默认时间公差（以分钟为单位）
        当执行顺序是“无”
    </ description >
</ property >

<！ -协调员操作默认长度- >
< property >
    < name > oozie.coord.actions.default.length </ name >
    < value > 1000 </ value >
    < description >
        info命令检索的协调器动作的默认数量
    </ description >
</ property >

<！ - ForkJoin验证- >
< property >
    < name > oozie.validate.ForkJoin </ name >
    < value > true </ value >
    < description >
        如果为true，则应在wf提交时验证fork和join。
    </ description >
</ property >

< property >
    < name > oozie.workflow.parallel.fork.action.start </ name >
    < value > true </ value >
    < description >
        确定Oozie如何处理分叉动作的开始。如果属实，分叉行为和他们的工作提交
        并行完成，这对性能是最好的。如果为false，则按顺序提交。
    </ description >
</ property >

< property >
    < name > oozie.coord.action.get.all.attributes </ name >
    < value > false </ value >
    < description >
        设置为true不建议使用，因为坐标作业/操作信息会将操作的所有列都带到内存中。
        只有在需要对操作/作业信息进行向后兼容时，才能将其设置为true。
    </ description >
</ property >

< property >
    < name > oozie.service.HadoopAccessorService.supported.filesystems </ name >
    < value > hdfs，hftp，webhdfs </ value >
    < description >
        争取支持联邦的不同文件系统。如果指定了通配符“*”
        那么所有的文件计划将被允许。
    </ description >
</ property >

< property >
    < name > oozie.service.URIHandlerService.uri.handlers </ name >
    < value > org.apache.oozie.dependency.FSURIHandler </ value >
    < description >
            争取支持数据可用性检查的不同uri处理程序。
    </ description >
</ property >
<！ - Oozie HTTP通知- >

< property >
    < name > oozie.notification.url.connection.timeout </ name >
    < value > 10000 </ value >
    < description >
        定义Oozie HTTP通知回调的超时时间（以毫秒为单位）。Oozie呢
        用于设置“oozie.wf.action.notification.url”的工作流作业的HTTP通知，
        'oozie.wf.worklfow.notification.url'和/或'oozie.coord.action.notification.url'
        属性在他们的job.properties。请参阅“5 Oozie通知”部分
        工作流规范的细节。
    </ description >
</ property >


<！ -为Hadoop 2.0.2-alpha（MAPREDUCE-4820）- >启用分布式缓存解决方案
< property >
    < name > oozie.hadoop-2.0.2-alpha.workaround.for.distributed.cache </ name >
    < value > false </ value >
    < description >
        由于Hadoop 2.0.2-alpha，MAPREDUCE-4820中的错误，启动器作业无法设置
        动作作业的分布式缓存，因为本地JAR是隐含的
        包括触发重复检查。
        该标志将删除操作的分布式缓存文件
        包括来自提交该行动的JobClient（MRApps）的本地JAR
        从发射器工作。
    </ description >
</ property >

< property >
    < name > oozie.service.EventHandlerService.filter.app.types </ name >
    < value > workflow_job，coordinator_action </ value >
    < description >
        工作流程/协调员/捆绑工作/行动中的应用程序类型
        系统启用的事件。
    </ description >
</ property >

< property >
    < name > oozie.service.EventHandlerService.event.queue </ name >
    < value > org.apache.oozie.event.MemoryEventQueue </ value >
    < description >
        EventHandlerService使用的EventQueue的实现。
    </ description >
</ property >

< property >
    < name > oozie.service.EventHandlerService.event.listeners </ name >
    < value > org.apache.oozie.jms.JMSJobEventListener </ value >
</ property >

< property >
    < name > oozie.service.EventHandlerService.queue.size </ name >
    < value > 10000 </ value >
    < description >
        事件队列中包含的最大事件数。
    </ description >
</ property >

< property >
    < name > oozie.service.EventHandlerService.worker.interval </ name >
    < value > 30 </ value >
    < description >
        工作线程计划运行的缺省间隔（秒）
        并处理事件。
    </ description >
</ property >

< property >
    < name > oozie.service.EventHandlerService.batch.size </ name >
    < value > 10 </ value >
    < description >
        批处理大小，用于从事件队列中为每个线程分批排水。
    </ description >
</ property >

< property >
    < name > oozie.service.EventHandlerService.worker.threads </ name >
    < value > 3 </ value >
    < description >
        要计划运行和处理事件的工作线程数。
    </ description >
</ property >

< property >
    < name > oozie.sla.service.SLAService.capacity </ name >
    < value > 5000 </ value >
    < description >
         内存结构中要包含的最大sla记录数。
    </ description >
</ property >

< property >
    < name > oozie.sla.service.SLAService.alert.events </ name >
    < 值 > END_MISS </ 值 >
    < description >
         被警告的SLA事件的默认类型。
    </ description >
</ property >

< property >
    < name > oozie.sla.service.SLAService.calculator.impl </ name >
    < value > org.apache.oozie.sla.SLACalculatorMemory </ value >
    < description >
         SLAService使用的SLACalculator的实现。
    </ description >
</ property >

< property >
    < name > oozie.sla.service.SLAService.job.event.latency </ name >
    < value > 90000 </ value >
    < description >
         以毫秒为单位的时间来说明获取作业状态事件的延迟时间
         比较反对和决定sla miss / met
    </ description >
</ property >

< property >
    < name > oozie.sla.service.SLAService.check.interval </ name >
    < value > 30 </ value >
    < description >
         SLA Worker将按计划运行的时间间隔（以秒为单位）
    </ description >
</ property >

< property >
    < name > oozie.sla.disable.alerts.older.than </ name >
    < value > 48 </ value >
    < description >
         时间阈值（以小时计），用于禁用SLA警报
         标称时间比这更老。
    </ description >
</ property >

<！ - ZooKeeper配置- >
< property >
    < name > oozie.zookeeper.connection.string </ name >
    < value > localhost：2181 </ value >
    < description >
        ZooKeeper服务器的主机：端口对的逗号分隔值。
    </ description >
</ property >

< property >
    < name > oozie.zookeeper.namespace </ name >
    < value > oozie </ value >
    < description >
        要使用的命名空间。所有正在计划互相交谈的Oozie服务器应该都是一样的
        命名空间。
    </ description >
</ property >

< property >
    < name > oozie.zookeeper.connection.timeout </ name >
    < value > 180 </ value >
    < description >
    默认ZK连接超时（以秒为单位）。
    </ description >
</ property >
< property >
    < name > oozie.zookeeper.session.timeout </ name >
    < value > 300 </ value >
    < description >
        默认ZK会话超时（以秒为单位）。如果重试后连接丢失，则Oozie服务器将关闭
        本身如果oozie.zookeeper.server.shutdown.ontimeout为真。
    </ description >
</ property >
< property >
    < name > oozie.zookeeper.max.retries </ name >
    < value > 10 </ value >
    < description >
        最大重试次数。
    </ description >
</ property >

< property >
    < name > oozie.zookeeper.server.shutdown.ontimeout </ name >
    < value > true </ value >
    < description >
        如果为true，Oozie服务器将在ZK上自行关闭
        连接超时。
    </ description >
</ property >

< property >
    < name > oozie.http.hostname </ name >
    < value > 0.0.0.0 </ value >
    < description >
        Oozie服务器主机名称。网络接口Oozie服务器绑定为IP地址或主机名。
        大多数用户不需要从默认值更改此设置。
    </ description >
</ property >

< property >
    < name > oozie.http.port </ name >
    < 值 > 11000 </ 值 >
    < description >
        Oozie服务器端口。
    </ description >
</ property >

< property >
    < name > oozie.http.request.header.size </ name >
    < value > 65536 </ value >
    < description >
        Oozie HTTP请求标头大小。
    </ description >
</ property >

< property >
    < name > oozie.http.response.header.size </ name >
    < value > 65536 </ value >
    < description >
        Oozie HTTP响应头大小。
    </ description >
</ property >

< property >
    < name > oozie.https.port </ name >
    < 值 > 11443 </ value >
    < description >
        Oozie SSL服务器端口。
    </ description >
</ property >

< property >
    < name > oozie.https.enabled </ name >
    < value > false </ value >
    < description >
        控制是否启用SSL加密。
    </ description >
</ property >

< property >
    < name > oozie.https.truststore.file </ name >
    < value > </ value >
    < description >
        TrustStore文件的路径。
    </ description >
</ property >

< property >
    < name > oozie.https.truststore.pass </ name >
    < value > </ value >
    < description >
        TrustStore的密码。
    </ description >
</ property >

< property >
    < name > oozie.https.keystore.file </ name >
    < value > </ value >
    < description >
        KeyStore文件的路径。
    </ description >
</ property >

< property >
    < name > oozie.https.keystore.pass </ name >
    < value > </ value >
    < description >
        密码到KeyStore。
    </ description >
</ property >

< property >
    < name > oozie.https.include.protocols </ name >
    < value > TLSv1，SSLv2Hello，TLSv1.1，TLSv1.2 </ value >
    < description >
        启用TLS协议。
    </ description >
</ property >

< property >
    < name > oozie.https.exclude.protocols </ name >
    < value > </ value >
    < description >
        禁用TLS协议。
    </ description >
</ property >

< property >
    < name > oozie.https.include.cipher.suites </ name >
    < value > </ value >
    < description >
        包含密码套件的列表。
    </ description >
</ property >

< property >
    < name > oozie.https.exclude.cipher.suites </ name >
    < 值 > TLS_ECDHE_RSA_WITH_RC4_128_SHA，SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA，SSL_RSA_WITH_DES_CBC_SHA，SSL_DHE_RSA_WITH_DES_CBC_SHA，SSL_RSA_EXPORT_WITH_RC4_40_MD5，SSL_RSA_EXPORT_WITH_DES40_CBC_SHA，SSL_RSA_WITH_RC4_128_MD5 </ 值 >
    < description >
        要排除的弱密码套件列表。
    </ description >
</ property >

< property >
    < name > oozie.jsp.tmp.dir </ name >
    < value > / tmp </ value >
    < description >
        用于编译JSP页面的临时目录。
    </ description >
</ property >

< property >
    < name > oozie.server.threadpool.max.threads </ name >
    < value > 150 </ value >
    < description >
         控制Oozie服务器的线程池大小（如果使用嵌入式Jetty）
    </ description >
</ property >

<！ - Sharelib配置- >
< property >
    < name > oozie.service.ShareLibService.mapping.file </ name >
    < value > </ value >
    < description >
        Sharelib映射文件包含key = value列表，
        其中key将是操作的sharelib名称，value是以逗号分隔的列表
        DFS或本地文件系统目录或jar文件。
        例。
        oozie.pig_10 = HDFS：///share/lib/pig/pig-0.10.1/lib/
        oozie.pig = HDFS：///share/lib/pig/pig-0.11.1/lib/
        oozie.distcp = HDFS：///share/lib/hadoop-2.2.0/share/hadoop/tools/lib/hadoop-distcp-2.2.0.jar
        oozie.hive =文件：/// USR /本地/ Oozie的/共享/ LIB /蜂巢/
    </ description >

</ property >
    < property >
    < name > oozie.service.ShareLibService.fail.fast.on.startup </ name >
    < value > false </ value >
    < description >
        如果sharelib初始化失败，服务器失败将失败。
    </ description >
</ property >

< property >
    < name > oozie.service.ShareLibService.purge.interval </ name >
    < value > 1 </ value >
    < description >
        多久Oozie应该检查旧的ShareLibs和LauncherLibs以清除HDFS。
    </ description >
</ property >

< property >
    < name > oozie.service.ShareLibService.temp.sharelib.retention.days </ name >
    < value > 7 </ value >
    < description >
        ShareLib保留时间（以天为单位）。
    </ description >
</ property >

< property >
    < name > oozie.action.ship.launcher.jar </ name >
    < value > false </ value >
    < description >
        指定启动程序jar是否运送。
    </ description >
</ property >

< property >
    < name > oozie.action.jobinfo.enable </ name >
    < value > false </ value >
    < description >
    JobInfo将包含包，协调员，工作流和操作的信息。如果启用，hadoop的工作将有
    属性（oozie.job.info），其值是由“，”分隔的多个键/值对。这个信息可以用于
    分析，例如在一个特定的时期内提交了多少个oozie作业，失败的猪作业总数是多少，
    从mapreduce作业历史日志和配置等。
    用户还可以添加自定义工作流属性到jobinfo，通过添加属性前缀“oozie.job.info”。
    例如。
    oozie.job.info =“bundle.id =，bundle.name =，coord.name =，coord.nominal.time =，coord.name =，wf.id =，
    wf.name =，= action.name，action.type =，=发射真正的”
    </ description >
</ property >

< property >
    < name > oozie.service.XLogStreamingService.max.log.scan.duration </ name >
    < value > -1 </ value >
    < description >
    最大日志扫描持续时间（小时）如果日志扫描请求end_date  -  start_date>值，
    然后抛出异常来减少扫描持续时间。-1表示没有限制。
    </ description >
</ property >

< property >
    < name > oozie.service.XLogStreamingService.actionlist.max.log.scan.duration </ name >
    < value > -1 </ value >
    < description >
    指定操作列表时，协调器作业的最大日志扫描持续时间（以小时为单位）。
    如果日志流请求end_date  -  start_date>值，则抛出异常以减少扫描持续时间。
    -1表示没有限制。
    此设置与max.log.scan.duration分开，因为我们希望在指定操作时允许更长的持续时间。
    </ description >
</ property >

<！ - JvmPauseMonitorService配置- >
< property >
    < name > oozie.service.JvmPauseMonitorService.warn-threshold.ms </ name >
    < value > 10000 </ value >
    < description >
        JvmPauseMonitorService运行一个线程，它反复尝试检测JVM何时暂停，这可能表示
        JVM或主机超载或其他问题。这个线程睡500ms; 如果它睡着了
        明显更长，那么可能有一个问题。这个属性指定Oozie应该记录的线程
        WARN级别的消息; 还有一个名为“jvm.pause.warn-threshold”的计数器。
    </ description >
</ property >

< property >
    < name > oozie.service.JvmPauseMonitorService.info-threshold.ms </ name >
    < value > 1000 </ value >
    < description >
        JvmPauseMonitorService运行一个线程，它反复尝试检测JVM何时暂停，这可能表示
        JVM或主机超载或其他问题。这个线程睡500ms; 如果它睡着了
        明显更长，那么可能有一个问题。这个属性指定Oozie应该记录的线程
        一个INFO级别的消息; 还有一个名为“jvm.pause.info-threshold”的计数器。
    </ description >
</ property >

< property >
    < name > oozie.service.ZKLocksService.locks.reaper.threshold </ name >
    < value > 300 </ value >
    < description >
        ChildReaper运行的频率。
        持续时间应该是秒。默认是5分钟。
    </ description >
</ property >

< property >
    < name > oozie.service.ZKLocksService.locks.reaper.threads </ name >
    < value > 2 </ value >
    < description >
        ChildReaper使用的固定线程数
        删除空锁。
    </ description >
</ property >

< property >
    < name > oozie.service.AbandonedCoordCheckerService.check.interval
    </ name >
    < value > 1440 </ value >
    < description >
        AbandonedCoordCheckerService应运行的时间间隔（分钟）。
    </ description >
</ property >

< property >
    < name > oozie.service.AbandonedCoordCheckerService.check.delay
    </ name >
    < value > 60 </ value >
    < description >
        AbandonedCoordCheckerService运行的延迟时间，以分钟为单位。
    </ description >
</ property >

< property >
    < name > oozie.service.AbandonedCoordCheckerService.failure.limit
    </ name >
    < value > 25 </ value >
    < description >
        失败的限制。如果一个工作的总次数被认为是被放弃/错误的
        失败/超时/暂停> =“失败限制”，并没有成功的行动。
    </ description >
</ property >

< property >
    < name > oozie.service.AbandonedCoordCheckerService.kill.jobs
    </ name >
    < value > false </ value >
    < description >
        如果属实的话，AbandonedCoordCheckerService会杀死被遗弃的coords。
    </ description >
</ property >

< property >
    < name > oozie.service.AbandonedCoordCheckerService.job.older.than </ name >
    < value > 2880 </ value >
    < description >
     在几分钟内，如果工作年龄大于这个值，工作将被视为被遗弃/有缺陷。
    </ description >
</ property >

< property >
    < name > oozie.notification.proxy </ name >
    < value > </ value >
    < description >
     作业通知的系统级别代理设置。
    </ description >
</ property >

< property >
    < name > oozie.wf.rerun.disablechild </ name >
    < value > false </ value >
    < description >
        通过设置此选项，如果存在父级工作流或协调员，则工作流重新运行将被禁用
        它只会通过父母重新运行。
    </ description >
</ property >

< property >
    < name > oozie.use.system.libpath </ name >
    < value > false </ value >
    < description >
        oozie.use.system.libpath的默认值。如果用户没有指定= oozie.use.system.libpath =
        在job.properties和这个值是真实的，Oozie将包括工作流的sharelib罐子。
    </ description >
</ property >

< property >
    < name > oozie.service.PauseTransitService.callable.batch.size
    </ name >
    < value > 10 </ value >
    < description >
        这个值决定了可以一起调用的可调用的数量
        由单个线程执行。
    </ description >
</ property >

<！ - XConfiguration - >
< property >
    < name > oozie.configuration.substitute.depth </ name >
    < value > 20 </ value >
    < description >
        该值决定配置中的替换深度。
        如果设置-1，则不会限制替换。
    </ description >
</ property >

< property >
    < name > oozie.service.SparkConfigurationService.spark.configurations </ name >
    < value > * = spark-conf </ value >
    < description >
        逗号分隔的AUTHORITY = SPARK_CONF_DIR，其中AUTHORITY是HOST：PORT
        YARN集群的ResourceManager。通配符“*”的配置是
        在没有完全匹配权威的情况下使用。SPARK_CONF_DIR包含
        相关的spark-defaults.conf属性文件。如果路径是相对的被查看
        Oozie配置目录; 尽管路径可以是绝对的。这只用于
        当Spark主机被设置为“yarn-client”或“yarn-cluster”时。
    </ description >
</ property >

< property >
    < name > oozie.service.SparkConfigurationService.spark.configurations.blacklist </ name >
    < value > spark.yarn.jar，spark.yarn.jars </ value >
    < description >
         逗号分隔的属性列表，以忽略从中指定的任何Spark配置
         oozie.service.SparkConfigurationService.spark.configurations属性。
    </ description >
</ property >

< property >
    < name > oozie.service.SparkConfigurationService.spark.configurations.ignore.spark.yarn.jar </ name >
    < value > true </ value >
    < description >
         已过时。改为使用oozie.service.SparkConfigurationService.spark.configurations.blacklist。
         如果为true，则Oozie将忽略来自任何Spark配置中的“spark.yarn.jar”属性
         oozie.service.SparkConfigurationService.spark.configurations。如果错误，Oozie不会忽略它。建议
         因为这可能会干扰Spark sharelib中的罐子，所以将其保留为true。
     </ description >
</ property >

< property >
    < name > oozie.email.attachment.enabled </ name >
    < value > true </ value >
    < description >
        该值决定是否支持HDFS上的电子邮件附件。
        如果有任何安全问题，请将其设置为false。
    </ description >
</ property >

< property >
  < name > oozie.email.smtp.host </ name >
  < value > localhost </ value >
  < description >
      电子邮件操作可能找到SMTP服务器的主机。
  </ description >
</ property >

< property >
  < name > oozie.email.smtp.port </ name >
  < value > 25 </ value >
  < description >
      连接到SMTP服务器的端口，用于电子邮件操作。
  </ description >
</ property >

< property >
  < name > oozie.email.smtp.auth </ name >
  < value > false </ value >
  < description >
      在使用电子邮件操作时，如果要执行身份验证，则切换布尔属性。
  </ description >
</ property >

< property >
  < name > oozie.email.smtp.username </ name >
  < value > </ value >
  < description >
      如果对电子邮件操作启用了身份验证，则将登录的用户名（SMTP服务器）。
  </ description >
</ property >

< property >
  < name > oozie.email.smtp.password </ name >
  < value > </ value >
  < description >
      如果对电子邮件操作启用了身份验证，则使用（到SMTP服务器）登录的密码。
  </ description >
</ property >

< property >
  < name > oozie.email.from.address </ name >
  < value > oozie @ localhost </ value >
  < description >
      发件人地址用于邮寄通过电子邮件操作完成的所有电子邮件。
  </ description >
</ property >

< property >
  < name > oozie.email.smtp.socket.timeout.ms </ name >
  < value > 10000 </ value >
  < description >
      应用于电子邮件操作期间完成的所有SMTP服务器套接字操作的超时。
  </ description >
</ property >

< property >
    < name > oozie.actions.default.name-node </ name >
    < value > </ value >
    < description >
        用于＆lt; 名称节点＆gt; 元素适用的操作类型。这个值将被使用
        动作本身和全局部分都不指定＆lt; 名称节点＆gt; 。不出所料，它应该是这样的形式
        “HDFS：// HOST：PORT”。
    </ description >
</ property >

< property >
    < name > oozie.actions.default.job-tracker </ name >
    < value > </ value >
    < description >
        用于＆lt; 工作跟踪器＆gt; 元素适用的操作类型。这个值将被使用
        动作本身和全局部分都不指定＆lt; 工作跟踪器＆gt; 。不出所料，它应该是这样的形式
        “HOST：PORT”。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaCheckerService.check.interval </ name >
    < value > 168 </ value >
    < description >
        这是Oozie检查数据库模式的时间间隔，以小时为单位。
        零或负值将禁用检查器。
    </ description >
</ property >

< property >
    < name > oozie.service.SchemaCheckerService.ignore.extras </ name >
    < value > false </ value >
    < description >
        设置为false时，模式检查器将考虑额外的（未使用的）表，列和索引不正确。什么时候
        设置为true，这些将被忽略。
    </ description >
</ property >

< property >
    < name > oozie.hcat.uri.regex.pattern </ name >
    < 值 >（[AZ] +：// [\ W \ \ - ] +：\ d + [，] *）？+ / \ W + / \ W + / [\ W + =; \ - ] * </ 值 >
    < description > HCat URI的正则表达式模式。正则表达式可以根据需要由用户修改
        用于解析/拆分HCat URI。</ description >
</ property >

< property >
    < name > oozie.action.null.args.allowed </ name >
    < value > true </ value >
    < description >
        当设置为true时，空参数（如＆lt; arg ＆gt;＆lt; / arg ＆gt;）将作为“null”传递给
        给予行动。也就是说，args []数组将包含“null”元素。当设置为false时，“nulls”被删除。
    </ description >
</ property >

< property >
    < name > oozie.javax.xml.parsers.DocumentBuilderFactory </ name >
    < value > org.apache.xerces.jaxp.DocumentBuilderFactoryImpl </ value >
    < description >
        Oozie会将javax.xml.parsers.DocumentBuilderFactory Java系统属性设置为这个值。这有助于加速
        XML处理，因为JVM不必每次都搜索适当的类。空白或空白值
        跳过设置系统属性。Oozie使用的默认实现是Xerces。
        大多数用户不应该改变这一点。
    </ description >
</ property >

< property >
    < name > oozie.graphviz.timeout.seconds </ name >
    < value > 60 </ value >
    < description >
        Graphviz图形生成的默认秒数将超时。
    </ description >
</ property >

< property >
    < name > oozie.launcher.default.vcores </ name >
    < value > 1 </ value >
    < description >
        为启动器AM分配的默认vcore数量
    </ description >
</ property >

< property >
    < name > oozie.launcher.default.memory.mb </ name >
    < value > 2048 </ value >
    < description >
        为启动器AM分配的默认内存量（MB）
    </ description >
</ property >

< property >
    < name > oozie.launcher.default.priority </ name >
    < value > 0 </ value >
    < description >
        启动器AM的默认YARN优先级
    </ description >
</ property >

< property >
    < name > oozie.launcher.default.queue </ name >
    < value > default </ value >
    < description >
        启动器AM所在的默认YARN队列
    </ description >
</ property >

< property >
    < name > oozie.launcher.default.max.attempts </ name >
    < value > 2 </ value >
    < description >
        启动器AM的默认YARN最大尝试次数
    </ description >
</ property >

< property >
    < name > oozie.launcher.override </ name >
    < value > true </ value >
    < description >
        提交YARN时是否需要考虑oozie.launcher.override。*和oozie.launcher.prepend。*参数
        LauncherAM。也就是说，操作配置中使用的现有MapReduce v1，MapReduce v2或YARN参数应该是
        填充到应用程序主启动器配置，或不。通常，第一和LT; 启动器/ ＆gt; 标记特定的用户
        设置，然后YARN配置设置，然后MapReduce V2，最后，MapReduce v1属性被复制到
        启动器配置。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.max.attempts </ name >
    < value > mapreduce.map.maxattempts，mapred.map.max.attempts </ value >
    < description >
        MapReduce v1和MapReduce v2属性的逗号分隔列表，以覆盖MapReduce的最大尝试次数
        应用程序主。找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.memory.mb </ name >
    < value > yarn.app.mapreduce.am.resource.mb，mapreduce.map.memory.mb，mapred.job.map.memory.mb </ value >
    < description >
        以逗号分隔的MapReduce v1，MapReduce v2和YARN属性列表，以覆盖
        MapReduce应用程序主文件。找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.vcores </ name >
    < 值 > yarn.app.mapreduce.am.resource.cpu-vcores，mapreduce.map.cpu.vcores </ 值 >
    < description >
        以逗号分隔的MapReduce v1，MapReduce v2和YARN属性列表覆盖CPU的vcore计数
        MapReduce应用程序主文件。找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.log.level </ name >
    < value > mapreduce.map.log.level，mapred.map.child.log.level </ value >
    < description >
        MapReduce v1，MapReduce v2和YARN属性的逗号分隔列表，以覆盖MapReduce的日志记录级别
        应用程序主。找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.javaopts </ name >
    < value > yarn.app.mapreduce.am.command-opts，mapreduce.map.java.opts，mapred.child.java.opts </ value >
    < description >
        MapReduce v1，MapReduce v2和YARN属性的逗号分隔列表，以覆盖MapReduce Application Master JVM
        选项。找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.prepend.javaopts </ name >
    < value > yarn.app.mapreduce.am.admin-command-opts </ value >
    < description >
        YARN属性的逗号分隔列表，以预先添加到MapReduce Application Master JVM选项。第一个是
        找到的将被添加到JVM选项的列表中。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.env </ name >
    < value > yarn.app.mapreduce.am.env，mapreduce.map.env，mapred.child.env </ value >
    < description >
        MapReduce v1，MapReduce v2和YARN属性的逗号分隔列表，以覆盖MapReduce Application Master
        环境变量设置。找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.prepend.env </ name >
    < value > yarn.app.mapreduce.am.admin.user.env </ value >
    < description >
        YARN属性的逗号分隔列表，用于添加到MapReduce Application Master环境设置。第一个
        找到的将被预先添加到环境设置列表中。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.priority </ name >
    < value > mapreduce.job.priority，mapred.job.priority </ value >
    < description >
        以逗号分隔的MapReduce v1和MapReduce v2列表覆盖MapReduce Application Master作业的优先级。首先
        找到一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.queue </ name >
    < value > mapreduce.job.queuename，mapred.job.queue.name </ value >
    < description >
        以逗号分隔的MapReduce v1和MapReduce v2属性列表覆盖MapReduce Application Master作业队列
        名称。找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.view.acl </ name >
    < value > mapreduce.job.acl-view-job </ value >
    < description >
        以逗号分隔的MapReduce v1和MapReduce v2属性列表，以覆盖MapReduce View ACL设置。
        找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.launcher.override.modify.acl </ name >
    < value > mapreduce.job.acl-modify-job </ value >
    < description >
        以逗号分隔的MapReduce v1和MapReduce v2属性的列表覆盖MapReduce修改ACL设置。
        找到的第一个将被使用。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.needed.for.distcp </ name >
    < value > true </ value >
    < description >
    是否将MapReduce jar添加到DistCp操作的类路径中。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.needed.for.hive </ name >
    < value > true </ value >
    < description >
        是否将MapReduce jar添加到Hive操作的类路径默认情况下。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.needed.for.hive2 </ name >
    < value > true </ value >
    < description >
        是否将MapReduce jar添加到Hive2 action的classpath默认情况下。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.needed.for.java </ name >
    < value > true </ value >
    < description >
        是否默认将MapReduce jar添加到Java动作的类路径中。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.needed.for.map-reduce </ name >
    < value > true </ value >
    < description >
        是否将MapReduce jar添加到Map-Reduce操作的类路径中。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.needed.for.pig </ name >
    < value > true </ value >
    < description >
        是否将MapReduce jar添加到Pig动作的类路径默认情况下。
    </ description >
</ property >

< property >
    < name > oozie.action.mapreduce.needed.for.sqoop </ name >
    < value > true </ value >
    < description >
        是否将MapReduce Jar添加到Sqoop动作的类路径中。
    </ description >
</ property >
```