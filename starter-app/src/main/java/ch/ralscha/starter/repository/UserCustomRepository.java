package ch.ralscha.starter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import ch.ralscha.starter.entity.QUser;
import ch.ralscha.starter.entity.User;

import com.mysema.query.BooleanBuilder;

@Repository
public class UserCustomRepository {

	@Autowired
	private UserRepository userRepository;

	public Page<User> findWithFilter(String filterValue, Pageable pageable) {
		if (!StringUtils.hasText(filterValue)) {
			return userRepository.findAll(pageable);
		}

		BooleanBuilder bb = new BooleanBuilder();
		if (StringUtils.hasText(filterValue)) {
			String likeValue = "%" + filterValue.toLowerCase() + "%";
			bb.or(QUser.user.userName.lower().like(likeValue));
			bb.or(QUser.user.name.lower().like(likeValue));
			bb.or(QUser.user.firstName.lower().like(likeValue));
			bb.or(QUser.user.email.lower().like(likeValue));
		}

		return userRepository.findAll(bb, pageable);
	}
}
