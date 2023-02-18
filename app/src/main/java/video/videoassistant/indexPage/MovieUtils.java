package video.videoassistant.indexPage;

import com.alibaba.fastjson.JSON;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import video.videoassistant.cloudPage.ListMovieBean;
import video.videoassistant.cloudPage.MovieBean;
import video.videoassistant.cloudPage.MovieItemBean;
import video.videoassistant.cloudPage.XmlMovieBean;
import video.videoassistant.util.UiUtil;

public class MovieUtils {


    public static List<XmlMovieBean> xml(String s) {
        List<XmlMovieBean> movieList = new ArrayList<>();
        try {
            List<MovieItemBean> movieItemBeans = null;
            XmlMovieBean movieBean = null;
            InputStream inputStream = new ByteArrayInputStream(s.getBytes());
            // 创建一个xml解析的工厂
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            // 获得xml解析类的引用
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            // 获得事件的类型
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if ("video".equals(parser.getName())) {
                            movieBean = new XmlMovieBean();
                        } else if ("name".equals(parser.getName())) {
                            movieBean.setName(parser.nextText());
                        } else if ("pic".equals(parser.getName())) {
                            movieBean.setPic(parser.nextText());
                        } else if ("type".equals(parser.getName())) {
                            movieBean.setType(parser.nextText());
                        } else if ("lang".equals(parser.getName())) {
                            movieBean.setLang(parser.nextText());
                        } else if ("area".equals(parser.getName())) {
                            movieBean.setArea(parser.nextText());
                        } else if ("year".equals(parser.getName())) {
                            movieBean.setYear(parser.nextText());
                        } else if ("note".equals(parser.getName())) {
                            movieBean.setNote(parser.nextText());
                        } else if ("id".equals(parser.getName())) {
                            movieBean.setId(parser.nextText());
                        } else if ("actor".equals(parser.getName())) {
                            movieBean.setActor(parser.nextText());
                        } else if ("director".equals(parser.getName())) {
                            movieBean.setDirector(parser.nextText());
                        } else if ("dl".equals(parser.getName())) {
                            movieItemBeans = new ArrayList<>();
                        } else if ("dd".equals(parser.getName())) {
                            MovieItemBean itemBean = new MovieItemBean();
                            itemBean.setFrom(parser.getAttributeValue(0));
                            itemBean.setPlayUrl(parser.nextText());
                            movieItemBeans.add(itemBean);

                        } else if ("des".equals(parser.getName())) {
                            movieBean.setInfo(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("video".equals(parser.getName())) {
                            movieBean.setMovieItemBeans(movieItemBeans);
                            movieList.add(movieBean);
                            movieBean = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            return movieList;
        } catch (Exception e) {
            return null;
        }

    }

    public static List<XmlMovieBean> initJson(String s) {
        try {
            ListMovieBean b = JSON.parseObject(s, ListMovieBean.class);
            List<XmlMovieBean> movieList = new ArrayList<>();
            List<MovieBean> movieBeanList = b.getMovieBeanList();
            for (MovieBean movieBean : movieBeanList) {
                XmlMovieBean bean = new XmlMovieBean();
                bean.setActor(movieBean.getVodActor());
                bean.setArea(movieBean.getVodArea());
                bean.setActor(movieBean.getVodActor());
                bean.setDirector(movieBean.getVodDirector());
                bean.setPic(movieBean.getVodPic());
                bean.setName(movieBean.getVodName());
                bean.setYear(movieBean.getVodYear());
                bean.setLang(movieBean.getVodLang());
                bean.setId(movieBean.getVodId() + "");
                bean.setNote(movieBean.getVodRemarks());
                bean.setInfo(movieBean.getVodContent());
                List<MovieItemBean> list = new ArrayList<>();
                if (!movieBean.getVodPlayFrom().contains("$$$")) {
                    MovieItemBean itemBean = new MovieItemBean();
                    itemBean.setFrom(movieBean.getVodPlayFrom());
                    itemBean.setPlayUrl(movieBean.getVodPlayUrl());
                    list.add(itemBean);
                } else {
                    List<String> type = Arrays.asList(movieBean.getVodPlayFrom().split("\\$\\$\\$"));
                    List<String> address = Arrays.asList(movieBean.getVodPlayUrl().split("\\$\\$\\$"));
                    for (int i = 0; i < type.size(); i++) {
                        MovieItemBean be = new MovieItemBean();
                        be.setFrom(type.get(i));
                        be.setPlayUrl(address.get(i));
                        list.add(be);
                    }

                }
                bean.setMovieItemBeans(list);
                movieList.add(bean);
            }
            return movieList;
        } catch (Exception e) {
            return null;
        }

    }
}
