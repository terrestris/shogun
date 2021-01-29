package de.terrestris.shogun.lib.graphql.resolver;

import de.terrestris.shogun.lib.model.Layer;
import de.terrestris.shogun.lib.service.LayerService;
import org.springframework.stereotype.Component;

@Component
public class LayerGraphQLDataFetcher extends BaseGraphQLDataFetcher<Layer, LayerService> { }
