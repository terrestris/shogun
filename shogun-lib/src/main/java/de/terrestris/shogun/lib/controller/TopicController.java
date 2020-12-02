package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.Topic;
import de.terrestris.shogun.lib.service.TopicService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/topics")
@ConditionalOnExpression("${controller.topics.enabled:true}")
public class TopicController extends BaseController<TopicService, Topic> { }
