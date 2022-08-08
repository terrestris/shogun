SET search_path TO shogun_rev, public;

alter table shogun_rev.applications_rev add column if not exists created_mod bool;
alter table shogun_rev.applications_rev add column if not exists modified_mod bool;
alter table shogun_rev.applications_rev add column if not exists client_config_mod bool;
alter table shogun_rev.applications_rev add column if not exists i18n_mod bool;
alter table shogun_rev.applications_rev add column if not exists layer_config_mod bool;
alter table shogun_rev.applications_rev add column if not exists layer_tree_mod bool;
alter table shogun_rev.applications_rev add column if not exists name_mod bool;
alter table shogun_rev.applications_rev add column if not exists state_only_mod bool;
alter table shogun_rev.applications_rev add column if not exists tool_config_mod bool;

alter table shogun_rev.groupclasspermissions_rev add column if not exists created_mod bool;
alter table shogun_rev.groupclasspermissions_rev add column if not exists modified_mod bool;
alter table shogun_rev.groupclasspermissions_rev add column if not exists class_name_mod bool;
alter table shogun_rev.groupclasspermissions_rev add column if not exists permissions_mod bool;
alter table shogun_rev.groupclasspermissions_rev add column if not exists group_mod bool;

alter table shogun_rev.groupinstancepermissions_rev add column if not exists created_mod bool;
alter table shogun_rev.groupinstancepermissions_rev add column if not exists modified_mod bool;
alter table shogun_rev.groupinstancepermissions_rev add column if not exists entity_id_mod bool;
alter table shogun_rev.groupinstancepermissions_rev add column if not exists permissions_mod bool;
alter table shogun_rev.groupinstancepermissions_rev add column if not exists group_mod bool;

alter table shogun_rev.groups_rev add column if not exists created_mod bool;
alter table shogun_rev.groups_rev add column if not exists modified_mod bool;
alter table shogun_rev.groups_rev add column if not exists keycloak_id_mod bool;

alter table shogun_rev.layers_rev add column if not exists created_mod bool;
alter table shogun_rev.layers_rev add column if not exists modified_mod bool;
alter table shogun_rev.layers_rev add column if not exists client_config_mod bool;
alter table shogun_rev.layers_rev add column if not exists features_mod bool;
alter table shogun_rev.layers_rev add column if not exists name_mod bool;
alter table shogun_rev.layers_rev add column if not exists source_config_mod bool;
alter table shogun_rev.layers_rev add column if not exists type_mod bool;

alter table shogun_rev.userclasspermissions_rev add column if not exists created_mod bool;
alter table shogun_rev.userclasspermissions_rev add column if not exists modified_mod bool;
alter table shogun_rev.userclasspermissions_rev add column if not exists class_name_mod bool;
alter table shogun_rev.userclasspermissions_rev add column if not exists permissions_mod bool;
alter table shogun_rev.userclasspermissions_rev add column if not exists user_mod bool;

alter table shogun_rev.userinstancepermissions_rev add column if not exists created_mod bool;
alter table shogun_rev.userinstancepermissions_rev add column if not exists modified_mod bool;
alter table shogun_rev.userinstancepermissions_rev add column if not exists entity_id_mod bool;
alter table shogun_rev.userinstancepermissions_rev add column if not exists permissions_mod bool;
alter table shogun_rev.userinstancepermissions_rev add column if not exists user_mod bool;

alter table shogun_rev.users_rev add column if not exists created_mod bool;
alter table shogun_rev.users_rev add column if not exists modified_mod bool;
alter table shogun_rev.users_rev add column if not exists client_config_mod bool;
alter table shogun_rev.users_rev add column if not exists details_mod bool;
alter table shogun_rev.users_rev add column if not exists keycloak_id_mod bool;
