insert into t_user(id, username, password, create_date, update_date, active) values(1, 'eric', '', '2017-12-01 23:59:59', now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(2, 'alan', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(3, 'aaron', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(4, 'mike', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(5, 'dave', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(6, 'ellen', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(7, 'sean', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(8, 'tim', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(9, 'steven', '', now(), now(), 1);
insert into t_user(id, username, password, create_date, update_date, active) values(10, 'tom', '', now(), now(), 0);

insert into t_role(id, name, active, create_date, update_date) values(1, 'TRAINER', 1, now(), now());
insert into t_role(id, name, active, create_date, update_date) values(2, 'TRAINEE', 1, now(), now());

insert into t_user_role(user_id, role_id) values (3, 1);
insert into t_user_role(user_id, role_id) values  (1, 2);