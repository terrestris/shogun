package de.terrestris.shoguncore.graphql.fetchers;

import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.service.GroupService;
import org.springframework.stereotype.Component;

@Component
public class GroupGraphQLProvider extends BaseGraphQLDataFetcher<Group, GroupService> { }
