/**
 * 
 */
$(window)
		.load(
				function() {
					var v = {};
					v.isEmail = function(email) {
						var re = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
						return re.test(email);
					}
					v.isUnsigned = function(value) {
						var re = /^(\d+)$/i;
						return re.test(value);
					}
					v.isInt = function(value) {
						var re = /^([+-]?\d+)$/i;
						return re.test(value);
					}
					v.isDecimal = function(value) {
						var re = /^([+-]?\d+)(.\d+)?$/i;
						return re.test(value);
					}

					window.validator = v;
				});
