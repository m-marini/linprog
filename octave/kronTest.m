clear all;
A = [1 2;3,4];
B = A;
C = [3 1;1 4];
M = kron(transpose (B), A);
M1 = inverse(M);

CV = C(:);
XV = M1 * CV;
X = reshape(XV, 2, 2)

C1 = A*X*B
C