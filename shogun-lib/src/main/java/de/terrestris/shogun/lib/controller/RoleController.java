package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.Role;
import de.terrestris.shogun.lib.service.RoleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController extends BaseController<RoleService, Role> {
}
