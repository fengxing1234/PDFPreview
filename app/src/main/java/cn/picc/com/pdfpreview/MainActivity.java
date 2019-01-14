package cn.picc.com.pdfpreview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {

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
    }

    private String getPath() {
        File filesDir = getExternalFilesDir("PDF");
        return new File(filesDir, "demo.pdf").getAbsolutePath();

    }
}
