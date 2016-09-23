package com.inuc.inuc.beans;

import java.io.Serializable;

/**
 * Created by 景贝贝 on 2016/8/7.
 */
public class LastApp implements Serializable {
    private int LogID;
    private String PublishTime;
    private int VersionID;
    private String LogContents;
    private String Url;
    private boolean Forced;

    public int getLogID() {
        return LogID;
    }

    public void setLogID(int logID) {
        LogID = logID;
    }

    public String getPublishTime() {
        return PublishTime;
    }

    public void setPublishTime(String publishTime) {
        PublishTime = publishTime;
    }

    public int getVersionID() {
        return VersionID;
    }

    public void setVersionID(int versionID) {
        VersionID = versionID;
    }

    public String getLogContents() {
        return LogContents;
    }

    public void setLogContents(String logContents) {
        LogContents = logContents;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public boolean isForced() {
        return Forced;
    }

    public void setForced(boolean forced) {
        Forced = forced;
    }
}