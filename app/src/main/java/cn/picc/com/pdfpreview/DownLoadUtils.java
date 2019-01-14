package cn.picc.com.pdfpreview;

import android.content.Context;
import android.telecom.Call;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class DownLoadUtils {

    public interface OnDownloadListener {

        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed(String error);

    }


    public static void downloadPdf(Context context, String url, final File savePath, final String fileName, final OnDownloadListener listener) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        okhttp3.Request request = requestBuilder.url(url).build();

        OkHttpClient client = builder.connectTimeout(1, TimeUnit.MINUTES).readTimeout(3, TimeUnit.MINUTES).build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // 下载失败
                listener.onDownloadFailed(e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, fileName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDownloadFailed(e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        listener.onDownloadFailed(e.getMessage());
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        listener.onDownloadFailed(e.getMessage());
                    }
                }
            }
        });
    }
}
