package de.terrestris.shoguncore.graphql.fetcher;

import de.terrestris.shoguncore.model.Layer;
import de.terrestris.shoguncore.service.LayerService;
import org.springframework.stereotype.Component;

@Component
public class LayerGraphQLDataFetcher extends BaseGraphQLDataFetcher<Layer, LayerService> { }
