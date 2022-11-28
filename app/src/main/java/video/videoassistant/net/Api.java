package video.videoassistant.net;

import androidx.annotation.NonNull;

import com.azhon.basic.retrofit.BaseApi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * 项目名:    TODO-MVVM
 * 包名       com.azhon.mvvm.api
 * 文件名:    Api
 * 创建时间:  2019-03-27 on 14:56
 * 描述:     TODO 使用Retrofit基础服务
 *
 * @author 阿钟
 */

public class Api extends BaseApi implements RequestHandler {

    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 10000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 10000;
    private static final String TAG = "Api";


    /**
     * 静态内部类单例
     */
    private static class ApiHolder {
        private static Api api = new Api();
        private final static ApiService url = api.initRetrofit(ApiService.URL)
                .create(ApiService.class);
    }


    public static ApiService getApi() {
        return ApiHolder.url;
    }


    /**
     * 做自己需要的操作
     */
    @Override
    protected OkHttpClient setClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.followRedirects(true);
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        });


        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        builder.addInterceptor(new NetInterceptor(this));
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
        return builder.build();
    }

    final X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };


    public SSLContext getSSLContext() {
        X509TrustManager xtm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
            //sslContext = SSLContext.getInstance("TLSv1.2");
            //sslContext.init(null, null, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    @Override
    public Request onBeforeRequest(Request request, Interceptor.Chain chain) {
        Request.Builder builder = chain.request().newBuilder();
        return builder.build();
    }


    @Override
    public Response onAfterRequest(@NonNull Response response, Interceptor.Chain chain) throws IOException {
        ApiException e = null;

        if(302 == response.code()){
            throw new ApiException("302");
        }

        if (401 == response.code()) {
            throw new ApiException("登录已过期,请重新登录!");
        }
        if (String.valueOf(response.code()).endsWith("5")) {
            throw new ApiException("服务器错误");
        }
        if (404 == response.code()) {
            throw new ApiException("接口不存在");
        }
        return response;
    }

}
