package de.terrestris.shogun.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final String resourceId;

    private final String principalClaimName;

    public KeycloakJwtAuthenticationConverter(String resourceId, String principalClaimName) {
        this.resourceId = resourceId;
        this.principalClaimName = principalClaimName;
    }

    @Override
    public AbstractAuthenticationToken convert(final Jwt jwt) {
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) extractResourceRoles(jwt, resourceId);

        String principalClaimValue = jwt.getClaimAsString(this.principalClaimName);

        return new JwtAuthenticationToken(jwt, authorities, principalClaimValue);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt, final String resourceId) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null || resourceAccess.get(resourceId) == null ||
            ((Map<String, Object>) resourceAccess.get(resourceId)).get("roles") == null) {
            return Collections.emptySet();
        }

        Collection<String> resourceRoles = (Collection<String>) ((Map<String, Object>) resourceAccess.get(resourceId)).get("roles");

        return resourceRoles.stream()
            .map(role -> new SimpleGrantedAuthority(String.format("ROLE_%s", role).toUpperCase()))
            .collect(Collectors.toSet());
    }
}
