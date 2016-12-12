/**
 * 
 */
$(window)
		.load(
				function() {
					var alert = window.alert;
					var $ = window.$;
					var console = window.console;
					$(document).ajaxStart(renderBlockUI).ajaxStop($.unblockUI);
					var msg = "...";
					hideAlert();
					var supplierOrder = [ 'campo', 'mangimificio', 'gallina',
							'mucca', 'maiale', 'panetteria', 'caseificio',
							'zuccherificio', 'stufa_popcorn', 'barbeque',
							'forno_pasticci', 'melo' ];
					var productOrder = [ 'grano', 'mais', 'soia',
							'canna_zucchero', 'carote', 'indaco', 'zucca',
							'mangime_galline', 'mangime_mucca',
							'mangime_maiale', 'uova', 'pane', 'pane_integrale',
							'biscotto', 'latte', 'panna', 'burro', 'formaggio',
							'zucchero_canna', 'zucchero', 'pancake',
							'pancetta_uova', 'popcorn', 'pancetta',
							'pasticcio_carote', 'pasticcio_zucca', 'mela'

					];
					var productNameMap = {
						grano : 'Grano',
						mais : 'Mais',
						soia : 'Soia',
						carote : 'Carote',
						mangime_galline : 'Mangime per galline',
						mangime_mucca : 'Mangime per mucche',
						mangime_maiale : 'Mangime per maiali',
						mangime_pecora : 'Mangime per pecore',
						uova : 'Uova',
						latte : 'Latte',
						pancetta : 'Pancetta',
						lana : 'Lana',
						pane_integrale : 'Pane integrale',
						pane : 'Pane',
						panna : 'Panna',
						burro : 'Burro',
						formaggio : 'Formaggio',
						pancetta_uova : 'Pancetta e uova',
						hamburger : 'Hamburger',
						maglione : 'Maglione',
						pasticcio_carote : 'Pasticcio di carote',
						pasticcio_pancetta : 'Pasticcio di pancetta',
						canna_zucchero : 'Canna da zucchero',
						zucchero_canna : 'Zucchero di canna',
						zucchero : 'Zucchero',
						zucca : 'Zucca',
						pasticcio_zucca : 'Torta di zucca',
						indaco : 'Indaco',
						biscotto : 'Biscotto',
						popcorn : 'Popcorn',
						pancake : 'Pancake',
						mela : 'Mela'
					};
					var supplierNameMap = {
						pecora : 'Pecore',
						maiale : 'Maiali',
						barbeque : 'Barbeque',
						campo : 'Campi',
						mucca : 'Mucche',
						forno_pasticci : 'Forni per pasticci',
						caseificio : 'Caseifici',
						mangimificio : 'Mangimifici',
						panetteria : 'Panetterie',
						gallina : 'Galline',
						telaio : 'Telai',
						stufa_popcorn : 'Stufe per popcorn',
						melo : 'Meli',
						zuccherificio : 'Zuccherifici'
					};

					window.hdaApi = {
						signIn : signIn,
						send : send,
						alert : hdaAlert,
						hideAlert : hideAlert,
						getFarmer : getFarmer,
						productName : productName,
						supplierName : supplierName,
						getTemplate : getFarmerTemplate,
						getConfig : getConfig,
						productOrder : productOrder,
						supplierOrder : supplierOrder
					};

					function getConfig(id) {
						return send('/v1/farmers/' + id + '/suppliers');
					}

					/* Converts the supplier id to name */
					function supplierName(id) {
						var name = supplierNameMap[id];
						if (name) {
							return name
						} else {
							return id + '(?)';
						}
					}

					/* Converts the product id to name */
					function productName(id) {
						var name = productNameMap[id];
						if (name) {
							return name
						} else {
							return id + '(?)';
						}
					}

					/* Hides alert panel */
					function hideAlert() {
						return $('#alertPane').hide();
					}

					/* Shows alert panel with a message */
					function hdaAlert(msg) {
						return $('#alertPane').html(msg).show();
					}

					/*
					 * Sends a request and return a promise of response. Handles
					 * the errors.
					 */
					function send(url) {
						return $.getJSON(url).fail(handleError);
					}

					/* Handles http errors */
					function handleError(jqxhr, textStatus, error) {
						var msg = ' (HTTP ' + jqxhr.status + ') ' + error;
						console.error(msg);
						hdaAlert(msg);
					}

					/* Sign in the user */
					function signIn(user, password) {
						msg = 'Signing in ...';
						return getFarmerTemplate('base').pipe(store);

						function store(farmer) {
							farmer.name = user;
							return putFarmer(farmer);
						}
					}

					/* Returns the promise of farm template */
					function getFarmerTemplate(template) {
						return send('/v1/farmers/new?t=' + template);
					}

					/* Returns the promise of farm */
					function getFarmer(id) {
						return send('/v1/farmers/' + id);
					}

					/*
					 * Puts a farmer in the repository. Returns the promise of
					 * put farmer
					 */
					function putFarmer(farmer) {
						return $.ajax({
							type : 'PUT',
							url : '/v1/farmers/' + farmer.id,
							contentType : 'application/json; charset=UTF-8',
							data : JSON.stringify(farmer)
						}).fail(handleError);
					}

					/* Renders blockui */
					function renderBlockUI() {
						var waitMsg = '<div class="progress"><div class="progress progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 100%">'
								+ msg + '</div></div>';
						$.blockUI({
							message : waitMsg,
							showOverlay : false,
							fadeOut : 1,
							fadeIn : 0
						});
					}

				});
