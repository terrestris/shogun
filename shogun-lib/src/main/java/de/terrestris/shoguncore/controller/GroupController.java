package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.Group;
import de.terrestris.shoguncore.service.GroupService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class GroupController extends BaseController<GroupService, Group> {
}
