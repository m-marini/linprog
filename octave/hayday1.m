
noProducts = 12;
noSuppliers = 6;

# Supplier indexes
supplier_campo = 1;
supplier_gallina = 2;
supplier_maiale = 3;
supplier_mangimificio = 4;
supplier_mucca = 5;
supplier_pecora = 6;

# Supplier names
supplierNames = {
"campo",
"gallina",
"maiale",
"mangimificio",
"mucca",
"pecora"
};

# Product indexes
product_carote = 1;
product_grano = 2;
product_lana = 3;
product_latte = 4;
product_mais = 5;
product_mangime_galline = 6;
product_mangime_maiale = 7;
product_mangime_mucca = 8;
product_mangime_pecora = 9;
product_pancetta = 10;
product_soia = 11;
product_uova = 12;

# Product names
productNames = {
"carote",
"grano",
"lana",
"latte",
"mais",
"mangime_galline",
"mangime_maiale",
"mangime_mucca",
"mangime_pecora",
"pancetta",
"soia",
"uova"
};

# Supplier by product
s = zeros(noProducts, 1);
s(product_carote) = supplier_campo;
s(product_grano) = supplier_campo;
s(product_lana) = supplier_pecora;
s(product_latte) = supplier_mucca;
s(product_mais) = supplier_campo;
s(product_mangime_galline) = supplier_mangimificio;
s(product_mangime_maiale) = supplier_mangimificio;
s(product_mangime_mucca) = supplier_mangimificio;
s(product_mangime_pecora) = supplier_mangimificio;
s(product_pancetta) = supplier_maiale;
s(product_soia) = supplier_campo;
s(product_uova) = supplier_gallina;

# No of suppliers
n = zeros(noSuppliers, 1);
n(supplier_campo) = 10.0;
n(supplier_gallina) = 18.0;
n(supplier_maiale) = 10.0;
n(supplier_mangimificio) = 2.0;
n(supplier_mucca) = 15.0;
n(supplier_pecora) = 10.0;

# Quantity of products by supplier
q = zeros(noProducts, 1);
q(product_carote) = 2.0;
q(product_grano) = 2.0;
q(product_lana) = 1.0;
q(product_latte) = 1.0;
q(product_mais) = 2.0;
q(product_mangime_galline) = 3.0;
q(product_mangime_maiale) = 3.0;
q(product_mangime_mucca) = 3.0;
q(product_mangime_pecora) = 3.0;
q(product_pancetta) = 1.0;
q(product_soia) = 2.0;
q(product_uova) = 1.0;

# Value of products
v = zeros(noProducts, 1);
v(product_carote) = 7.2;
v(product_grano) = 3.6;
v(product_lana) = 54.0;
v(product_latte) = 32.4;
v(product_mais) = 7.2;
v(product_mangime_galline) = 7.2;
v(product_mangime_maiale) = 0.0;
v(product_mangime_mucca) = 0.0;
v(product_mangime_pecora) = 0.0;
v(product_pancetta) = 50.4;
v(product_soia) = 10.8;
v(product_uova) = 18.0;

# Interval for product by supplier
t = zeros(noProducts, 1);
t(product_carote) = 600;
t(product_grano) = 120;
t(product_lana) = 21600;
t(product_latte) = 3600;
t(product_mais) = 300;
t(product_mangime_galline) = 240;
t(product_mangime_maiale) = 1200;
t(product_mangime_mucca) = 600;
t(product_mangime_pecora) = 0;
t(product_pancetta) = 14400;
t(product_soia) = 1200;
t(product_uova) = 600;

# Consumptions of product by product
D = zeros(noProducts, noProducts);
D(product_carote, product_carote) = 1.0;
D(product_grano, product_grano) = 1.0;
D(product_lana, product_mangime_pecora) = 1.0;
D(product_latte, product_mangime_mucca) = 1.0;
D(product_mais, product_mais) = 1.0;
D(product_mangime_galline, product_grano) = 2.0;
D(product_mangime_galline, product_mais) = 1.0;
D(product_mangime_maiale, product_carote) = 1.0;
D(product_mangime_maiale, product_soia) = 0.0;
D(product_mangime_mucca, product_mais) = 1.0;
D(product_mangime_mucca, product_soia) = 2.0;
D(product_mangime_pecora, product_grano) = 3.0;
D(product_mangime_pecora, product_soia) = 1.0;
D(product_pancetta, product_mangime_maiale) = 1.0;
D(product_soia, product_soia) = 1.0;
D(product_uova, product_mangime_galline) = 1.0;
