package com.inuc.inuc.beans;

import java.io.Serializable;

/**
 * Created by 景贝贝 on 2016/9/2.
 */
public class Letter {
    private int LetterID;
    private String Title;
    private String Contents;
    private String RepliedContents;
    private String SubmittedTime;
    private String RepliedTime;

    public int getLetterID() {
        return LetterID;
    }

    public void setLetterID(int letterID) {
        LetterID = letterID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public String getRepliedContents() {
        return RepliedContents;
    }

    public void setRepliedContents(String repliedContents) {
        RepliedContents = repliedContents;
    }

    public String getSubmittedTime() {
        return SubmittedTime;
    }

    public void setSubmittedTime(String submittedTime) {
        SubmittedTime = submittedTime;
    }

    public String getRepliedTime() {
        return RepliedTime;
    }

    public void setRepliedTime(String repliedTime) {
        RepliedTime = repliedTime;
    }
}
