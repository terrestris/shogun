package de.terrestris.shoguncore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseCrudRepository<T, ID> extends CrudRepository<T, ID> {
//    // '{"country": "Peru"}'
//    @Query(
//        value = "SELECT * FROM #{#entityName} WHERE client_config @> cast(:filter as jsonb)",
//        //value = "SELECT * FROM #{#entityName} WHERE jsonb_extract_path_text(:column, '{key,subkey}') = :filter",
//        nativeQuery = true
//    )
//    //List<T> findByFilter(@Param("column") String column, @Param("filter") String filter);
//    List<T> findByFilter(@Param("filter") String filter);
}
