package de.terrestris.shogun.boot.service;

import de.terrestris.shogun.boot.dto.ApplicationInfo;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Informational service that provides general info about the application.
 */
@Component
public class ApplicationInfoService {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    private SecurityContextUtil securityContextUtil;

    /**
     * Returns general application information such as the version.
     *
     * @return a JSON string
     */
    public ApplicationInfo getApplicationInfo() {
        String name = "/META-INF/build-info.properties";

        Properties props = new Properties();
        try (InputStream in = getClass().getResourceAsStream(name)) {
            props.load(in);
        } catch (Exception e) {
            LOG.error("Could not load build informations from file {}. Please " +
                    "ensure the file is present and you have built the application " +
                    "completely", name);
            LOG.trace("Full stack trace: ", e);
        }

        Optional<User> userOpt = securityContextUtil.getUserBySession();
        ApplicationInfo applicationInfo = new ApplicationInfo();

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            applicationInfo.setUserId(user.getId());
            List<GrantedAuthority> grantedAuthorities = securityContextUtil.getGrantedAuthorities();

            if (!grantedAuthorities.isEmpty()) {
                List<String> simpleAuthList = new ArrayList<>();
                for (GrantedAuthority authority : grantedAuthorities) {
                    simpleAuthList.add(authority.getAuthority());
                }
                applicationInfo.setAuthorities(simpleAuthList);
            }
        }

        applicationInfo.setBuildTime(props.getProperty("build.time"));
        applicationInfo.setVersion(props.getProperty("build.version"));
        applicationInfo.setCommitHash(props.getProperty("build.commithash"));

        return applicationInfo;
    }

}
