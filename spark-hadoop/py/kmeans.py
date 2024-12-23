from pyspark.sql import SparkSession
from pyspark.ml.clustering import KMeans
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml.evaluation import ClusteringEvaluator

def main():
    # 初始化 SparkSession
    spark = SparkSession.builder \
        .appName("KMeans Clustering with HDFS") \
        .config("spark.hadoop.fs.defaultFS", "hdfs://hadoop-spark-master:9000") \
        .getOrCreate()

    try:
        # 读取数据
        data_path = "hdfs://hadoop-spark-master:9000/data.csv"  # HDFS 文件路径
        data = spark.read.csv(data_path, header=True, inferSchema=True)
        print("原始数据：")
        data.show(5)
        
        all_columns = data.columns

       

        # 特征工程：组合特征列
        feature_cols = data.columns  # 替换为你的特征列
        assembler = VectorAssembler(inputCols=all_columns, outputCol="features")
        assembled_data = assembler.transform(data)

        # 训练 KMeans 模型
        k = 10  # 设置聚类数
        kmeans = KMeans().setK(k).setSeed(1).setMaxIter(20)
        model = kmeans.fit(assembled_data)

        # 进行聚类预测
        predictions = model.transform(assembled_data)
        print("聚类结果：")
        predictions.select("features", "prediction").show(5)

        # 评估模型
        evaluator = ClusteringEvaluator()
        silhouette = evaluator.evaluate(predictions)
        print(f"轮廓系数 (Silhouette Score): {silhouette}")

        # 将结果保存到 HDFS
        # output_path = "hdfs://hadoop-spark-master:9000/result"
        # predictions.select("prediction").write.csv(output_path,header=True,mode = 'append')
        # print(f"聚类结果已保存到 HDFS：{output_path}")
    
    finally:
        # 关闭 SparkSession
        spark.stop()

if __name__ == "__main__":
    main()


