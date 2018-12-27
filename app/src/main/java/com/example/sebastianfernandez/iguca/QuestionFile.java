package com.example.sebastianfernandez.iguca;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.firebase.database.DataSnapshot;

import java.io.File;

public class QuestionFile extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    Integer pageNumber = 0;
    private DataSnapshot dataSnapshot;
    private SharedPreferences sharedPreferences;
    String pdfName = "";
    PDFView pdfView;

    private String courseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_question_file );


        sharedPreferences = getSharedPreferences ( "myPref", Context.MODE_PRIVATE );

        String i = sharedPreferences.getString ( "SelectedCourse", "" );
        courseKey = sharedPreferences.getString ( "CourseKey" + i, "" );
        // 1. get passed intent
        Intent intent = getIntent();

        // 2. get x & y from intent
        String currentQuestion = intent.getStringExtra("currentQuestion").toString ();

        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfName = "Respuestas " + getSharedPreferences ( "myPref", Context.MODE_PRIVATE).getString ("CourseName" + i,"");
        pdfView.fromFile (new File ( "/data/data/com.example.sebastianfernandez.iguca/cache/" + courseKey + "/Question" + (Integer.parseInt(currentQuestion) + 1) + ".pdf" ) )
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle (this))
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
