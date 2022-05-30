alter table if exists shogun_rev.users_rev rename column keycloak_id_mod to auth_provider_id_mod;
alter table if exists shogun_rev.groups_rev rename column keycloak_id_mod to auth_provider_id_mod;
