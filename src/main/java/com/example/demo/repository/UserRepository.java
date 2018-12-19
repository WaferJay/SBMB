package com.example.demo.repository;

import com.example.demo.bean.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    <S extends User> Optional<S> findOne(Example<S> example);

    @Override
    Optional<User> findById(Long aLong);
}
