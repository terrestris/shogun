package de.terrestris.shoguncore.graphql.fetchers;

import de.terrestris.shoguncore.model.Layer;
import de.terrestris.shoguncore.service.LayerService;
import org.springframework.stereotype.Component;

@Component
public class LayerGraphQLProvider extends BaseGraphQLDataFetcher<Layer, LayerService> { }
