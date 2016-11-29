/**
 * 
 */
$(window)
		.load(
				function() {
					var alert = window.alert;
					var $ = window.$;
					var console = window.console;
					var _ = window._;
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
						hdaApi.getConfig(id).then(renderConfig);

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
						var rows = $.map(data.productions, toUIModel);
						renderTableRows($('#prodConf'), rows, renderRow);

						function toUIModel(v) {
							return {
								productName : hdaApi.productName(v.name),
								productImage : './img/' + v.name + '.png',
								supplierName : hdaApi
										.supplierName(v.supplierName),
								supplierImage : './img/' + v.supplierName
										+ '.png',
								randomQty : v.random,
								fixedQty : v.fixed
							};
						}

						function renderRow(f) {
							var html = '<td><img src="' + f.supplierImage
									+ '" alt="Image of ' + f.supplierName
									+ '" class="img-rounded hda-image">'
									+ f.supplierName + '</td><td><img src="'
									+ f.productImage + '" alt="Image of '
									+ f.productName
									+ '" class="img-rounded hda-image">'
									+ f.productName + '</td><td>' + f.fixedQty
									+ '</td><td>' + f.randomQty + '</td><td>'
									+ (f.fixedQty + f.randomQty) + '</td>';
							return html;
						}
					}

					/* Renders forecast table */
					function renderForecasts(data) {
						clearTable('forecasts');
						var rows = $.map(data.consumptions, toUIModel);
						renderTableRows($('#forecasts'), rows, renderRow);

						function toUIModel(v, key) {
							return {
								name : hdaApi.productName(key),
								image : './img/' + key + '.png',
								qty : v
							};
						}

						function renderRow(f) {
							var html = '<td><img src="' + f.image
									+ '" alt="Image of ' + f.name
									+ '" class="img-rounded hda-image">'
									+ f.name + '</td><td>' + f.qty + '</td>';
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
								image : './img/' + key + '.png',
								name : hdaApi.productName(key),
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
								image : './img/' + key + '.png',
								name : hdaApi.supplierName(key),
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

				});
