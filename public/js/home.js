/*
 * 
 */
window.HomeApp = function(window) {
	var $ = window.$;
	var _ = window._;
	var console = window.console;
	var hdaApi = window.hdaApi;
	var builder = window.builder;

	$('#signInForm').submit(signIn);
	$('#newFarmer').on('click', showRegistration);
	$('#abortRegistration').on('click', abortRegistration);
	$('#registrationForm').submit(createFarmer);
	$('#registrationPane').hide();

	/*
	 * Signs in
	 */
	function signIn() {
		var emailField = new builder.Field('#email')
				.validate(builder.validateEmail);
		var pswField = new builder.Field('#psw')
				.validate(builder.validateMandatory);
		var errors = _.filter([ emailField, pswField ], isInvalid);
		if (errors.length == 0) {
			hdaApi.signIn(emailField.value(), pswField.value()).then(signOk);
		}
		return false;
	}

	/*
	 * Shows registration form
	 */
	function showRegistration() {
		hdaApi.hideAlert();
		$('#welcomePane').hide();
		$('#registrationPane').show();
	}

	/*
	 * Aborts registration form
	 */
	function abortRegistration() {
		hdaApi.hideAlert();
		$('#registrationPane').hide();
		$('#welcomePane').show();
	}

	/*
	 * Creates a farmer
	 */
	function createFarmer() {
		if (validateRegistration()) {
			// Validates registration form
			var email = new builder.Field('#registrationEmail').value();
			var psw = new builder.Field('#registrationPsw').value();
			var template = $('#registrationTemplate').val();
			var level = $('#registrationLevel').val();
			hdaApi.register(email, psw, template, level).done(signOk);

		}
		return false;
	}

	/*
	 * Validates registration form
	 */
	function validateRegistration() {
		var emailField = new builder.Field('#registrationEmail')
				.validate(builder.validateEmail);
		var pswField = new builder.Field('#registrationPsw')
				.validate(builder.validateMandatory);
		var pswConfField = new builder.Field('#registrationConfirmPsw')
				.validate(validatePsw);
		var errors = _
				.filter([ emailField, pswField, pswConfField ], isInvalid);
		return errors.length == 0;

		function validatePsw(value) {
			if (!value) {
				return 'Type a value';
			} else if (value != pswField.value()) {
				return 'Passwords does not match';
			} else {
				return null;
			}

		}
	}

	/*
	 * Checks if field is invalid
	 */
	function isInvalid(field) {
		return !field.validate();
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
	window.HomeApp(window);
});