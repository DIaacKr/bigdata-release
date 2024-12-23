# MapReduce测试K-Means迭代算法
## 环境搭建
### 镜像载入
下载镜像文件hadoop.tar  
`docker load -i hadoop.tar`   
得到hadoop2镜像  
### 开启容器

在三个shell下分别运行如下命令：

```bash
docker run -it -h h01 --name h01 -p 9870:9870 -p 8088:8088 hadoop2 /bin/bash
docker run -it -h h02 --name h02 -p 8042:8042 hadoop2 /bin/bash
docker run -it -h h03 --name h03 -p 8043:8042 hadoop2 /bin/bash
```

### 修改配置

修改h02,h03容器内的/usr/local/hadoop/etc/hadoop/workers文件，修改为localhost  
修改h01,h02,h03容器内的/etc/hosts文件，将ip地址与主机名进行映射  
eg:  
```
172.17.0.2      h01
172.17.0.3      h02
172.17.0.4      h03
```

## 运行测试
在h01中进行如下：  
开启hadoop，执行:`bash start-all.sh`  
在hdfs中创建文件夹`/center_input`和`/data_input_all`  
将`center.csv`上传至`/center_input`，将`data.csv`上传至`/data_input_all`  
若有`/kmeans_output`文件夹需要删除    

取消安全模式：`hdfs dfsadmin -safemode leave`  
可运行`top.py`脚本作为性能检测  
运行限定迭代次数为5的kmeans测试：`hadoop jar kmeans_all_5.jar`  
测试结束后自行Ctrl+C暂停`top.py`脚本  

退出：`bash stop-all.sh`  
再次开启环境使用`docker restart h01 h02 h03`

