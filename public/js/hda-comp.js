/**
 * 
 */
window.builder = function(window) {

	var builder = {
		Field : Field,
		createFieldHtml : createFieldHtml,
		validateEmail : validateEmail,
		validateUnsigned : validateUnsigned,
		validateInt : validateInt,
		validateDecimal : validateDecimal,
		validateMandatory : validateMandatory
	};

	return builder;

	function Field(ref) {
		this.ref = ref;
		this.value = function(val) {
			if (val) {
				$(ref + ' input').val(val);
				return this;
			} else {
				return $(ref + ' input').val();
			}
		};
		this.validator = function() {
			return null;
		};
		this.errorMessage = function() {
			return this.validator(this.value());
		};
		this.validate = function(validator) {
			if (validator) {
				this.validator = validator;
				return this;
			} else {
				var err = this.errorMessage();
				if (err) {
					$(ref + ' span').html(err);
					return false;
				} else {
					$(ref + ' span').html('');
					return true;
				}
			}
		};
	}

	function createFieldHtml(id, value, placeHolder) {
		return '<div id="'
				+ id
				+ '"class="input-group"><input type="text" class="form-control" placeholder="'
				+ placeHolder + '" aria-describedby="' + id + '-addon" value="'
				+ value + '"><span class="input-group-addon" id="' + id
				+ '-addon"></span></div>';
	}

	function validateEmail(email) {
		var re = /^(([^<>()\[\]\.,;:\s@\"]+(\.[^<>()\[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
		if (re.test(email)) {
			return null;
		} else {
			return "Invalid email";
		}
	}
	function validateUnsigned(value) {
		var re = /^(\d+)$/i;
		if (re.test(value)) {
			return null;
		} else {
			return "Invalid unsigned number";
		}
	}
	function validateInt(value) {
		var re = /^([+-]?\d+)$/i;
		if (re.test(value)) {
			return null;
		} else {
			return "Invalid number";
		}
	}
	function validateDecimal(value) {
		var re = /^([+-]?\d+)(.\d+)?$/i;
		if (re.test(value)) {
			return null;
		} else {
			return "Invalid decimal number";
		}
	}
	function validateMandatory(value) {
		if (value != '') {
			return null;
		} else {
			return "Type a value";
		}
	}
}(window);
