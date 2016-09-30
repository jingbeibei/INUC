package com.inuc.inuc.zone;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/23.
 * 评论对象
 */
public class Moment {

    public String mContent;
    public ArrayList<Comment> mComment; // 评论列表

    public Moment(String mContent, ArrayList<Comment> mComment) {
        this.mComment = mComment;
        this.mContent = mContent;
    }
}