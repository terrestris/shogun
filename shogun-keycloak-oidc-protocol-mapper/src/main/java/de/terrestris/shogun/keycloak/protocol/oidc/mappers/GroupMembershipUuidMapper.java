
package de.terrestris.shogun.keycloak.protocol.oidc.mappers;

import org.keycloak.models.GroupModel;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.GroupMembershipMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.IDToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link org.keycloak.protocol.ProtocolMapper} that add custom optional claim containing the uuids of groups the user
 * is assigned to
 */
public class GroupMembershipUuidMapper extends GroupMembershipMapper {

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static final String CLAIM_NAME = "groups_uuid";

    static {
        OIDCAttributeMapperHelper.addTokenClaimNameConfig(configProperties);

        ProviderConfigProperty providerConfigProperty = new ProviderConfigProperty();
        providerConfigProperty.setName("claim.name");
        providerConfigProperty.setLabel("Token Claim Name");
        providerConfigProperty.setType(ProviderConfigProperty.STRING_TYPE);
        providerConfigProperty.setDefaultValue(CLAIM_NAME);
        providerConfigProperty.setHelpText("Claim containing the uuids of groups the user");
        providerConfigProperty.setSecret(true);
        configProperties.add(providerConfigProperty);

        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, GroupMembershipUuidMapper.class);
    }

    public static final String PROVIDER_ID = "oidc-group-membership-uuid-mapper";

    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Group Membership (UUID)";
    }

    public String getDisplayCategory() {
        return TOKEN_MAPPER_CATEGORY;
    }

    @Override
    public String getHelpText() {
        return "Add group membership to user (list of group uuids)";
    }

    /**
     * Adds the group membership information to the {@link IDToken#otherClaims}.
     * @param token The {@link IDToken}
     * @param mappingModel The {@link ProtocolMapperModel}
     * @param userSession The {@link UserSessionModel}
     */
    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel, UserSessionModel userSession) {
        List<String> membership = userSession.getUser().getGroupsStream().map(GroupModel::getId).collect(Collectors.toList());
        String protocolClaim = mappingModel.getConfig().get(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME);
        token.getOtherClaims().put(protocolClaim, membership);
    }

    /**
     *
     * @param name
     * @param tokenClaimName
     * @param consentRequired
     * @param consentText
     * @param accessToken
     * @param idToken
     * @return
     */
    public static ProtocolMapperModel create(String name,
                                             String tokenClaimName,
                                             boolean consentRequired, String consentText,
                                             boolean accessToken, boolean idToken) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<>();
        config.put(OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME, CLAIM_NAME);
        config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        mapper.setConfig(config);

        return mapper;
    }

}
