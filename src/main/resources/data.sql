/*
 Insert into table role
 */
INSERT INTO role (id, role_name)
VALUES (1, 'ROLE_CLIENT');
INSERT INTO role (id, role_name)
VALUES (2, 'ROLE_ADMIN');
INSERT INTO role (id, role_name)
VALUES (3, 'ROLE_MANAGE_USER');
INSERT INTO role (id, role_name)
VALUES (4, 'ROLE_MANAGE_ORDER');

/*
 Insert into table user
 */
INSERT INTO user(username, password, email, phone, role_id)
VALUES ('ThanhTan', '6723', 'thanhtan8900@gmail.com', '0865603890', 1);

SELECT *
FROM user;
SELECT *
FROM role;
