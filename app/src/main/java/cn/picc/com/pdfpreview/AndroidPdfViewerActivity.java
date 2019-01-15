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

        //pdfView.fromBytes(byte[]) //没测试

        //pdfView.fromStream(InputStream) //没测试 stream is written to bytearray - native code cannot use Java Streams

        //pdfView.fromSource() //没测试

        //通过
        //pdfView.fromAsset(assetName)


        //通过
        pdfView.fromFile(file)

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
