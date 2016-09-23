package com.inuc.inuc.beans;

import java.io.Serializable;

/**
 * Created by 景贝贝 on 2016/7/30.
 */
public class Personnel implements Serializable {
    private String Username;
    private String Name;
    private String Sex;
    private String Nickname;
    private String MobilePhone;
    private String PictureUrl;
    private String StudentNo;

    public String getStudentNo() {
        return StudentNo;
    }

    public void setStudentNo(String studentNo) {
        this.StudentNo = studentNo;
    }


    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getMobilePhone() {
        return MobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        MobilePhone = mobilePhone;
    }

    public String getPictureUrl() {
        return PictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        PictureUrl = pictureUrl;
    }
}
