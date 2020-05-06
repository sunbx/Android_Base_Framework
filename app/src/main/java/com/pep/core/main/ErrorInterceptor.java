package com.pep.core.main;

/**
 * @author sunbaixin QQ:283122529
 * @name AndroidBaseFrame
 * @class name：com.pep.core.main
 * @class describe
 * @time 2020-04-07 17:03
 * @change
 * @chang time
 * @class describe
 */

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

public class ErrorInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request  request  = chain.request();
        Response response = chain.proceed(request);

        ResponseBody responseBody = response.body();
        String       string       = responseBody.string();

        return response;
    }


}
