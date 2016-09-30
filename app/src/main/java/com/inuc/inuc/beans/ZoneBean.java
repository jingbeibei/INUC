package com.inuc.inuc.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/28.
 */
public class ZoneBean {
    private int ID;
    private String PublishTime;
    private String Contents;
    private int Hits;//浏览
    private int RemarkCount;
    private int Positive;
    private int Negative;
    private List<Remarks> Remarks;
    private String Nickname;
    private String UserPictureUrl;

    public String getUserPictureUrl() {
        return UserPictureUrl;
    }

    public void setUserPictureUrl(String userPictureUrl) {
        UserPictureUrl = userPictureUrl;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPublishTime() {
        return PublishTime;
    }

    public void setPublishTime(String publishTime) {
        PublishTime = publishTime;
    }

    public String getContents() {
        return Contents;
    }

    public void setContents(String contents) {
        Contents = contents;
    }

    public int getHits() {
        return Hits;
    }

    public void setHits(int hits) {
        Hits = hits;
    }

    public int getRemarkCount() {
        return RemarkCount;
    }

    public void setRemarkCount(int remarkCount) {
        RemarkCount = remarkCount;
    }

    public int getPositive() {
        return Positive;
    }

    public void setPositive(int positive) {
        Positive = positive;
    }

    public int getNegative() {
        return Negative;
    }

    public void setNegative(int negative) {
        Negative = negative;
    }

    public List<Remarks> getRemarksArrayList() {
        return Remarks;
    }

    public void setRemarksArrayList(ArrayList<Remarks> Remarks) {
        this.Remarks = Remarks;
    }



}
