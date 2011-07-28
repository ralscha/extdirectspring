package ch.ralscha.starter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.ralscha.starter.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
