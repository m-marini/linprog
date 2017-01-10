/*
 * 
 */
window.RegisterApp = function(window) {
	var $ = window.$;
	var _ = window._;
	var console = window.console;
	var hdaApi = window.hdaApi;
	var builder = window.builder;

	var myArray = /.*?id=(.*)#/g.exec(window.location.href);
	var id = null;
	$('#exitForm').submit(logoff)
	$('#registrationForm').submit(toLogin)
	if (myArray) {
		id = myArray[1];
		hdaApi.getFarmerName(id).then(renderName);
	} else {
		hdaApi.alert('Missing id');
	}

	/*
	 * Creates a farmer
	 */
	function logoff() {
		hdaApi.deleteToken(id).done(logoffOk);
		return false;
	}


	/*
	 * Handles sign in confirmation
	 */
	function logoffOk() {
		hdaApi.hideAlert();
		window.location.href = 'index.html';
	}

	/*
	 * Creates a farmer
	 */
	function createFarmer() {
		var template = $('#registrationTemplate').val();
		var level = $('#registrationLevel').val();
		hdaApi.createFarmer(id, template, level).done(signOk);
		return false;
	}

	/*
	 * Creates a farmer
	 */
	function toLogin() {
		return false;
	}

	function renderName(name) {
		$('#userInfo').html('Signed in as ' + name);
		$('#registrationForm').submit(createFarmer)
		return name;
	}

	/*
	 * Handles sign in confirmation
	 */
	function signOk(data) {
		console.info(data);
		hdaApi.hideAlert();
		window.location.href = 'hda-main.html?id=' + data.id;
	}
}

$(window).load(function() {
	window.RegisterApp(window);
});