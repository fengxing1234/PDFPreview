# PDFPreview
PDF展示
#前言
Android系统是不支持阅读PDF的。

# 1. Mozilla PDF.js

##什么是PDF.js 

**PDF.js**、**pdf.js**是一款使用[HTML5 Canvas](https://zh.wikipedia.org/wiki/Canvas_(HTML%E5%85%83%E7%B4%A0) "Canvas (HTML元素)")安全地渲染[PDF](https://zh.wikipedia.org/wiki/PDF "PDF")文件以及遵从[网页标准](https://zh.wikipedia.org/wiki/%E7%B6%B2%E9%A0%81%E6%A8%99%E6%BA%96 "网页标准")的[网页浏览器](https://zh.wikipedia.org/wiki/%E7%BD%91%E9%A1%B5%E6%B5%8F%E8%A7%88%E5%99%A8 "网页浏览器")渲染PDF文件的[JavaScript](https://zh.wikipedia.org/wiki/JavaScript "JavaScript")库。

**下载地址**
[pdf.js文件](http://mozilla.github.io/pdf.js/getting_started/)

[github地址](https://github.com/mozilla/pdf.js)


**webview配置setting**

```
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
```
## 使用方式

在`assets`目录中加入测试PDF文件`demo.pdf`

##### 方式1    自定义预览界面，PDF.js使用cdn的方式导入
APK大小：2.7M
首先在`assets`文件中添加两个文件`index.html`文件和`index.js`文件。

`index.html`文件
```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
    <title>Document</title>
    <style type="text/css">
        canvas {
            width: 100%;
            height: 100%;
            border: 1px solid black;
        }
    </style>
    <script src="https://unpkg.com/pdfjs-dist@1.9.426/build/pdf.min.js"></script>
    <script type="text/javascript" src="index.js"></script>
</head>
<body>
</body>
</html>
```

`index.js`文件
```
var url = location.search.substring(1);

PDFJS.cMapUrl = 'https://unpkg.com/pdfjs-dist@1.9.426/cmaps/';
PDFJS.cMapPacked = true;

var pdfDoc = null;

function createPage() {
    var div = document.createElement("canvas");
    document.body.appendChild(div);
    return div;
}

function renderPage(num) {
    pdfDoc.getPage(num).then(function (page) {
        var viewport = page.getViewport(2.0);
        var canvas = createPage();
        var ctx = canvas.getContext('2d');

        canvas.height = viewport.height;
        canvas.width = viewport.width;

        page.render({
            canvasContext: ctx,
            viewport: viewport
        });
    });
}

PDFJS.getDocument(url).then(function (pdf) {
    pdfDoc = pdf;
    for (var i = 1; i <= pdfDoc.numPages; i++) {
        renderPage(i)
    }
});
```
在webview中就可以使用了。
正常加载地址就可以了
`webview.loadUrl("file:///android_asset/index.html?" + pdfUrl);`
1. 加载assets目录：`url = "file:///android_asset/demo.pdf"`
2. 加载手机目录 `url="file://"+pdfPath`
pdfPath是pdf文件在手机目录中的位置
```
private String getPath() {
        File filesDir = getExternalFilesDir("PDF");
        return new File(filesDir, "demo.pdf").getAbsolutePath();

    }
```
3. 加载网络地址:` url = "http://10.126.24.89:8080/demo.pdf";`
4. 下载到本地在加载：根据网络地址下载到本地目录进行加载 其实就是方式2.

#####效果
![image.png](https://upload-images.jianshu.io/upload_images/4118241-0a93926860ed2286.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 缺点
- 不能缩放
本身不能缩放，但是我们可以自己实现，pdf.js本身就是使用webView加载的，我们可以让webView能进行放大缩小。

首先让webview支持缩放功能。修改webview的setting。

```
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);//不显示那个丑东西
```
运行程序试了一下，发现不能用。。。。why，看看html页面发现这么一句话.`content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no"/>`

原来忘记修改html了，现在再来修改下html页面的属性。

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width,initial-scale=1.0,maximum-scale=4.0,user-scalable=yes"/>
    <title>Document</title>
    <style type="text/css">
        canvas {
            width: 100%;
            height: 100%;
            border: 1px solid black;
        }
    </style>
    <script src="https://unpkg.com/pdfjs-dist@1.9.426/build/pdf.min.js"></script>
    <script type="text/javascript" src="index.js"></script>
</head>
<body>
</body>
</html>
```
最大倍数4，最小1，支持缩放。

```
width=device-width ：表示宽度是设备屏幕的宽度
initial-scale=1.0：表示初始的缩放比例
minimum-scale=0.5：表示最小的缩放比例
maximum-scale=4.0：表示最大的缩放比例
user-scalable=yes：表示用户是否可以调整缩放比例
```
在次运行就支持了缩放了。

#####方式2
pdfjs文件大小 12.4M
APK大小：7.3M 
APK增加了 4.6M

首先下载[pdf.js](http://mozilla.github.io/pdf.js/getting_started/)文件 放在assets中.
![image.png](https://upload-images.jianshu.io/upload_images/4118241-28e99e6bc7976c22.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


build目录是PDF.js的核心文件。

web目录是PDF.js的配置与显示文件。

viewer.html是负责显示PDF的，viewer.js是负责配置的文件

使用方式和方式一 类似 url不同而已

```
String PDFJs_url = "file:///android_asset/pdfjs/web/viewer.html?file=" + pdfUrl;
        webView.loadUrl(PDFJs_url);
```

**注意一个小问题**
如果加载的是http地址，有可能会显示不出来，原因是不支持跨域请求。
例如：
```
String http_url = "http://10.126.24.89:8080/demo.pdf";

String PDFJs_url = "file:///android_asset/pdfjs/web/viewer.html?file=" + http_url;

webView.loadUrl(PDFJs_url);
```
错误：`file origin does not match viewer’s，`
说明不支持跨域请求。
**解决办法**
注释掉viewer.js的下面代码，一般log日志中有具体在多少行。
```
if (origin !== viewerOrigin && protocol !== 'blob:') {
         throw new Error('file origin does not match viewer\'s');
}
```
### 实现效果
![image.png](https://upload-images.jianshu.io/upload_images/4118241-e5ee71152d919cc9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 缺点
apk大了
界面太丑

#2. 通过Google Docs Viewer
前提需要科学上网，这种方式没有成功 
```
//4.使用谷歌文档服务
         pdfViewerWeb.loadUrl("http://docs.google.com/gview?embedded=true&url="+pdfUrl);

        String data = "<iframe src='http://docs.google.com/gview?embedded=true&url="+pdfUrl+"'"+" width='100%' height='100%' style='border: none;'></iframe>"
        pdfViewerWeb.loadData(data, "text/html", "UTF-8");
```
两种方式都没有成功。
如果出现`net err_cleartext_not_permitted`需要配置manifest文件。

在`<application`中加入代码`android:usesCleartextTraffic="true"`

原因：Android 9.0（API级别28）开始，默认情况下禁用明文支持。因此http的url均无法在webview中加载

#3. 使用mozilla官方demo加载在线pdf
同样无效
```
//2.使用mozilla官方demo加载在线pdf  assets 无效 没反应  手机目录 提示找不到文件
        /**
         * asset 报错 [INFO:CONSOLE(1856)] "Failed to load file:///android_asset/demo.pdf: Cross origin requests are only supported for protocol schemes: http, data, chrome, https.", source: http://mozilla.github.io/pdf.js/web/viewer.js (1856)

         http 报错  No 'Access-Control-Allow-Origin' header is present on the requested resource. Origin 'http://mozilla.github.io' is therefore not allowed access. If an opaque response serves your needs, set the request's mode to 'no-cors' to fetch the resource with CORS disabled.", source: http://mozilla.github.io/pdf.js/web/viewer.html?file=http://10.126.24.89:8080/demo.pdf (0)
         01-14 10:58:10.243 13371-13371/cn.picc.com.pdfpreview I/chromium: [INFO:CONSOLE(16729)] "Uncaught (in promise) DataCloneError: Failed to execute 'postMessage' on 'Worker': TypeError: Failed to fetch could not be cloned.", source: http://mozilla.github.io/pdf.js/build/pdf.js (16729)
         [INFO:CONSOLE(1036)] "Uncaught (in promise) Error: 载入 PDF 时发生错误。", source: http://mozilla.github.io/pdf.js/web/viewer.js (1036)


         */
        pdfViewerWeb.loadUrl("http://mozilla.github.io/pdf.js/web/viewer.html?file=" + pdfUrl);


```

#4. 第三方应用阅读本地PDF文件
- 只能加载本地的文件
- 需要跳转到其它的app

```
    public void getPdfFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/pdf");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
        }
        startActivity(Intent.createChooser(intent, "打开PDF文件"));
    }
```

#5.  使用第三方库显示PDF

*   [PDFium](https://android.googlesource.com/platform/external/pdfium/)：Google 和 Foxit 合作开源的 Foxit 的 PDF 源码，作为 Chrome 浏览器的 PDF 渲染引擎组件，当然这是 C/C++ 实现的；
*   [PdfiumAndroid](https://github.com/barteksc/PdfiumAndroid)：mshockwave 基于 PDFium 基础上适配 Android 平台的函数库；
*   [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)：barteksc 基于 PdfiumAndroid 基础上实现的一个 PDF 阅读 Demo，支持常见的手势，缩放，双击等效果。
*   [MuPDF](http://mupdf.com/): 一个轻量级的 开源 PDF 和 XPS 查看器


## AndroidPdfViewer

[AndroidPdfViewer github地址](https://github.com/barteksc/AndroidPdfViewer)

使用前apk大小7.6M

加载第三方库之后26.5M

apk体积增加了快20M。

加载本地文件速度很快 要比webview方式快很多。

支持双击屏幕放大缩小。

支持双指放大 缩小。

支持横向 纵向 显示。

支持自定义view。


#### 使用
导入依赖
```
implementation 'com.github.barteksc:android-pdf-viewer:2.8.2'
```

添加布局
```
<?xml version="1.0" encoding="utf-8"?>
<com.github.barteksc.pdfviewer.PDFView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/pdfView"
    tools:context=".AndroidPdfViewerActivity"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
添加代码
```
package cn.picc.com.pdfpreview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;

import static cn.picc.com.pdfpreview.MainActivity.PDF_PATH;

public class AndroidPdfViewerActivity extends AppCompatActivity {

    private static final String TAG = "AndroidPdfViewer";
    private PDFView pdfView;

    public static void getPDFActivity(Context context, String path) {
        Intent intent = new Intent(context, AndroidPdfViewerActivity.class);
        intent.putExtra(PDF_PATH, path);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_pdf_viewer);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        initPDFView();

    }

    private void initPDFView() {

        //pdfView.fromUri(Uri)

        String assetName = "test.pdf";


        Intent intent = getIntent();
        String pdfPath = intent.getStringExtra(PDF_PATH);
        File file = new File(pdfPath);

        //通过

        //pdfView.fromAsset(assetName)



        pdfView.fromFile(file)

        //pdfView.fromBytes(byte[])

        //pdfView.fromStream(InputStream) // stream is written to bytearray - native code cannot use Java Streams

        //pdfView.fromSource()


                // 默认显示所有页面 all pages are displayed by default
                //过滤 0显示第一个页面 2 显示第三个页面 1显示第二个页面 3显示第四个页面
                //总共显示5个页面 分别是 PDF文档显示为132444页面
                //.pages(0, 2, 1, 3, 3, 3)

                //允许使用滑动阻止更改页面 allows to block changing pages using swipe
                .enableSwipe(true)

                //横向滑动 还是竖向滑动  意思就是 垂直滚动 还是水平滚动
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)

                //允许在当前页面上绘制某些内容，通常在屏幕中间可见
                // allows to draw something on the current page, usually visible in the middle of the screen
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        if (displayedPage % 2 == 0) {
                            Paint paint = new Paint();
                            paint.setColor(Color.RED);
                            paint.setAntiAlias(true);
                            paint.setStrokeWidth(100);
                            canvas.drawLine(0, pageHeight / 2, pageWidth, pageHeight / 2, paint);
                        }
                    }
                })
                //允许在所有页面上绘制内容，分别为每个页面绘制。仅针对可见页面调用
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                .onDrawAll(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
                        Paint paint = new Paint();
                        paint.setColor(Color.GREEN);
                        paint.setAntiAlias(true);
                        paint.setStrokeWidth(100);
                        float startY = pageHeight / 2;
                        canvas.drawLine(0, startY / 2, pageWidth, startY / 2, paint);
                        canvas.drawLine(0, startY + startY / 2, pageWidth, startY + startY / 2, paint);


                        paint.setColor(Color.BLUE);
                        canvas.drawLine(0, pageHeight, pageWidth, pageHeight, paint);
                    }
                })

                //在加载文档并开始渲染之后调用
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        toast("loadComplete = " + nbPages);
                        Log.d(TAG, "loadComplete: " + nbPages);
                    }
                }) // called after document is loaded and starts to be rendered
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        toast("onPageChanged :" + "page = " + page + "  pageCount = " + pageCount);
                        Log.d(TAG, "onPageChanged :" + "page = " + page + "  pageCount = " + pageCount);
                    }
                })
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                        toast("onPageScrolled :" + "page = " + page + "  positionOffset = " + positionOffset);
                        Log.d(TAG, "onPageScrolled :" + "page = " + page + "  positionOffset = " + positionOffset);
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        toast("onError = " + t.getMessage());
                        Log.d(TAG, "onError: " + t.getMessage());
                    }
                })
                .onPageError(new OnPageErrorListener() {
                    @Override
                    public void onPageError(int page, Throwable t) {
                        t.printStackTrace();
                        toast("onPageError :" + "page = " + page + "  msg = " + t.getMessage());
                        Log.d(TAG, "onPageError :" + "page = " + page + "  msg = " + t.getMessage());
                    }
                })

                //在第一次呈现文档后调用 called after document is rendered for the first time
                .onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                        toast("onInitiallyRendered :" + "nbPages = " + nbPages + "  pageWidth = " + pageWidth + "  pageHeight = " + pageHeight);
                        Log.d(TAG, "onInitiallyRendered :" + "nbPages = " + nbPages + "  pageWidth = " + pageWidth + "  pageHeight = " + pageHeight);
                    }
                })

                //单击时调用，如果处理则返回true，false则切换滚动句柄可见性
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {
                        Log.d(TAG, "onTap: " + e.toString());
                        Log.d(TAG, "onTap: " + "X = " + e.getX() + "  Y = " + e.getY());
                        return false;
                    }
                })
                //.onLongPress()

                //渲染注释（例如注释，颜色或表单） render annotations (such as comments, colors or forms)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)

                //在低分辨率屏幕上改进渲染 improve rendering a little bit on low-res screens
                .enableAntialiasing(true)

                //dp  页面之间的间距。要定义间距颜色，请设置视图背景
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(20)

                // //添加动态间距以在屏幕上自己适合每个页面 add dynamic spacing to fit each page on its own on the screen
                //.autoSpacing(false)
                //.linkHandler(DefaultLinkHandler)
                //.pageFitPolicy(FitPolicy.WIDTH)

                //将页面捕捉到屏幕边界 snap pages to screen boundaries
                //.pageSnap(true)

                //只对像ViewPager这样的单个页面进行一次变更 make a fling change only a single page like ViewPager
                //.pageFling(false)

                //切换夜间模式 toggle night mode
                //.nightMode(false)
                .load();
    }

    private void toast(String msg) {
        //Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}

```

下面注释 有些方法不存在，可能我用是的稳定版本，没有使用最新的版本。

还可以使用使用`com.github.barteksc.pdfviewer.ScrollBar`
ScrollBar类把滚动条放在PDFView旁边。

## android-pdfview

[github地址](https://github.com/JoanZapata/android-pdfview)
改项目已经不在维护


You can find a good replacement [here](https://github.com/barteksc/AndroidPdfViewer), which is a fork relying on Pdfium instead of Vudroid/MuPDF for decoding PDF files, allowing it to use the Apache License 2.0 which gives you much more freedom.

意思是有一个更好的替代品，（点击here其实就是上面的AndroidPdfViewer）。这是一个依靠Pdfium而不是Vudroid / MuPDF来解码PDF文件的分支，允许它使用Apache License 2.0，它可以让你更自由。

都已经放弃了就不建议使用了。


#MuPdf mini版

apk大小：使用前27.3M
apk大小：使用后 44M
增加了17M，目前位置最大的了。

### 前言

[MuPdf官网](https://mupdf.com/index.html)
[源码下载地址](https://mupdf.com/downloads/)

MuPDF库需要Android 4.1或更高版本。确保应用程序build.gradle中的minSdkVersion至少为16.
```
android {
	defaultConfig {
		minSdkVersion 16
		...
	}
	...
}
```


### 如何使用

##### 方式1 添加依赖
apk大小：
使用前2.5M
使用后18.7M
apk体积增加了16M

可左右滑动翻页
可点击左右翻页
加载速度非常快
支持方法缩小


可以从Maven存储库中添加MuPDF库。将maven存储库添加到项目中。在项目的top build.gradle中：
```
allprojects {
	repositories {
		jcenter()
		maven { url 'http://maven.ghostscript.com' }
		...
	}
}
```

然后将MuPDF查看器库添加到应用程序的依赖项中。在app的build.gradle中，

```
dependencies {
	implementation 'com.artifex.mupdf:viewer:1.14.0'
	...
}
```
通过Intent查看Pdf文件
```
import com.artifex.mupdf.viewer.DocumentActivity;

public void startMuPDFActivity(Uri documentUri) {
	Intent intent = new Intent(this, DocumentActivity.class);
	intent.setAction(Intent.ACTION_VIEW);
	intent.setData(documentUri);
	startActivity(intent);
}

public void startMuPDFActivityWithExampleFile() {
	File dir = Environment.getExternalStoragePublicDirectory
		(Environment.DIRECTORY_DOWNLOADS);
	File file = new File(dir, "example.pdf")
	Uri uri = Uri.fromFile(file);
	startMuPDFActivity(uri);
}
```

#####效果图

![image.png](https://upload-images.jianshu.io/upload_images/4118241-30a919e399106af7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



##### 方式2 编译mini

源码apk大小：使用前27.3M
apk大小：使用后 44M
增加了17M，目前位置最大的了。

只能点击翻页不能滑动翻页，
只能左右翻页
速度很快
使用简单

##### 步骤 
- 拉远程项目到本地

```
git clone --recursive git://git.ghostscript.com/mupdf-android-viewer-mini.git
```

加上 –recursive 标记是为了让 Git 可以递归地下载 mupdf-android-viewer-mini 工程及其依赖的所有工程。MuPDF 由于牵涉到多个模块，因而采用了 Git 的 submodule 机制来管理这些模块。在 mupdf-android-viewer-mini 工程的根目录下，有一个名为 .gitmodules 描述了它依赖的子模块：
```
[submodule "jni"]
	path = jni
	url = ../mupdf-android-fitz.git
```

在下载代码时，加了 –recursive 标记，Git 在下载完 mupdf-android-viewer-mini 工程之后，就会下载 mupdf-android-fitz 工程，并把它放在 mupdf-android-viewer-mini 工程的 jni 子目录下。而在 mupdf-android-fitz 工程的根目录下，同样有一个 .gitmodules 文件，描述 mupdf-android-fitz 工程依赖的模块：
```
[submodule "thirdparty/jbig2dec"]
	path = thirdparty/jbig2dec
	url = ../jbig2dec.git
[submodule "thirdparty/mujs"]
	path = thirdparty/mujs
	url = ../mujs.git
[submodule "thirdparty/freetype"]
	path = thirdparty/freetype
	url = ../thirdparty-freetype2.git
[submodule "thirdparty/harfbuzz"]
	path = thirdparty/harfbuzz
	url = ../thirdparty-harfbuzz.git
[submodule "thirdparty/jpeg"]
	path = thirdparty/libjpeg
	url = ../thirdparty-libjpeg.git
[submodule "thirdparty/lcms2"]
	path = thirdparty/lcms2
	url = ../thirdparty-lcms2.git
[submodule "thirdparty/openjpeg"]
	path = thirdparty/openjpeg
	url = ../thirdparty-openjpeg.git
[submodule "thirdparty/zlib"]
	path = thirdparty/zlib
	url = ../thirdparty-zlib.git
[submodule "thirdparty/curl"]
	path = thirdparty/curl
	url = ../thirdparty-curl.git
[submodule "thirdparty/freeglut"]
	path = thirdparty/freeglut
	url = ../thirdparty-freeglut.git
```
Git 在下载完 mupdf-android-fitz 工程之后，还会下载这些模块，并放在 mupdf-android-fitz 工程目录的 thirdparty 目录下，即 mupdf-android-viewer-mini/jni/libmupdf 目录下。

直接使用 Git 的 –recursive 标记下载，与如下的命令序列是等价的：

```
$ git clone git://git.ghostscript.com/mupdf-android-viewer-mini.git
$ cd mupdf-android-viewer-mini
$ git submodule update --init
$ cd jni
$ git submodule update --init
$ cd libmupdf
$ git submodule update --init
```

在开始构建之前，还需要在 mupdf-android-viewer-mini 工程的根目录下创建 local.properties 文件，配置 Android SDK 和 NDK 的路径：

```
ndk.dir=/Users/fengxing/Library/Android/sdk/ndk-bundle
sdk.dir=/Users/fengxing/Library/Android/sdk
```

此外，还需要在 mupdf-android-viewer-mini/jni/libmupdf 目录下执行 make generate 命令生成必要的文件：
```
➜  libmupdf git:(d63bd227) ✗ make generate
```
之后就可以在 mupdf-android-viewer-mini 工程的根目录下执行如下命令来构建了：

```
➜  mupdf-android-viewer-mini git:(master) ✗ make
```

编译通过就可以用as打开他。

运行一下看看效果。
 
##### 效果图
主页面
![image.png](https://upload-images.jianshu.io/upload_images/4118241-cdad1997f7f670be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

找到pdf文件
![image.png](https://upload-images.jianshu.io/upload_images/4118241-4858500ac3fc73c0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


显示文件

![image.png](https://upload-images.jianshu.io/upload_images/4118241-8d03f21c5127ed70.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##### 导入项目中

剩下的事情就简单了

![image.png](https://upload-images.jianshu.io/upload_images/4118241-47d4df50a600b6c2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

复制lib和jni两个文件到你的根目录，
设置setting.gradle文件
```
include ':app'
include ':jni'
include ':lib'
```
设置app的gradle文件
```
 apply plugin: 'com.android.application'

android {
    compileSdkVersion 28
    defaultConfig {
        applicationId "cn.picc.com.pdfpreview"
        minSdkVersion 16
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.+'
    implementation 'com.android.support.constraint:constraint-layout:1.0.2'
    implementation 'com.squareup.okhttp3:okhttp:3.12.1'
    implementation 'com.github.barteksc:android-pdf-viewer:2.8.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.1'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.1'
    implementation project(path: ':lib')
    implementation project(path: ':jni')
}

```

##### 使用方式
```
protected void showMuPDF(File file) {
        Intent intent = new Intent(this, DocumentActivity.class);
        // API>=21: intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); /* launch as a new document */
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); /* launch as a new document */
        //intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.fromFile(file));
        startActivity(intent);
    }
```
我把编译通过的MuPdf mini版 提交到了 github上

[MuPDF Mini github地址](https://www.jianshu.com/p/abcdccbe6984)

##### Mupdf So库方式
使用前 1.6M
使用so库 9.5M

api增加了大约8M左右。
