package de.terrestris.shoguncore.repository;

import de.terrestris.shoguncore.model.Application;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends BaseCrudRepository<Application, Long>, JpaSpecificationExecutor<Application> {
}
