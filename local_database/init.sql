-- # create & use the app schema
-- create schema if not exists epic;
--
-- # create the database user
-- create user if not exists 'mercamas'@'localhost' identified by 'fADw0CHKJqpDinkW';
-- grant all privileges on epic.* to 'mercamas'@'localhost';

# ------------------------ creation of familia table ------------------------
create table familia
(
    familia_id int auto_increment
        primary key,
    nombre     varchar(30) not null,
    constraint familia_nombre_uindex
        unique (nombre)
);


# ------------------------ creation of producto table ------------------------
create table producto
(
    producto_id  int auto_increment
        primary key,
    codigo       varchar(20)          not null,
    desc_larga   varchar(50)          not null,
    desc_corta   varchar(25)          not null,
    precio_venta double               not null,
    existencias  int                  not null,
    activo       tinyint(1) default 1 not null,
    familia      int                  null,
    constraint producto_codigo_uindex
        unique (codigo),
    constraint producto_desc_corta_uindex
        unique (desc_corta),
    constraint producto_desc_larga_uindex
        unique (desc_larga),
    constraint producto_familia_familia_id_fk
        foreign key (familia) references familia (familia_id)
            on update cascade
);


# ------------------------ creation of proveedor table ------------------------
create table proveedor
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


# ------------------------ creation of empleado table ------------------------
create table empleado
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


# ------------------------ creation of cliente table ------------------------
create table cliente
(
    cliente_id int auto_increment
        primary key,
    nombre     varchar(50)  not null,
    telefono   varchar(20)  not null,
    direccion  varchar(100) null,
    constraint cliente_nombre_uindex
        unique (nombre),
    constraint cliente_telefono_uindex
        unique (telefono)
);


# ------------------------ creation of pedido table ------------------------
create table pedido
(
    pedido_id  int auto_increment
        primary key,
    fecha_hora datetime not null,
    proveedor  int      not null,
    empleado   int      not null,
    constraint pedido_empleado_empleado_id_fk
        foreign key (empleado) references empleado (empleado_id)
            on update cascade,
    constraint pedido_proveedor_proveedor_id_fk
        foreign key (proveedor) references proveedor (proveedor_id)
            on update cascade
);


# ------------------------ creation of lote table ------------------------
create table lote
(
    lote_id       int auto_increment
        primary key,
    cantidad      int    not null,
    precio_compra double not null,
    producto      int    not null,
    pedido        int    not null,
    constraint lote_pedido_pedido_id_fk
        foreign key (pedido) references pedido (pedido_id)
            on update cascade,
    constraint lote_producto_producto_id_producto_id_fk
        foreign key (producto) references producto (producto_id)
            on update cascade
);


# ------------------------ creation of venta table ------------------------
create table venta
(
    venta_id      int auto_increment
        primary key,
    fecha_hora    datetime not null,
    precio_total  int      not null,
    pago_recibido int      not null,
    empleado      int      not null,
    cliente       int      not null,
    constraint venta_cliente_cliente_id_fk
        foreign key (cliente) references cliente (cliente_id)
            on update cascade,
    constraint venta_empleado_empleado_id_fk
        foreign key (empleado) references empleado (empleado_id)
            on update cascade
);


# ------------------------ creation of item_venta table ------------------------
create table item_venta
(
    item_venta_id int auto_increment
        primary key,
    cantidad      int    not null,
    precio_venta  double not null,
    producto      int    not null,
    venta         int    not null,
    constraint item_venta_producto_producto_id_fk
        foreign key (producto) references producto (producto_id)
            on update cascade,
    constraint item_venta_venta_venta_id_fk
        foreign key (venta) references venta (venta_id)
            on update cascade
);


# ======================== parse lote and add existencias to producto ========================
create trigger lote_existencias_producto_trg
    after insert
    on epic.lote
    for each row
begin
    update producto
    set existencias = existencias + new.cantidad
    where producto_id = new.producto;
end;

# ======================== parse item_venta and subtract existencias to producto ========================
create trigger item_venta_existencias_producto_trg
    after insert
    on epic.item_venta
    for each row
begin
    update producto
    set existencias = existencias - new.cantidad
    where producto_id = new.producto;
end;
