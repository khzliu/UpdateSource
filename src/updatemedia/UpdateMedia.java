/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package updatemedia;
//git test
/**
 *
 * @author Administrator
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

public class UpdateMedia {
    private List<String> voiceContentsNameList;
    private List<String> videoContentsNameList;
    private List<Voice> voiceElementList;          ;
    private List<Video> videoElementList;
    
    private Hashtable<String,Integer> hasVideoElementList;
    private Hashtable<String,Integer> hasVoiceElementList;
    //file list
    private final String propertiesFileName = "info.properties";
    private final String voiceContentsName = "/sdcard/voice";
    private final String videoContentsName = "/sdcard/videos";
    
    private String currentDate;
    private final String umount = "umount /sdcard";
    private final String mount = "mount /dev/sdcard /usr/local/apache-tomcat/webapps/ROOT/data";
    private final String delSdcard = "rm -rf /sdcard";
    //sql
    // 驱动程序名
    private final String driver = "com.mysql.jdbc.Driver";

    // URL指向要访问的数据库名wb
    private final String url = "jdbc:mysql://localhost:3306/vehicle";

    // MySQL配置时的用户名
    private final String sql_user = "root"; 
  
    // MySQL配置时的密码
    private final String sql_passwd = "526156";
    
    private final String videoTables = "t_videos";
    private final String voiceTables = "t_voice";
    
    private final String encoding="UTF-8";

    public UpdateMedia() {
        this.videoElementList = new ArrayList<Video>();
        this.voiceElementList = new ArrayList<Voice>();
        this.videoContentsNameList = new ArrayList<String>();
        this.voiceContentsNameList = new ArrayList<String>();
        this.currentDate = getCurrentDate();
    }
    /**
     * @param args the command line arguments
     */
    
    private void getAllVideosContents() {
        // 建立当前目录中文件的File对象  
        File file = new File(videoContentsName);  
        // 取得代表目录中所有文件的File对象数组  
        File[] list = file.listFiles(); 
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory())
                videoContentsNameList.add(list[i].getName());
        }
    }
    private void getAllVoiceContents() {
        // 建立当前目录中文件的File对象  
        File file = new File(voiceContentsName);  
        // 取得代表目录中所有文件的File对象数组  
        File[] list = file.listFiles(); 
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory())
                voiceContentsNameList.add(list[i].getName());
        }
    }
    private void analysisAllVideosProperties() {
        List<String> linesTxt = new ArrayList<String>();
        try{
            for(int i=0;i<videoContentsNameList.size();i++){
                String filePath = videoContentsName+"/"+videoContentsNameList.get(i)+"/"+propertiesFileName;
                try {
                    File file=new File(filePath);
                    if(file.isFile() && file.exists()){ //判断文件是否存在
                        InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
                        BufferedReader bufferedReader = new BufferedReader(read);
                        String lineTxt = null;
                        while((lineTxt = bufferedReader.readLine()) != null){
                            String[] ary = lineTxt.split("=");
                            if(ary.length == 1)
                                linesTxt.add("none");
                            else
                                linesTxt.add(ary[1]);
                        }
                        read.close();
                    }else{
                        System.out.println("找不到指定的文件");
                    }
                    Video video = new Video();
                    video.setDirname(linesTxt.get(0));
                    video.setType(linesTxt.get(1));
                    video.setName(linesTxt.get(2));
                    video.setDuration(linesTxt.get(3));
                    video.setDate(this.currentDate);
                    video.setClicks(0);
                    video.setDes(linesTxt.get(4));
                    video.setScale_x(Float.parseFloat(linesTxt.get(5)));
                    video.setScale_y(Float.parseFloat(linesTxt.get(6)));
                    video.setDirector(linesTxt.get(7));
                    video.setActor(linesTxt.get(8));
                    video.setSinger(linesTxt.get(9));
                    video.setAlbum(linesTxt.get(10));
                
                    this.videoElementList.add(video);
                    linesTxt.clear();
                } catch (Exception e) {
                    System.out.println("读取文件内容出错");
                    e.printStackTrace();
                }
            }
            videoContentsNameList.clear();
        } catch(NumberFormatException e){
            System.out.println("error:"+e);
        }

    }
    private void analysisAllVoiceProperties(){
        try{
            List<String> linesTxt = new ArrayList<String>();
            for(int i=0;i<voiceContentsNameList.size();i++){
                String filePath = voiceContentsName+"/"+voiceContentsNameList.get(i)+"/"+propertiesFileName;
                 try {
                    File file=new File(filePath);
                    if(file.isFile() && file.exists()){ //判断文件是否存在
                        InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
                        BufferedReader bufferedReader = new BufferedReader(read);
                        String lineTxt = null;
                        while((lineTxt = bufferedReader.readLine()) != null){
                            String[] ary = lineTxt.split("=");
                            if(ary.length == 1)
                                linesTxt.add("none");
                            else
                                linesTxt.add(ary[1]);
                        }
                        read.close();
                    }else{
                        System.out.println("找不到指定的文件");
                    }
                    Voice voice = new Voice();
                    voice.setDirname(linesTxt.get(0));
                    voice.setType(linesTxt.get(1));
                    voice.setName(linesTxt.get(2));
                    voice.setDuration(linesTxt.get(3));
                    voice.setDate(this.currentDate);
                    voice.setClicks(0);
                    voice.setDes(linesTxt.get(4));
                    voice.setActor(linesTxt.get(5));
          
                    this.voiceElementList.add(voice);
                    linesTxt.clear();
                    
                } catch (Exception e) {
                    System.out.println("读取文件内容出错");
                    e.printStackTrace();
                }         
            }
            voiceContentsNameList.clear();
        } catch(NumberFormatException e){
            System.out.println("error:"+e);
        }
    };
    
    private void updateVideosTable(){
        String sql = "";
        Video video;
         try {
            // 加载驱动程序
            Class.forName(driver);
            // 连续数据库
            Connection conn = DriverManager.getConnection(url, sql_user, sql_passwd);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();

            if(HasTable(conn,videoTables)){
                sql = "select route,clicks from "+videoTables;
                ResultSet rs = statement.executeQuery(sql);
                hasVideoElementList = new Hashtable();
                while(rs.next())
                {
                    hasVideoElementList.put(rs.getString("route"),rs.getInt("clicks"));     
                }
                sql = "TRUNCATE TABLE "+videoTables;
                statement.executeUpdate(sql);
            } else{
                sql = "create table "+videoTables+"(id bigint(20) primary key not null,route varchar(255) not null,type varchar(255) not null,name varchar(255) not null,"
                        + "duration varchar(255) not null,date varchar(255) not null,clicks bigint(20) not null,description varchar(255),"
                        + "scale_x bigint(20) not null,scale_y bigint(20) not null,director varchar(255),actor varchar(255),singer varchar(255),album varchar(255))";
                statement.executeUpdate(sql);
            }
            for(int i=0;i<videoElementList.size();i++)
            {   
                video = videoElementList.get(i);
                if(hasVideoElementList.containsKey(video.getDirname()))
                    video.setClicks(hasVideoElementList.get(video.getDirname()));
                sql = "insert into "+videoTables+"(id,route,type,name,duration,date,clicks,description,scale_x,scale_y,director,actor,singer,album)"
                        +" values("+i+",\'"+video.getDirname()+"\',\'"+video.getType()+"\',\'"+video.getName()+"\',\'"+video.getDuration()+"\',\'"+video.getDate()+"\',"
                        +video.getClicks()+",\'"+video.getDes()+"\',"+video.getScale_x()+","+video.getScale_y()+",\'"+video.getDirector()+"\',\'"+video.getActor()+"\',\'"+video.getSinger()+"\',"
                        + "\'"+video.getAlbum()+"\')";
                System.out.println(sql);
                statement.executeUpdate(sql);
            }
            videoElementList.clear();
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }
    };
    private void updateVoiceTable(){
        String sql = "";
        Voice voice;
         try {
            // 加载驱动程序
            Class.forName(driver);
            // 连续数据库
            Connection conn = DriverManager.getConnection(url, sql_user, sql_passwd);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();

            if(HasTable(conn,voiceTables)){
                sql = "select route,clicks from "+voiceTables;
                ResultSet rs = statement.executeQuery(sql);
                hasVoiceElementList = new Hashtable();
                while(rs.next())
                {
                    hasVoiceElementList.put(rs.getString("route"),rs.getInt("clicks"));     
                }
                sql = "TRUNCATE TABLE "+voiceTables;
                statement.executeUpdate(sql);
            } else{
                sql = "create table "+voiceTables+"(id bigint(20) primary key not null,route varchar(255) not null,type varchar(255) not null,name varchar(255) not null,"
                        + "duration varchar(255) not null,date varchar(255) not null,clicks bigint(20) not null,description varchar(255),actor varchar(255))";
                statement.executeUpdate(sql);
            }
            for(int i=0;i<voiceElementList.size();i++)
            {   
                voice = voiceElementList.get(i);  
                if(hasVoiceElementList.containsKey(voice.getDirname()))
                    voice.setClicks(hasVoiceElementList.get(voice.getDirname()));
                sql = "insert into "+voiceTables+"(id,route,type,name,duration,date,clicks,description,actor)"
                        +" values("+i+",\'"+voice.getDirname()+"\',\'"+voice.getType()+"\',\'"+voice.getName()+"\',\'"+voice.getDuration()+"\',\'"+voice.getDate()+"\',"
                        +voice.getClicks()+",\'"+voice.getDes()+"\',\'"+voice.getActor()+"\')";
                System.out.println(sql);
                statement.executeUpdate(sql);
            }
            voiceElementList.clear();
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }
    };
    private String getCurrentDate(){
        String temp_str=null;   
        Date dt = new Date();   
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制   
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
        temp_str=sdf.format(dt);   
        return temp_str;  
    }
    //判断一个表是否存在
    public boolean HasTable(Connection conn,String tableName) {
        //判断某一个表是否存在
        boolean result = false;
        try {
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            
            DatabaseMetaData meta = (DatabaseMetaData) conn.getMetaData();
            ResultSet set = meta.getTables (null, null, tableName, null);
            while (set.next()) {
                result = true;
            }
        } catch (SQLException eHasTable) {
            System.err.println(eHasTable);
        }
        return result;
    } 
    //执行挂载u盘命令
    private void executeDiskMount() {
	try{
            Process p = Runtime.getRuntime().exec(umount);
            p.waitFor();
            p = Runtime.getRuntime().exec(mount);
            p.waitFor();
            p = Runtime.getRuntime().exec(delSdcard);
            p.waitFor();
	}catch (InterruptedException | IOException e) {  
             System.out.println("error:"+e);  
	}
    }
    //运行监听程序
    public void run(){   
        if(true){
            // 建立当前目录中文件的File对象  
            File file = new File("/sdcard");  
            if (!file.exists()) {
                System.out.println("no sdcard or sdcard in use!");
            } else{
                //设置视频
                File videoFile = new File(videoContentsName);
                if (!videoFile.exists()) {
                   videoFile.mkdir();
                }
                getAllVideosContents();
                analysisAllVideosProperties();
                updateVideosTable();
                //设置音频
                File voiceFile = new File(voiceContentsName);
                if (voiceFile.exists()) {
                    voiceFile.mkdir();
                }
                getAllVoiceContents();
                analysisAllVoiceProperties();
                updateVoiceTable();
                
                //挂载sdcard到指定目录
                executeDiskMount();
                
            }
        }
        
    }
    public static void main(String[] args) {
        UpdateMedia updateMedia = new UpdateMedia();
        updateMedia.run();
    }
}
