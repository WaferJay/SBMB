package com.example.demo;

import com.example.demo.bean.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.PasswordUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
    private UserService userService;

	private static RowMapper<User> userMapper = (resultSet, i) -> {
	    User user = new User();
	    user.setId(resultSet.getLong(1));
	    user.setName(resultSet.getString(2));
	    user.setEmail(resultSet.getString(3));

	    return user;
	};

	@Test
	public void contextLoads() {
	}

	@Test
	public void testSQLite() {
	    final String createTableSql = "create table user (id integer not null primary key, name text not null, email text not null default '')";
	    final String insertUsers = "insert into user (name, email) values ('Tom', 'tom@domain.com'), ('Mike', 'mike@domain.com'), ('Alice', 'alice@domain.com')";
	    final String querySql = "select * from user;";

        final User[] userArr = {
                new User(1, "Tom", "tom@domain.com"),
                new User(2, "Mike", "mike@domain.com"),
                new User(3, "Alice", "alice@domain.com")
        };


	    jdbcTemplate.execute(createTableSql);
	    jdbcTemplate.execute(insertUsers);

	    List<User> users = jdbcTemplate.query(querySql, userMapper);
		for (int i = 0; i < userArr.length; i++) {
			User user = userArr[i];
			assert users.contains(user);
			assert users.get(i).toString().equals(user.toString());
		}
	}

	@Test
    public void testPasswordUtil() {
		String pwd = "test pwd";
		String cipherPwd = PasswordUtil.encryptPwd(pwd);
		assert cipherPwd.length() == 40;

		assert PasswordUtil.verifyPassword(pwd, cipherPwd);
		assert !PasswordUtil.verifyPassword("wrong pwd", cipherPwd);

		assert !cipherPwd.equals(PasswordUtil.encryptPwd(pwd));
	}
}

