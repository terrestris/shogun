package de.terrestris.shogun.boot.websocket;

import de.terrestris.shogun.lib.util.KeycloakUtil;
import lombok.extern.log4j.Log4j2;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Component
public class SocketService {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private KeycloakUtil keycloakUtil;

    @Autowired
    SimpUserRegistry userRegistry;

    /**
     * Sends a socket message to the passed user.
     *
     * @param userName The name of the user to send the message to.
     * @param message The message to send.
     */
    public void sendMessageToUser(String userName, SocketMessage message) {
        SimpUser subscribedUser = userRegistry.getUser(userName);

        if (subscribedUser == null) {
            log.warn("The user {} is not subscribed. No message will be send", userName);
            return;
        }

        Set<String> users = Set.of(subscribedUser.getName());

        sendMessageToUsers(users, message);
    }

    /**
     * Sends a socket message to all subscribed users.
     *
     * @param message The message to send.
     */
    public void sendMessageToAllUsers(SocketMessage message) {
        Set<String> subscribedUsers = getSubscribedUsers().stream()
            .map(user -> user.getName())
            .collect(Collectors.toSet());

        if (subscribedUsers == null || subscribedUsers.isEmpty()) {
            log.warn("No subscribed users found. No messages will be send");
            return;
        }

        sendMessageToUsers(subscribedUsers, message);
    }

    /**
     * Sends a socket message to all subscribed admin users.
     *
     * @param message The message to send.
     */
    public void sendMessageToAdminUsers(SocketMessage message) {
        Set<String> subscribedAdminUsers = getSubscribedUsers(subscription -> {
            SimpUser simpUser = subscription.getSession().getUser();

            return ((KeycloakAuthenticationToken) simpUser.getPrincipal()).getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }).stream()
            .map(user -> user.getName())
            .collect(Collectors.toSet());

        if (subscribedAdminUsers == null || subscribedAdminUsers.isEmpty()) {
            log.warn("No subscribed admin users found, no messages will be send.");
            return;
        }

        sendMessageToUsers(subscribedAdminUsers, message);
    }

    /**
     * Sends a socket message to the given set of users.
     *
     * @param userNames The set of user names to send the messages to.
     * @param message The message to send.
     */
    public void sendMessageToUsers(Set<String> userNames, SocketMessage message) {
        userNames.stream()
            .forEach(userName -> {
                try {
                    messagingTemplate.convertAndSendToUser(userName, "/queue/reply", message);
                    log.debug("Successfully sent message to user {}", userName);
                } catch (MessagingException e) {
                    log.error("Could not send message to user {}", userName);
                    log.trace("Full stack trace ", e);
                }
            });
    }

    /**
     * Returns all users that are currently subscribed to /user/queue.
     *
     * @return The subscribed users.
     */
    public Set<SimpUser> getSubscribedUsers() {
        return getSubscribedUsers(subscription -> true);
    }

    /**
     * Returns all (filtered) users that are currently subscribed to /user/queue.
     *
     * @param matcher The filter to use to get the users.
     * @return The subscribed users.
     */
    public Set<SimpUser> getSubscribedUsers(SimpSubscriptionMatcher matcher) {
        Set<SimpUser> subscribedUsers = userRegistry
            .findSubscriptions(matcher)
            .stream()
            .map(subscription -> subscription.getSession().getUser())
            .collect(Collectors.toSet());

        return subscribedUsers;
    }

}
