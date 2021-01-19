package de.terrestris.shoguncore.repository.security.permission;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.permission.UserClassPermission;
import de.terrestris.shoguncore.repository.BaseCrudRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserClassPermissionRepository extends BaseCrudRepository<UserClassPermission, Long>, JpaSpecificationExecutor<UserClassPermission> {

    List<UserClassPermission> findAllByUser(User user);

    @Modifying
    @Query(value = "DELETE FROM userclasspermissions u WHERE u.user_id=:userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);

}
