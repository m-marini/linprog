clear all;

hayday1;

Theta = zeros(noSuppliers, noProducts);
for i = 1 : noProducts
  Theta(s(i), i) = 1;
endfor

Q = diag(q);
N = diag(n(s));
T = diag(t);

# Flusso effettivo di produzione
F = zeros(noProducts, noProducts);
F = (Q - D') * N;

# Flusso dei profitti
g = zeros(noProducts, 1);
g = N' * (Q' - D) * v;

# Tempo totale
U = zeros(noSuppliers, noProducts);
U = Theta * T;

noVars = noProducts + noSuppliers;
c = zeros(noVars, 1);
c = [-g; zeros(noSuppliers, 1)];

noConstraints = noSuppliers + noProducts;
A = zeros(noConstraints, noVars);
# Vincolo sul tempo di produzione dei produttori
A(1 : noSuppliers, :) = [U eye(noSuppliers)];
# Vincolo sul flusso di produzione reale dei prodotti
A(noSuppliers + 1 : noSuppliers + noProducts, :) = [F zeros(noProducts, noSuppliers)];

b = [ ones(noSuppliers, 1); zeros(noProducts, 1)];

ctype = blanks(noConstraints);
ctype(1 : noSuppliers) = "S";
ctype(noSuppliers + 1 : noSuppliers + noProducts) = "L";

lb = zeros(noVars, 1);
ub = [];

x = glpk(c , A , b , lb , ub , ctype);

w = x(1 : noProducts);
z = x(noProducts + 1 : end);

W = Theta .* w';

# P prodotti (distribuzione prodotti)
PP = W ./ (sum(W, 2) + z);

# P temporale (distribuzione produttori)
NS = U .* w' * N;

# Profitto per ora
Profit = -c' * x * 3600;

# Vendite per ora
Selling = F * w * 3600;

printf("#### Vendite orarie ####\n");
productNames(find(Selling > 0.1))
Selling(find(Selling > 0.1))


printf("#### numero di produttori fissi per prodotto ####\n");
# Numero di produttori per prodotto
np = N * T * w; 
# Numero fisso di produttori per prodotto
nf = floor(np);
# numero variabile di produttori per prodotto
r = np - nf;
r1 = max(ceil(Theta*r), ones(noSuppliers, 1));

p1 = diag(r) * Theta' * inv(diag(r1));