package com.inuc.inuc.zone;

/**
 * Created by Administrator on 2016/9/23.
*
 * 评论对象
 */
public class Comment {
    public String mContent; // 评论内容
    public User mCommentator; // 评论者
    public User mReceiver; // 接收者（即回复谁）

    public Comment(User mCommentator, String mContent, User mReceiver) {
        this.mCommentator = mCommentator;
        this.mContent = mContent;
        this.mReceiver = mReceiver;
    }
}
