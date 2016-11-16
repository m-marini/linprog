clear all;

hayday1;

# Flusso di ricavi per produttore, prodotto
REVENUES = zeros(noSuppliers, noProducts);
for i = 1 : noProducts
  j = S(i); 
  REVENUES(j, i) = N(j) * Q(i) * V(i);
endfor

# Flusso di spese per produttore, prodotto
EXPENSES = zeros(noSuppliers, noProducts);
for i = 1 : noProducts
  j = S(i);
  EXPENSES(j, i) = N(j) * C(i, :) * V;
endfor

LOSS = EXPENSES - REVENUES;

c = [LOSS(:); zeros(noSuppliers, 1)];

noWeights = noSuppliers * noProducts;
noVars = noWeights + noSuppliers;
noConstraints = noSuppliers + noWeights + 1;

Z0 = ones(1, noSuppliers);
A = zeros(noConstraints, noVars);
for i = 1 : noSuppliers
  Z = zeros(noSuppliers, noProducts);
  for j = 1: noProducts
    if  i == S(j)
      Z(i, j) = T(j);
    endif
  endfor
  A(i, :) = [Z(:)' Z0];
endfor

A(noSuppliers + 1 : end - 1, :) = [eye(noWeights) zeros(noWeights, noSuppliers)];

A(end,:)  = [LOSS(:)' zeros(1, noSuppliers)];

b = ones(noConstraints, 1);
b(end) = 0;

ctype = blanks(noConstraints);
ctype(1 : noSuppliers) = "S";
ctype(noSuppliers + 1 : end) = "U";

lb = zeros(noVars, 1);
ub = [];

X = glpk(c , A , b , lb , ub , ctype);

Y = reshape(X(1 : noWeights), noSuppliers, noProducts);
Z0 = X(noWeights + 1 : end);