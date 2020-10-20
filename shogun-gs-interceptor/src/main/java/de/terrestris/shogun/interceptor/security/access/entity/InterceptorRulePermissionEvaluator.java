package de.terrestris.shogun.interceptor.security.access.entity;

import de.terrestris.shogun.interceptor.model.InterceptorRule;
import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.security.SecurityContextUtil;
import de.terrestris.shogun.lib.security.access.entity.BaseEntityPermissionEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Permission evaluator for {@link InterceptorRule}s
 */
@Component
public class InterceptorRulePermissionEvaluator extends BaseEntityPermissionEvaluator<InterceptorRule> {

    @Autowired
    protected SecurityContextUtil securityContextUtil;

    @Override
    public boolean hasPermission(User user, InterceptorRule entity, PermissionType permission) {
        List<GrantedAuthority> authorities = securityContextUtil.getGrantedAuthorities();
        boolean isInterceptorAdmin = authorities.stream().anyMatch(
            grantedAuthority -> StringUtils.endsWithIgnoreCase(grantedAuthority.getAuthority(), "INTERCEPTOR_ADMIN") ||
                StringUtils.endsWithIgnoreCase(grantedAuthority.getAuthority(), "ADMIN")
        );

        if (isInterceptorAdmin) {
            return true;
        }

        return super.hasPermission(user, entity, permission);
    }
}


