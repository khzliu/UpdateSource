/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package updatesource;

/**
 *
 * @author liu.huazhou <khzliu@163.com>
 */
public class BeanVideo {
    private String name;        //视频名称    
    private String type;        //视频类型	
    private String duration;    //视频时长
    private Integer clicks;     //视频点击数
    private String des;         //视频描述
    private Float scale_x;      //视频x宽
    private Float scale_y;      //视频y高
    private String actor;       //演员
    private Integer priase;     //点赞次数
    private Integer isFile;     //是否是文件夹

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getType() {
            return this.type;
    }
    public void setType(String type) {
            this.type = type;
    }
    public String getName() {
            return name;
    }

    public void setName(String name) {
            this.name = name;
    }

    public String getDuration() {
            return duration;
    }

    public void setDuration(String duration) {
            this.duration = duration;
    }

    public Integer getClicks() {
            return clicks;
    }

    public void setClicks(Integer clicks) {
            this.clicks = clicks;
    }

    public String getDes() {
            return des;
    }

    public void setDes(String des) {
            this.des = des;
    }

    public Float getScale_x() {
            return scale_x;
    }

    public void setScale_x(Float scale_x) {
            this.scale_x = scale_x;
    }

    public Float getScale_y() {
            return scale_y;
    }

    public void setScale_y(Float scale_y) {
            this.scale_y = scale_y;
    }

    public Integer getPriase(){
        return priase;
    }
    public void setPriase(Integer priase){
        this.priase = priase;
    }
    public Integer getIsFile(){
        return isFile;
    }
    public void setIsFile(Integer isFile){
        this.isFile = isFile;
    }
        

}
