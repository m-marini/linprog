/**
 * 
 */
$(window).load(function() {
	var alert = window.alert;
	var $ = window.$;
	var console = window.console;
	var hdaApi = window.hdaApi;

	$('#signIn').on('click', signIn)
	$('#newFarmer').on('click', createFarmer)

	function signIn() {
		var name = $('#email').val();

		// Validation
		if (name) {
			hdaApi.signIn(name, '').done(signOk);
		} else {
			hdaApi.alert('Missing email');
		}
	}

	function createFarmer() {
		// Validation
		hdaApi.signIn('Default', '').done(signOk);
	}

	function signOk(data) {
		console.info(data);
		hdaApi.hideAlert();
		window.location.href = 'hda-main.html?id=' + data.id;
	}

});
