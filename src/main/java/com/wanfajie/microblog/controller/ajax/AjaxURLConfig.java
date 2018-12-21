package com.wanfajie.microblog.controller.ajax;

public interface AjaxURLConfig {

    String PREFIX_USER = "/ajax/user";
    String PREFIX_MICROBLOG = "/ajax/microblog";

    interface User {
        String PING = PREFIX_USER + "/ping";
        String REGISTER_USER = PREFIX_USER;
        String FETCH_USER_INFO = PREFIX_USER + "/{id}";
        String UPDATE_USER_INFO = PREFIX_USER + "/{id}";
        String USER_LOGIN = PREFIX_USER + "/login";
        String USER_LOGOUT = PREFIX_USER + "/logout";
    }

    interface MicroBlog {
        String POST_MICROBLOG = PREFIX_MICROBLOG;
        String FETCH_ONE_MICROBLOG = PREFIX_MICROBLOG + "/{id}";
        String DELETE_MICROBLOG = PREFIX_MICROBLOG + "/{id}";
        String FETCH_ALL_MICROBLOG = PREFIX_MICROBLOG;
        String FETCH_USER_MICROBLOG = PREFIX_USER + "/{userId}/microblog";
        String FETCH_SUB_MICROBLOG = PREFIX_MICROBLOG + "/subscribed";
    }

    interface Subscribe {
        String SUBSCRIBE_USER = PREFIX_USER + "/{id}/subscribe";
        String UNSUBSCRIBE_USER = PREFIX_USER + "/{id}/unsubscribe";
    }
}
