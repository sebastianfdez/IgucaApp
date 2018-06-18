package com.example.sebastianfernandez.iguca;

import android.provider.BaseColumns;

public final class ExamContract {

    private ExamContract() {}

    public static class ExamTable implements BaseColumns {
        public static final String TABLE_NAME = "exam_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_ALTA = "alternativeA";
        public static final String COLUMN_ALTB = "alternativeB";
        public static final String COLUMN_ALTC = "alternativeC";
        public static final String COLUMN_ALTD = "alternativeD";
        public static final String COLUMN_CORRECT = "alternative_nr";
    }
}
