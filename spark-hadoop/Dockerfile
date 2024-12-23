# 基于 bitnami/spark:latest 镜像
FROM bitnami/spark:latest

# 切换到 root 用户，安装必要依赖
USER root
# RUN apt-get update
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    util-linux \
    curl \
    wget \
    unzip \
    nano \
    sudo \
    openssh-client \
    openssh-server && \
    ssh-keygen -A && \
    mkdir -p /root/.ssh && \
    ssh-keygen -t rsa -P '' -f /root/.ssh/id_rsa && \
    cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys && \
    chmod 0600 /root/.ssh/authorized_keys && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 配置 SSH 服务
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config && \
    echo "StrictHostKeyChecking no" >> /etc/ssh/ssh_config

# 设置 Hadoop 和 Java 环境变量
ENV HADOOP_VERSION=3.3.6
ENV HADOOP_HOME=/opt/hadoop
ENV JAVA_HOME=/opt/bitnami/java
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$JAVA_HOME/bin
ENV HDFS_NAMENODE_USER=root
ENV HDFS_DATANODE_USER=root
ENV HDFS_SECONDARYNAMENODE_USER=root
ENV YARN_RESOURCEMANAGER_USER=root
ENV YARN_NODEMANAGER_USER=root

# 下载并安装 Hadoop
RUN wget -qO- https://mirrors.tuna.tsinghua.edu.cn/apache/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz | tar -xz -C /opt/ && \
    mv /opt/hadoop-${HADOOP_VERSION} $HADOOP_HOME && \
    rm -f /opt/hadoop-${HADOOP_VERSION}.tar.gz

# 复制 Hadoop 配置文件
COPY core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml
COPY hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml
RUN echo "export JAVA_HOME=${JAVA_HOME}" >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh
# 配置 Hadoop
#RUN mkdir -p $HADOOP_HOME/etc/hadoop && \
#    echo "export HDFS_NAMENODE_USER=${HDFS_NAMENODE_USER}" >> /etc/profile.d/hadoop.sh && \
#    echo "export HDFS_NAMENODE_USER=${HDFS_NAMENODE_USER}" >> /opt/hadoop/etc/hadoop/hadoop-env.sh && \
#    echo "export HDFS_DATANODE_USER=${HDFS_DATANODE_USER}" >> /etc/profile.d/hadoop.sh && \
#    echo "export HDFS_SECONDARYNAMENODE_USER=${HDFS_SECONDARYNAMENODE_USER}" >> /etc/profile.d/hadoop.sh && \
#    echo "export YARN_RESOURCEMANAGER_USER=${YARN_RESOURCEMANAGER_USER}" >> /etc/profile.d/hadoop.sh && \
#    echo "export YARN_NODEMANAGER_USER=${YARN_NODEMANAGER_USER}" >> /etc/profile.d/hadoop.sh && \
#    echo "export JAVA_HOME=${JAVA_HOME}" >> /etc/profile.d/hadoop.sh && \
#    echo "export JAVA_HOME=${JAVA_HOME}" >> /opt/hadoop/etc/hadoop/hadoop-env.sh && \
#    echo "export HADOOP_HOME=${HADOOP_HOME}" >> /etc/profile.d/hadoop.sh && \
#    echo "export PATH=\$PATH:\$JAVA_HOME/bin:\$HADOOP_HOME/bin:\$HADOOP_HOME/sbin" >> /etc/profile.d/hadoop.sh

RUN pip install numpy

COPY ./data  /opt/data
COPY ./py   /opt/py
#RUN chmod -R 777 $HADOOP_HOME/etc/hadoop/

# 启动脚本
COPY start-all.sh /start-all.sh
RUN chmod +x /start-all.sh

# 暴露必要端口
EXPOSE 22 9000 9870 8080 8081 8082 7077 4040

# 容器启动时运行的命令
CMD ["/start-all.sh"]
