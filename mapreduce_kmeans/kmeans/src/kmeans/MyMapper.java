package kmeans;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MyMapper extends Mapper<LongWritable, Text, IntWritable, Text> {
    ArrayList<ArrayList<Double>> centerList=new ArrayList<ArrayList<Double>>();
    
    public void setup(Context context) throws IOException{
        Configuration conf=context.getConfiguration();
        String centerPath=conf.get("centerPath");
        centerList=Utils.GetCenterFromHDFS(centerPath,false);
        
    }
    
    public void map(LongWritable ikey, Text ivalue, Context context)
            throws IOException, InterruptedException {
        ArrayList<Double> point=Utils.TextToArray(ivalue);
//        int size=point.size();
        double distance=0.0;
        double mindis=9999.0;
        int index=-1;
        
        for(int i=0;i<centerList.size();i++){
            double currentDistance=0;
            for(int j=1;j<point.size();j++){//原文是j=0
                double centerPoint = Math.abs(centerList.get(i).get(j));
                double filed = Math.abs(point.get(j));
                currentDistance += Math.pow((centerPoint - filed) / (centerPoint + filed), 2);
                
            }
            if(currentDistance<mindis){
                mindis=currentDistance;
                index=i;
            }
        }
        
        /*
        for(int i=0;i<centerList.size();i++){
            distance=Utils.getDistance(centerList.get(i),point);
            if(distance<mindis){
                mindis=distance;
                index=i+1;
            }
        }
        */
//        String value="";

        context.write(new IntWritable(index+1),ivalue);
        
    }

}