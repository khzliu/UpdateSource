package updatemedia;

/**
 * @author louxuezheng 2013年11月7日
 */
public class Voice {
	//private Long id;            //视频id
        private String dirname;     //音频目录
        private String type;        //视频类型
	private String name;        //视频名称
	private String duration;    //视频时长
	private String date;        //视频日期
	private Integer clicks;     //视频点击数
	private String des;         //视频描述
        private String actor;       //演员

        public String getActor() {
            return actor;
        }
        public void setActor(String actor) {
            this.actor = actor;
        }
        public String getDirname() {
		return dirname;
	}

	public void setDirname(String dirname) {
		this.dirname = dirname;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
}