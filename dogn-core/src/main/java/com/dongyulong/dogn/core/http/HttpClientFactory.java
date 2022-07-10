package com.dongyulong.dogn.core.http;

import okhttp3.OkHttpClient;

/**
 *
 * 简单的请求数据信息
 * @author zhangshaolong
 * @create 2022/1/26
 */
public class HttpClientFactory {

	/**
     * 获取链接的builder
	 * @param httpName
     * @return
     */
	public static HttpClient newBuilder(String httpName) {
		return new HttpClient.Builder(httpName).build();
	}

	/**
     * 获取链接的builder
	 * @param client
     * @param client
     * @return
     */
	public static HttpClient newBuilder(OkHttpClient client, String httpName){
		return new HttpClient.Builder(client,httpName).build();
	}

	/**
	 * 获取链接自定义链接的
	 * @param httpName
	 * @return
	 */
	public static HttpClient.Builder newBuilderClient(String httpName) {
		return new HttpClient.Builder(httpName);
	}
}
