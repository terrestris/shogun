package de.terrestris.shogun.lib.service;

import de.terrestris.shogun.lib.model.Topic;
import de.terrestris.shogun.lib.repository.TopicRepository;
import org.springframework.stereotype.Service;

@Service
public class TopicService extends BaseService<TopicRepository, Topic> {
}
