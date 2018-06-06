insert into t_user(id, username, password, create_date, update_date, active) values(1, 'aaronluo', '123abc', now(), now(), true);
insert into t_user_property(id, owner_id, name, value, create_date, update_date, active) values(1, 1, 'gender', 'male', now(), now(), true);
insert into t_user_property(id, owner_id, name, value, create_date, update_date, active) values(2, 1, 'age', '40', now(), now(), true);
insert into t_user_property(id, owner_id, name, value, create_date, update_date, active) values(3, 1, 'nickename', 'tom', now(), now(), false);