#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import ${package}.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {
	User findByUserName(String userName);
}
