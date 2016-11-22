
noProducts = 22;
noSuppliers = 11;

# Supplier indexes
supplier_barbeque = 1;
supplier_campo = 2;
supplier_caseificio = 3;
supplier_forno_pasticci = 4;
supplier_gallina = 5;
supplier_maiale = 6;
supplier_mangimificio = 7;
supplier_mucca = 8;
supplier_panetteria = 9;
supplier_pecora = 10;
supplier_telaio = 11;

# Supplier names
supplierNames = {
"barbeque",
"campo",
"caseificio",
"forno_pasticci",
"gallina",
"maiale",
"mangimificio",
"mucca",
"panetteria",
"pecora",
"telaio"
};

# Product indexes
product_burro = 1;
product_carote = 2;
product_formaggio = 3;
product_grano = 4;
product_hamburger = 5;
product_lana = 6;
product_latte = 7;
product_maglione = 8;
product_mais = 9;
product_mangime_galline = 10;
product_mangime_maiale = 11;
product_mangime_mucca = 12;
product_mangime_pecora = 13;
product_pancetta = 14;
product_pancetta_uova = 15;
product_pane = 16;
product_pane_integrale = 17;
product_panna = 18;
product_pasticcio_carote = 19;
product_pasticcio_pancetta = 20;
product_soia = 21;
product_uova = 22;

# Product names
productNames = {
"burro",
"carote",
"formaggio",
"grano",
"hamburger",
"lana",
"latte",
"maglione",
"mais",
"mangime_galline",
"mangime_maiale",
"mangime_mucca",
"mangime_pecora",
"pancetta",
"pancetta_uova",
"pane",
"pane_integrale",
"panna",
"pasticcio_carote",
"pasticcio_pancetta",
"soia",
"uova"
};

# Supplier by product
s = zeros(noProducts, 1);
s(product_burro) = supplier_caseificio;
s(product_carote) = supplier_campo;
s(product_formaggio) = supplier_caseificio;
s(product_grano) = supplier_campo;
s(product_hamburger) = supplier_barbeque;
s(product_lana) = supplier_pecora;
s(product_latte) = supplier_mucca;
s(product_maglione) = supplier_telaio;
s(product_mais) = supplier_campo;
s(product_mangime_galline) = supplier_mangimificio;
s(product_mangime_maiale) = supplier_mangimificio;
s(product_mangime_mucca) = supplier_mangimificio;
s(product_mangime_pecora) = supplier_mangimificio;
s(product_pancetta) = supplier_maiale;
s(product_pancetta_uova) = supplier_barbeque;
s(product_pane) = supplier_panetteria;
s(product_pane_integrale) = supplier_panetteria;
s(product_panna) = supplier_caseificio;
s(product_pasticcio_carote) = supplier_forno_pasticci;
s(product_pasticcio_pancetta) = supplier_forno_pasticci;
s(product_soia) = supplier_campo;
s(product_uova) = supplier_gallina;

# No of suppliers
n = zeros(noSuppliers, 1);
n(supplier_barbeque) = 1.0;
n(supplier_campo) = 57.0;
n(supplier_caseificio) = 1.0;
n(supplier_forno_pasticci) = 1.0;
n(supplier_gallina) = 18.0;
n(supplier_maiale) = 15.0;
n(supplier_mangimificio) = 2.0;
n(supplier_mucca) = 15.0;
n(supplier_panetteria) = 1.0;
n(supplier_pecora) = 10.0;
n(supplier_telaio) = 1.0;

# Quantity of products by supplier
q = zeros(noProducts, 1);
q(product_burro) = 1.0;
q(product_carote) = 2.0;
q(product_formaggio) = 1.0;
q(product_grano) = 2.0;
q(product_hamburger) = 1.0;
q(product_lana) = 1.0;
q(product_latte) = 1.0;
q(product_maglione) = 1.0;
q(product_mais) = 2.0;
q(product_mangime_galline) = 3.0;
q(product_mangime_maiale) = 3.0;
q(product_mangime_mucca) = 3.0;
q(product_mangime_pecora) = 3.0;
q(product_pancetta) = 1.0;
q(product_pancetta_uova) = 1.0;
q(product_pane) = 1.0;
q(product_pane_integrale) = 1.0;
q(product_panna) = 1.0;
q(product_pasticcio_carote) = 1.0;
q(product_pasticcio_pancetta) = 1.0;
q(product_soia) = 2.0;
q(product_uova) = 1.0;

# Value of products
v = zeros(noProducts, 1);
v(product_burro) = 82.0;
v(product_carote) = 7.2;
v(product_formaggio) = 122.0;
v(product_grano) = 3.6;
v(product_hamburger) = 169.0;
v(product_lana) = 54.0;
v(product_latte) = 32.4;
v(product_maglione) = 151.0;
v(product_mais) = 7.2;
v(product_mangime_galline) = 7.2;
v(product_mangime_maiale) = 14.4;
v(product_mangime_mucca) = 14.4;
v(product_mangime_pecora) = 14.4;
v(product_pancetta) = 50.4;
v(product_pancetta_uova) = 201.0;
v(product_pane) = 21.0;
v(product_pane_integrale) = 72.0;
v(product_panna) = 50.0;
v(product_pasticcio_carote) = 82.0;
v(product_pasticcio_pancetta) = 219.0;
v(product_soia) = 10.8;
v(product_uova) = 18.0;

# Interval for product by supplier
t = zeros(noProducts, 1);
t(product_burro) = 1800;
t(product_carote) = 600;
t(product_formaggio) = 3600;
t(product_grano) = 120;
t(product_hamburger) = 7200;
t(product_lana) = 21600;
t(product_latte) = 3600;
t(product_maglione) = 7200;
t(product_mais) = 300;
t(product_mangime_galline) = 240;
t(product_mangime_maiale) = 1200;
t(product_mangime_mucca) = 600;
t(product_mangime_pecora) = 1800;
t(product_pancetta) = 14400;
t(product_pancetta_uova) = 3600;
t(product_pane) = 240;
t(product_pane_integrale) = 1800;
t(product_panna) = 1200;
t(product_pasticcio_carote) = 3600;
t(product_pasticcio_pancetta) = 10800;
t(product_soia) = 1200;
t(product_uova) = 600;

# Consumptions of product by product
D = zeros(noProducts, noProducts);
D(product_burro, product_latte) = 2.0;
D(product_carote, product_carote) = 1.0;
D(product_formaggio, product_latte) = 3.0;
D(product_grano, product_grano) = 1.0;
D(product_hamburger, product_pancetta) = 2.0;
D(product_hamburger, product_pane) = 2.0;
D(product_lana, product_mangime_pecora) = 1.0;
D(product_latte, product_mangime_mucca) = 1.0;
D(product_maglione, product_lana) = 2.0;
D(product_mais, product_mais) = 1.0;
D(product_mangime_galline, product_grano) = 2.0;
D(product_mangime_galline, product_mais) = 1.0;
D(product_mangime_maiale, product_carote) = 2.0;
D(product_mangime_maiale, product_soia) = 1.0;
D(product_mangime_mucca, product_mais) = 1.0;
D(product_mangime_mucca, product_soia) = 2.0;
D(product_mangime_pecora, product_grano) = 3.0;
D(product_mangime_pecora, product_soia) = 1.0;
D(product_pancetta, product_mangime_maiale) = 1.0;
D(product_pancetta_uova, product_pancetta) = 2.0;
D(product_pancetta_uova, product_uova) = 4.0;
D(product_pane, product_grano) = 3.0;
D(product_pane_integrale, product_grano) = 2.0;
D(product_pane_integrale, product_uova) = 2.0;
D(product_panna, product_latte) = 1.0;
D(product_pasticcio_carote, product_carote) = 3.0;
D(product_pasticcio_carote, product_grano) = 2.0;
D(product_pasticcio_carote, product_uova) = 1.0;
D(product_pasticcio_pancetta, product_grano) = 2.0;
D(product_pasticcio_pancetta, product_pancetta) = 3.0;
D(product_pasticcio_pancetta, product_uova) = 1.0;
D(product_soia, product_soia) = 1.0;
D(product_uova, product_mangime_galline) = 1.0;
