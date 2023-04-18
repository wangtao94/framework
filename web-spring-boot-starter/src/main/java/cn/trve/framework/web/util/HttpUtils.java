package cn.trve.framework.web.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * JDK的HttpClient工具类
 *
 * @author Wangtao
 * @since 0.0.1-Alpha
 */
public class HttpUtils {
    private static volatile HttpClient client;
    private static final HttpClientProps HTTP_CLIENT_PROPS = new HttpClientProps();

    static {
        if (client == null) {
            synchronized (HttpUtils.class) {
                if (client == null) {
                    HttpClient.Builder builder = HttpClient.newBuilder().version(HTTP_CLIENT_PROPS.getVersion())
                        .connectTimeout(Duration.ofMillis(HTTP_CLIENT_PROPS.getConnectTimeout()))
                        .followRedirects(HTTP_CLIENT_PROPS.getRedirect());
                    Optional.ofNullable(HTTP_CLIENT_PROPS.getAuthenticator()).ifPresent(builder::authenticator);
                    Optional.ofNullable(HTTP_CLIENT_PROPS.getCookieHandler()).ifPresent(builder::cookieHandler);
                    Optional.ofNullable(HTTP_CLIENT_PROPS.getProxySelector()).ifPresent(builder::proxy);
                    Optional.ofNullable(HTTP_CLIENT_PROPS.getExecutor()).ifPresent(builder::executor);
                    client = builder.build();
                }
            }
        }
    }

    /**
     * 同步GET请求，返回值解析为字符串
     *
     * @param url 访问URL
     * @return java.lang.String
     * @throws IOException          IO异常
     * @throws InterruptedException exception: InterruptedException
     * @author Wangtao
     */
    public static String doGet(String url) throws IOException, InterruptedException {
        return doGet(url, Map.of());
    }

    /**
     * 同步GET请求，返回值解析为字符串
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @return java.lang.String
     * @throws IOException          IO异常
     * @throws InterruptedException exception: InterruptedException
     * @author Wangtao
     */
    public static String doGet(String url, Map<String, String> headerMap) throws IOException, InterruptedException {
        return doGet(url, headerMap, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步GET请求，返回值解析为字符串
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @return java.lang.String
     * @throws IOException IO异常 exception: InterruptedException
     * @author Wangtao
     */
    public static String doGet(String url, Map<String, String> headerMap, long timeout)
        throws IOException, InterruptedException {
        return doGet(url, headerMap, timeout, String.class);
    }

    /**
     * 同步GET请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.lang.String
     * @throws IOException          IO异常
     * @throws InterruptedException exception: InterruptedException
     * @author Wangtao
     */
    public static <T> T doGet(String url, Map<String, String> headerMap, long timeout, Class<T> resClass)
        throws IOException, InterruptedException {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步GET请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doGetResponse(String url, Map<String, String> headerMap, long timeout,
        Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步GET请求，返回byte[]
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<byte[]>> doGetByteResponseAsync(String url,
        Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 同步GET请求，返回String
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<String>> doGetStringResponseAsync(String url,
        Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 同步GET请求，返回InputStream
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<InputStream>> doGetInputStreamResponseAsync(String url,
        Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildGetRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }
    //=================================GET END========================================//

    //=================================POST BEGIN========================================//

    /**
     * 同步POST请求，通过请求体传送数据
     *
     * @param requestBody 请求体
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPost(String url, String requestBody) throws IOException, InterruptedException {
        return doPost(url, Map.of(), requestBody, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过form传送数据
     *
     * @param form form表单
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPost(String url, Map<String, Object> form) throws IOException, InterruptedException {
        return doPost(url, Map.of(), form, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPost(String url, Map<String, String> headerMap, String requestBody)
        throws IOException, InterruptedException {
        return doPost(url, headerMap, requestBody, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过Form传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPost(String url, Map<String, String> headerMap, Map<String, Object> form)
        throws IOException, InterruptedException {
        return doPost(url, headerMap, form, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步POST请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPost(String url, Map<String, String> headerMap, String requestBody, long timeout)
        throws IOException, InterruptedException {
        return doPost(url, headerMap, requestBody, timeout, String.class);
    }

    /**
     * 同步POST请求，通过FORM传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPost(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout)
        throws IOException, InterruptedException {
        return doPost(url, headerMap, form, timeout, String.class);
    }

    /**
     * 同步POST请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @param resClass    返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> T doPost(String url, Map<String, String> headerMap, String requestBody, long timeout,
        Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步POST请求，通过FORM传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap header键值对
     * @param form      form表单
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> T doPost(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout,
        Class<T> resClass) throws IOException, InterruptedException {
        return doPostResponse(url, headerMap, form, timeout, resClass).body();
    }

    /**
     * 同步POST请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @param resClass    返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doPostResponse(String url, Map<String, String> headerMap, String requestBody,
        long timeout, Class<T> resClass) throws IOException, InterruptedException {
        return doPostResponse(url, headerMap, HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8),
            timeout, resClass);
    }

    /**
     * 同步POST请求，通过FORM表单传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doPostResponse(String url, Map<String, String> headerMap,
        Map<String, Object> form, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        String[] headers = createHeader(headerMap, "application/x-www-form-urlencoded");
        Map<String, String> newHeader = new HashMap<>();
        for (int i = 0; i < headers.length; i = i + 2) {
            newHeader.put(headers[i], headers[i + 1]);
        }
        HttpRequest httpRequest = buildPostRequest(url, newHeader, form, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步POST请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap     header键值对
     * @param bodyPublisher 请求体
     * @param timeout       超时时间
     * @param resClass      返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doPostResponse(String url, Map<String, String> headerMap,
        HttpRequest.BodyPublisher bodyPublisher, long timeout, Class<T> resClass)
        throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, bodyPublisher, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<byte[]>> doPostByteResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<InputStream>> doPostInputStreamResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<String>> doPostStringResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 异步POST请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<byte[]>> doPostByteResponse(String url, Map<String, String> headerMap,
        String requestBody, long timeout) {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步POST请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<InputStream>> doPostInputStreamResponse(String url,
        Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步POST请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<String>> doPostStringResponseAsync(String url,
        Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPostRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    //=================================POST END========================================//

    //=================================文件上传 BEGIN========================================//

    /**
     * 同步上传文件，也可以附带数据，如果是文件，formData的value是FileProvider类型
     *
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doUploadResponse(String url, Map<String, String> headerMap,
        Map<String, Object> formData, long timeout, Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, formData, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步上传文件，也可以附带数据，如果是文件，formData的value是FileProvider类型
     *
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> T doUpload(String url, Map<String, String> headerMap, Map<String, Object> formData, long timeout,
        Class<T> resClass) throws IOException, InterruptedException {
        return doUploadResponse(url, headerMap, formData, timeout, resClass).body();
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<byte[]>> doUploadByteResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<InputStream>> doUploadInputStreamResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步POST请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<String>> doUploadStringResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) throws IOException {
        HttpRequest httpRequest = buildUploadRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    //=================================文件上传 END========================================//

    //=================================文件下载 BEGIN========================================//

    /**
     * 同步下载文件，构建httpRequest的方式参见 {@link #buildGetRequest(String, Map, long)}
     * {@link #buildPostRequest(String, Map, String, long)} {@link #buildPostRequest(String, Map, Map, long)}
     * {@link #buildPostRequest(String, Map, HttpRequest.BodyPublisher, long)}
     *
     * @param filePath 文件路径
     * @since 0.0.1-Alpha
     */
    public static Path doDownload(HttpRequest httpRequest, String filePath) throws IOException, InterruptedException {
        HttpResponse<Path> httpResponse =
            client.send(httpRequest, HttpResponse.BodyHandlers.ofFile(new File(filePath).toPath()));
        return httpResponse.body();
    }

    /**
     * 同步下载文件，构建httpRequest的方式参见 {@link #buildGetRequest(String, Map, long)}
     * {@link #buildPostRequest(String, Map, String, long)} {@link #buildPostRequest(String, Map, Map, long)}
     * {@link #buildPostRequest(String, Map, HttpRequest.BodyPublisher, long)}
     *
     * @param filePath 文件路径
     * @since 0.0.1-Alpha
     */
    public static HttpResponse<Path> doDownloadResponse(HttpRequest httpRequest, String filePath)
        throws IOException, InterruptedException {
        return client.send(httpRequest, HttpResponse.BodyHandlers.ofFile(new File(filePath).toPath()));
    }
    //=================================文件下载 END========================================//

    //=================================PUT BEGIN========================================//

    /**
     * 同步PUT请求，通过请求体传送数据
     *
     * @param requestBody 请求体
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPut(String url, String requestBody) throws IOException, InterruptedException {
        return doPut(url, Map.of(), requestBody, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过form传送数据
     *
     * @param form form表单
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPut(String url, Map<String, Object> form) throws IOException, InterruptedException {
        return doPut(url, Map.of(), form, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPut(String url, Map<String, String> headerMap, String requestBody)
        throws IOException, InterruptedException {
        return doPut(url, headerMap, requestBody, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过Form传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPut(String url, Map<String, String> headerMap, Map<String, Object> form)
        throws IOException, InterruptedException {
        return doPut(url, headerMap, form, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步PUT请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPut(String url, Map<String, String> headerMap, String requestBody, long timeout)
        throws IOException, InterruptedException {
        return doPut(url, headerMap, requestBody, timeout, String.class);
    }

    /**
     * 同步PUT请求，通过FORM传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static String doPut(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout)
        throws IOException, InterruptedException {
        return doPut(url, headerMap, form, timeout, String.class);
    }

    /**
     * 同步PUT请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @param resClass    返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> T doPut(String url, Map<String, String> headerMap, String requestBody, long timeout,
        Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步PUT请求，通过FORM传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap header键值对
     * @param form      form表单
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> T doPut(String url, Map<String, String> headerMap, Map<String, Object> form, long timeout,
        Class<T> resClass) throws IOException, InterruptedException {
        return doPutResponse(url, headerMap, form, timeout, resClass).body();
    }

    /**
     * 同步Put请求，通过请求体传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @param resClass    返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doPutResponse(String url, Map<String, String> headerMap, String requestBody,
        long timeout, Class<T> resClass) throws IOException, InterruptedException {
        return doPutResponse(url, headerMap, HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8),
            timeout, resClass);
    }

    /**
     * 同步Put请求，通过FORM表单传送数据，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doPutResponse(String url, Map<String, String> headerMap, Map<String, Object> form,
        long timeout, Class<T> resClass) throws IOException, InterruptedException {
        String[] headers = createHeader(headerMap, "application/x-www-form-urlencoded");
        Map<String, String> newHeader = new HashMap<>();
        for (int i = 0; i < headers.length; i = i + 2) {
            newHeader.put(headers[i], headers[i + 1]);
        }
        HttpRequest httpRequest = buildPutRequest(url, newHeader, form, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步Put请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap     header键值对
     * @param bodyPublisher 请求体
     * @param timeout       超时时间
     * @param resClass      返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doPutResponse(String url, Map<String, String> headerMap,
        HttpRequest.BodyPublisher bodyPublisher, long timeout, Class<T> resClass)
        throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, bodyPublisher, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 异步PUT请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<byte[]>> doPutByteResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步PUT请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<InputStream>> doPutInputStreamResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步PUT请求，通过form表单传送数据
     *
     * @param headerMap header键值对
     * @param form      表单
     * @param timeout   超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<String>> doPutStringResponseAsync(String url,
        Map<String, String> headerMap, Map<String, Object> form, long timeout) {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, form, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 异步PUT请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<byte[]>> doPutByteResponse(String url, Map<String, String> headerMap,
        String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 异步Put请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<InputStream>> doPutInputStreamResponse(String url,
        Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }

    /**
     * 异步Put请求，通过请求体传送数据
     *
     * @param headerMap   header键值对
     * @param requestBody 请求体
     * @param timeout     超时时间
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<String>> doPutStringResponseAsync(String url,
        Map<String, String> headerMap, String requestBody, long timeout) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildPutRequest(url, headerMap, requestBody, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }
    //=================================PUT END========================================//

    //=================================DELETE BEGIN========================================//

    /**
     * 同步DELETE请求，返回值解析为字符串
     *
     * @param url 访问URL
     * @return java.lang.String
     * @throws IOException          IO异常
     * @throws InterruptedException
     * @author Wangtao
     */
    public static String doDelete(String url) throws IOException, InterruptedException {
        return doDelete(url, Map.of());
    }

    /**
     * 同步DELETE请求，返回值解析为字符串
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @return java.lang.String
     * @throws IOException          IO异常
     * @throws InterruptedException
     * @author Wangtao
     */
    public static String doDelete(String url, Map<String, String> headerMap) throws IOException, InterruptedException {
        return doDelete(url, headerMap, HTTP_CLIENT_PROPS.getDefaultReadTimeout());
    }

    /**
     * 同步DELETE请求，返回值解析为字符串
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @return java.lang.String
     * @throws IOException IO异常 exception: InterruptedException
     * @author Wangtao
     */
    public static String doDelete(String url, Map<String, String> headerMap, long timeout)
        throws IOException, InterruptedException {
        return doDelete(url, headerMap, timeout, String.class);
    }

    /**
     * 同步DELETE请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.lang.String
     * @throws IOException          IO异常
     * @throws InterruptedException
     * @author Wangtao
     */
    public static <T> T doDelete(String url, Map<String, String> headerMap, long timeout, Class<T> resClass)
        throws IOException, InterruptedException {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return getResData(httpRequest, resClass);
    }

    /**
     * 同步DELETE请求，返回值支持的解析类型有byte[]、String、InputStream
     *
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @param resClass  返回类型，支持byte[].class、String.class、InputStream.class，其他类型会抛出UnsupportedOperationException
     * @return java.net.http.HttpResponse<T>
     * @since 0.0.1-Alpha
     */
    public static <T> HttpResponse<T> doDeleteResponse(String url, Map<String, String> headerMap, long timeout,
        Class<T> resClass) throws IOException, InterruptedException {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return getRes(httpRequest, resClass);
    }

    /**
     * 同步DELETE请求，返回byte[]
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<byte[]>> doDeleteByteResponseAsync(String url,
        Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * 同步DELETE请求，返回String
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<String>> doDeleteStringResponseAsync(String url,
        Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 同步Delete请求，返回InputStream
     *
     * @param url       访问URL
     * @param headerMap header键值对
     * @param timeout   超时时间
     * @since 0.0.1-Alpha
     */
    public static CompletableFuture<HttpResponse<InputStream>> doDeleteInputStreamResponseAsync(String url,
        Map<String, String> headerMap, long timeout) {
        HttpRequest httpRequest = buildDeleteRequest(url, headerMap, timeout);
        return client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
    }
    //=================================DELETE END========================================//

    /**
     * 获取Http客户端
     *
     * @return HttpClient
     * @since 0.0.1-Alpha
     */
    public static HttpClient getClient() {
        return client;
    }

    private static <T> T getResData(HttpRequest httpRequest, Class<T> resClass)
        throws IOException, InterruptedException {
        T t;
        if (byte[].class == resClass) {
            t = (T)client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray()).body();
        } else if (String.class == resClass) {
            t = (T)client.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } else if (InputStream.class == resClass) {
            t = (T)client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream()).body();
        } else {
            throw new UnsupportedOperationException(MessageFormat.format("不支持的返回类型:[{0}]", resClass));
        }
        return t;
    }

    private static <T> HttpResponse<T> getRes(HttpRequest httpRequest, Class<T> resClass)
        throws IOException, InterruptedException {
        HttpResponse<T> response = null;
        if (byte[].class == resClass) {
            response = (HttpResponse<T>)client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        } else if (String.class == resClass) {
            response = (HttpResponse<T>)client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } else if (InputStream.class == resClass) {
            response = (HttpResponse<T>)client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        } else {
            throw new UnsupportedOperationException(MessageFormat.format("不支持的返回类型:[{0}]", resClass));
        }
        return response;
    }

    public static HttpRequest buildGetRequest(String url, Map<String, String> headerMap, long timeout) {
        return HttpRequest.newBuilder().GET()
            .headers(createHeader(headerMap, HTTP_CLIENT_PROPS.getDefaultContentType())).uri(URI.create(url))
            .timeout(Duration.ofMillis(timeout)).build();
    }

    public static HttpRequest buildDeleteRequest(String url, Map<String, String> headerMap, long timeout) {
        return HttpRequest.newBuilder().DELETE()
            .headers(createHeader(headerMap, HTTP_CLIENT_PROPS.getDefaultContentType())).uri(URI.create(url))
            .timeout(Duration.ofMillis(timeout)).build();
    }

    public static HttpRequest buildPostRequest(String url, Map<String, String> headerMap, Map<String, Object> form,
        long timeout) {
        StringJoiner sj = new StringJoiner("&");
        form.forEach((k, v) -> sj.add(k + "=" + v.toString()));
        HttpRequest.BodyPublisher bodyPublisher =
            HttpRequest.BodyPublishers.ofString(sj.toString(), StandardCharsets.UTF_8);
        return buildPostRequest(url, headerMap, bodyPublisher, timeout);
    }

    public static HttpRequest buildPutRequest(String url, Map<String, String> headerMap, Map<String, Object> form,
        long timeout) {
        StringJoiner sj = new StringJoiner("&");
        form.forEach((k, v) -> {
            sj.add(k + "=" + v.toString());
        });
        HttpRequest.BodyPublisher bodyPublisher =
            HttpRequest.BodyPublishers.ofString(sj.toString(), StandardCharsets.UTF_8);
        return buildPutRequest(url, headerMap, bodyPublisher, timeout);
    }

    public static HttpRequest buildPostRequest(String url, Map<String, String> headerMap, String requestBody,
        long timeout) {
        return buildPostRequest(url, headerMap,
            HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8), timeout);
    }

    public static HttpRequest buildPutRequest(String url, Map<String, String> headerMap, String requestBody,
        long timeout) {
        return buildPutRequest(url, headerMap, HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8),
            timeout);
    }

    public static HttpRequest buildPostRequest(String url, Map<String, String> headerMap,
        HttpRequest.BodyPublisher bodyPublisher, long timeout) {
        return HttpRequest.newBuilder().POST(bodyPublisher)
            .headers(createHeader(headerMap, HTTP_CLIENT_PROPS.getDefaultContentType())).uri(URI.create(url))
            .timeout(Duration.ofMillis(timeout)).build();
    }

    public static HttpRequest buildPutRequest(String url, Map<String, String> headerMap,
        HttpRequest.BodyPublisher bodyPublisher, long timeout) {
        return HttpRequest.newBuilder().PUT(bodyPublisher)
            .headers(createHeader(headerMap, HTTP_CLIENT_PROPS.getDefaultContentType())).uri(URI.create(url))
            .timeout(Duration.ofMillis(timeout)).build();
    }

    /**
     * 构建jdk 11 的文件上传请求
     *
     * @param url
     * @param headerMap
     * @param formData
     * @param timeout
     * @return
     * @throws IOException
     */
    public static HttpRequest buildUploadRequest(String url, Map<String, String> headerMap,
        Map<String, Object> formData, long timeout) throws IOException {
        String multipartFormDataBoundary = "--Java11HttpClientFormBoundary";

        HttpRequest.BodyPublisher tempPublisher = HttpRequest.BodyPublishers.noBody();
        if (formData != null) {
            for (Map.Entry<String, Object> entry : formData.entrySet()) {
                String k = entry.getKey();
                Object v = entry.getValue();
                if (v instanceof FileProvider fileV) {

                    String s = multipartFormDataBoundary + String.format(
                        multipartFormDataBoundary + "\nContent-Disposition: form-data; name=\"s%\"; filename=\"s%\"\nContent-Type: s%\n\n",
                        k, fileV.getFileName(), fileV.getContentType());

                    tempPublisher =
                        HttpRequest.BodyPublishers.concat(tempPublisher, HttpRequest.BodyPublishers.ofString(s),
                            HttpRequest.BodyPublishers.ofInputStream(fileV::getInputStream));
                } else {

                    String s = multipartFormDataBoundary + String.format(
                        multipartFormDataBoundary + "\nContent-Disposition: form-data; name=\"s%\"\n\n" + "s%", k, v);

                    tempPublisher = HttpRequest.BodyPublishers.concat(tempPublisher,
                        HttpRequest.BodyPublishers.ofString(s, StandardCharsets.UTF_8));
                }
            }

            tempPublisher = HttpRequest.BodyPublishers.concat(tempPublisher,
                HttpRequest.BodyPublishers.ofString(multipartFormDataBoundary + "--", StandardCharsets.UTF_8));
        }

        return HttpRequest.newBuilder().POST(tempPublisher)
            .headers(createHeader(headerMap, "multipart/form-data; boundary=" + multipartFormDataBoundary))
            .uri(URI.create(url)).timeout(Duration.ofMillis(timeout)).build();
    }

    private static String[] createHeader(Map<String, String> headerMap, String contentType) {
        if (headerMap == null) {
            headerMap = new HashMap<>();
            headerMap.put("Content-Type", contentType);
        } else {
            headerMap = new HashMap<>(headerMap);
            Set<String> headerKeys = headerMap.keySet();
            if (headerKeys.stream().noneMatch("Content-Type"::equalsIgnoreCase)) {
                headerMap.put("Content-Type", contentType);
            }
        }
        String[] result = new String[headerMap.size() * 2];
        int index = 0;
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            result[index++] = entry.getKey();
            result[index++] = entry.getValue();
        }
        return result;
    }

    /**
     * 获取文件流
     */
    public interface FileProvider {
        InputStream getInputStream();

        String getFileName();

        String getContentType();
    }

    /**
     * 从路径获取文件流
     */
    public static class PathFileProvider implements FileProvider {

        private Path param;

        public PathFileProvider(Path param) {
            this.param = param;
        }

        @Override
        public InputStream getInputStream() {
            try {
                return Files.newInputStream(param);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getFileName() {
            return "file";
        }

        @Override
        public String getContentType() {
            return "image/png";
        }
    }


    /**
     * @author Wangtao
     */
    public static class HttpClientProps {


        /**
         * http版本
         */
        private HttpClient.Version version = HttpClient.Version.HTTP_1_1;

        /**
         * 转发策略
         */
        private HttpClient.Redirect redirect = HttpClient.Redirect.NORMAL;

        /**
         * 线程池
         */
        private Executor executor;

        /**
         * 认证
         */
        private Authenticator authenticator;

        /**
         * 代理
         */
        private ProxySelector proxySelector;

        /**
         * cookiehandler
         */
        private CookieHandler cookieHandler;

        /**
         * sslContext
         */
        private SSLContext sslContext;

        /**
         * sslParams
         */
        private SSLParameters sslParameters;

        /**
         * 连接超时时间毫秒
         */
        private int connectTimeout = 10000;

        /**
         * 默认读取数据超时时间
         */
        private int defaultReadTimeout = 1200000;

        /**
         * 默认content-type
         */
        private String defaultContentType = "application/json";




        public HttpClient.Version getVersion() {
            return version;
        }

        public void setVersion(HttpClient.Version version) {
            this.version = version;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }


        public HttpClient.Redirect getRedirect() {
            return redirect;
        }

        public void setRedirect(HttpClient.Redirect redirect) {
            this.redirect = redirect;
        }

        public Executor getExecutor() {
            return executor;
        }

        public void setExecutor(Executor executor) {
            this.executor = executor;
        }

        public Authenticator getAuthenticator() {
            return authenticator;
        }

        public void setAuthenticator(Authenticator authenticator) {
            this.authenticator = authenticator;
        }

        public ProxySelector getProxySelector() {
            return proxySelector;
        }

        public void setProxySelector(ProxySelector proxySelector) {
            this.proxySelector = proxySelector;
        }

        public CookieHandler getCookieHandler() {
            return cookieHandler;
        }

        public void setCookieHandler(CookieHandler cookieHandler) {
            this.cookieHandler = cookieHandler;
        }

        public int getDefaultReadTimeout() {
            return defaultReadTimeout;
        }

        public void setDefaultReadTimeout(int defaultReadTimeout) {
            this.defaultReadTimeout = defaultReadTimeout;
        }

        public String getDefaultContentType() {
            return defaultContentType;
        }

        public void setDefaultContentType(String defaultContentType) {
            this.defaultContentType = defaultContentType;
        }
    }
}

