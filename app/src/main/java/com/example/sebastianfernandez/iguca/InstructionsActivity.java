package com.example.sebastianfernandez.iguca;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

public class InstructionsActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    Integer pageNumber = 0;
    String pdfName = "manualexcelencia.pdf";
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        Log.d("page 7", pageNumber + "");

        if (savedInstanceState != null)
        {
            pageNumber = savedInstanceState.getInt("CURRENTPAGE");
        }
        Log.d("page 8", pageNumber + "");


        pdfView = (PDFView) findViewById(R.id.pdfView);

        pdfView.fromAsset(pdfName)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        Log.d("page", page + "");
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfName, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        Log.d("page 2", pageNumber + "");
        Log.d("page 4", nbPages + "");
        if (pageNumber >= 0)
        {
            Log.d("page 3", pageNumber + "");
            pdfView.jumpTo(pageNumber);
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        pageNumber = savedInstanceState.getInt("CURRENTPAGE");
        Log.d("page 6", pageNumber + "");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENTPAGE", pageNumber);
    }
}
