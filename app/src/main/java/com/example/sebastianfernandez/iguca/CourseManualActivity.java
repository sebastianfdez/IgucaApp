package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.File;

public class CourseManualActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    Integer pageNumber = 0;
    String pdfName = "manualexcelencia.pdf";
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_course_manual );

        if (savedInstanceState != null)
        {
            pageNumber = savedInstanceState.getInt("CURRENTPAGE");
        }


        pdfView = (PDFView) findViewById(R.id.courseManualPdfView);
        String i = getSharedPreferences("myPref", Context.MODE_PRIVATE).getString ("SelectedCourse","");
        String courseKey = getSharedPreferences ( "myPref", Context.MODE_PRIVATE).getString ("CourseKey" + i,"");
        pdfName = "Manual " + getSharedPreferences ( "myPref", Context.MODE_PRIVATE).getString ("CourseName" + i,"");

        pdfView.fromFile (new File ( "/data/data/com.example.sebastianfernandez.iguca/cache/Manual/" + courseKey + ".pdf" ) )
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .pageFitPolicy( FitPolicy.BOTH)
                .load();

    }

    @Override
    public void loadComplete(int nbPages) {
        if (pageNumber >= 0)
        {
            pdfView.jumpTo(pageNumber);
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        pageNumber = savedInstanceState.getInt("CURRENTPAGE");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENTPAGE", pageNumber);
    }
}
