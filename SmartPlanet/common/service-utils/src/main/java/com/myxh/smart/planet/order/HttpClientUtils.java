package com.myxh.smart.planet.order;

import lombok.Getter;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author MYXH
 * @date 2023/10/21
 * @description http 请求客户端
 */
public class HttpClientUtils
{
    private String url;
    private Map<String, String> param;
    @Getter
    private int statusCode;
    @Getter
    private String content;
    @Getter
    private String xmlParam;
    private boolean isHttps;

    public HttpClientUtils(String url)
    {
        this.url = url;
    }

    public HttpClientUtils(String url, Map<String, String> param)
    {
        this.url = url;
        this.param = param;
    }

    public void setXmlParam(String xmlParam)
    {
        this.xmlParam = xmlParam;
    }

    public boolean isHttps()
    {
        return isHttps;
    }

    public void setHttps(boolean isHttps)
    {
        this.isHttps = isHttps;
    }

    public void setParameter(Map<String, String> map)
    {
        param = map;
    }

    public void addParameter(String key, String value)
    {
        if (param == null)
        {
            param = new HashMap<>();
        }

        param.put(key, value);
    }

    public void post() throws IOException
    {
        HttpPost http = new HttpPost(url);
        setEntity(http);
        execute(http);
    }

    public void put() throws IOException
    {
        HttpPut http = new HttpPut(url);
        setEntity(http);
        execute(http);
    }

    public void get() throws IOException
    {
        if (param != null)
        {
            StringBuilder url = new StringBuilder(this.url);
            boolean isFirst = true;

            for (String key : param.keySet())
            {
                if (isFirst)
                {
                    url.append("?");
                    isFirst = false;
                }
                else
                {
                    url.append("&");
                }

                url.append(key).append("=").append(param.get(key));
            }

            this.url = url.toString();
        }

        HttpGet http = new HttpGet(url);
        execute(http);
    }

    /**
     * 设置 http post、put 参数
     */
    private void setEntity(HttpEntityEnclosingRequestBase http)
    {
        if (param != null)
        {
            List<NameValuePair> nvps = new LinkedList<>();

            for (String key : param.keySet())
            {
                // 参数
                nvps.add(new BasicNameValuePair(key, param.get(key)));
            }

            // 设置参数
            http.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        }

        if (xmlParam != null)
        {
            http.setEntity(new StringEntity(xmlParam, Consts.UTF_8));
        }
    }

    private void execute(HttpUriRequest http) throws IOException
    {
        CloseableHttpClient httpClient = null;

        try
        {
            if (isHttps)
            {
                // 信任所有
                SSLContext sslContext = SSLContextBuilder.create()
                        .loadTrustMaterial((chain, authType) -> true).build();

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            }
            else
            {
                httpClient = HttpClients.createDefault();
            }

            try (CloseableHttpResponse response = httpClient.execute(http))
            {
                if (response != null)
                {
                    if (response.getStatusLine() != null)
                    {
                        statusCode = response.getStatusLine().getStatusCode();
                    }

                    HttpEntity entity = response.getEntity();

                    // 响应内容
                    content = EntityUtils.toString(entity, Consts.UTF_8);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            httpClient.close();
        }
    }
}
