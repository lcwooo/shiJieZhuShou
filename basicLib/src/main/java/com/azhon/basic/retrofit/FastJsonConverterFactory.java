package com.azhon.basic.retrofit;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by remilia on 2017/5/11.
 */

public class FastJsonConverterFactory extends Converter.Factory {

    public static FastJsonConverterFactory create() {
        return new FastJsonConverterFactory();
    }

    private FastJsonConverterFactory() {

    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new FastJsonResponseBodyConverter<>(type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new FastJsonRequestBodyConverter<>();
    }

    static final class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

        @Override
        public RequestBody convert(T value) {
            try {
                return RequestBody.create(MEDIA_TYPE, JSON.toJSONBytes(value));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static final class FastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Type type;

        public FastJsonResponseBodyConverter(Type type) {
            this.type = type;
        }

        /**
         * 转换方法
         */
        @Override
        public T convert(ResponseBody value) {
            try {
                BufferedSource bufferedSource = Okio.buffer(value.source());
                String tempStr = bufferedSource.readUtf8();
                bufferedSource.close();
                T t = JSON.parseObject(tempStr, type);
                return t;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

