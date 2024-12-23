#!/bin/bash

# 启动 SSH 服务
service ssh start

# 格式化 NameNode（仅第一次运行需要）
if [ ! -d "/hadoop/dfs/name" ]; then
  $HADOOP_HOME/bin/hdfs namenode -format
fi

# 启动 Hadoop 服务
$HADOOP_HOME/sbin/start-dfs.sh

# 启动 Spark 服务
if [ "$SPARK_MODE" == "master" ]; then
  $SPARK_HOME/bin/spark-class org.apache.spark.deploy.master.Master --host 0.0.0.0
elif [ "$SPARK_MODE" == "worker" ]; then
  $SPARK_HOME/bin/spark-class org.apache.spark.deploy.worker.Worker $SPARK_MASTER_URL
fi

# 保持容器运行
tail -f /dev/null