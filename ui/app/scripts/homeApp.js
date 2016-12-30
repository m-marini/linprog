/**
 * scripts/app.js
 * 
 * This is a sample CommonJS module. Take a look at http://browserify.org/ for
 * more info
 */

'use strict';

var $ = require('jquery');
var hda = require('./hda-api.js');

function HomeApp() {
	console.log('HomeApp initialized');
	$('#signForm').submit(signIn);
	$('#newFarmer').on('click', showRegistration);
	$('#abortRegistration').on('click', abortRegistration);
	$('#register').on('click', createFarmer);
	$('#registrationPane').hide();
	$('#registrationTemplate').val('base');
	$('#registrationLevel').val('3');

	/* Sign in */
	function signIn(event) {
		var name = $('#email').val();
		// Validation
		if (!name) {
			hda.alert('Missing email');
		} else if (!validateEmail(name)) {
			hda.alert('Invalid email');
		} else {
			hda.signIn(name, '').then(signOk);
		}
		return false;
	}

	/* Registration form */
	function showRegistration() {
		$('#welcomePane').hide();
		$('#registrationPane').show();
	}

	/* Registration form */
	function abortRegistration() {
		$('#registrationPane').hide();
		$('#welcomePane').show();
	}

	/* Creates a farmer */
	function createFarmer() {
		var errMsg = validateRegistration();
		if (errMsg) {
			hda.alert(errMsg);
		} else {
			// Validates registration form
			var email = $('#registrationEmail').val();
			var psw = $('#registrationPsw').val();
			var template = $('#registrationTemplate').val();
			var level = $('#registrationLevel').val();
			hda.register(email, psw, template, level).done(signOk);
		}
	}

	/* Sign in confirmed */
	function signOk(data) {
		console.info(data);
		hda.hideAlert();
		window.location.href = 'hda-main.html?id=' + data.id;
	}

	function validateEmail(email) {
		var re = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
		return re.test(email);
	}

	function validateRegistration() {
		var email = $('#registrationEmail').val();
		if (!email) {
			return 'Missing email';
		}
		if (!validateEmail(email)) {
			return 'Invalid email';
		}

		var psw = $('#registrationPsw').val();
		if (!psw) {
			return 'Missing Password';
		}
		var confPsw = $('#registrationConfirmPsw').val();
		if (psw != confPsw) {
			return 'Passwords does not match';
		}

		var template = $('#registrationTemplate').val();
		if (!template) {
			return 'Missing template';
		}

		var level = $('#registrationLevel').val();
		if (!level) {
			return 'Missing level';
		}

		return '';
	}
}

module.exports = HomeApp;
