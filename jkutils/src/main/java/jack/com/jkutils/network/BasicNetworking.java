package jack.com.jkutils.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import jack.com.jkutils.JKUtils;
import jack.com.jkutils.file.FileUtils;
import jack.com.jkutils.thread.OperationQueue;

public class BasicNetworking {

    private static final int Time_Out_Interval = 5000;
    private static final String REQUEST_BOUNDARY = "__JK_Networking_Boundary__";

    private static final String userAgent = "JKNetworking/1.0 Android HttpURLConnection";

    private static volatile BasicNetworking sharedInstance;
    private HashMap<String, OperationQueue.OperationTask> excutingTasks = new HashMap();
    private Semaphore semaphore = new Semaphore(0);

    private BasicNetworking() {

    }

    public static BasicNetworking sharedNetworking() {
        if (sharedInstance == null) {
            synchronized (BasicNetworking.class) {
                if (sharedInstance == null) {
                    sharedInstance = new BasicNetworking();
                }
            }

        }
        return sharedInstance;
    }

    private void removeTask(String token) {
        if (token == null) {
            return;
        }

        try {
            semaphore.acquire();

            if (excutingTasks.containsKey(token)) {
                excutingTasks.remove(token);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public void cancelTask(String token) {
        if (token == null) {
            return;
        }

        try {

            semaphore.acquire();

            if (excutingTasks.containsKey(token)) {
                OperationQueue.OperationTask task = excutingTasks.get(token);
                if (task != null) {
                    task.cancel(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    private void addTask(String token, OperationQueue.OperationTask task) {

        try {
            semaphore.acquire();

            if (token != null && task != null) {
                excutingTasks.put(token, task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    /**
     * Define
     * */

    public static class Response {

        public int code;
        public JSONObject header;

        private Response(int code, JSONObject header) {
            this.code = code;
            this.header = header;
        }
    }

    public interface CompletionHandler {

        void progressChange(float progress);
        void completion(Response response, Object responseObject, Error error);
    }

    public enum ContentType {
        CONTENT_TYPE_FORM_URLENCODE,    // application/x-www-form-urlencoded
        CONTENT_TYPE_MULTIPART,         // multipart/form-data
        CONTENT_TYPE_JSON               // application/json
    }

    /**
     * Method
     * */

    private static JSONObject getResponseHeader(HttpURLConnection conn) {

        if (conn == null) {
            return null;
        }

        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        try {
            JSONObject responseHeader = new JSONObject();
            for(int i = 0; i < size; i++){
                String responseHeaderKey = conn.getHeaderFieldKey(i);
                String responseHeaderValue = conn.getHeaderField(i);
                if (responseHeaderKey != null && !responseHeaderKey.isEmpty() && responseHeaderValue != null) {
                    responseHeader.put(responseHeaderKey,responseHeaderValue);
                }
            }
            return responseHeader;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseSuggestedFilename(JSONObject response) {

        if (response == null) {
            return null;
        }

        try {
            String key = "Content-Disposition";
            String content_disposition = response.getString(key);
            if (content_disposition != null) {

                String keyword = "filename=";
                if (content_disposition.contains(keyword)) {

                    int index = content_disposition.indexOf(keyword);
                    String fileName = content_disposition.substring(index + keyword.length());

                    return fileName;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * 写请求体
     * */
    private void writeOutputStream(OutputStream out, String params) {

        if (out == null || params == null) {
            return;
        }

        try {

            OutputStreamWriter writer = new OutputStreamWriter(out);
            writer.write(params);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取请求结果
     * */
    private String readInputStream(InputStream inputStream, long total,CompletionHandler completionHandler) {

        if (inputStream == null) {
            return null;
        }

        try {
            // 对获取到到输入流读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;

            long read = 0;
            while ((line = reader.readLine()) != null) {
                response.append(line);

                if (completionHandler != null) {
                    read += line.getBytes().length;

                    float p = 0.0f;
                    if (total > 0) {
                        p = read * 1.0f / total;
                    }

                    completionHandler.progressChange(p);
                }
            }

            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String createJsonParams(JSONObject params) {
        if (params == null) {
            return "";
        }
        return params.toString();
    }

    private static String createFormParams(JSONObject params) {
        if (params == null || params.length() == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> iterator = params.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = params.opt(key);
            if (value != null) {
                if (first)
                    first = false;
                else
                    result.append("&");

                try {

                    result.append(URLEncoder.encode(key, "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(value.toString(), "UTF-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();

    }

    private static String createMultipartParams(JSONObject params) {
        if (params == null || params.length() == 0) {
            return "";
        }

        String startBoundary = "--" + REQUEST_BOUNDARY;
        String endBoundary = "--" + REQUEST_BOUNDARY + "--";

        StringBuilder stringBuilder = new StringBuilder();

        Iterator<String> iterator = params.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = params.opt(key);

            if (value != null) {

                stringBuilder.append(String.format("%s\r\n",startBoundary));
                stringBuilder.append(String.format("Content-Disposition: form-data; name=\"%s\"\r\n\r\n",key));
                stringBuilder.append(String.format("%s\r\n",value.toString()));
            }

        }
        stringBuilder.append(String.format("%s\r\n",endBoundary));

        return stringBuilder.toString();
    }

    private static void setContentType(HttpURLConnection connection, ContentType contentType) {

        String contentTypeValue;
        switch (contentType) {
            default:
            case CONTENT_TYPE_FORM_URLENCODE: {
                contentTypeValue = "application/x-www-form-urlencoded; charset=utf-8";
            }
            break;
            case CONTENT_TYPE_JSON: {
                contentTypeValue = "application/json; charset=utf-8";
            }
            break;
            case CONTENT_TYPE_MULTIPART: {
                contentTypeValue = "multipart/form-data; charset=utf-8; boundary=" + REQUEST_BOUNDARY;
            }
            break;
        }

        connection.setRequestProperty("Content-Type",contentTypeValue);
    }

    private static String setRequestData(JSONObject params, ContentType contentType) {
        if (params == null || params.length() == 0) {
            return "";
        }

        switch (contentType) {
            case CONTENT_TYPE_FORM_URLENCODE: {
                return createFormParams(params);
            }
            case CONTENT_TYPE_JSON: {
                return createJsonParams(params);
            }
            case CONTENT_TYPE_MULTIPART: {
                return createMultipartParams(params);
            }
        }

        return "";
    }

    /**
     * Request
     * */

    private void get(String urlString, CompletionHandler completionHandler) {

        Response response = null;
        Error error = null;
        Object responseObject = null;

        HttpURLConnection connection = null;
        try {

            URL url = new URL(urlString);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(Time_Out_Interval);
            connection.setReadTimeout(Time_Out_Interval);

            // 解决 getContentLength == -1
            connection.setRequestProperty("Accept-Encoding","identity");
            connection.setRequestProperty("User-Agent", userAgent);

            // 读取返回
            long contentLen = connection.getContentLength();
            responseObject = readInputStream(connection.getInputStream(), contentLen,completionHandler);

            // 读取Response
            JSONObject responseHeader = getResponseHeader(connection);
            int responseCode = connection.getResponseCode();

            response = new Response(responseCode,responseHeader);

        } catch (IOException e) {

            error = new Error(e);
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        if (completionHandler != null) {
            completionHandler.completion(response, responseObject, error);
        }
    }

    public String getContentOfURL(final String urlString, final CompletionHandler completionHandler) {

        if (urlString == null || urlString.length() == 0) {
            return null;
        }

        final String md5 = JKUtils.md5OfString(urlString);

        OperationQueue.OperationTask task = OperationQueue.sharedOperationQueue().addOperationTask(new OperationQueue.OperationCallback() {
            @Override
            public Object operationDoInBackground() {

                get(urlString, completionHandler);

                return null;
            }

            @Override
            public void operationCompletion(Object object) {

                removeTask(md5);
            }

            @Override
            public void operationCancelled() {

                removeTask(md5);
            }
        });

        addTask(md5, task);

        return md5;
    }

    private void post(String urlString, JSONObject params, ContentType contentType, CompletionHandler completionHandler) {

        if (urlString == null || urlString.length() == 0) {
            return;
        }

        Response response = null;
        Error error = null;
        Object responseObject = null;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);

            connection = (HttpURLConnection)url.openConnection();
            // 设置连接超时
            connection.setConnectTimeout(Time_Out_Interval);
            // 读取超时
            connection.setReadTimeout(Time_Out_Interval * 2);

            connection.setRequestMethod("POST");
            // 不缓存
            connection.setUseCaches(false);

            // default false,POST请求需要自己构造部分Http请求的内容
            // 因此我们需要使用OutputStream来进行数据写如操作
            // true就可以使用connection.getOutputStream().write()
            connection.setDoOutput(true);

            connection.setDoInput(true);

            // 解决 getContentLength == -1
            connection.setRequestProperty("Accept-Encoding","identity");
            connection.setRequestProperty("User-Agent", userAgent);

            setContentType(connection, contentType);

            String parameterString = setRequestData(params, contentType);
            // 写请求体
            writeOutputStream(connection.getOutputStream(),parameterString);

            // 读取结果
            InputStream inputStream = connection.getInputStream();

            long contentLen = connection.getContentLength();
            responseObject = readInputStream(inputStream, contentLen,completionHandler);

            // 读取Response
            JSONObject responseHeader = getResponseHeader(connection);
            int responseCode = connection.getResponseCode();

            response = new Response(responseCode,responseHeader);

        } catch (Exception e) {
            error = new Error(e);

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        if (completionHandler != null) {
            completionHandler.completion(response, responseObject, error);
        }
    }

    private String postContentOfURL(final String urlString, final JSONObject params, final ContentType contentType, final CompletionHandler completionHandler) {

        if (urlString == null || urlString.length() == 0) {
            return null;
        }

        final String md5 = JKUtils.md5OfString(urlString);

        OperationQueue.OperationTask task = OperationQueue.sharedOperationQueue().addOperationTask(new OperationQueue.OperationCallback() {
            @Override
            public Object operationDoInBackground() {

                post(urlString, params, contentType, completionHandler);

                return null;
            }

            @Override
            public void operationCompletion(Object object) {

                removeTask(md5);
            }

            @Override
            public void operationCancelled() {

                removeTask(md5);
            }
        });

        addTask(md5, task);

        return md5;
    }

    public String postFormData(String url, JSONObject params, CompletionHandler completionHandler) {
        return postContentOfURL(url, params, ContentType.CONTENT_TYPE_FORM_URLENCODE, completionHandler);
    }

    public String postJson(String url, JSONObject params, CompletionHandler completionHandler) {
        return postContentOfURL(url, params, ContentType.CONTENT_TYPE_JSON, completionHandler);
    }

    public String postMultipartData(String url, JSONObject params, CompletionHandler completionHandler) {
        return postContentOfURL(url, params, ContentType.CONTENT_TYPE_MULTIPART, completionHandler);
    }

    /**
     * Download
     * */
    private void download(String download_url, String dir, final CompletionHandler completionHandler) {


        HttpURLConnection connection = null;

        Response response = null;
        Error error = null;
        Object responseObject = null;

        try {

            // 创建一个URL对象
            URL url=new URL(download_url);

            // 创建一个HTTP链接
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(Time_Out_Interval);
            connection.setReadTimeout(Time_Out_Interval * 2);

            // 解决 getContentLength == -1
            connection.setRequestProperty("Accept-Encoding","identity");
            connection.setRequestProperty("User-Agent", userAgent);

            // 获取响应
            int code = connection.getResponseCode();
            JSONObject responseHeader = getResponseHeader(connection);
            String suggestedFileName = getResponseSuggestedFilename(responseHeader);

            response = new Response(code, responseHeader);

            if (code == 200) {

                String fileName;
                if (suggestedFileName != null && !suggestedFileName.isEmpty()) {
                    fileName = suggestedFileName;
                } else {
                    fileName = UUID.randomUUID().toString();
                }

                File downloadFile = FileUtils.sharedUtils().createFileInDir(fileName, dir);

                // 使用IO流获取数据
                InputStream inputStream = connection.getInputStream();

                final int contentLen = connection.getContentLength();

                // 写文件
                boolean success = FileUtils.sharedUtils().writeData2DiskFromInput(downloadFile, inputStream, new FileUtils.ProgressHandler() {
                    @Override
                    public void progress(long progress) {

                        float p = 0.0f;
                        if (contentLen > 0) {
                            p = (progress * 1.0f) / contentLen;
                        }
                        if (completionHandler != null) {
                            completionHandler.progressChange(p);
                        }

                    }
                });

                if (success && downloadFile != null) {
                    responseObject = downloadFile.getAbsolutePath();
                }

            } else {

                long contentLen = connection.getContentLength();
                responseObject = readInputStream(connection.getInputStream(), contentLen,completionHandler);
            }

        } catch (IOException e) {

            error = new Error(e);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        if (completionHandler != null) {
            completionHandler.completion(response, responseObject, error);
        }

    }

    public String downloadFile(final String download_url, final String dir, final CompletionHandler completionHandler) {

        if (download_url == null || download_url.length() == 0 || dir == null || dir.length() == 0) {

            if (completionHandler != null) {
                completionHandler.completion(null, null, new Error("url or directory is null"));
            }

            return null;
        }

        final String md5 = JKUtils.md5OfString(download_url);
        OperationQueue.OperationTask task = OperationQueue.sharedOperationQueue().addOperationTask(new OperationQueue.OperationCallback() {

            @Override
            public Object operationDoInBackground() {

                download(download_url, dir, completionHandler);

                return null;
            }

            @Override
            public void operationCompletion(Object object) {

                removeTask(md5);
            }

            @Override
            public void operationCancelled() {

                removeTask(md5);
            }
        });

        addTask(md5, task);

        return md5;
    }

    /**
     * Upload
     * */
    private void upload(File file, JSONObject params, String urlString, CompletionHandler completionHandler) {

        Response response = null;
        Error error = null;
        Object responseObject = null;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);

            connection = (HttpURLConnection)url.openConnection();
            // 设置连接超时
            connection.setConnectTimeout(Time_Out_Interval);
            // 读取超时
            connection.setReadTimeout(Time_Out_Interval * 2);

            connection.setRequestMethod("POST");
            // 不缓存
            connection.setUseCaches(false);

            // default false,POST请求需要自己构造部分Http请求的内容
            // 因此我们需要使用OutputStream来进行数据写如操作
            // true就可以使用connection.getOutputStream().write()
            connection.setDoOutput(true);

            connection.setDoInput(true);

            // 解决 getContentLength == -1
            connection.setRequestProperty("Accept-Encoding","identity");
            connection.setRequestProperty("User-Agent", userAgent);

            connection.setRequestProperty("Content-Type","multipart/form-data; charset=utf-8; boundary=" + REQUEST_BOUNDARY);

            OutputStream outputStream = connection.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            String startBoundary = "--" + REQUEST_BOUNDARY;
            String endBoundary = "--" + REQUEST_BOUNDARY + "--";

            StringBuilder stringBuilder = new StringBuilder();

            if (params != null && params.length() > 0) {

                Iterator<String> iterator = params.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = params.opt(key);

                    if (value != null) {

                        stringBuilder.append(String.format("%s\r\n",startBoundary));
                        stringBuilder.append(String.format("Content-Disposition: form-data; name=\"%s\"\r\n\r\n",key));
                        stringBuilder.append(String.format("%s\r\n",value.toString()));
                    }

                }

            }

            stringBuilder.append(String.format("%s\r\n",startBoundary));
            stringBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" );
            stringBuilder.append("Content-Type: application/octet-stream; charset=utf-8\r\n\r\n");
            String parameterString = stringBuilder.toString();

            // 写参数
            dataOutputStream.write(parameterString.getBytes());

            // 写文件
            InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            long total = file.length();

            long send = 0;

            int len = 0;

            while((len=is.read(bytes))!=-1){

                dataOutputStream.write(bytes, 0, len);

                if (completionHandler != null) {

                    send += len;
                    float p = 0.0f;
                    if (total > 0) {
                        p =send * 1.0f / total;
                    }
                    completionHandler.progressChange(p);
                }


            }

            is.close();
            dataOutputStream.write("\r\n".getBytes());// 换行
            dataOutputStream.write(String.format("%s\r\n",endBoundary).getBytes()); // 结束
            dataOutputStream.flush();
            dataOutputStream.close();

            // 读取结果
            InputStream inputStream = connection.getInputStream();

            long contentLen = connection.getContentLength();
            responseObject = readInputStream(inputStream, contentLen,completionHandler);

            // 读取Response
            JSONObject responseHeader = getResponseHeader(connection);
            int responseCode = connection.getResponseCode();

            response = new Response(responseCode,responseHeader);

        } catch (Exception e) {
            error = new Error(e);

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }

        if (completionHandler != null) {
            completionHandler.completion(response, responseObject, error);
        }

    }

    public String uploadFile(final File file, final JSONObject params, final String urlString, final CompletionHandler completionHandler) {

        if (file == null) {
            if (completionHandler != null) {
                completionHandler.completion(null, null, new Error("file is null"));
            }
            return null;
        } else if (urlString == null) {
            if (completionHandler != null) {
                completionHandler.completion(null, null, new Error("url is null"));
            }
            return null;
        } else if (!file.exists()) {
            if (completionHandler != null) {
                completionHandler.completion(null, null, new Error("file does not exist"));
            }
            return null;
        } else {

            final String md5 = JKUtils.md5OfString(urlString);

            OperationQueue.OperationTask task = OperationQueue.sharedOperationQueue().addOperationTask(new OperationQueue.OperationCallback() {
                @Override
                public Object operationDoInBackground() {

                    upload(file, params, urlString, completionHandler);

                    return null;
                }

                @Override
                public void operationCompletion(Object object) {

                    removeTask(md5);
                }

                @Override
                public void operationCancelled() {

                    removeTask(md5);
                }
            });

            addTask(md5, task);

            return md5;
        }
    }

}
