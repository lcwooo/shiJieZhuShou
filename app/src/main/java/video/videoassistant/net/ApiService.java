package video.videoassistant.net;


import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import video.videoassistant.cloudPage.ListMovieBean;
import video.videoassistant.playPage.PlayBean;

/**
 * 项目名:    TODO-MVVM
 * 包名       com.azhon.mvvm.api
 * 文件名:    ApiService
 * 创建时间:  2019-03-27 on 14:55
 * 描述:     TODO 声明接口
 *
 * @author 阿钟
 */

public interface ApiService {


    String URL = "https://www.233dy.top";


    @GET()
    Flowable<String> checkUrl(@Url String url);

    @GET()
    Flowable<PlayBean> getPlayUrl(@Url String url);

    @GET("")
    Flowable<String> initListJson(@Url String url, @QueryMap Map<String, String> map);

    @GET("")
    Flowable<String> initListType(@Url String url,@QueryMap Map<String, String> map);


}
