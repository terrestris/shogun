drop function if exists has_permission(integer, text, text[], text);

create or replace function has_permission(e_id int, user_uuid text, group_ids text[], classname text) returns boolean
	as $$
	declare
	userinstanceperms int;
	begin

    raise info 'user_uuid %', user_uuid;
    raise info 'entity id %', e_id;
    raise info 'group_ids %', group_ids;
    raise info 'classname %', classname;

	return (select
  		(a.count +  b.count +  c.count +  d.count) > 0
	from
		(
		select count(*) from userinstancepermissions uip
			left join users u ON u.id = uip.user_id
			left join permissions p on uip.permissions_id = p.id
		where
			u.keycloak_id = user_uuid and
			uip.entity_id = e_id and
			(p.name like '%READ%' or p.name = 'ADMIN')
		) as a
		,
		(
		select count(*) from groupinstancepermissions gip
			left join "groups" g ON g.id = gip.group_id
			left join permissions p on gip.permissions_id = p.id
		where
			g.keycloak_id = any (group_ids) and
			--g.keycloak_id = 'e8bc650f-c577-4b63-a6bf-90a70482c25a' and
			gip.entity_id = e_id and
			(p.name like '%READ%' or p.name = 'ADMIN')
		) as b,
		(
		select count(*) from userclasspermissions ucp
			left join users u ON u.id = ucp.user_id
			left join permissions p on ucp.permissions_id = p.id
		where
			u.keycloak_id = user_uuid and
			ucp.class_name = classname and
			(p.name like '%READ%' or p.name = 'ADMIN')
		) as c,
		(
		select count(*) from groupclasspermissions gcp
			left join "groups" g ON g.id = gcp.group_id
			left join permissions p on gcp.permissions_id = p.id
		where
			g.keycloak_id = any (group_ids) and
			gcp.class_name = classname and
			(p.name like '%READ%' or p.name = 'ADMIN')
		) as d);
	end;
$$ language plpgsql;

-- select has_permission ('8f7c1a90-5f33-4cd4-be81-5f9458dcaa75', 11, ARRAY['e8bc650f-c577-4b63-a6bf-90a70482c25a'], 'de.terrestris.progemis.model.Application');
