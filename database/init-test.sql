# create the app schema
create schema if not exists `epic-test`;

# create the database user
create user if not exists 'mercamas'@'localhost' identified by 'Epic271';
create user if not exists 'mercamas'@'%' identified by 'Epic271';

grant all privileges on `epic-test`.* to 'mercamas'@'localhost';
grant all privileges on `epic-test`.* to 'mercamas'@'%';

# ------------------------ create the familia table ------------------------
create table if not exists `epic-test`.familia
(
    familia_id int auto_increment
        primary key,
    nombre     varchar(30) not null,
    constraint familia_nombre_uindex
        unique (nombre)
);


# ------------------------ create the producto table ------------------------
create table if not exists `epic-test`.producto
(
    producto_id            int auto_increment
        primary key,
    codigo                 varchar(20)          not null,
    desc_larga             varchar(50)          not null,
    desc_corta             varchar(25)          not null,
    precio_venta           int                  not null,
    precio_compra_efectivo int                  not null,
    margen                 double               not null,
    existencias            int                  not null,
    activo                 tinyint(1) default 1 not null,
    familia                int                  not null,
    alerta_existencias     int                  not null,
    constraint producto_codigo_uindex
        unique (codigo),
    constraint producto_desc_corta_uindex
        unique (desc_corta),
    constraint producto_desc_larga_uindex
        unique (desc_larga),
    constraint producto_familia_familia_id_fk
        foreign key (familia) references `epic-test`.familia (familia_id)
            on update cascade
);


# ------------------------ create the proveedor table ------------------------
create table if not exists `epic-test`.proveedor
(
    proveedor_id int auto_increment
        primary key,
    nombre       varchar(50)  not null,
    telefono     varchar(20)  not null,
    correo       varchar(40)  null,
    direccion    varchar(100) null,
    constraint proveedor_correo_uindex
        unique (correo),
    constraint proveedor_nombre_uindex
        unique (nombre),
    constraint proveedor_telefono_uindex
        unique (telefono)
);


# ------------------------ create the empleado table ------------------------
create table if not exists `epic-test`.empleado
(
    empleado_id int auto_increment
        primary key,
    nombre      varchar(50) not null,
    telefono    varchar(20) not null,
    constraint empleado_nombre_uindex
        unique (nombre),
    constraint empleado_telefono_uindex
        unique (telefono)
);


# ------------------------ create the cliente table ------------------------
create table if not exists `epic-test`.cliente
(
    cliente_id int auto_increment
        primary key,
    nombre     varchar(50)  not null,
    telefono   varchar(20)  null,
    direccion  varchar(100) null,
    birthday_day   int      null,
    birthday_month int      null,
    constraint cliente_nombre_uindex
        unique (nombre),
    constraint cliente_telefono_uindex
        unique (telefono)
);


# ------------------------ create the pedido table ------------------------
create table if not exists `epic-test`.pedido
(
    pedido_id  int auto_increment
        primary key,
    fecha_hora datetime not null,
    proveedor  int      not null,
    empleado   int      not null,
    constraint pedido_empleado_empleado_id_fk
        foreign key (empleado) references `epic-test`.empleado (empleado_id)
            on update cascade,
    constraint pedido_proveedor_proveedor_id_fk
        foreign key (proveedor) references `epic-test`.proveedor (proveedor_id)
            on update cascade
);


# ------------------------ create the lote table ------------------------
create table if not exists `epic-test`.lote
(
    lote_id       int auto_increment
        primary key,
    cantidad      int not null,
    precio_compra int not null,
    producto      int not null,
    pedido        int not null,
    constraint lote_pedido_pedido_id_fk
        foreign key (pedido) references `epic-test`.pedido (pedido_id)
            on update cascade,
    constraint lote_producto_producto_id_producto_id_fk
        foreign key (producto) references `epic-test`.producto (producto_id)
            on update cascade
);


# ------------------------ create the venta table ------------------------
create table if not exists `epic-test`.venta
(
    venta_id      int auto_increment
        primary key,
    fecha_hora    datetime not null,
    precio_total  int      not null,
    pago_recibido int      not null,
    empleado      int      not null,
    cliente       int      not null,
    constraint venta_cliente_cliente_id_fk
        foreign key (cliente) references `epic-test`.cliente (cliente_id)
            on update cascade,
    constraint venta_empleado_empleado_id_fk
        foreign key (empleado) references `epic-test`.empleado (empleado_id)
            on update cascade
);


# ------------------------ create the item_venta table ------------------------
create table if not exists `epic-test`.item_venta
(
    item_venta_id int auto_increment
        primary key,
    fecha_hora     datetime not null,
    cantidad       int      not null,
    precio_venta   int      not null,
    producto       int      not null,
    venta          int      not null,
    cliente        int      not null,
    constraint item_venta_cliente_cliente_id_fk
        foreign key (cliente) references `epic-test`.cliente (cliente_id)
            on update cascade,
    constraint item_venta_producto_producto_id_fk
        foreign key (producto) references `epic-test`.producto (producto_id)
            on update cascade,
    constraint item_venta_venta_venta_id_fk
        foreign key (venta) references `epic-test`.venta (venta_id)
            on update cascade
);

# ------------------------ add bolsa ------------------------
insert into `epic-test`.familia(nombre) VALUE ('Bolsas');
insert into `epic-test`.producto(codigo, desc_larga, desc_corta, precio_venta, precio_compra_efectivo, margen, existencias, activo, familia, alerta_existencias)
    value ('bolsa', 'Bolsa', 'Bolsa', 50, 0, 0.1, 0, 1, 1, 0);