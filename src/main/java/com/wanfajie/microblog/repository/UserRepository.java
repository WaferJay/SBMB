package com.wanfajie.microblog.repository;

import com.wanfajie.microblog.bean.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Override
    <S extends User> Optional<S> findOne(Example<S> example);

    @Override
    Optional<User> findById(Long aLong);

    @Query(value = "select u.* from mb_user_subscribe as us " +
            "inner join mb_user as u on u.id = us.following_id " +
            "where us.follower_id = :userId",
            countQuery = "SELECT count(us.following_id) FROM mb_user_subscribe AS us \n" +
            "INNER JOIN mb_user AS u ON u.id = us.following_id \n" +
            "WHERE us.follower_id = :userId", nativeQuery = true)
    Page<User> findFollowings(@Param("userId") long userId, Pageable pageable);

    @Query(value = "select u.* from mb_user_subscribe as us " +
            "inner join mb_user as u on u.id = us.follower_id " +
            "where us.following_id = :userId",
            countQuery = "SELECT count(us.follower_id) FROM mb_user_subscribe AS us \n" +
            "INNER JOIN mb_user AS u ON u.id = us.follower_id \n" +
            "WHERE us.following_id = :userId", nativeQuery = true)
    Page<User> findFollowers(@Param("userId") long userId, Pageable pageable);

    // XXX: int转boolean
    @Query(value = "select count(1)>0 from mb_user_subscribe as us " +
            "where us.follower_id = :userId and us.following_id = :followingId", nativeQuery = true)
    int isFollowing(@Param("userId") long userId, @Param("followingId") long followingId);
}
