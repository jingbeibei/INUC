package com.inuc.inuc.beans;

/**
 * Created by Administrator on 2016/9/29.
 */
public class Remarks {

    String Remarks;//评论内容
    String Username;
    String NiCheng;
    String UserPictureUrl;

    String RemarkTime;

    public String getRemarkTime() {
        return RemarkTime;
    }

    public void setRemarkTime(String remarkTime) {
        RemarkTime = remarkTime;
    }

    public String getUserPictureUrl() {
        return UserPictureUrl;
    }

    public void setUserPictureUrl(String userPictureUrl) {
        UserPictureUrl = userPictureUrl;
    }

    public String getNiCheng() {
        return NiCheng;
    }

    public void setNiCheng(String niCheng) {
        NiCheng = niCheng;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

}
