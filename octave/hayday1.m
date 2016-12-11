
noProducts = 4;
noSuppliers = 3;

# Supplier indexes
supplier_campo = 1;
supplier_gallina = 2;
supplier_mangimificio = 3;

# Supplier names
supplierNames = {
"campo",
"gallina",
"mangimificio"
};

# Product indexes
product_grano = 1;
product_mais = 2;
product_mangime_galline = 3;
product_uova = 4;

# Product names
productNames = {
"grano",
"mais",
"mangime_galline",
"uova"
};

# Supplier by product
s = zeros(noProducts, 1);
s(product_grano) = supplier_campo;
s(product_mais) = supplier_campo;
s(product_mangime_galline) = supplier_mangimificio;
s(product_uova) = supplier_gallina;

# No of suppliers
n = zeros(noSuppliers, 1);
n(supplier_campo) = 10.0;
n(supplier_gallina) = 6.0;
n(supplier_mangimificio) = 1.0;

# Quantity of products by supplier
q = zeros(noProducts, 1);
q(product_grano) = 2.0;
q(product_mais) = 2.0;
q(product_mangime_galline) = 3.0;
q(product_uova) = 1.0;

# Value of products
v = zeros(noProducts, 1);
v(product_grano) = 3.6;
v(product_mais) = 7.2;
v(product_mangime_galline) = 7.2;
v(product_uova) = 18.0;

# Interval for product by supplier
t = zeros(noProducts, 1);
t(product_grano) = 120;
t(product_mais) = 300;
t(product_mangime_galline) = 240;
t(product_uova) = 600;

# Consumptions of product by product
D = zeros(noProducts, noProducts);
D(product_grano, product_grano) = 1.0;
D(product_mais, product_mais) = 1.0;
D(product_mangime_galline, product_grano) = 2.0;
D(product_mangime_galline, product_mais) = 1.0;
D(product_uova, product_mangime_galline) = 1.0;
