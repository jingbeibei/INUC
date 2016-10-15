package com.inuc.inuc.beans;

/**
 * Created by 景贝贝 on 2016/10/15.
 */

public class OAMeeting {
    /**
     * ID : 14760032021305153526
     * Title : 2016/2017学年第一学期学生工作部署大会
     * DepartName : null
     * Time : 2016-10-11T08:30:00
     * Address : 主楼15层会议室
     * Attendees : 薛智,邱建国,张清,顾宁,常旭青,贾鹏,李秀玲,李云,廖海洪,刘光,屈有明,田维飞,魏媛媛,薛亚奎,杨湖,杨晓东,段义权,周毅
     * Contents :
     */

    private String ID;
    private String Title;
    private String Time;
    private String Address;
    private String Attendees;
    private String Contents;

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public void setAttendees(String Attendees) {
        this.Attendees = Attendees;
    }

    public void setContents(String Contents) {
        this.Contents = Contents;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public String getTime() {
        return Time;
    }

    public String getAddress() {
        return Address;
    }

    public String getAttendees() {
        return Attendees;
    }

    public String getContents() {
        return Contents;
    }
}
