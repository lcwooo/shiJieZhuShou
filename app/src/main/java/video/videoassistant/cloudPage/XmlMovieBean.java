package video.videoassistant.cloudPage;

import java.util.List;

public class XmlMovieBean {
    String name;
    String type;
    String pic;
    String lang;
    String area;
    String year;
    String note;
    String actor;
    String director;
    String dl;
    List<MovieItemBean> movieItemBeans;

    public List<MovieItemBean> getMovieItemBeans() {
        return movieItemBeans;
    }

    public void setMovieItemBeans(List<MovieItemBean> movieItemBeans) {
        this.movieItemBeans = movieItemBeans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDl() {
        return dl;
    }

    public void setDl(String dl) {
        this.dl = dl;
    }


}
