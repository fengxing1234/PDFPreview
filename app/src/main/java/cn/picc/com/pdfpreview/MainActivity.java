package cn.picc.com.pdfpreview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String PDF_PATH = "pdf_path";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.getPDFActivity(MainActivity.this, getPath());
            }
        });
        findViewById(R.id.tv_pdf_viewer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidPdfViewerActivity.getPDFActivity(MainActivity.this, getPath());
            }
        });

        findViewById(R.id.tv_pdf_intent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPdfFileIntent(new File(getPath()));
            }
        });

    }

    private String getPath() {
        File filesDir = getExternalFilesDir("PDF");
        return new File(filesDir, "demo.pdf").getAbsolutePath();

    }

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

}
