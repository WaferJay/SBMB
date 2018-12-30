package com.wanfajie.microblog.service.impl;

import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.bean.UserSubPrimaryKey;
import com.wanfajie.microblog.bean.UserSubscribe;
import com.wanfajie.microblog.controller.ajax.AjaxURLConfig;
import com.wanfajie.microblog.interceptor.SessionCookieService;
import com.wanfajie.microblog.repository.UserRepository;
import com.wanfajie.microblog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final String LOGGED_IN_SESSION_KEY = "_current_logged_in_user";

    public static final String USER_ID_COOKIE_KEY = "user_id";

    @Resource
    private UserRepository repository;

    @PersistenceContext
    private EntityManager manager;

    @Resource
    private SessionCookieService interceptor;

    @Override
    public User findById(long id) {
        Optional<User> optional =  repository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public <T> User findBy(String field, T v) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.where(
            builder.equal(root.<T>get(field), v)
        );

        User user = null;
        try {
            user = manager.createQuery(query)
                    .getSingleResult();
        } catch (NoResultException ignored) {
        }

        return user;
    }

    @Override
    public User findByName(String name) {
        return findBy("name", name);
    }

    @Override
    public User findByEmail(String email) {
        return findBy("email", email);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean exists(long id) {
        return repository.existsById(id);
    }

    @Override
    public void login(User user, boolean rememberMe) {
        // TODO: rememberMe 自动登录
        HttpSession session = interceptor.getCurrentSession();

        session.setAttribute(LOGGED_IN_SESSION_KEY, user);
        interceptor.addCookie(new Cookie(USER_ID_COOKIE_KEY, String.valueOf(user.getId())));
    }

    @Override
    public void logout() {
        interceptor.getCurrentSession()
                .removeAttribute(LOGGED_IN_SESSION_KEY);

        interceptor.removeCookie(USER_ID_COOKIE_KEY);
    }

    @Override
    public User getCurrentUser() {
        return (User) interceptor.getCurrentSession()
                .getAttribute(LOGGED_IN_SESSION_KEY);
    }

    private UserSubscribe getSubscribeRecord(User follower, User following) {
        UserSubPrimaryKey primaryKey = new UserSubPrimaryKey(follower, following);
        return manager.find(UserSubscribe.class, primaryKey);
    }

    @Override
    @Transactional
    public boolean follow(User follower, User following) {
        if (getSubscribeRecord(follower, following) != null) return false;
        UserSubscribe record = new UserSubscribe(follower, following);
        manager.persist(record);

        return true;
    }

    @Override
    @Transactional
    public boolean unFollow(User follower, User following) {
        UserSubscribe record = getSubscribeRecord(follower, following);
        if (record == null) return false;
        manager.remove(record);

        return true;
    }
}
