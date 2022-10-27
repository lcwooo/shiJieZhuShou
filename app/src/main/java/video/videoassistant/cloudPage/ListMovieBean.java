package video.videoassistant.cloudPage;

import com.alibaba.fastjson.annotation.JSONField;


import java.util.List;

public class ListMovieBean {


    @JSONField(name = "code")
    private int code;
    @JSONField(name = "msg")
    private String msg;
    @JSONField(name = "page")
    private String page;
    @JSONField(name = "pagecount")
    private int pagecount;
    @JSONField(name = "limit")
    private String limit;
    @JSONField(name = "total")
    private int total;
    @JSONField(name = "list")
    private List<MovieBean> movieBeanList;

    public List<MovieBean> getMovieBeanList() {
        return movieBeanList;
    }

    public void setMovieBeanList(List<MovieBean> movieBeanList) {
        this.movieBeanList = movieBeanList;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
