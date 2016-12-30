/**
 * scripts/app.js
 * 
 * This is a sample CommonJS module. Take a look at http://browserify.org/ for
 * more info
 */

'use strict';

require('bootstrap');
//var $ = require('jquery');
//var _ = require('lodash');
//var hda = require('./hda-api.js');

var imgPath = './images/';

function MainApp() {
//	$('#mainTab a').click(function(e) {
//		e.preventDefault()
//		$(this).tab('show')
//	});
	console.log('MainApp initialized');
	var myArray = /.*?id=(.*)/g.exec(window.location.href);
	var id = null;
	if (myArray) {
		id = myArray[1];
	} else {
		hda.alert('Missing id');
	}
	if (id) {
//		loadPage(id);
	}

	function loadPage(id) {

		hda.getFarmer(id).then(rendemainApp.jsrPage);
//		hda.getConfig(id).then(renderConfig);

		function renderPage(data) {
			$('#userInfo').html('Signed in as ' + data.name);
			renderMarket(data);
			renderConf(data);
			return data;
		}

		function renderConfig(data) {
			renderProduction(data);
			renderForecasts(data);
			return data;
		}
	}

	/* Clears table */
	function clearTable(name) {
		$('#' + name + ' tr').remove();
	}

	/* Renders production table */
	function renderProduction(data) {
		clearTable('prodConf');
		var rows = _(hda.productOrder).filter(productExists).map(toUIModel)
				.value();
		renderTableRows($('#prodConf'), rows, renderRow);

		function productExists(key) {
			return key in data.productions
					&& (data.productions[key].fixed + data.productions[key].random) > 0;
		}

		function toUIModel(key) {
			var v = data.productions[key];
			return {
				productName : hda.productName(v.name),
				productImage : imgPath + v.name + '.png',
				supplierName : hda.supplierName(v.supplierName),
				supplierImage : imgPath + v.supplierName + '.png',
				randomQty : v.random,
				fixedQty : v.fixed
			};
		}

		function renderRow(f) {
			var html = '<td>' + f.supplierName + '</td><td><img src="'
					+ f.supplierImage + '" alt="Image of ' + f.supplierName
					+ '" class="img-rounded hda-image"></td><td>'
					+ f.productName + '</td><td><img src="' + f.productImage
					+ '" alt="Image of ' + f.productName
					+ '" class="img-rounded hda-image"></td><td>' + f.fixedQty
					+ '</td><td>' + f.randomQty + '</td><td>'
					+ (f.fixedQty + f.randomQty) + '</td>';
			return html;
		}
	}

	/* Renders forecast table */
	function renderForecasts(data) {
		clearTable('forecasts');
		var rows = _(hda.productOrder).filter(productExists).map(toUIModel)
				.value();

		renderTableRows($('#forecasts'), rows, renderRow);

		function productExists(key) {
			return key in data.consumptions && data.consumptions[key] > 0;
		}

		function toUIModel(key) {
			return {
				name : hda.productName(key),
				image : imgPath + key + '.png',
				qty : data.consumptions[key]
			};
		}

		function renderRow(f) {
			var html = '<td>' + f.name + '</td><td><img src="' + f.image
					+ '" alt="Image of ' + f.name
					+ '" class="img-rounded hda-image"></td><td>' + f.qty
					+ '</td>';
			return html;
		}
	}

	/* Renders market table */
	function renderMarket(data) {
		clearTable('marketTable');
		var rows = _(hda.productOrder).filter(productExists).map(toRenderModel)
				.value();
		renderTableRows($('#marketTable'), rows, renderRow);

		function productExists(name) {
			var ex = name in data.values;
			return ex;
		}

		function toRenderModel(key) {
			var value = data.values[key];
			return {
				image : imgPath + key + '.png',
				name : hda.productName(key),
				value : value
			};
		}

		function renderRow(f) {
			var html = '<td>'
					+ f.name
					+ '</td><td><img src="'
					+ f.image
					+ '" alt="Image of '
					+ f.name
					+ '" class="img-rounded hda-image"></td><td><input type="text" class="form-control" placeholder="Price of '
					+ 'grano' + '" value="' + f.value + '"></td>';
			return html;
		}
	}

	/* Renders configuration table */
	function renderConf(data) {
		clearTable('confTable');
		var rows = _(hda.supplierOrder).filter(supplierExists).map(
				toRenderModel).value();
		renderTableRows($('#confTable'), rows, renderRow);

		function supplierExists(key) {
			return key in data.suppliers;
		}

		function toRenderModel(key) {
			var value = data.suppliers[key];
			return {
				image : imgPath + key + '.png',
				name : hda.supplierName(key),
				qty : value
			};
		}

		function renderRow(f) {
			var html = '<td>'
					+ f.name
					+ '</td><td><img src="'
					+ f.image
					+ '" alt="Image of '
					+ f.name
					+ '" class="img-rounded hda-image"></td><td><input type="text" class="form-control" placeholder="Price of '
					+ 'grano' + '" value="' + f.qty + '"></td>';
			return html;
		}
	}

	/* Renders a table */
	function renderTableRows(target, list, rowRenderer) {
		return $.each(list, rowBuild);

		function rowBuild(i, data) {
			var html = '<tr>' + rowRenderer(data) + '</tr>';
			target.append(html);
		}
	}

}

module.exports = MainApp;
