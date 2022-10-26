package video.videoassistant.net;


import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Url;

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

}
