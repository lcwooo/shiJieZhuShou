package video.videoassistant.cloudPage;

public class SortTypeBean {
    String area;
    String year;
    boolean isCheck;
    String type;

    public SortTypeBean(String area, boolean isCheck, String type) {
        this.area = area;
        this.isCheck = isCheck;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
