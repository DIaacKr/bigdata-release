# Flink
## 镜像制作或载入
可以选择从tar包载入镜像：`docker load -i flink.tar`  
也可选择依据给出的`dockerfile`自行构建镜像。
## flink集群环境构建
依据`docker-compose.yml`文件构建集群环境：`docker-compose up`
## 简单测试
进入jobmanager容器内，运行flink官方给的kmeans测试样例：`./bin/flink run examples/batch/KMeans.jar --output Print`
