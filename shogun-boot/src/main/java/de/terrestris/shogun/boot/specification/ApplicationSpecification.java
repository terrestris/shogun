package de.terrestris.shogun.boot.specification;

import de.terrestris.shoguncore.model.Application;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ApplicationSpecification implements Specification<Application> {

    private String name;
    private String nameToSearch;

    public ApplicationSpecification(String name, String nameToSearch) {
        this.name = name;
        this.nameToSearch = nameToSearch;
    }

    @Override
    public Predicate toPredicate(Root<Application> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return builder.equal(
                builder.function(
                        "jsonb_extract_path_text",
                        String.class,
                        root.<String>get("clientProperties"),
                        builder.literal(this.name)
                ),
                this.nameToSearch
        );
    }
}
