package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.service.GroupService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
@ConditionalOnExpression("${controller.groups.enabled:true}")
public class GroupController extends BaseController<GroupService, Group> { }
