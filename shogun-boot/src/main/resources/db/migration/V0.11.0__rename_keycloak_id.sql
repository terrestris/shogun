alter table if exists shogun.users rename column keycloak_id to auth_provider_id;
alter table if exists shogun.groups rename column keycloak_id to auth_provider_id;
alter table if exists shogun_rev.users_rev rename column keycloak_id to auth_provider_id;
alter table if exists shogun_rev.groups_rev rename column keycloak_id to auth_provider_id;
