package kmeans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MyReducer extends Reducer<IntWritable, Text, Text, Text> {

    public void reduce(IntWritable _key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        // process values
        ArrayList<ArrayList<Double>> pointList=new ArrayList<ArrayList<Double>>();
        for (Text val : values) {
            ArrayList<Double> point=Utils.TextToArray(val);
            pointList.add(point);
        }
        int row=pointList.size();
        int col=pointList.get(0).size();
        double[] avg=new double[col];
        for(int i=1;i<col;i++){//原文是i=0
            double sum=0;
            for(int j=0;j<row;j++){
                sum+=pointList.get(j).get(i);
            }
            avg[i]=sum/row;
        }
        context.write(new Text("") , new Text(Arrays.toString(avg).replace("[", "").replace("]", "")));
    }

}