package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseCrudRepository<User, Long>, JpaSpecificationExecutor<User> {
}
