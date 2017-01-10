/**
 * 
 */
window.hdaApi = function(window) {
	var alert = window.alert;
	var $ = window.$;
	var console = window.console;
	$(document).ajaxStart(renderBlockUI).ajaxStop($.unblockUI);
	var msg = "...";
	hideAlert();
	var supplierOrder = [ 'campo', 'mangimificio', 'gallina', 'mucca',
			'maiale', 'panetteria', 'caseificio', 'zuccherificio',
			'stufa_popcorn', 'barbeque', 'forno_pasticci', 'melo' ];
	var productOrder = [ 'grano', 'mais', 'soia', 'canna_zucchero', 'carote',
			'indaco', 'zucca', 'mangime_galline', 'mangime_mucca',
			'mangime_maiale', 'uova', 'pane', 'pane_integrale', 'biscotto',
			'latte', 'panna', 'burro', 'formaggio', 'zucchero_canna',
			'zucchero', 'pancake', 'pancetta_uova', 'popcorn', 'pancetta',
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

	return {
		alert : hdaAlert,
		createFarmer : createFarmer,
		deleteToken : deleteToken,
		getConfig : getConfig,
		getFarmer : getFarmer,
		getFarmerName : getFarmerName,
		hideAlert : hideAlert,
		productName : productName,
		productOrder : productOrder,
		putFarmer : putFarmer,
		send : send,
		supplierName : supplierName,
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
	 * Sends a request and return a promise of response. Handles the errors.
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

	function getFarmerName(id) {
		return send('/v1/farmers/' + id + "/name");
	}

	/* Sign in the user */
	function register(template, level) {
		msg = 'Registering ...';
		return getFarmerByName(user).pipe(checkDuplicate).pipe(createTemplate)
				.pipe(store);

		function checkDuplicate(list) {
			if (list.length == 0) {
				return list;
			} else {
				var msg = 'User ' + user + ' already registrered';
				console.error(msg);
				hdaAlert(msg);
				return jQuery.Deferred().reject(msg);
			}
		}

		function createTemplate() {
			return getFarmerTemplate(template, level);
		}

		function store(farmer) {
			farmer.name = user;
			return putFarmer(farmer);
		}
	}

	/* Sign in the user */
	function createFarmer(id, template, level) {
		msg = 'Createing farmer ...';
		return getFarmerByTemplate(id, template, level).pipe(store);

		function store(farmer) {
			return putFarmer(farmer);
		}
	}

	/* Returns the promise of farm template */
	function getFarmerByTemplate(id, template, level) {
		return send('/v1/farmers/' + id + '/new?t=' + template + '&l=' + level);
	}

	/* Returns the promise of farm */
	function getFarmer(id) {
		return send('/v1/farmers/' + id);
	}

	/* Returns the promise of farm */
	function logoff(id) {
		return send('/v1/farmers/' + id + '/logoff');
	}

	/*
	 * Puts a farmer in the repository. Returns the promise of put farmer
	 */
	function putFarmer(farmer) {
		return $.ajax({
			type : 'PUT',
			url : '/v1/farmers/' + farmer.id,
			contentType : 'application/json; charset=UTF-8',
			data : JSON.stringify(farmer)
		}).fail(handleError);
	}

	/*
	 * Puts a farmer in the repository. Returns the promise of put farmer
	 */
	function deleteToken(id) {
		return $.ajax({
			type : 'DELETE',
			url : '/v1/farmers/' + id + '/token'
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

}(window);
