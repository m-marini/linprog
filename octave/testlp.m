#
# Esempio:
#
# La fabbrica produce due beni Y1 e Y2 consumando X
# Puo produrre Y1 ad una velocita P1 consumando Q1 beni X
# Puo produrre Y2 ad una velocita P2 consumando Q2 beni X
# 
# La produzione è distribuita in percentuale p e (1-p) tra i prodotti Y1 e Y2.
#
# Il valore dei prodotti è rispettivamente V1 e V2 e V0 per il prodotto X
#
# Il ricavo totale è quindi
# P = p P1 V1 + (1-p) P2 V2
# mentre la spesa è
# S = p Q1 V0 + (1-p) Q2 V0 = [p Q1 + (1-p) Q2] V0
# Il guadagno quindi è
# G = P - S
# 
# Problema: trovare il valore di p che massimizza G
#
# Normaliziamo il sistema:
#
# G(p) = (P1 V1 - P2 V2) p + P2 V2 - [(Q1 V0 - Q2 V0) p + Q2 V0]
#      = [P1 V1 - P2 V2 - (Q2-Q1) V0] p + P2 V2 - Q2 V0
#
# P2 V2 - Q2 V0 è una costante quindi non ha influenza
#
# max [P1 V1 - P2 V2 - (Q2-Q1) V0] p = min -[P1 V1 - P1 V2 - (Q2-Q1) V0] p
# 
# per quanto riguarda i vincoli abbiamo che
# p >= 0
# p <= 1
#
# quindi
#
# C = [P2 V2 - P1 V1 + (Q2-Q1) V0]
# A = [1]
# B = [1]
# LB = [0]
# UB = []
# CTYPE = ["U"]
#

V0 = 1;
V1 = 20;
V2 = 10;

P1 = 3;
P2 = 1;

Q1 = 2;
Q2 = 1;

A = [1];
B = [1];
C = [P2 * V2 - P2 * V1 + (Q2-Q1) * V0];
CTYPE = ["U"];

X = glpk(C, A, B, LB, UB ,CTYPE)
