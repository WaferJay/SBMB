package com.wanfajie.microblog.controller.ajax;

public interface AjaxURLConfig {

    String PREFIX_USER = "/ajax/user";
    String PREFIX_MICROBLOG = "/ajax/microblog";
    String PREFIX_COMMENT = "/ajax/microblog/{mbId}/comment";

    interface User {
        String USER_PING = PREFIX_USER + "/ping";
        String USER_REGISTER = PREFIX_USER;
        String USER_INFO = PREFIX_USER + "/{id}";
        String USER_LOGIN = PREFIX_USER + "/login";
        String USER_LOGOUT = PREFIX_USER + "/logout";
    }

    interface MicroBlog {
        String MICROBLOG_CREATE = PREFIX_MICROBLOG;
        String MICROBLOG = PREFIX_MICROBLOG + "/{id}";
        String MICROBLOG_FETCH = PREFIX_MICROBLOG;
        String MICROBLOG_USER_FETCH = PREFIX_USER + "/{userId}/microblog";
        String MICROBLOG_SUB_FETCH = PREFIX_MICROBLOG + "/subscribed";
    }

    interface Comment {
        String COMMENT_CREATE = PREFIX_COMMENT;
        String COMMENT = PREFIX_COMMENT + "/{id}";
    }

    interface Subscribe {
        String SUBSCRIBE_USER = PREFIX_USER + "/{id}/subscribe";
        String UNSUBSCRIBE_USER = PREFIX_USER + "/{id}/unsubscribe";
    }
}
