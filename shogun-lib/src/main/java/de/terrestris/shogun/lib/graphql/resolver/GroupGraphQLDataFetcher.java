package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.service.GroupService;
import org.springframework.stereotype.Component;

@Component
public class GroupGraphQLDataFetcher extends BaseGraphQLDataFetcher<Group, GroupService> { }
