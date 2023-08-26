package video.videoassistant.cloudPage;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XmlDataUtil {


    public static String xml = "xml";
    public static String json = "json";
    public static String other = "other";


    //判断数据类型
    public static String dataType(String data) {
        if (data.startsWith("<?xml")) {
            return xml;
        } else if (data.startsWith("{")) {
            return json;
        } else {
            return other;
        }
    }


    public static List<XmlMovieBean> initXms(String s) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("SearchAdapter", "initXms: "+movieList.size());
        return movieList;
    }


}
