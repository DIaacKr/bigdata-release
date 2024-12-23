package kmeans;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.LineReader;

public class Utils {
    
    /**
     * @param args
     * @throws IOException 
     */
    public static ArrayList<Double> TextToArray(Text text){
        ArrayList<Double> centers=new ArrayList<Double>();
        String[] line=text.toString().split(",");
        for(int i=0;i<line.length;i++){
            double center=Double.parseDouble(line[i]);
            centers.add(center);
        }
        return centers;
    }
    
    
    public static ArrayList<ArrayList<Double>> GetCenterFromHDFS(String centerPath,boolean isDirectory) throws IOException{
        Configuration conf=new Configuration();
        Path path=new Path(centerPath);
        FileSystem fs=path.getFileSystem(conf);
        
        ArrayList<ArrayList<Double>> result=new ArrayList<ArrayList<Double>>();
        
        if(isDirectory){
            FileStatus[] fileStatus=fs.listStatus(path);
            for(int i=0;i<fileStatus.length;i++){
                if(fileStatus[i].isFile()){
                    result.addAll(GetCenterFromHDFS(fileStatus[i].getPath().toString(),false));
                }
            }
            return result;
        }
        FSDataInputStream infs=fs.open(path);
        LineReader reader=new LineReader(infs,conf);
        Text line=new Text();
        while(reader.readLine(line)>0){
            ArrayList<Double> center=TextToArray(line);
            result.add(center);
        }
        reader.close();
        return result;
    }
    
    public static void deleteDir(String deletepath) throws IOException{
        Configuration conf=new Configuration();
        Path path=new Path(deletepath);
        FileSystem fs=path.getFileSystem(conf);
        fs.delete(path,true);
    }
    
    
    public static boolean CompareCenters(String oldPath, String newPath) throws IOException {
        // 读取旧质心和新质心
        ArrayList<ArrayList<Double>> oldcenters = Utils.GetCenterFromHDFS(oldPath, false);
        ArrayList<ArrayList<Double>> newcenters = Utils.GetCenterFromHDFS(newPath, true);

        // 检查两个列表是否为空或大小不匹配
        if (oldcenters == null || newcenters == null || oldcenters.size() != newcenters.size()) {
            System.out.println("The number of old and new centers does not match.");
            return false;
        }

        // 确保至少有一个质心可以比较
        if (oldcenters.isEmpty() || newcenters.isEmpty()) {
            System.out.println("One of the center lists is empty.");
            return false;
        }

        int row = oldcenters.size();
        int col = oldcenters.get(0).size();

        // 检查列数是否一致
        for (int i = 0; i < row; i++) {
            if (oldcenters.get(i).size() != newcenters.get(i).size()) {
                System.out.println("The dimensions of old and new centers do not match.");
                return false;
            }
        }

        double threshold = 1e-6; // 设置一个阈值来判断质心是否稳定
        double distanceSum = 0.0;

        // 计算所有质心之间的欧几里得距离平方和
        for (int i = 0; i < row; i++) {
            double distanceSquared = 0.0;
            for (int j = 0; j < col; j++) {
                double diff = oldcenters.get(i).get(j) - newcenters.get(i).get(j);
                distanceSquared += diff * diff;
            }
            distanceSum += Math.sqrt(distanceSquared);
        }

        // 如果总距离小于阈值，则认为质心已经稳定
        if (distanceSum <= threshold) {
            Utils.deleteDir(newPath);
            return true;
        } else {
            // 更新旧质心文件为新质心文件
            Configuration conf = new Configuration();
            Path outPath = new Path(oldPath);
            FileSystem fs = outPath.getFileSystem(conf);

            // 删除旧质心文件内容
            try (FSDataOutputStream overWrite = fs.create(outPath, true)) {
                overWrite.writeChars("");
            }

            Path inPath = new Path(newPath);
            FileStatus[] listFiles = fs.listStatus(inPath);
            if (listFiles != null) {
                for (FileStatus fileStatus : listFiles) {
                    Path srcPath = fileStatus.getPath();
                    fs.rename(srcPath, outPath); // 使用 rename 方法简化文件移动
                }
            }

            Utils.deleteDir(newPath);
        }

        return false;
    }
    
    public static double getDistance(ArrayList<Double> point1,ArrayList<Double> point2){
        double distance=0.0;
        if(point1.size()!=point2.size()){
            System.err.println("point size not match!!");
            System.exit(1);
        }else{
            for(int i=0;i<point1.size();i++){
                double t1=Math.abs(point1.get(i));
                double t2=Math.abs(point2.get(i));
                distance+=Math.pow((t1-t1)/(t1+t2),2);
            }
        }
        return distance;
    }
    
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        String oldpath="hdfs://h01:9000/center_input/centers.txt";
        String newpath="hdfs://h01:9000/Kmeans_output/";
        if(Utils.CompareCenters(oldpath,newpath)){
            System.out.println("equals");
        }else{
            System.out.println("not equals");
        }
        /*
        ArrayList<ArrayList<Double>> centers=Utils.GetCenterFromHDFS(path,true);
        for(ArrayList<Double> center:centers){
            System.out.println("                  ");
            for(double point:center){
                System.out.println(point);
            }
        }
        */
        //String deletepath="hdfs://10.107.8.110:9000/output/";
        //Utils.deleteDir(deletepath);
    }

}