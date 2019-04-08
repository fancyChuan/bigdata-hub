## spark学习环境搭建(windows)

### 配置环境变量
```
新增环境变量 SPARK_HOME 值为 D:\ProgramData\spark-2.3.3-bin-hadoop2.7
PATH在后面追加 %SPARK_HOME%\bin
```

### python
- 使用原生的python终端解释器
```
pyspark
```
- 使用jupyter notebook
```
# 1. 设置环境变量 PYSPARK_DRIVER_PYTHON，值设为jupyter
# 2. 设置环境变量 PYSPARK_DRIVER_PYTHON_OPTS，值设为notebook
# 3. 进入到想要运行的位置执行启动命令
pyspark
```
### java
使用maven
```maven
groupId: org.apache.spark
artifactId: spark-core_2.11
version: 2.3.3
```
对应maven中央仓库的位置：http://repo.maven.apache.org/maven2/org/apache/spark/spark-core_2.11/2.3.3/
### scala
```
spark-shell
```



### 其他
pyspark启动源码
```
call "%~dp0find-spark-home.cmd"

call "%SPARK_HOME%\bin\load-spark-env.cmd"
set _SPARK_CMD_USAGE=Usage: bin\pyspark.cmd [options]

rem Figure out which Python to use.
if "x%PYSPARK_DRIVER_PYTHON%"=="x" (
  set PYSPARK_DRIVER_PYTHON=python
  if not [%PYSPARK_PYTHON%] == [] set PYSPARK_DRIVER_PYTHON=%PYSPARK_PYTHON%
)

set PYTHONPATH=%SPARK_HOME%\python;%PYTHONPATH%
set PYTHONPATH=%SPARK_HOME%\python\lib\py4j-0.10.7-src.zip;%PYTHONPATH%

set OLD_PYTHONSTARTUP=%PYTHONSTARTUP%
set PYTHONSTARTUP=%SPARK_HOME%\python\pyspark\shell.py

call "%SPARK_HOME%\bin\spark-submit2.cmd" pyspark-shell-main --name "PySparkShell" %*
```

降低日志级别：log4j.properties.template
```
log4j.rootCategory=INFO, console 改为 log4j.rootCategory=WARN, console
```