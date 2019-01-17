package cn.picc.com.pdfpreview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.artifex.mupdf.mini.DocumentActivity;

import java.io.File;

import static cn.picc.com.pdfpreview.MainActivity.PDF_PATH;

public class MuPDFActivity extends AppCompatActivity {


    public static void getPDFActivity(Context context, String path) {
        Intent intent = new Intent(context, MuPDFActivity.class);
        intent.putExtra(PDF_PATH, path);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String pdf_path = intent.getStringExtra(PDF_PATH);
        showPDF(new File(pdf_path));
    }

    protected void showPDF(File file) {
        Intent intent = new Intent(this, DocumentActivity.class);
        // API>=21: intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT); /* launch as a new document */
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET); /* launch as a new document */
        //intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.fromFile(file));
        startActivity(intent);
    }
}
