package com.wanfajie.microblog.bean.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class CommentForm {

    @NotEmpty(message = "评论内容不能为空")
    private String message;

    @Size(max = 9, message = "最多上传9张图片")
    private List<Long> imageIds;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds;
    }
}
