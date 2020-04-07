package de.terrestris.shogun.lib.controller;

import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController<UserService, User> { }
