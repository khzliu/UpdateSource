/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package updatesource;

/**
 *
 * @author liu.huazhou <khzliu@163.com>
 */
public class BeanApp {
        private String type;        //视频类型
	private String name;        //视频名称
	private Float size;     //视频点击数
	private String desShort;         //视频描述
        private String desLong;
        private Integer posterNum;     //poster个数
        private Integer clicks; //点击下载次数

        public String getDesShort() {
            return desShort;
        }
        public void setDesShort(String desShort) {
            this.desShort = desShort;
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

	public String getDesLong() {
		return desLong;
	}

	public void setDesLong(String desLong) {
		this.desLong = desLong;
	}

	public Integer getClicks() {
		return clicks;
	}

	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}
        public Integer getPosterNum() {
		return posterNum;
	}

	public void setPosterNum(Integer posterNum) {
		this.posterNum = posterNum;
	}

        public float getSize(){
            return size;
        }
        public void setSize(float size){
            this.size = size;
        }
    
}
