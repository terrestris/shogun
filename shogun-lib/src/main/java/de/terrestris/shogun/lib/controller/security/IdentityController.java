package de.terrestris.shogun.lib.controller.security;

import de.terrestris.shogun.lib.controller.BaseController;
import de.terrestris.shogun.lib.model.Group;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.model.security.Identity;
import de.terrestris.shogun.lib.service.GroupService;
import de.terrestris.shogun.lib.service.UserService;
import de.terrestris.shogun.lib.service.security.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/identities")
public class IdentityController extends BaseController<IdentityService, Identity> {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/getByGroup/{groupId}")
    public List<Identity> getByGroup(@PathVariable("groupId") long groupId) {

        try {
            Optional<Group> group = groupService.findOne(groupId);
            if (group.isPresent()) {
                return service.findAllIdentitiesBy(group.get());
            } else {
                LOG.error("Could not find group with id {}", groupId);
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage(
                        "BaseController.INTERNAL_SERVER_ERROR",
                        null,
                        LocaleContextHolder.getLocale()
                    )
                );
            }
        } catch (Exception e) {
            LOG.error("Error while requesting identities for group with id: {}", groupId);
            LOG.trace("Full stack trace: ", e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage(
                    "BaseController.INTERNAL_SERVER_ERROR",
                    null,
                    LocaleContextHolder.getLocale()
                )
            );
        }
    }

    @GetMapping(value = "/getByUser/{userId}")
    public List<Identity> getByUser(@PathVariable("userId") long userId) {

        try {
            Optional<User> user = userService.findOne(userId);
            if (user.isPresent()) {
                return service.findAllIdentitiesBy(user.get());
            } else {
                LOG.error("Could not find user with id {}", userId);
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage(
                        "BaseController.INTERNAL_SERVER_ERROR",
                        null,
                        LocaleContextHolder.getLocale()
                    )
                );
            }
        } catch (Exception e) {
            LOG.error("Error while requesting identities for user with id: {}", userId);
            LOG.trace("Full stack trace: ", e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                messageSource.getMessage(
                    "BaseController.INTERNAL_SERVER_ERROR",
                    null,
                    LocaleContextHolder.getLocale()
                )
            );
        }
    }
}
