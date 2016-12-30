/**
 * scripts/app.js
 * 
 * This is a sample CommonJS module. Take a look at http://browserify.org/ for
 * more info
 */

'use strict';

var $ = require('jquery');
var _ = require('lodash');
var hda = require('./hda-api.js');
//require('bootstrap');

var imgPath = './images/';

function MainApp() {
	$('#mainTab a').click(function(e) {
		e.preventDefault()
		$(this).tab('show')
	});
	console.log('MainApp initialized');
}

module.exports = MainApp;
