package updatemedia;

/**
 * @author louxuezheng 2013年11月7日
 */
public class Video {
	//private Long id;            //视频id
        private String dirname;    //目录名
        private String type;        //视频类型
	private String name;        //视频名称
	private String duration;    //视频时长
	private String date;        //视频日期
	private Integer clicks;     //视频点击数
	private String des;         //视频描述
	private Float scale_x;      //视频x宽
	private Float scale_y;      //视频y高
        private String director;    //导演
        private String actor;       //演员
        private String singer;      //歌手
        private String album;       //专辑

	public String getDirector() {
            return director;
        }
        public String getActor() {
            return actor;
        }
        public String getSinger() {
            return singer;
        }
        public String getAlbum() {
            return album;
        }
        
        public void setDirector(String director){
            this.director = director;
        }
        public void setActor(String actor) {
            this.actor = actor;
        }
        public void setSinger(String singer) {
            this.singer = singer;
        }
        public void setAlbum(String album) {
            this.album = album;
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

}
