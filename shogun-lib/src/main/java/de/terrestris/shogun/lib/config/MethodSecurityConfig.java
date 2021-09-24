package de.terrestris.shogun.lib.config;

import de.terrestris.shogun.lib.security.access.BasePermissionEvaluator;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private BasePermissionEvaluator basePermissionEvaluator;

    @Autowired
    private List<BaseEntityPermissionEvaluator> permissionEvaluators;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(basePermissionEvaluator);
        expressionHandler.setApplicationContext(appContext);

        return expressionHandler;
    }

}
