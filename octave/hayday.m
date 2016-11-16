#
# A insieme dei produttori
#   1 - campo
#   2 - mangim1ficio
#   3 - gallina
#   4 - mucca
#   5 - maiale
#   6 - pecora
#
# B insieme dei prodotti
#   1 - grano
#   2 - mais
#   3 - soia
#   4 - carota
#   5 - mangime gallina
#   6 - mangime mucca
#   7 - mangime maiale
#   8 - mangime pecora
#   9 - uova
#   11 - latte
#   11 - pancetta
#   12 - lana
#

clear all;

nProduttori = 6;
nProdotti = 12;

nVar = nProdotti * nProduttori + nProduttori;

campi = 10;
mangimifici = 2;
galline = 6*3;
mucche = 5*3;
maiali = 5*2;
pecore = 5*2;

valoreGrano = 3.6;
valoreMais = 7.2;
valoreSoia = 10.8;
valoreCarota = 7.2;
valoreMangimeGallina = 7.2;
valoreMangimeMucca = 0;
valoreMangimeMaiale = 0;
valoreMangimePecora = 0;
valoreUovo = 18;
valoreLatte = 32.4;
valorePancetta = 50.4;
valoreLana = 54;

qtaGrano = 2;
qtaMais = 2;
qtaSoia = 2;
qtaCarote = 2;
qtaMangimeGallina = 3;
qtaMangimeMucca= 4;
qtaMangimeMaiale= 3;
qtaMangimePecora= 3;
qtaUova = 1;
qtaLatte = 1;
qtaPancetta = 1;
qtaLana = 1;

tGrano          = 2 * 60;
tMais           = 5 * 60;
tSoia           = 20 * 60;
tCarote         = 10 * 60;
tMangimeGallina = 4 * 60;
tMangimeMucca   = 10 * 60;
tMangimeMaiale  = 10 * 60;
tMangimePecora  = 0;
tUova           = 10 * 60;
tLatte          = 60 * 60;
tPancetta       = 4 * 60 * 60;
tLana           = 6 * 60 * 60;

qtaGrano4Grano            = 1;
qtaMais4Mais              = 1;
qtaSoia4Soia              = 1;
qtaCarote4Carota          = 1;
qtaGrano4MangimeGallina   = 2;
qtaMais4MangimeGallina    = 1;
qtaSoia4MangimeMucca      = 1;
qtaMais4MangimeMucca      = 1;
qtaSoia4MangimeMaiale     = 0;
qtaCarote4MangimeMaiale   = 1;
qtaGrano4MangimePecora    = 3;
qtaSoia4MangimePecora     = 1;
qtaMangimeGalline4Uovo    = 1;
qtaMangimeMucca4Latte     = 1;
qtaMangimeMaiale4Pancetta = 1;
qtaMangimePecora4Lana     = 1;

# Coefficenti di produzione per prodotto
pGrano          = campi * qtaGrano;
pMais           = campi * qtaMais;
pSoia           = campi * qtaSoia;
pCarote         = campi * qtaCarote;
pMangimeGallina = mangimifici * qtaMangimeGallina;
pMangimeMucca   = mangimifici * qtaMangimeMucca;
pMangimeMaiale  = mangimifici * qtaMangimeMaiale;
pMangimePecora  = mangimifici * qtaMangimePecora;
pUova           = galline * qtaUova;
pLatte          = mucche * qtaLatte;
pPancetta       = maiali * qtaPancetta;
pLana           = pecore * qtaLana;

# Coefficienti di consumo per prodotto
eGrano4Grano            = campi * qtaGrano4Grano;
eMais4Mais              = campi * qtaMais4Mais;
eSoia4Soia              = campi * qtaSoia4Soia;
eCarote4Carota          = campi * qtaCarote4Carota;
eGrano4MangimeGallina   = mangimifici * qtaGrano4MangimeGallina;
eMais4MangimeGallina    = mangimifici * qtaMais4MangimeGallina;
eSoia4MangimeMucca      = mangimifici * qtaSoia4MangimeMucca;
eMais4MangimeMucca      = mangimifici * qtaMais4MangimeMucca;
eSoia4MangimeMaiale     = mangimifici * qtaSoia4MangimeMaiale;
eCarote4MangimeMaiale   = mangimifici * qtaCarote4MangimeMaiale;
eGrano4MangimePecora    = mangimifici * qtaGrano4MangimePecora;
eSoia4MangimePecora     = mangimifici * qtaSoia4MangimePecora;
eMangimeGalline4Uovo    = galline * qtaMangimeGalline4Uovo;
eMangimeMucca4Latte     = mucche * qtaMangimeMucca4Latte;
eMangimeMaiale4Pancetta = maiali * qtaMangimeMaiale4Pancetta;
eMangimePecora4Lana     = pecore * qtaMangimePecora4Lana;

# flusso di valore per prodotto e produttore
vGrano4Campo  = (pGrano - eGrano4Grano) * valoreGrano;
vMais4Campo   = (pMais - eMais4Mais) * valoreMais;
vSoia4Campo   = (pSoia - eSoia4Soia) * valoreSoia;
vCarote4Campo = (pCarote - eCarote4Carota) * valoreCarota;
vMangimeGallina4Mangimificio  = pMangimeGallina * valoreMangimeGallina
                              - eGrano4MangimeGallina * valoreGrano
                              - eMais4MangimeGallina * valoreMais;
vMangimeMucca4Mangimificio    = pMangimeMucca * valoreMangimeMucca
                              - eMais4MangimeMucca * valoreMais
                              - eSoia4MangimeMucca * valoreSoia;
vMagimeMaiale4Mangimificio    = pMangimeMaiale * valoreMangimeMaiale
                              - eCarote4MangimeMaiale * valoreCarota
                              - eSoia4MangimeMucca * valoreSoia;
vMagimePecora4Mangimificio    = pMangimePecora * valoreMangimePecora
                              - eGrano4MangimePecora * valoreGrano
                              - eSoia4MangimePecora * valoreSoia;
vUova4Gallina     = pUova * valoreUovo - eMangimeGalline4Uovo * valoreMangimeGallina;
vLatte4Mucca      = pLatte * valoreLatte - eMangimeMucca4Latte * valoreMangimeMucca;
vPancetta4Maiale  = pPancetta * valorePancetta - eMangimeMaiale4Pancetta * valoreMangimeMaiale;
vLana4Pecora      = pLana * valoreLana - eMangimePecora4Lana * valoreMangimePecora;

C1 = zeros(nProdotti, nProduttori);
C1(1, 1) = vGrano4Campo;
C1(2, 1) = vMais4Campo;
C1(3, 1) = vSoia4Campo;
C1(4, 1) = vCarote4Campo;
C1(5, 2) = vMangimeGallina4Mangimificio;
C1(6, 2) = vMangimeMucca4Mangimificio;
C1(7, 2) = vMagimeMaiale4Mangimificio;
C1(8, 2) = vMagimePecora4Mangimificio;
C1(9, 3) = vUova4Gallina;
C1(10, 4) = vLatte4Mucca;
C1(11, 5) = vPancetta4Maiale;
C1(12, 6) = vLana4Pecora;


# coefficienti di tempo per produttore e prodotto,inattività
zCampo = zeros(nProduttori, nProdotti);
zCampo(1, 1) = tGrano;
zCampo(1, 2) = tMais;
zCampo(1, 3) = tSoia;
zCampo(1, 4) = tCarote;

zMangimificio = zeros(nProduttori, nProdotti);
zMangimificio(2, 5) = tMangimeGallina;
zMangimificio(2, 6) = tMangimeMucca;
zMangimificio(2, 7) = tMangimeMaiale;
zMangimificio(2, 8) = tMangimePecora;

zGallina = zeros(nProduttori, nProdotti);
zGallina(3, 9) = tUova;

zMucca = zeros(nProduttori, nProdotti);
zMucca(4, 10) = tLatte;

zMaiale = zeros(nProduttori, nProdotti);
zMaiale(5, 11) = tPancetta;

zPecora = zeros(nProduttori, nProdotti);
zPecora(6, 12) = tLana;

AZ = zeros(nProduttori, nVar);
AZ(1, 1 : nProdotti * nProduttori) = zCampo(:)';
AZ(2, 1 : nProdotti * nProduttori) = zMangimificio(:)';
AZ(3, 1 : nProdotti * nProduttori) = zGallina(:)';
AZ(4, 1 : nProdotti * nProduttori) = zMucca(:)';
AZ(5, 1 : nProdotti * nProduttori) = zMaiale(:)';
AZ(6, 1 : nProdotti * nProduttori) = zPecora(:)';
AZ(:, nProdotti * nProduttori + 1:end) = eye(nProduttori);

BZ = ones(nProduttori, 1);

AY = zeros(nProduttori * nProdotti, nVar);
AY(:, 1 : nProduttori * nProdotti) = eye(nProduttori * nProdotti, nProduttori * nProdotti);
BY = ones(nProduttori * nProdotti, 1);

A = [AZ; AY];
B = [BZ; BY];

C = -[C1'(:); zeros(nProduttori, 1)];
LB = zeros(nVar, 1);

CTYPE = blanks(size(A, 1));
CTYPE(1:nProduttori) = "S";
CTYPE(nProduttori+1:end) = "U";

X = glpk(C, A, B, LB, [] ,CTYPE);

Y = reshape(X(1:nProdotti*nProduttori), nProduttori, nProdotti);
Z = X(nProdotti*nProduttori+1 : end);