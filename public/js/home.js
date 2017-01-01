/**
 * 
 */
$(window)
		.load(
				function() {
					var alert = window.alert;
					var $ = window.$;
					var console = window.console;
					var hdaApi = window.hdaApi;

					// $('#signIn').on('click', signIn);
					$('#signInForm').submit(signIn);
					$('#newFarmer').on('click', showRegistration);
					$('#abortRegistration').on('click', abortRegistration);
					$('#register').on('click', createFarmer);
					$('#registrationPane').hide();
					$('#registrationTemplate').val('base');
					$('#registrationLevel').val('3');

					/* Sign in */
					function signIn() {
						var name = $('#email').val();

						// Validation
						if (!name) {
							hdaApi.alert('Missing email');
						} else if (!validateEmail(name)) {
							hdaApi.alert('Invalid email');
						} else {
							hdaApi.signIn(name, '').then(signOk);
						}
						return false;
					}

					/* Registration form */
					function showRegistration() {
						hdaApi.hideAlert();
						$('#welcomePane').hide();
						$('#registrationPane').show();
					}

					/* Registration form */
					function abortRegistration() {
						hdaApi.hideAlert();
						$('#registrationPane').hide();
						$('#welcomePane').show();
					}

					/* Creates a farmer */
					function createFarmer() {
						var errMsg = validateRegistration();
						if (errMsg) {
							hdaApi.alert(errMsg);
						} else {
							// Validates registration form
							var email = $('#registrationEmail').val();
							var psw = $('#registrationPsw').val();
							var template = $('#registrationTemplate').val();
							var level = $('#registrationLevel').val();
							hdaApi.register(email, psw, template, level).done(
									signOk);
						}

					}

					function validateEmail(email) {
						var re = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
						return re.test(email);
					}

					function validateRegistration() {
						var email = $('#registrationEmail').val();
						if (!email) {
							return "Missing email";
						}
						if (!validateEmail(email)) {
							return "Invalid email";
						}

						var psw = $('#registrationPsw').val();
						if (!psw) {
							return "Missing Password";
						}
						var confPsw = $('#registrationConfirmPsw').val();
						if (psw != confPsw) {
							return "Passwords does not match";
						}

						var template = $('#registrationTemplate').val();
						if (!template) {
							return "Missing template";
						}

						var level = $('#registrationLevel').val();
						if (!level) {
							return "Missing level";
						}

						return "";
					}

					/* Sign in confirmed */
					function signOk(data) {
						console.info(data);
						hdaApi.hideAlert();
						window.location.href = 'hda-main.html?id=' + data.id;
					}

				});