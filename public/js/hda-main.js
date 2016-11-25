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

					var myArray = /.*?id=(.*)/g.exec(window.location.href);
					var id = null;
					if (myArray) {
						id = myArray[1];
					} else {
						hdaApi.alert('Missing id');
					}

					if (id) {
						loadPage(id)
					}

					function loadPage(id) {

						hdaApi.getFarmer(id).then(renderPage);

						function renderPage(data) {
							console.info(data);
							$('#userInfo').html('Signed in as ' + data.name);
							renderProduction();
							renderForecasts();
							renderMarket(data);
							renderConf(data);
							return data;
						}
					}

					/* Clears table */
					function clearTable(name) {
						$('#' + name + ' tr').remove();
					}

					/* Renders production table */
					function renderProduction() {
						clearTable('prodConf');
						var rows = [];
						for (i = 0; i < 10; i++) {
							rows[i] = {
								supplierImage : './img/demo.jpg',
								supplierName : 'Supplier ' + i,
								productImage : './img/demo.jpg',
								productName : 'Product ' + i,
								fixedQty : 10,
								randomQty : 13
							}
						}
						renderTableRows($('#prodConf'), rows, renderRow);

						function renderRow(f) {
							var html = '<td><img src="' + f.supplierImage
									+ '" alt="Image of ' + f.supplierName
									+ '" class="img-rounded">' + f.supplierName
									+ '</td><td><img src="' + f.productImage
									+ '" alt="Image of ' + f.productName
									+ '" class="img-rounded">' + f.productName
									+ '</td><td>' + f.fixedQty + '</td><td>'
									+ f.randomQty + '</td><td>'
									+ (f.fixedQty + f.randomQty) + '</td>';
							return html;
						}
					}

					/* Renders forecast table */
					function renderForecasts() {
						clearTable('forecasts');
						var rows = [];
						for (i = 0; i < 10; i++) {
							rows[i] = {
								image : './img/demo.jpg',
								name : 'Product ' + i,
								qty : 10,
							}
						}
						renderTableRows($('#forecasts'), rows, renderRow);

						function renderRow(f) {
							var html = '<td><img src="' + f.image
									+ '" alt="Image of ' + f.name
									+ '" class="img-rounded">' + f.name
									+ '</td><td>' + f.qty + '</td>';
							return html;
						}
					}

					/* Renders market table */
					function renderMarket(data) {
						clearTable('marketTable');

						var rows = $.map(data.values, toRenderModel);
						renderTableRows($('#marketTable'), rows, renderRow);

						function toRenderModel(value, key) {
							return {
								image : './img/demo.jpg',
								name : hdaApi.productName(key),
								value : value
							};
						}

						function renderRow(f) {
							var html = '<td><img src="'
									+ f.image
									+ '" alt="Image of '
									+ f.name
									+ '" class="img-rounded">'
									+ f.name
									+ '</td><td><input type="text" class="form-control" placeholder="Price of '
									+ 'grano' + '" value="' + f.value
									+ '"></td>';
							return html;
						}
					}

					/* Renders configuration table */
					function renderConf(data) {
						clearTable('confTable');

						var rows = $.map(data.suppliers, toRenderModel);
						renderTableRows($('#confTable'), rows, renderRow);

						function toRenderModel(value, key) {
							return {
								image : './img/demo.jpg',
								name : hdaApi.supplierName(key),
								qty : value
							};
						}

						function renderRow(f) {
							var html = '<td><img src="'
									+ f.image
									+ '" alt="Image of '
									+ f.name
									+ '" class="img-rounded">'
									+ f.name
									+ '</td><td><input type="text" class="form-control" placeholder="Price of '
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

				});
