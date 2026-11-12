CREATE TABLE category (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    name       VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort       INT         NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品分类';

CREATE TABLE product (
    id             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    sku            VARCHAR(50)    NOT NULL COMMENT '商品编码',
    barcode        VARCHAR(50)    NULL COMMENT '条码',
    name           VARCHAR(100)   NOT NULL COMMENT '商品名称',
    category_id    BIGINT         NOT NULL COMMENT '分类ID',
    unit           VARCHAR(16)    NOT NULL DEFAULT '件' COMMENT '计量单位',
    purchase_price DECIMAL(12, 2) NOT NULL DEFAULT 0 COMMENT '进货价',
    sale_price     DECIMAL(12, 2) NOT NULL DEFAULT 0 COMMENT '零售价',
    stock          INT            NOT NULL DEFAULT 0 COMMENT '库存快照',
    warning_stock  INT            NOT NULL DEFAULT 5 COMMENT '库存预警值',
    status         TINYINT        NOT NULL DEFAULT 1 COMMENT '1 在售 0 停售',
    remark         VARCHAR(255)   NULL COMMENT '备注',
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_sku (sku),
    UNIQUE KEY uk_product_barcode (barcode),
    KEY idx_product_category (category_id),
    KEY idx_product_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '商品';

CREATE TABLE supplier (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
    name       VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact    VARCHAR(50)  NULL COMMENT '联系人',
    phone      VARCHAR(30)  NULL COMMENT '联系电话',
    address    VARCHAR(255) NULL COMMENT '地址',
    remark     VARCHAR(255) NULL COMMENT '备注',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_supplier_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '供应商';

CREATE TABLE member (
    id         BIGINT         NOT NULL AUTO_INCREMENT COMMENT '会员ID',
    name       VARCHAR(50)    NOT NULL COMMENT '会员姓名',
    phone      VARCHAR(30)    NOT NULL COMMENT '手机号',
    points     INT            NOT NULL DEFAULT 0 COMMENT '积分',
    total_spent DECIMAL(14, 2) NOT NULL DEFAULT 0 COMMENT '累计消费',
    created_at DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_phone (phone)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '会员';

CREATE TABLE employee (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    name          VARCHAR(50)  NOT NULL COMMENT '姓名',
    username      VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希',
    role          VARCHAR(20)  NOT NULL COMMENT 'ADMIN / MANAGER / CASHIER',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 0 禁用',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_employee_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '员工';

CREATE TABLE purchase_order (
    id           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '采购单ID',
    order_no     VARCHAR(50)    NOT NULL COMMENT '采购单号',
    supplier_id  BIGINT         NOT NULL COMMENT '供应商ID',
    employee_id  BIGINT         NULL COMMENT '经办员工ID',
    total_amount DECIMAL(14, 2) NOT NULL DEFAULT 0 COMMENT '采购总额',
    status       VARCHAR(20)    NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT / RECEIVED / CANCELLED',
    remark       VARCHAR(255)   NULL COMMENT '备注',
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_purchase_order_no (order_no),
    KEY idx_purchase_supplier (supplier_id),
    KEY idx_purchase_created (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '采购单';

CREATE TABLE purchase_order_item (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    purchase_order_id BIGINT         NOT NULL COMMENT '采购单ID',
    product_id        BIGINT         NOT NULL COMMENT '商品ID',
    quantity          INT            NOT NULL COMMENT '采购数量',
    unit_cost         DECIMAL(12, 2) NOT NULL COMMENT '采购单价',
    amount            DECIMAL(14, 2) NOT NULL COMMENT '小计',
    PRIMARY KEY (id),
    KEY idx_purchase_item_order (purchase_order_id),
    KEY idx_purchase_item_product (product_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '采购单明细';

CREATE TABLE sale_order (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '销售单ID',
    order_no        VARCHAR(50)    NOT NULL COMMENT '销售单号',
    member_id       BIGINT         NULL COMMENT '会员ID',
    employee_id     BIGINT         NULL COMMENT '收银员ID',
    total_amount    DECIMAL(14, 2) NOT NULL DEFAULT 0 COMMENT '商品总额',
    discount_amount DECIMAL(14, 2) NOT NULL DEFAULT 0 COMMENT '优惠金额',
    paid_amount     DECIMAL(14, 2) NOT NULL DEFAULT 0 COMMENT '实收金额',
    payment_method  VARCHAR(20)    NOT NULL DEFAULT 'CASH' COMMENT 'CASH / WECHAT / ALIPAY',
    status          VARCHAR(20)    NOT NULL DEFAULT 'PAID' COMMENT 'PAID / REFUNDED / CANCELLED',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_sale_order_no (order_no),
    KEY idx_sale_created (created_at),
    KEY idx_sale_member (member_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '销售单';

CREATE TABLE sale_order_item (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    sale_order_id BIGINT         NOT NULL COMMENT '销售单ID',
    product_id    BIGINT         NOT NULL COMMENT '商品ID',
    product_name  VARCHAR(100)   NOT NULL COMMENT '商品名称快照',
    quantity      INT            NOT NULL COMMENT '数量',
    unit_price    DECIMAL(12, 2) NOT NULL COMMENT '销售单价',
    amount        DECIMAL(14, 2) NOT NULL COMMENT '小计',
    PRIMARY KEY (id),
    KEY idx_sale_item_order (sale_order_id),
    KEY idx_sale_item_product (product_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '销售单明细';

CREATE TABLE stock_movement (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '流水ID',
    product_id    BIGINT       NOT NULL COMMENT '商品ID',
    type          VARCHAR(20)  NOT NULL COMMENT 'PURCHASE_IN / SALE_OUT / ADJUST / RETURN_IN / RETURN_OUT',
    quantity      INT          NOT NULL COMMENT '变动数量，入库为正、出库为负',
    balance_after INT          NOT NULL COMMENT '变动后库存',
    reference_no  VARCHAR(50)  NULL COMMENT '关联单号',
    remark        VARCHAR(255) NULL COMMENT '备注',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_stock_product_created (product_id, created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '库存流水';

CREATE TABLE cashier_shift (
    id           BIGINT         NOT NULL AUTO_INCREMENT COMMENT '班次ID',
    employee_id  BIGINT         NOT NULL COMMENT '收银员ID',
    opened_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开班时间',
    closed_at    DATETIME       NULL COMMENT '交班时间',
    opening_cash DECIMAL(14, 2) NOT NULL DEFAULT 0 COMMENT '备用金',
    closing_cash DECIMAL(14, 2) NULL COMMENT '交班现金',
    status       VARCHAR(20)    NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN / CLOSED',
    PRIMARY KEY (id),
    KEY idx_shift_employee (employee_id, opened_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '收银班次';
