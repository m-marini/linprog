function Y = extend(A,B)
  Y1 = (A(:)*B(:)')(:);
  Y = reshape(Y1, [size(A), size(B)]);
endfunction