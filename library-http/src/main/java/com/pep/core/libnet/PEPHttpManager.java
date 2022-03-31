package com.pep.core.libnet;

import android.text.TextUtils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.pep.core.libnet.PEPHttpCoinfig.BASE_URL;
import static com.pep.core.libnet.PEPHttpCoinfig.IS_DEBUG;


/**
 * The type Http manager.
 *
 * @author sunbaixin
 */
public class PEPHttpManager {

    private Retrofit retrofit;

    private PEPHttpManager() {
    }


    private static class InnerObject {
        private static PEPHttpManager single = new PEPHttpManager();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PEPHttpManager getInstance() {
        return InnerObject.single;
    }

    /**
     * interceptors.
     */
    private ArrayList<Interceptor> interceptors = new ArrayList<>();

    /**
     * networkInterceptor.
     */
    private ArrayList<Interceptor> networkInterceptor = new ArrayList<>();
    /**
     * networConverterFactoryk.
     */
    private ArrayList<Converter.Factory> networConverterFactoryk = new ArrayList<>();


    /**
     * Init.
     *
     * @param baseUrl the base url
     * @throws RuntimeException the runtime exception
     */
    public void init(String baseUrl) throws RuntimeException {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new RuntimeException("baseUrl is null");
        }
        PEPHttpCoinfig.BASE_URL = baseUrl;
        OkHttpClient.Builder builder = getOkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        if (IS_DEBUG) {
            // Log Interceptor print
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();

            //Log pring level
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            addInterceptor(loggingInterceptor);
        }

        // set interceptors in okhttp builder
        setInterceptors(builder);


        OkHttpClient client = builder.build();
//        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder().baseUrl(BASE_URL).client(client);
        setConverterFactorys(retrofitBuilder);
        retrofit = retrofitBuilder.build();
    }

    private void setInterceptors(OkHttpClient.Builder builder) {

        for (int i = 0; i < interceptors.size(); i++) {
            builder.addInterceptor(interceptors.get(i));
        }

        for (int i = 0; i < networkInterceptor.size(); i++) {
            builder.addNetworkInterceptor(networkInterceptor.get(i));
        }
    }


    private void setConverterFactorys(Retrofit.Builder retrofitBuilder) {

//        for (int i = 0; i < networConverterFactoryk.size(); i++) {
//            retrofitBuilder.addConverterFactory(networConverterFactoryk.get(i));
//        }
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
    }

    private static OkHttpClient getOkHttpClient() {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            OkHttpClient okHttpClient;
            if (IS_DEBUG) {
                okHttpClient = getOkHttpClientTest();
            } else {
                okHttpClient = builder.build();
            }
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有证书OkHttpClient
     */
    private static OkHttpClient getOkHttpClientTest() {
        OkHttpClient client = null;
        try {
            X509TrustManager x509TrustManager = new X509TrustManager() {

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };

            SSLContext sslContext;
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager).hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            //关键代码，返回false证书验证，返回true信任所有证书。
                            return true;
                        }
                    })
                    .build();
        } catch (Exception e) {
            client = new OkHttpClient.Builder().build();
            e.printStackTrace();
        }
        return client;
    }


    /**
     * 设置测试证书
     */
    private static void setTestSSL(OkHttpClient.Builder builder) {
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };

        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
            builder.sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add interceptor.
     *
     * @param interceptor the interceptor
     */
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Add interceptor.
     *
     * @param converterFactory the interceptor
     */
    public void addConverterFactory(Converter.Factory converterFactory) {
        networConverterFactoryk.add(converterFactory);
    }

    /**
     * Gets retrofit.
     *
     * @return the retrofit
     */
    public Retrofit getRetrofit() {
        if (retrofit == null) {
            return null;
        }
        return retrofit;
    }

    /**
     * Gets service.
     *
     * @param <T>     the type parameter
     * @param service the service
     * @return the service
     */
    public <T> T getService(final Class<T> service) {
        return retrofit.create(service);
    }

}
