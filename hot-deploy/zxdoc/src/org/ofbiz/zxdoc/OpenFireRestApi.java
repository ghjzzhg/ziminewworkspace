package org.ofbiz.zxdoc;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import java.io.IOException;
import java.util.Map;

/**
 * Created by galaxypan on 16.12.20.
 */
public class OpenFireRestApi {

    /**
     * @param param参数（主要是xml字符串或json字符串）
     * @param url（请求地址）
     * @param format（xml，                json）
     * @param httpMethod（请求方式）
     * @return
     */
    public static Map<String, Object> doExecute(String param, String url, String format, String httpMethod) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpRequestBase httpRequestBase = null;
        switch (httpMethod) {
            case "post":
                httpRequestBase = new HttpPost(url);
                break;
            case "get":
                httpRequestBase = new HttpGet(url);
                break;
            case "put":
                httpRequestBase = new HttpPut(url);
                break;
            case "delete":
                httpRequestBase = new HttpDelete(url);
                break;
            default:
                httpRequestBase = null;
        }
        httpRequestBase.setHeader("Authorization", UtilProperties.getPropertyValue("zxdoc", "rest_secret_key"));
        if (UtilValidate.isNotEmpty(param) && httpRequestBase instanceof HttpEntityEnclosingRequestBase) {
            StringEntity stringEntity = new StringEntity(param, "utf-8");
            stringEntity.setContentEncoding("UTF-8");
            if ("json".equals(format)) {
                stringEntity.setContentType("application/json");
            } else {
                stringEntity.setContentType("application/xml");
            }
            ((HttpEntityEnclosingRequestBase) httpRequestBase).setEntity(stringEntity);
        }
        switch (format) {
            case "json":
                httpRequestBase.setHeader(HTTP.CONTENT_TYPE, "application/json");
                break;
            case "xml":
                httpRequestBase.setHeader(HTTP.CONTENT_TYPE, "application/xml");
                break;
            default:
                httpRequestBase.setHeader(HTTP.CONTENT_TYPE, "text/html");
                break;
        }
        HttpResponse response = httpClient.execute(httpRequestBase);
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        int code = response.getStatusLine().getStatusCode();
        if (code == 200 || 201 == code) {
            return UtilMisc.toMap("status", true, "result", result != null ? result : "");
        } else {
            return UtilMisc.toMap("status", false, "result", result != null ? result : "");
        }
    }
}