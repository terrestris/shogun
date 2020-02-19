package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.service.GroupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class GroupController extends BaseController<GroupService, Group> {
}
