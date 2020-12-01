package de.terrestris.shoguncore.controller;

import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.service.RoleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@ConditionalOnExpression("${controller.roles.enabled:true}")
public class RoleController extends BaseController<RoleService, Role> { }
