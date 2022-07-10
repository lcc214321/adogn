package com.dongyulong.dogn.core.http;

import com.dongyulong.dogn.core.http.builder.BasicGetBuilder;
import com.dongyulong.dogn.core.http.builder.BasicPostBuilder;
import com.dongyulong.dogn.core.http.builder.JsonPostBuilder;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * httpclient
 *
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class HttpClient {

    private OkHttpClient okHttpClient;

    public HttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public BasicGetBuilder get() {
        return new BasicGetBuilder(this);
    }

    public BasicPostBuilder postForm() {
        return new BasicPostBuilder(this);
    }

    public JsonPostBuilder postJson() {
        return new JsonPostBuilder(this);
    }

    /**
     * 构造http请求参数信息
     */
    public static class Builder {
        /**
         * 基本的配置时间
         */
        private long readTimeOut = 2000;
        private long writeTimeOut = 2000;
        private long connTimeOut = 2000;
        private OkHttpClient okHttpClient;
        private String httpName;
        private OkHttpClient.Builder builder;
        private ConnectionPool connectionPool;
        private SSLSocketFactory sslSocketFactory;
        private HostnameVerifier hostnameVerifier;

        public Builder(String httpName) {
            if (StringUtils.isNotEmpty(httpName)) {
                this.httpName = httpName;
            }
        }

        public Builder(OkHttpClient okHttpClient, String httpName) {
            this.builder = okHttpClient.newBuilder();
            this.httpName = httpName;
        }


        /**
         * 读超时配置
         *
         * @param readTimeOut
         * @return
         */
        public Builder readTimeOut(long readTimeOut) {
            if (readTimeOut <= 0) {
                return this;
            }
            this.readTimeOut = readTimeOut;
            return this;
        }

        /**
         * 写超时配置
         *
         * @param writeTimeOut
         * @return
         */
        public Builder writeTimeOut(long writeTimeOut) {
            if (writeTimeOut <= 0) {
                return this;
            }
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        /**
         * 连接超时配置
         *
         * @param connTimeOut
         * @return
         */
        public Builder connTimeOut(long connTimeOut) {
            if (connTimeOut <= 0) {
                return this;
            }
            this.connTimeOut = connTimeOut;
            return this;
        }

        /**
         * 自定义OkHttpClient
         *
         * @param client
         * @return
         */
        public Builder okHttpClient(OkHttpClient client) {
            this.okHttpClient = client;
            return this;
        }

        /**
         * 自定义SSLSocketFactory
         *
         * @param sslSocketFactory
         * @return
         */
        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        /**
         * 自定义HostnameVerifier
         *
         * @param hostnameVerifier
         * @return
         */
        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        /**
         * 这是链接池
         *
         * @param maxIdleConnections
         * @return
         */
        public Builder connectionPool(int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
            if (readTimeOut <= 0) {
                return this;
            }
            this.connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, timeUnit);
            return this;
        }

        /**
         * 构造http请求信息
         *
         * @return
         */
        public HttpClient build() {
            if (okHttpClient == null) {
                if (builder == null) {
                    builder = new OkHttpClient.Builder();
                }
                HttpInterceptor logging = new HttpInterceptor(httpName);
                TimeInterceptor time = new TimeInterceptor(httpName);

                builder.connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                        .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                        .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                        .addInterceptor(time)
                        .addInterceptor(logging);
                //添加链接池信息
                if (connectionPool != null) {
                    builder.connectionPool(connectionPool);
                }
                if (sslSocketFactory != null) {
                    builder.sslSocketFactory(sslSocketFactory, new X509TrustManager() {
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
                }
                if (hostnameVerifier != null) {
                    builder.hostnameVerifier(hostnameVerifier);
                }
                okHttpClient = builder.build();
            }
            return new HttpClient(okHttpClient);
        }
    }
}
