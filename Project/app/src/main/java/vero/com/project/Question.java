package vero.com.project;

/**
 * Created by vero on 2016/10/10.
 */

public class Question {
    private String strQuestion;
    private String rbLeft;
    private String rbRight;
    private int Res;

    public Question(String tvQuestion, String rbLeft, String rbRight, int res) {
        strQuestion = tvQuestion;
        this.rbLeft = rbLeft;
        this.rbRight = rbRight;
        Res = res;
    }

    public String getStrQuestion() {
        return strQuestion;
    }

    public void setStrQuestion(String strQuestion) {
        this.strQuestion = strQuestion;
    }

    public String getRbLeft() {
        return rbLeft;
    }

    public void setRbLeft(String rbLeft) {
        this.rbLeft = rbLeft;
    }

    public String getRbRight() {
        return rbRight;
    }

    public void setRbRight(String rbRight) {
        this.rbRight = rbRight;
    }

    public int getRes() {
        return Res;
    }

    public void setRes(int res) {
        Res = res;
    }
}
