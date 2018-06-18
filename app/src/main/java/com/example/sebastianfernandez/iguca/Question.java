package com.example.sebastianfernandez.iguca;

public class Question {

    private String question;
    private String alternativeA;
    private String alternativeB;
    private String alternativeC;
    private String alternativeD;
    private Character correct;

    public Question () {}

    public Question (String q, String a, String b, String c, String d, Character correct) {
        this.question = q;
        this.alternativeA = a;
        this.alternativeB = b;
        this.alternativeC = c;
        this.alternativeD = d;
        this.correct = correct;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAlternativeA() {
        return alternativeA;
    }

    public void setAlternativeA(String alternativeA) {
        this.alternativeA = alternativeA;
    }

    public String getAlternativeB() {
        return alternativeB;
    }

    public void setAlternativeB(String alternativeB) {
        this.alternativeB = alternativeB;
    }

    public String getAlternativeC() {
        return alternativeC;
    }

    public void setAlternativeC(String alternativeC) {
        this.alternativeC = alternativeC;
    }

    public String getAlternativeD() {
        return alternativeD;
    }

    public void setAlternativeD(String alternativeD) {
        this.alternativeD = alternativeD;
    }

    public Character getCorrect() {
        return correct;
    }

    public void setCorrect(Character correct) {
        this.correct = correct;
    }
}
