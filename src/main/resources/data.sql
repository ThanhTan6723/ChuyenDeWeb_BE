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

-- Insert vào bảng brand
INSERT INTO brand (id, name)
VALUES (1, 'KOKIMI'),
       (2, 'SUN BLOCK'),
       (3, 'Sunplay'),
       (4, 'CENTELLA'),
       (5, 'POND S'),
       (6, 'LA ROCHE-POSAY'),
       (7, 'MAYBELLINE'),
       (8, 'Vedette'),
       (9, 'klairs'),
       (10, 'Cetaphil'),
       (11, 'CeraVe'),
       (12, 'Good Morning'),
       (13, 'SVR'),
       (14, 'Eucerin'),
       (15, 'LOREAL'),
       (16, 'BIODERMA'),
       (17, 'cocoon'),
       (18, 'Good Skin'),
       (19, 'COLORKEY');

-- Insert vào bảng category
INSERT INTO category (id, name)
VALUES (1, 'Tẩy trang'),
       (2, 'Toner'),
       (3, 'Sửa rữa mặt'),
       (4, 'Kem chống nắng'),
       (5, 'Bông tẩy trang'),
       (6, 'Kem dưỡng'),
       (7, 'Mặt nạ'),
       (8, 'Serum'),
       (9, 'Kem nền');

-- Insert vào bảng product
INSERT INTO product (name, price, description, brand_id, category_id, view_count)
VALUES ('Sample Product', 100.0, 'This is a sample product description', 1, 1, 0);

-- Insert vào bảng productvariant
INSERT INTO productvariant (id, product_id, product_attribute, variant, price, quantity)
VALUES (1, 1, 'Color: Blue', 'Blue Variant', 100.0, 50),
       (2, 1, 'Color: Red', 'Red Variant', 120.0, 30);

-- Insert vào bảng images
INSERT INTO images (id, public_id)
VALUES (1, 'sample_product_image_blue'),
       (2, 'sample_product_image_red');

-- Insert vào bảng productimage
INSERT INTO productimage (product_variant_id, image_id, is_main)
VALUES (1, 1, TRUE),
       (2, 2, TRUE);

SELECT *
FROM user;
SELECT *
FROM role;
