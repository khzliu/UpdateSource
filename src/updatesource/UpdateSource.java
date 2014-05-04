/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package updatesource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author liu.huazhou <khzliu@163.com>
 */
public class UpdateSource {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UpdateSource updateSource = new UpdateSource();
        updateSource.run();
    }
    private List<String> beanContentsList; //分类下的目录和文件列表
    
    //元素哈希表
    private Hashtable<String,Integer> videoTypeList; //视频元素类型种类哈希表
    private Hashtable<String,Integer> voiceTypeList; //音频元素类型种类哈希表
    private List<String> appTypeList; //app类型种类哈希表
    
    private List<BeanVoice> voiceElementList; //音频元素列表          ;
    private List<BeanVideo> videoElementList;//视频元素列表
    private List<BeanApp> appElementList; //app元素列表
    
    //元素哈希表
    private Hashtable<String,Integer> clickVideoElementList; //视频点击次数哈希表
    private Hashtable<String,Integer> clickVoiceElementList; //音频点击次数哈希表
    private Hashtable<String,Integer> priaseVideoElementList;//视频点赞次数哈希表
    private Hashtable<String,Integer> priaseVoiceElementList;//音频点赞次数哈希表
    private Hashtable<String,Integer> hasAppElementList;   //app元素哈希表
    
    //显示模式 默认是0，1代表丰富模式，0代表简单模式
    private Integer videoViewMode = 0;
    private Integer voiceViewMode = 0;
    
    //file list
    private final String SIMPLE_MODE = "simpleMode";
    private final String RICH_MODE = "richMode";
    private final String MP4 = "mp4";
    private final String MP3 = "mp3";
    private final String ICON = "icon.jpg";
    private final String POSTER = "poster.jpg";
    private final String CONFIG_FILE = "配置文件.txt";
    private final String PROPERTIES_FILE = "info.properties";//属性文件名
    private final String SDCARD = "/sdcard";
    private final String APP_PROPERTIES_FILE = "app说明.txt";//app属性说明文件名
    private final String VOICE_CONTENTS = "/sdcard/悦听";//音频存放目录
    private final String VIDEO_CONTENTS = "/sdcard/视频";//视频存放目录
    private final String APP_CONTENTS = "/sdcard/app";//app存放目录
    
    //设置sdcard命令
    private String currentDate;
    private final String UMOUNT = "umount /sdcard";
    private final String MOUNT = "mount /dev/sdcard /usr/local/apache-tomcat/webapps/ROOT/data";
    private final String DEL_SDCARD = "rm -rf /sdcard";
    
    //sql
    private final String DRIVER = "com.mysql.jdbc.Driver";// 驱动程序名
    private final String SQL_URL = "jdbc:mysql://localhost:3306/vehicle";// URL指向要访问的数据库名wb
    private final String SQL_USER = "root"; // MySQL配置时的用户名
    private final String SQL_PASSWD = "526156";// MySQL配置时的密码
    
    private final String VIDEO_TABLE = "video"; //video表
    private final String VOICE_TABLE = "voice";//voice表
    private final String APP_TABLE = "t_app";//app表
    private final String VIDEO_TYPE_TABLE = "videotype";//videokind表
    private final String VOICE_TYPE_TABLE = "voicetype";//voicekind表
    private final String APP_TYPE_TABLE = "t_app_type";//appkind表
    
    //字符集设置
    private final String ENCODING="UTF-8";
    
    //默认构找函数
    public UpdateSource() {
        this.videoElementList = new ArrayList<BeanVideo>();
        this.voiceElementList = new ArrayList<BeanVoice>();
        this.appElementList = new ArrayList<BeanApp>();
        this.beanContentsList = new ArrayList<String>();
        this.currentDate = getCurrentDate();
    }
    //分析配置文件
    private void analysisConfigProperties(String path){
        try{
            // 建立当前目录中文件的File对象
            List<String> linesTxt = new ArrayList<String>();
            File file = new File(path);  
            if(file.isFile() && file.exists()){  
                try{
                    InputStreamReader read = new InputStreamReader(new FileInputStream(file),ENCODING);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    if((lineTxt = bufferedReader.readLine()) != null){
                        String[] ary = lineTxt.split("=");
                        if(ary.length == 1)
                            linesTxt.add("none");
                        else
                            linesTxt.add(ary[1]);   
                    }else{
                        linesTxt.add("none");
                    }
                    if((lineTxt = bufferedReader.readLine()) != null){
                        String[] ary = lineTxt.split("=");
                        if(ary.length == 1)
                            linesTxt.add("none");
                        else
                            linesTxt.add(ary[1]);   
                    }else{
                        linesTxt.add("none");
                    }
                    read.close();
                } catch (Exception e) {
                    System.out.println("read file error!");
                    e.printStackTrace();
                }
                if(linesTxt.get(0).equals(RICH_MODE))
                    this.videoViewMode = 1; 
                if(linesTxt.get(1).equals(RICH_MODE))
                    this.voiceViewMode = 1;
            }else{
                System.out.println("file not exits");
            }
              
        }catch(NullPointerException e){
            System.out.println("configure file not found\nNullPointerException:"+e);
        }    
    }
    //获取video分类目录
    public void getVideoTypeContentList(String contentPath){
        this.videoTypeList = new Hashtable();
        try{
            // 建立当前目录中文件的File对象  
            File file = new File(contentPath);  
            // 取得代表目录中所有文件的File对象数组  
            File[] list = file.listFiles(); 
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory())
                {
                    String typePath = list[i].getAbsolutePath();
                    File icon = new File(typePath+"/"+ICON);
                    if(icon.exists()){
                        Integer num = 0;
                        File[] allVideo = list[i].listFiles();
                        // 取得代表目录中所有文件的File对象数组  
                        for (int j = 0; j < allVideo.length; j++) {
                            if (!allVideo[j].isDirectory())
                            {
                                String fileName = allVideo[j].getName();
                                if(MP4.equals(fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()))){
                                    num++;
                                }
                            }else{
                                num++;
                            }   
                        }
                        this.videoTypeList.put(list[i].getName(),num); 
                    }
                }
            }
        }catch(NullPointerException e){
            System.out.println("video type content not found\nNullPointerException:"+e);
        }      
    }
    //获取video分类目录
    public void getVoiceTypeContentList(String contentPath){
        this.voiceTypeList = new Hashtable();
        try{
            // 建立当前目录中文件的File对象  
            File file = new File(contentPath);  
            // 取得代表目录中所有文件的File对象数组  
            File[] list = file.listFiles(); 
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory())
                {
                    String typePath = list[i].getAbsolutePath();
                    File icon = new File(typePath+"/"+ICON);
                    if(icon.exists()){
                        Integer num = 0;
                        File[] allVoice = list[i].listFiles();
                        // 取得代表目录中所有文件的File对象数组  
                        for (int j = 0; j < allVoice.length; j++) {
                            if (!allVoice[j].isDirectory())
                            {
                                String fileName = allVoice[j].getName();
                                if(MP3.equals(fileName.substring(fileName.lastIndexOf(".")+1,fileName.length()))){
                                    num++;
                                }
                            }else{
                                num++;
                            }   
                        }
                        this.voiceTypeList.put(list[i].getName(),num); 
                    }
                }
            }
        }catch(NullPointerException e){
            System.out.println("voice type content not found\nNullPointerException:"+e);
        }     
    }
    //获取app分类目录
    public void getAppTypeContentList(String contentPath){
        this.appTypeList = new ArrayList<String>();
        try{
            // 建立当前目录中文件的File对象  
            File file = new File(APP_CONTENTS);  
            // 取得代表目录中所有文件的File对象数组  
            File[] list = file.listFiles(); 
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory())
                {
                    this.appTypeList.add(list[i].getName()); 
                }
            }
        }catch(NullPointerException e){
            System.out.println("app type content not found\nNullPointerException:"+e);
        }     
    }
    //生成表videotype
    public void makeTableVideoType(){
        String sql = "";
         try {
            // 加载驱动程序
            Class.forName(DRIVER);
            // 连续数据库
            Connection conn = DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWD);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();
            if(HasTable(conn,VIDEO_TYPE_TABLE)){
                sql = "TRUNCATE TABLE "+VIDEO_TYPE_TABLE;
                statement.executeUpdate(sql);
            }else{
                sql = "create table "+VIDEO_TYPE_TABLE+"(id bigint(20) primary key not null,type_name varchar(255) not null,view_mode bigint(10) not null,file_num bigint(20) not null)";
                statement.executeUpdate(sql);
            }
            int i = 0;
            for(Iterator it = videoTypeList.keySet().iterator(); it.hasNext();)   {
                String key = (String) it.next();
                key = sqlStringFilter(key);
                Integer value = videoTypeList.get(key);
                sql = "insert into "+VIDEO_TYPE_TABLE+"(id,type_name,view_mode,file_num)"
                        +" values("+i+",\'"+key+"\',"+this.videoViewMode+","+value+")";
                System.out.println(sql);
                statement.executeUpdate(sql);
                i++;
            }
            //videoTypeList.clear();
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }
    }
    //生成表viocetype
    public void makeTableVoiceType(){
        String sql = "";
         try {
            // 加载驱动程序
            Class.forName(DRIVER);
            // 连续数据库
            Connection conn = DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWD);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();
            if(HasTable(conn,VOICE_TYPE_TABLE)){
                sql = "TRUNCATE TABLE "+VOICE_TYPE_TABLE;
                statement.executeUpdate(sql);
            }else{
                sql = "create table "+VOICE_TYPE_TABLE+"(id bigint(20) primary key not null,type_name varchar(255) not null,view_mode bigint(10) not null,file_num bigint(20) not null)";
                statement.executeUpdate(sql);
            }
            int i = 0;
            for(Iterator it = voiceTypeList.keySet().iterator(); it.hasNext();)   {
                String key = (String) it.next();
                key = sqlStringFilter(key);
                Integer value = voiceTypeList.get(key);
                sql = "insert into "+VOICE_TYPE_TABLE+"(id,type_name,view_mode,file_num)"
                        +" values("+i+",\'"+key+"\',"+this.voiceViewMode+","+value+")";
                System.out.println(sql);
                try{
                    statement.executeUpdate(sql);
                }catch(SQLException e){
                    System.out.println("插入失败:"+e);
                }
                i++;
            }
            //voiceTypeList.clear();
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }
        
    }
    //生成表apptype
    public void makeTableAppType(){
        String sql = "";
         try {
            // 加载驱动程序
            Class.forName(DRIVER);
            // 连续数据库
            Connection conn = DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWD);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();
            
            if(HasTable(conn,APP_TYPE_TABLE)){
                sql = "TRUNCATE TABLE "+APP_TYPE_TABLE;
                statement.executeUpdate(sql);
            }else{
                sql = "create table "+APP_TYPE_TABLE+"(id bigint(20) primary key not null,type varchar(255) not null)";
                statement.executeUpdate(sql);
            }

            for(int i=0;i<appTypeList.size();i++)   {
                String appTypeName = appTypeList.get(i);
                appTypeName = sqlStringFilter(appTypeName);
                sql = "insert into "+APP_TYPE_TABLE+"(id,type)"+" values("+i+",\'"+appTypeName+"\')";
                System.out.println(sql);
                try{
                    statement.executeUpdate(sql);
                }catch(SQLException e){
                    System.out.println("插入失败:"+e);
                }
            }
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }
    }
    //生成表video
    public void makeTableVideo(){
        String sql = "";
        BeanVideo video;
         try {
            // 加载驱动程序
            Class.forName(DRIVER);
            // 连续数据库
            Connection conn = DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWD);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();
            clickVideoElementList = new Hashtable();
            priaseVideoElementList = new Hashtable();
            if(HasTable(conn,VIDEO_TABLE)){
                sql = "select type,name,clicks,priase from "+VIDEO_TABLE;
                ResultSet rs = statement.executeQuery(sql);
                while(rs.next())
                {
                    clickVideoElementList.put(rs.getString("type")+"_with_"+rs.getString("name"),rs.getInt("clicks"));
                    priaseVideoElementList.put(rs.getString("type")+"_with_"+rs.getString("name"),rs.getInt("priase"));
                }
                sql = "TRUNCATE TABLE "+VIDEO_TABLE;
                statement.executeUpdate(sql);
            } else{
                sql = "create table "+VIDEO_TABLE+"(id bigint(20) primary key not null,type varchar(255) not null,name varchar(255) not null,"
                        + "duration varchar(255) not null,clicks bigint(20) not null,description varchar(255),actor varchar(255),"
                        + "priase bigint(20) not null,isfile bigint(10) not null,scale_x bigint(20) not null,scale_y bigint(20) not null)";
                statement.executeUpdate(sql);
            }
            for(int i=0;i<videoElementList.size();i++)
            {   
                video = videoElementList.get(i);
                if(!clickVideoElementList.isEmpty()){
                    if(clickVideoElementList.containsKey(video.getType()+"_with_"+video.getName()))
                        video.setClicks(clickVideoElementList.get(video.getType()+"_with_"+video.getName()));
                    if(priaseVideoElementList.containsKey(video.getType()+"_with_"+video.getName()))
                        video.setPriase(priaseVideoElementList.get(video.getType()+"_with_"+video.getName()));
                }
                sql = "insert into "+VIDEO_TABLE+"(id,type,name,duration,clicks,description,actor,priase,isfile,scale_x,scale_y)"
                        +" values("+i+",\'"+video.getType()+"\',\'"+video.getName()+"\',\'"+video.getDuration()+"\',"+video.getClicks()
                        +",\'"+video.getDes()+"\',\'"+video.getActor()+"\',"+video.getPriase()+","+video.getIsFile()+","
                        +video.getScale_x()+","+video.getScale_y()+")";
                System.out.println(sql);
                try{
                    statement.executeUpdate(sql);
                }catch(SQLException e){
                    System.out.println("插入失败:"+e);
                }
            }
            videoElementList.clear();
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }    
    }
    //生成表voice
    public void makeTableVoice(){
        String sql = "";
        BeanVoice voice;
         try {
            // 加载驱动程序
            Class.forName(DRIVER);
            // 连续数据库
            Connection conn = DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASSWD);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();
            clickVoiceElementList = new Hashtable();
            priaseVoiceElementList = new Hashtable();
            if(HasTable(conn,VOICE_TABLE)){
                sql = "select type,name,clicks from "+VOICE_TABLE;
                ResultSet rs = statement.executeQuery(sql);
                while(rs.next())
                {
                    clickVoiceElementList.put(rs.getString("type")+"_with_"+rs.getString("name"),rs.getInt("clicks"));
                    priaseVoiceElementList.put(rs.getString("type")+"_with_"+rs.getString("name"),rs.getInt("priase"));
                }
                sql = "TRUNCATE TABLE "+VOICE_TABLE;
                statement.executeUpdate(sql);
            } else{
                sql = "create table "+VOICE_TABLE+"(id bigint(20) primary key not null,type varchar(255) not null,name varchar(255) not null,"
                        + "duration varchar(255) not null,clicks bigint(20) not null,description varchar(255),actor varchar(255),priase bigint(20) not null,isfile bigint(10) not null)";
                statement.executeUpdate(sql);
            }
            for(int i=0;i<voiceElementList.size();i++)
            {   
                voice = voiceElementList.get(i);
                if((!priaseVoiceElementList.isEmpty())){
                    if(clickVoiceElementList.containsKey(voice.getType()+"_with_"+voice.getName()))
                        voice.setClicks(clickVoiceElementList.get(voice.getType()+"_with_"+voice.getName()));
                    if(priaseVoiceElementList.containsKey(voice.getType()+"_with_"+voice.getName()))
                        voice.setPriase(priaseVoiceElementList.get(voice.getType()+"_with_"+voice.getName()));
                }
                sql = "insert into "+VOICE_TABLE+"(id,type,name,duration,clicks,description,actor,priase,isfile)"
                        +" values("+i+",\'"+voice.getType()+"\',\'"+voice.getName()+"\',\'"+voice.getDuration()+"\',"
                        +voice.getClicks()+",\'"+voice.getDes()+"\',\'"+voice.getActor()+"\',"+voice.getPriase()+","+voice.getIsFile()+")";
                System.out.println(sql);
                try{
                    statement.executeUpdate(sql);
                }catch(SQLException e){
                    System.out.println("插入失败:"+e);
                }
            }
            voiceElementList.clear();
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }   
    }
    
    //生成表app
    public void makeTableApp(){
        String sql = "";
        BeanApp app;
         try {
            // 加载驱动程序
            Class.forName(DRIVER);
            // 连续数据库
            Connection conn = DriverManager.getConnection(SQL_URL, SQL_USER,SQL_PASSWD);
            if(!conn.isClosed())
                System.out.println("Succeeded connecting to the Database!");
            // statement用来执行SQL语句
            Statement statement = conn.createStatement();
            hasAppElementList = new Hashtable();
            if(HasTable(conn,APP_TABLE)){
                sql = "select type,name,clicks from "+APP_TABLE;
                ResultSet rs = statement.executeQuery(sql);
                while(rs.next())
                {
                    hasAppElementList.put(rs.getString("name")+"_with_"+rs.getString("type"),rs.getInt("clicks"));     
                }
                sql = "TRUNCATE TABLE "+APP_TABLE;
                statement.executeUpdate(sql);
            } else{
                sql = "create table "+APP_TABLE+"(id bigint(20) primary key not null,type varchar(255) not null,name varchar(255) not null,"
                        + "size float(20) not null,short_des varchar(255),long_des varchar(2550),poster_num bigint(10) not null,clicks bigint(20) not null)";
                statement.executeUpdate(sql);
            }
            for(int i=0;i<appElementList.size();i++)
            {   
                app = appElementList.get(i);
                if((!hasAppElementList.isEmpty())){
                    if(hasAppElementList.containsKey(app.getName()+"_with_"+app.getType()))
                        app.setClicks(hasAppElementList.get(app.getName()+"_with_"+app.getType()));
                }
                sql = "insert into "+APP_TABLE+"(id,type,name,size,short_des,long_des,poster_num,clicks)"
                        +" values("+i+",\'"+app.getType()+"\',\'"+app.getName()+"\',"+app.getSize()+",\'"+
                        app.getDesShort()+"\',\'"+app.getDesLong()+"\',"+app.getPosterNum()+","+app.getClicks()+")";
                System.out.println(sql);
                try{
                    statement.executeUpdate(sql);
                }catch(SQLException e){
                    System.out.println("sql insert error:"+e);
                }
            }
            appElementList.clear();
            conn.close();
        } catch (ClassNotFoundException | SQLException eHasTable) {
            System.err.println(eHasTable);
        }
    }
    //获取所有视频文件
    private void getAllVideoContents(){
        beanContentsList.clear();
        for(Iterator it = videoTypeList.keySet().iterator(); it.hasNext();)   {
            String key = (String) it.next();   
            // 建立当前目录中文件的File对象  
            File file = new File(VIDEO_CONTENTS+"/"+key);  
            // 取得代表目录中所有文件的File对象数组  
            File[] list = file.listFiles(); 
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory())
                    beanContentsList.add(list[i].getAbsolutePath());
                else{
                    String fileName = list[i].getName();
                    if(MP4.equals(fileName.substring(fileName.lastIndexOf(".")+1,fileName.length())))
                        beanContentsList.add(list[i].getAbsolutePath());
                }
            }
        }
    
    }
    //获取说有符合条件的音频文件
    private void getAllVoiceContents() {
        beanContentsList.clear();
        for(Iterator it = voiceTypeList.keySet().iterator(); it.hasNext();)   {
            String key = (String) it.next();   
            // 建立当前目录中文件的File对象  
            File file = new File(VOICE_CONTENTS+"/"+key);  
            // 取得代表目录中所有文件的File对象数组  
            File[] list = file.listFiles(); 
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory())
                    beanContentsList.add(list[i].getAbsolutePath());
                else{
                    String fileName = list[i].getName();
                    if(MP3.equals(fileName.substring(fileName.lastIndexOf(".")+1,fileName.length())))
                        beanContentsList.add(list[i].getAbsolutePath());
                }
            }
        }    
    }
    
    //获取说有符合条件的音频文件
    private void getAllAppContents() {
        beanContentsList.clear();
        for(int j=0;j<appTypeList.size();j++){
            String key = (String) appTypeList.get(j);   
            // 建立当前目录中文件的File对象  
            File file = new File(APP_CONTENTS+"/"+key);  
            // 取得代表目录中所有文件的File对象数组  
            File[] list = file.listFiles(); 
            for(int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()){
                    beanContentsList.add(list[i].getAbsolutePath());
                }
            }
        }    
    }
    //分析app文件夹
    private void analysisAllAppProperties() {
        List<String> linesTxt = new ArrayList<String>();
        try{
            for(int i=0;i<beanContentsList.size();i++){
                String filePath = beanContentsList.get(i);
                try {
                    File file=new File(filePath+"/"+APP_PROPERTIES_FILE);
                    float fileSize = 0.0f;
                    File apkFile = new File(filePath+"/"+filePath.substring(filePath.lastIndexOf("/")+1)+".apk");
                    if(apkFile.isFile() && apkFile.exists()){
                        fileSize = apkFile.length()/(1024.0f*1024.0f);
                        fileSize = Math.round(fileSize * 100);
                        fileSize = fileSize/100.0f;
                    }
                    if(file.isFile() && file.exists()){ //判断文件是否存在
                        InputStreamReader read = new InputStreamReader(new FileInputStream(file),ENCODING);//考虑到编码格式
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
                    BeanApp app = new BeanApp();
                    String tempStr = filePath.substring(0,filePath.lastIndexOf("/"));
                    app.setType(sqlStringFilter(tempStr.substring(tempStr.lastIndexOf("/")+1)));
                    app.setName(sqlStringFilter(filePath.substring(filePath.lastIndexOf("/")+1)));
                    app.setClicks(0);
                    app.setPosterNum(Integer.parseInt(linesTxt.get(0)));
                    app.setDesShort(sqlStringFilter(linesTxt.get(1)));
                    app.setDesLong(sqlStringFilter(linesTxt.get(2))); 
                    app.setSize(fileSize);
                
                    this.appElementList.add(app);
                    linesTxt.clear();
                } catch (Exception e) {
                    System.out.println("读取文件内容出错");
                    e.printStackTrace();
                }
            }
            beanContentsList.clear();
        } catch(NumberFormatException e){
            System.out.println("error:"+e);
        }

    }
    //分析视频文件属性
    private void analysisAllVideosProperties() {
        List<String> linesTxt = new ArrayList<String>();
        try{
            for(int i=0;i<beanContentsList.size();i++){
                String filePath = beanContentsList.get(i);//
                try {
                    File file=new File(filePath);
                    if(!file.isFile()){ //判断文件是否存在
                        String propertiesFile = filePath+"/"+PROPERTIES_FILE;
                        if(file.exists()){ //判断文件是否存在
                            InputStreamReader read = new InputStreamReader(new FileInputStream(propertiesFile),ENCODING);//考虑到编码格式
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
                            BeanVideo video = new BeanVideo();
                            String tempStr = filePath.substring(0,filePath.lastIndexOf("/"));
                            video.setType(sqlStringFilter(tempStr.substring(tempStr.lastIndexOf("/")+1)));
                            video.setName(sqlStringFilter(filePath.substring(filePath.lastIndexOf("/")+1)));
                            video.setDuration(linesTxt.get(0));
                            video.setClicks(0);
                            video.setPriase(0);
                            video.setDes(sqlStringFilter(linesTxt.get(1)));
                            video.setScale_x(Float.parseFloat(linesTxt.get(2)));
                            video.setScale_y(Float.parseFloat(linesTxt.get(3)));
                            video.setActor(sqlStringFilter(linesTxt.get(4)));
                            video.setIsFile(0);
                            this.videoElementList.add(video);
                            linesTxt.clear();
                            }else{
                                System.out.println("找不到指定的文件");
                            }
                        
                    }else{
                        BeanVideo video = new BeanVideo();
                        String tempStr = filePath.substring(0,filePath.lastIndexOf("/"));
                        video.setType(sqlStringFilter(tempStr.substring(tempStr.lastIndexOf("/")+1)));
                        video.setName(sqlStringFilter(filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."))));
                        video.setDuration("HH:MM:SS");
                        video.setClicks(0);
                        video.setPriase(0);
                        video.setDes("None");
                        video.setScale_x(640f);
                        video.setScale_y(480f);
                        video.setActor("None");
                        video.setIsFile(1);
                        this.videoElementList.add(video);
                    }
                } catch (Exception e) {
                    System.out.println("读取文件内容出错");
                    e.printStackTrace();
                }
            }
            beanContentsList.clear();
        } catch(Exception e){
            System.out.println("error:"+e);
        }

    }
    private void analysisAllVoiceProperties(){
        try{
            List<String> linesTxt = new ArrayList<String>();
            for(int i=0;i<beanContentsList.size();i++){
                String filePath = beanContentsList.get(i);
                 try {
                    File file=new File(filePath);
                    if(!file.isFile()){ //判断文件是否存在
                        String propertiesFile = filePath+"/"+PROPERTIES_FILE;
                        if(file.exists()){ //判断文件是否存在
                            InputStreamReader read = new InputStreamReader(new FileInputStream(propertiesFile),ENCODING);//考虑到编码格式
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
                            BeanVoice voice = new BeanVoice();
                            String tempStr = filePath.substring(0,filePath.lastIndexOf("/"));
                            voice.setType(sqlStringFilter(tempStr.substring(tempStr.lastIndexOf("/")+1)));
                            voice.setName(sqlStringFilter(filePath.substring(filePath.lastIndexOf("/")+1)));
                            voice.setDuration(linesTxt.get(0));
                            voice.setClicks(0);
                            voice.setPriase(0);
                            voice.setDes(sqlStringFilter(linesTxt.get(1)));
                            voice.setActor(sqlStringFilter(linesTxt.get(2)));
                            voice.setIsFile(0);
                            this.voiceElementList.add(voice);
                            linesTxt.clear();
                            }else{
                                    System.out.println("找不到指定的文件");
                            }
                        }else{
                            BeanVoice voice = new BeanVoice();
                            String tempStr = filePath.substring(0,filePath.lastIndexOf("/"));
                            voice.setType(sqlStringFilter(tempStr.substring(tempStr.lastIndexOf("/")+1)));
                            voice.setName(sqlStringFilter(filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."))));
                            voice.setDuration("--:--:--");
                            voice.setClicks(0);
                            voice.setPriase(0);
                            voice.setDes("None");
                            voice.setActor("None");
                            voice.setIsFile(1);
                            this.voiceElementList.add(voice);
                        }
                } catch (Exception e) {
                    System.out.println("读取文件内容出错");
                    e.printStackTrace();
                }         
            }
            beanContentsList.clear();
        } catch(Exception e){
            System.out.println("error:"+e);
        }
    };
    
   
    private void updateVoiceTable(){
        
    };
    //获取当前日期
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
            if(conn.isClosed())
                System.out.println("connection has closed!");
            
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
    //对字符串进行过滤，防止数据库注入
    private String sqlStringFilter(String sqlString){
        String src = sqlString;   
        src = src.replaceAll("\'", "\'\'"); 
        return src;
    }
    //执行挂载u盘命令
    private void executeDiskMount() {
	try{
            Process p = Runtime.getRuntime().exec(UMOUNT);
            p.waitFor();
            p = Runtime.getRuntime().exec(MOUNT);
            p.waitFor();
            p = Runtime.getRuntime().exec(DEL_SDCARD);
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
                //设置配置文件
                analysisConfigProperties(SDCARD+"/"+CONFIG_FILE);
                
                //设置视频
                File videoFile = new File(VIDEO_CONTENTS);
                if (!videoFile.exists()) {
                   videoFile.mkdir();
                }
                getVideoTypeContentList(VIDEO_CONTENTS);
                makeTableVideoType();
                getAllVideoContents();
                analysisAllVideosProperties();
                makeTableVideo();
                //**************************************************************************
                //设置音频
                File voiceFile = new File(VOICE_CONTENTS);
                if (!voiceFile.exists()) {
                    voiceFile.mkdir();
                }
                getVoiceTypeContentList(VOICE_CONTENTS);
                makeTableVoiceType();
                getAllVoiceContents();
                analysisAllVoiceProperties();
                makeTableVoice();
                
                //**************************************************************************
                //设置app
                File appFile = new File(APP_CONTENTS);
                if(!appFile.exists()){
                    appFile.mkdir();
                }
                getAppTypeContentList(APP_CONTENTS);
                makeTableAppType();
                getAllAppContents();
                analysisAllAppProperties();
                makeTableApp();
                //挂载sdcard到指定目录
                executeDiskMount();
                
            }
        }
        
    }
}
