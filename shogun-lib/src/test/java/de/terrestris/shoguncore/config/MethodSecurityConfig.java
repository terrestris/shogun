package de.terrestris.shoguncore.config;

import de.terrestris.shoguncore.security.access.BasePermissionEvaluator;
import de.terrestris.shoguncore.security.access.entity.BaseEntityPermissionEvaluator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private BasePermissionEvaluator basePermissionEvaluator;

    @Autowired
    private List<BaseEntityPermissionEvaluator> permissionEvaluators;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler =
                new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(basePermissionEvaluator);

        return expressionHandler;
    }

}
