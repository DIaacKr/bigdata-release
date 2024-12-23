package kmeans;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Kmeans {

    public static void run(String centerPath,String dataPath,String newCenterPath,boolean runReduce) throws IOException, ClassNotFoundException, InterruptedException{
        Configuration conf =new Configuration();

        conf.set("centerPath",centerPath);
        Job job=Job.getInstance(conf,"Kmeans");
        job.setJarByClass(kmeans.Kmeans.class);
        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Text.class);
        if(runReduce){
            job.setReducerClass(MyReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
        }

        FileInputFormat.addInputPath(job,new Path(dataPath));
        FileOutputFormat.setOutputPath(job,new Path(newCenterPath));
        System.out.println(job.waitForCompletion(true));

    }

    public static void main(String[] args) throws Exception {
        String centerPath = "hdfs://h01:9000/center_input/centers.csv";
        String dataPath = "hdfs://h01:9000/data_input_all/data.csv";
        String newCenterPath = "hdfs://h01:9000/kmeans_output";
        int maxIterations = 30; // 设置最大迭代次数为20
        int count = 1;

        while (count <= maxIterations) { // 使用计数器控制迭代次数
            run(centerPath,dataPath,newCenterPath,true);
            System.out.println("                           ");
            System.out.println("The "+ ++count+"th time's compution is completed");
            System.out.println("                           ");
            if(Utils.CompareCenters(centerPath,newCenterPath)){
                break;
            }
            if(count == 30) {
            	break;
            }
            Utils.deleteDir(newCenterPath);
            count++;
        }
    }
}