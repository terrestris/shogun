package de.terrestris.shogunboot.task.scheduled;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.token.UserVerificationToken;
import de.terrestris.shoguncore.repository.token.UserVerificationTokenRepository;
import de.terrestris.shoguncore.service.UserService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class UserTask {

    @Autowired
    private UserVerificationTokenRepository userVerificationTokenRepository;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "${tasks.scheduled.usertask.cleanupPendingRegistrations}")
    public void cleanupPendingRegistrations() {

        log.info("Trying to delete all pending user registrations.");

        try {
            log.trace("Collecting all pending user registrations to delete.");

            final Calendar cal = Calendar.getInstance();
            Date date = cal.getTime();

            List<UserVerificationToken> userVerificationTokens =
                userVerificationTokenRepository.findByExpiryDateBefore(date);

            log.info("Found {} pending user registrations to delete.",
                userVerificationTokens.size());

            for (UserVerificationToken userVerificationToken : userVerificationTokens) {
                User user = userVerificationToken.getUser();

                userService.deleteUser(user);
            }
        } catch (Exception e) {
            log.error("Error while deleting a pending user registration {}", e.getMessage());
            log.trace("Full stack trace: ", e);
        }

        log.info("Successfully deleted all pending user registrations");
    }
}
