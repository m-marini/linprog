
noProducts = 12;
noSuppliers = 6;

# Supplier indexes
supplier_campo = 1;
supplier_gallina = 2;
supplier_maiale = 3;
supplier_mangimificio = 4;
supplier_mucca = 5;
supplier_pecora = 6;

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

# Supplier by product
S = zeros(noProducts, 1);
S(product_carote) = supplier_campo;
S(product_grano) = supplier_campo;
S(product_lana) = supplier_pecora;
S(product_latte) = supplier_mucca;
S(product_mais) = supplier_campo;
S(product_mangime_galline) = supplier_mangimificio;
S(product_mangime_maiale) = supplier_mangimificio;
S(product_mangime_mucca) = supplier_mangimificio;
S(product_mangime_pecora) = supplier_mangimificio;
S(product_pancetta) = supplier_maiale;
S(product_soia) = supplier_campo;
S(product_uova) = supplier_gallina;

# No of suppliers
N = zeros(noSuppliers, 1);
N(supplier_campo) = 10.0;
N(supplier_gallina) = 18.0;
N(supplier_maiale) = 10.0;
N(supplier_mangimificio) = 2.0;
N(supplier_mucca) = 15.0;
N(supplier_pecora) = 10.0;

# Quantity of products by supplier
Q = zeros(noProducts, 1);
Q(product_carote) = 2.0;
Q(product_grano) = 2.0;
Q(product_lana) = 1.0;
Q(product_latte) = 1.0;
Q(product_mais) = 2.0;
Q(product_mangime_galline) = 3.0;
Q(product_mangime_maiale) = 3.0;
Q(product_mangime_mucca) = 3.0;
Q(product_mangime_pecora) = 3.0;
Q(product_pancetta) = 1.0;
Q(product_soia) = 2.0;
Q(product_uova) = 1.0;

# Value of products
V = zeros(noProducts, 1);
V(product_carote) = 7.2;
V(product_grano) = 3.6;
V(product_lana) = 54.0;
V(product_latte) = 32.4;
V(product_mais) = 7.2;
V(product_mangime_galline) = 7.2;
V(product_mangime_maiale) = 0.0;
V(product_mangime_mucca) = 0.0;
V(product_mangime_pecora) = 0.0;
V(product_pancetta) = 50.4;
V(product_soia) = 10.8;
V(product_uova) = 18.0;

# Interval for product by supplier
T = zeros(noProducts, 1);
T(product_carote) = 600;
T(product_grano) = 120;
T(product_lana) = 21600;
T(product_latte) = 3600;
T(product_mais) = 300;
T(product_mangime_galline) = 240;
T(product_mangime_maiale) = 1200;
T(product_mangime_mucca) = 600;
T(product_mangime_pecora) = 0;
T(product_pancetta) = 14400;
T(product_soia) = 1200;
T(product_uova) = 600;

# Consumptions of product by product
C = zeros(noProducts, noProducts);
C(product_carote, product_carote) = 1.0;
C(product_grano, product_grano) = 1.0;
C(product_lana, product_mangime_pecora) = 1.0;
C(product_latte, product_mangime_mucca) = 1.0;
C(product_mais, product_mais) = 1.0;
C(product_mangime_galline, product_grano) = 2.0;
C(product_mangime_galline, product_mais) = 1.0;
C(product_mangime_maiale, product_carote) = 1.0;
C(product_mangime_maiale, product_soia) = 0.0;
C(product_mangime_mucca, product_mais) = 1.0;
C(product_mangime_mucca, product_soia) = 2.0;
C(product_mangime_pecora, product_grano) = 3.0;
C(product_mangime_pecora, product_soia) = 1.0;
C(product_pancetta, product_mangime_maiale) = 1.0;
C(product_soia, product_soia) = 1.0;
C(product_uova, product_mangime_galline) = 1.0;
