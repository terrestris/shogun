package de.terrestris.shogunboot.service;

import de.terrestris.shogunboot.dto.ApplicationInfo;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.security.SecurityContextUtil;
import de.terrestris.shoguncore.service.security.IdentityService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Informational service that provides general info about the application.
 */
@Component
public class ApplicationInfoService {

    protected final Logger LOG = LogManager.getLogger(getClass());

    @Autowired
    private SecurityContextUtil securityContextUtil;

    @Autowired
    private IdentityService identityService;

    /**
     * Returns general application information such as the version.
     *
     * @return a JSON string
     * @throws IOException if the info could not be determined
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

        Optional<User> user = securityContextUtil.getUserBySession();

        ApplicationInfo applicationInfo = new ApplicationInfo();

        if (user.isPresent()) {
            applicationInfo.setUser(user.get());

            List<GrantedAuthority> grantedAuthorities = securityContextUtil
                .getGrantedAuthorities(user.get());

            if (!grantedAuthorities.isEmpty()) {
                List<String> simpleAuthList = new ArrayList<>();
                for (GrantedAuthority authority : grantedAuthorities) {
                    simpleAuthList.add(authority.getAuthority());
                }
                applicationInfo.setAuthorities(simpleAuthList);
            }
        } else {
            List<String> grantedAuthorities = new ArrayList();
            grantedAuthorities.add("ROLE_ANONYMOUS");

            applicationInfo.setAuthorities(grantedAuthorities);
        }

        applicationInfo.setBuildTime(props.getProperty("build.time"));
        applicationInfo.setVersion(props.getProperty("build.version"));

        return applicationInfo;
    }

}
