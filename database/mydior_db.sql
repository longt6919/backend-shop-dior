CREATE DATABASE ShopDior
GO
USE ShopDior
GO
CREATE TABLE roles (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);
GO
INSERT INTO roles (name) VALUES 
('admin'),
('user'),
('employee');

select*from token
CREATE TABLE users (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    fullname NVARCHAR(100) NULL DEFAULT '',
    phone_number VARCHAR(15) NULL,
    address NVARCHAR(200) NULL DEFAULT '',
    password CHAR(60) NOT NULL,
    create_at DATETIME NULL,
    update_at DATETIME NULL,
    is_active BIT NULL DEFAULT 1,
    date_of_birth DATE NULL,
    facebook_account_id VARCHAR(100) NULL,
    google_account_id VARCHAR(100) NULL,
    role_id INT NULL DEFAULT 1,
    email VARCHAR(255) NULL DEFAULT '',
    CONSTRAINT FK_users_roles FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION
);

GO
CREATE TABLE tokens (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME NULL,
    revoked BIT NOT NULL,
    expired BIT NOT NULL,
    user_id INT NULL,
    CONSTRAINT FK_tokens_users FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);
GO
CREATE TABLE brands (
    id INT PRIMARY KEY IDENTITY,            
    name NVARCHAR(255) NOT NULL UNIQUE,            --Tên thương hiệu
    description NVARCHAR(500),                    --Mô tả thương hiệu
	is_active BIT NOT NULL DEFAULT 1
);
GO

CREATE TABLE colors (
    id INT PRIMARY KEY IDENTITY,            
    name NVARCHAR(100) NOT NULL UNIQUE,         -- Tên màu

);
GO
INSERT INTO colors(name) VALUES 
(N'Xanh'), 
(N'Đỏ'), 
(N'Tím'), 
(N'Trắng'), 
(N'Vàng'), 
(N'Nâu'), 
(N'Đen'), 
(N'Xanh Lá')


CREATE TABLE size (
    id INT PRIMARY KEY IDENTITY,            
    name NVARCHAR(50) NOT NULL UNIQUE,              -- Tên kích thước
);
GO
INSERT INTO size (name) VALUES 
(N'XS'), 
(N'S'), 
(N'M'), 
(N'L'), 
(N'XL'), 
(N'XXL'), 
(N'XXXL'), 
(N'Free Size')
GO
CREATE TABLE materials (                     
    id INT PRIMARY KEY IDENTITY,            -- ID chính của bảng
    name NVARCHAR(100) NOT NULL UNIQUE,            -- Tên chất liệu
	is_active BIT NOT NULL DEFAULT 1
);
GO
CREATE TABLE styles (
    id INT PRIMARY KEY IDENTITY,            
    name NVARCHAR(100) NOT NULL UNIQUE,             -- Tên phong cách
	is_active BIT NOT NULL DEFAULT 1
);
GO
CREATE TABLE origins (
    id INT PRIMARY KEY IDENTITY,            
    name NVARCHAR(100) NOT NULL UNIQUE,     -- Tên quốc gia/xuất xứ
	is_active BIT NOT NULL DEFAULT 1
);
GO
CREATE TABLE categories (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE,
	is_active BIT NOT NULL DEFAULT 1
);

GO
CREATE TABLE products (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    name NVARCHAR(350) NULL,
    price DECIMAL(10,2) NULL,
    thumbnail VARCHAR(255) NULL,
    description NVARCHAR(MAX) NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    category_id INT NULL,
	origin_id INT NULL,
	style_id INT NULL,
	material_id INT NULL,
	brand_id INT NULL,
	is_active BIT NULL DEFAULT 1,
		 CONSTRAINT FK_products_brands FOREIGN KEY (brand_id)
        REFERENCES brands(id),
    CONSTRAINT FK_products_materials FOREIGN KEY (material_id)
        REFERENCES materials(id),
	  CONSTRAINT FK_products_origins FOREIGN KEY (origin_id)
        REFERENCES origins(id),
		  CONSTRAINT FK_products_style FOREIGN KEY (style_id)
        REFERENCES styles(id),
    CONSTRAINT FK_products_categories FOREIGN KEY (category_id)
        REFERENCES categories(id)
);



GO
CREATE TABLE product_detail (
    id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
    size_id INT NOT NULL,
    color_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    CONSTRAINT FK_detail_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT FK_detail_size FOREIGN KEY (size_id) REFERENCES size(id) ON DELETE NO ACTION,
    CONSTRAINT FK_detail_color FOREIGN KEY (color_id) REFERENCES colors(id) ON DELETE NO ACTION
);
GO
CREATE TABLE product_images (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_id INT NULL,
    image_url VARCHAR(300) NULL,
    CONSTRAINT FK_product_images_products FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);
GO
CREATE TABLE favorites (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    user_id INT NULL,
    product_id INT NULL,
    CONSTRAINT FK_favorites_users FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
    CONSTRAINT FK_favorites_products FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);
GO
CREATE TABLE comments (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    product_id INT NULL,
    user_id INT NULL,
    content VARCHAR(255) NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    CONSTRAINT FK_comments_products FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION,
    CONSTRAINT FK_comments_users FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);
GO
CREATE TABLE coupons (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    active BIT NOT NULL DEFAULT 1
);
GO
CREATE UNIQUE INDEX UX_coupons_code ON coupons(code);
GO
CREATE TABLE coupon_conditions (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    coupon_id INT NOT NULL,
    attribute VARCHAR(255) NOT NULL,
    operator VARCHAR(10) NOT NULL,
    value VARCHAR(255) NOT NULL,
    discount_amount DECIMAL(5,2) NOT NULL,
    CONSTRAINT FK_coupon_conditions_coupons FOREIGN KEY (coupon_id)
        REFERENCES coupons(id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);

GO
CREATE TABLE orders (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    user_id INT NULL,
    fullname NVARCHAR(100) NULL  DEFAULT '',
    email VARCHAR(100) NULL  DEFAULT '',
    phone_number VARCHAR(20) NOT NULL,
    address NVARCHAR(200) NOT NULL,
    note NVARCHAR(100) NULL  DEFAULT '',
    order_date DATETIME NOT NULL DEFAULT GETDATE(),
    status VARCHAR(20) NOT NULL,
    total_money DECIMAL NULL,
    shipping_method VARCHAR(100) NULL,
    shipping_address NVARCHAR(200) NULL,
    shipping_date DATE NULL,
    delivery_date DATE NULL,
    payment_method VARCHAR(100) NULL,
    active BIT NULL,
    coupon_id INT NULL,
    CONSTRAINT CHK_orders_status 
      CHECK (status IN ('pending',
    'processing',
    'shipped',
    'delivered',
    'refused_on_delivery', 
    'out_of_stock_pending',  
    'waiting_for_stock',     
    'cancelled_out_of_stock', 
	'cancelled')),
    CONSTRAINT FK_orders_users FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
        ON UPDATE NO ACTION,
    CONSTRAINT FK_orders_coupons FOREIGN KEY (coupon_id)
        REFERENCES coupons(id)
        ON DELETE SET NULL
        ON UPDATE NO ACTION
);
GO

CREATE TABLE order_details (
    id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    order_id INT NULL,
    product_detail_id  INT NULL,
    price DECIMAL(10,2) NULL,
    number_of_products INT NOT NULL DEFAULT 1,
    total_money DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    coupon_id INT NULL,
    CONSTRAINT FK_order_details_orders FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT FK_order_details_product_detail FOREIGN KEY (product_detail_id)
        REFERENCES product_detail(id)
        ON DELETE SET NULL,

    CONSTRAINT FK_order_details_coupons FOREIGN KEY (coupon_id)
        REFERENCES coupons(id)
        ON DELETE SET NULL
);
GO

insert into coupons(code) values('HEAVEN');
insert into coupon_conditions(coupon_id,attribute,operator,value,discount_amount)
values(1, 'minimum_amount','>','100',10)
