package cn.picc.com.pdfpreview;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.net.URL;

public class WebViewActivity extends AppCompatActivity {


    public static final String PDF_PATH = "pdf_path";


    private WebView pdfViewerWeb;
    private String pdfPath;


    public static void getPDFActivity(Context context, String path) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(PDF_PATH, path);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        pdfViewerWeb = findViewById(R.id.web_view);

        Intent intent = getIntent();
        pdfPath = intent.getStringExtra(PDF_PATH);

        //initView2();


        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//api >= 19

                            //加载资源文件中的pdf成功
                            //preView("file:///android_asset/demo.pdf");

                            //加载手机目录中的pdf 成功
                            //preView("file://" + pdfPath);

                            String http_url = "http://10.126.24.89:8080/demo.pdf";
                            preView(http_url);

                            //download(http_url);
                        }
                    }
                });
            }
        }).start();
    }

    private String getPath() {
        File filesDir = getExternalFilesDir("PDF");
        return new File(filesDir, "demo.pdf").getAbsolutePath();

    }

    private void download(String url) {
        DownLoadUtils.downloadPdf(WebViewActivity.this, url, getExternalFilesDir("PDF"), "demo.pdf", new DownLoadUtils.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        preView(getPath());
                    }
                });
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed(String error) {

            }
        });
    }

    private void initView2() {
        WebSettings settings = pdfViewerWeb.getSettings();
        settings.setSavePassword(false);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        pdfViewerWeb.addJavascriptInterface(new AndroidtoJs(), "android");//AndroidtoJS类对象映射到js的test对象
        pdfViewerWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;

            }

        });
        pdfViewerWeb.setWebChromeClient(new WebChromeClient());
    }

    public void initView() {
        WebSettings webSettings = pdfViewerWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        /**
         * 简单来说，该项设置决定了JavaScript能否访问来自于任何源的文件标识的URL。
         * 比如我们把PDF.js放在本地assets里，然后通过一个URL跳转访问来自于任何URL的PDF，所以这里我们需要将其设置为true。
         * 而一般情况下，为了安全起见，是需要将其设置为false的。
         */
        webSettings.setAllowUniversalAccessFromFileURLs(true);

    }

    public class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void back() {
            WebViewActivity.this.finish();
        }
    }

    private void preView(String pdfUrl) {
        //1.只使用pdf.js渲染功能，自定义预览UI界面

        //assets path url 都可以访问
        //pdfViewerWeb.loadUrl("file:///android_asset/index.html?" + pdfUrl);


        //3.pdf.js放到本地  都可以访问


        /**

         pdf.js不支持跨域请求，所以会报错：file origin does not match viewer’s，
         if (origin !== viewerOrigin && protocol !== 'blob:') {
         throw new Error('file origin does not match viewer\'s');
         }

         */
//        String PDFJs_url = "file:///android_asset/pdfjs/web/viewer.html?file=" + pdfUrl;
//        pdfViewerWeb.loadUrl(PDFJs_url);






        //2.使用mozilla官方demo加载在线pdf  assets 无效 没反应  手机目录 提示找不到文件
        /**
         * asset 报错 [INFO:CONSOLE(1856)] "Failed to load file:///android_asset/demo.pdf: Cross origin requests are only supported for protocol schemes: http, data, chrome, https.", source: http://mozilla.github.io/pdf.js/web/viewer.js (1856)

         http 报错  No 'Access-Control-Allow-Origin' header is present on the requested resource. Origin 'http://mozilla.github.io' is therefore not allowed access. If an opaque response serves your needs, set the request's mode to 'no-cors' to fetch the resource with CORS disabled.", source: http://mozilla.github.io/pdf.js/web/viewer.html?file=http://10.126.24.89:8080/demo.pdf (0)
         01-14 10:58:10.243 13371-13371/cn.picc.com.pdfpreview I/chromium: [INFO:CONSOLE(16729)] "Uncaught (in promise) DataCloneError: Failed to execute 'postMessage' on 'Worker': TypeError: Failed to fetch could not be cloned.", source: http://mozilla.github.io/pdf.js/build/pdf.js (16729)
         [INFO:CONSOLE(1036)] "Uncaught (in promise) Error: 载入 PDF 时发生错误。", source: http://mozilla.github.io/pdf.js/web/viewer.js (1036)


         */
        //pdfViewerWeb.loadUrl("http://mozilla.github.io/pdf.js/web/viewer.html?file=" + pdfUrl);


        //4.使用谷歌文档服务 都没反应 空白界面
        //pdfViewerWeb.loadUrl("http://docs.google.com/gview?embedded=true&url="+pdfUrl);

        //pdfViewerWeb.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfUrl);


//        String data = "<iframe src='http://docs.google.com/gview?embedded=true&url=" + pdfUrl + "'" + " width='100%' height='100%' style='border: none;'></iframe>";
//        pdfViewerWeb.loadData(data, "text/html", "UTF-8");
    }


}
