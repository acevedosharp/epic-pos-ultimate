# create the database user
create user mercamas@localhost identified by 'fADw0CHKJqpDinkW';

# create & use the app schema
create schema app;
use app;

# ------------------------ creation of familia table ------------------------
create table familia
(
    familia_id int auto_increment,
    nombre     varchar(30) not null,
    constraint familia_pk
        primary key (familia_id)
);
create unique index familia_nombre_uindex
    on familia (nombre);


# ------------------------ creation of producto table ------------------------
create table producto
(
    producto_id  int auto_increment,
    codigo       varchar(20) not null,
    desc_larga   varchar(50) not null,
    desc_corta   varchar(25) not null,
    precio_venta int         not null,
    existencias  int         not null,
    familia      int         null,
    constraint producto_pk
        primary key (producto_id),
    constraint producto_familia_familia_id_fk
        foreign key (familia) references familia (familia_id)
            on update cascade
);
create unique index producto_codigo_uindex
    on producto (codigo);
create unique index producto_desc_corta_uindex
    on producto (desc_corta);
create unique index producto_desc_larga_uindex
    on producto (desc_larga);


# ------------------------ creation of proveedor table ------------------------
create table proveedor
(
    proveedor_id int auto_increment,
    nombre       varchar(50)  not null,
    telefono     varchar(20)  not null,
    correo       varchar(40)  null,
    direccion    varchar(100) null,
    constraint proveedor_pk
        primary key (proveedor_id)
);
create unique index proveedor_correo_uindex
    on proveedor (correo);
create unique index proveedor_nombre_uindex
    on proveedor (nombre);
create unique index proveedor_telefono_uindex
    on proveedor (telefono);


# ------------------------ creation of empleado table ------------------------
create table empleado
(
    empleado_id int auto_increment,
    nombre      varchar(50) not null,
    telefono    varchar(20) not null,
    activo      bool        not null default true,
    constraint empleado_pk
        primary key (empleado_id)
);
create unique index empleado_nombre_uindex
    on empleado (nombre);
create unique index empleado_telefono_uindex
    on empleado (telefono);


# ------------------------ creation of cliente table ------------------------
create table cliente
(
    cliente_id int auto_increment,
    nombre     varchar(50)  not null,
    telefono   varchar(20)  not null,
    direccion  varchar(100) null,
    constraint cliente_pk
        primary key (cliente_id)
);
create unique index cliente_nombre_uindex
    on cliente (nombre);
create unique index cliente_telefono_uindex
    on cliente (telefono);


# ------------------------ creation of pedido table ------------------------
create table pedido
(
    pedido_id  int auto_increment,
    fecha_hora datetime not null,
    proveedor  int      not null,
    empleado   int      not null,
    constraint pedido_pk
        primary key (pedido_id),
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
    lote_id       int auto_increment,
    cantidad      int not null,
    precio_compra int not null,
    producto      int not null,
    pedido        int not null,
    constraint lote_pk
        primary key (lote_id),
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
    venta_id   int auto_increment,
    fecha_hora datetime not null,
    empleado   int      not null,
    cliente    int      not null,
    constraint venta_pk
        primary key (venta_id),
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
    item_venta_id int auto_increment,
    cantidad      int not null,
    precio_venta  int not null,
    producto      int not null,
    venta         int not null,
    constraint item_venta_pk
        primary key (item_venta_id),
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
    on app.lote
    for each row
begin
    update producto
    set existencias = existencias + new.cantidad
    where producto_id = new.producto;
end;