package de.terrestris.shogun.lib.dto;

import de.terrestris.shogun.lib.model.BaseEntity;
import graphql.relay.Connection;

public interface GraphQLConnection<T extends BaseEntity> extends Connection<T> {

    Integer getTotalCount();

}
