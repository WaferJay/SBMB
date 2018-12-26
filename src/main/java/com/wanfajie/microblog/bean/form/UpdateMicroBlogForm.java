package com.wanfajie.microblog.bean.form;

import org.hibernate.validator.constraints.Range;

public class UpdateMicroBlogForm {

    @Range(min = -1, max = 1)
    private int like;

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
