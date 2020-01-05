## Azkaban


组件：
- AzkabanWebServer：AzkabanWebServer是整个Azkaban工作流系统的主要管理者，用于用户登录认证、负责project管理、定时执行工作流、跟踪工作流执行进度等一系列任务。
- AzkabanExecutorServer：负责具体的工作流的提交、执行，它们通过mysql数据库来协调任务的执行


环境搭建
```
  242  cd  /opt/software/
  243  ll
  244  rz
  245  mkdir /opt/app/azkaban
  246  ll
  247  tar -zxvf azkaban-web-server-2.5.0.tar.gz -C ../app/azkaban/
  248  tar -zxvf azkaban-executor-server-2.5.0.tar.gz -C ../app/azkaban/
  249  tar -zxvf azkaban-sql-script-2.5.0.tar.gz -C ../app/azkaban/
  250  ll
  251  cd ../app/azkaban/
  252  ll
  253  cd ..
  254  mv azkaban azkaban2.5.0
  255  ll
  256  cd azkaban2.5.0/
  257  ll
  258  mv azkaban-web-2.5.0/ server
  259  ll server/
  260  ll
  261  mv azkaban-executor-2.5.0/ executor
  262  mysql -hhphost -uroot -p
  263  ll
  264  ll azkaban-2.5.0/
  265  keytool -keystore keystore -alias jetty -genkey -keyalg RSA
  266  cd server/
  267  keytool -keystore keystore -alias jetty -genkey -keyalg RSA
  268  ll
  269  keytool -keystore keystore -list
  270  cd ..
  271  pwd
  272  xcall sudo ln -s /opt/app/azkaban2.5.0 /usr/local/azkaban
  273  xcall /usr/local
  274  xcall ll /usr/local
  275  xcall ls /usr/local
  276  xcall ls -l /usr/local
```

### 实战案例
- 1.单一job
- 2.多个job