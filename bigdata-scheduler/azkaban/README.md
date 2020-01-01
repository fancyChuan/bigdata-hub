## Azkaban


组件：
- AzkabanWebServer：AzkabanWebServer是整个Azkaban工作流系统的主要管理者，用于用户登录认证、负责project管理、定时执行工作流、跟踪工作流执行进度等一系列任务。
- AzkabanExecutorServer：负责具体的工作流的提交、执行，它们通过mysql数据库来协调任务的执行


环境搭建

