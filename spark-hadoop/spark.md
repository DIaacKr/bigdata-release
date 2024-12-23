# MapReduce测试K-Means迭代算法
## 镜像构建或载入
可选择利用tar包载入镜像：`docker load -i my-spark-hadoop-image.tar`  
或者利用dockerfile自行构建镜像。  
## Hadoop-Spark集群环境构建
依据`docker-compose.yml`文件构建集群环境：`docker-compose up`  
放置数据文件到HDFS：
`hdfs dfs -put /opt/data/data.csv /`  
## 实验内容
让两个工作节点以4核3G内存的配置运行执行kmeans任务：
```bash
$SPARK_HOME/bin/spark-submit --master spark://hadoop-spark-master:7077 --num-executors 2 --executor-cores 4 --executor-memory 3g /opt/py/kmeans.py
```