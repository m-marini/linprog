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
					var valid = window.validator;
					var builder = window.builder;

					var myArray = /.*?id=(.*)/g.exec(window.location.href);
					var id = null;
					if (myArray) {
						id = myArray[1];
					} else {
						hdaApi.alert('Missing id');
					}
					if (id) {
						loadPage();
					}

					/*
					 * Loads page data
					 */
					function loadPage() {

						hdaApi.getFarmer(id).then(renderPage);
						hdaApi.getConfig(id).then(renderConfig);

						function renderPage(data) {
							$('#userInfo').html('Signed in as ' + data.name);
							renderMarket(data);
							renderConf(data);
							$('#newAdvice').click(loadPage);
							$('#marketForm').submit(saveMarket(data));
							$('#configForm').submit(saveConfig(data));
							return data;
						}

						function renderConfig(data) {
							renderProduction(data);
							renderForecasts(data);
							return data;
						}
					}

					/*
					 * Saves suppliers configuration
					 */
					function saveMarket(data) {
						return manageMarket;

						/**
						 * Manages configuration saving
						 */
						function manageMarket() {
							var keys = sortProducts(data);
							// gets all component
							var fields = _.map(keys, toField);
							// Filter all valid fields
							var errors = _.filter(fields, isError);

							// Shows error for invalid fields
							if (errors.length == 0) {
								_.forEach(_.zip(keys, fields), setValue);
								hdaApi.putFarmer(data).then(loadPage);
							} else {
								console.error(errors);
							}
							return false;

							function setValue(kv) {
								var key = kv[0];
								var field = kv[1];
								var value = _.toNumber(field.value());
								data.values[key] = value;
							}

							/*
							 * Converts key to field
							 */
							function toField(key) {
								var f = new builder.Field('#' + key + '-val');
								f.validate(builder.validateDecimal);
								return f;
							}

							function isError(field) {
								return !field.validate();
							}
						}
					}

					/*
					 * Saves suppliers configuration
					 */
					function saveConfig(data) {
						return manageConfig;

						/**
						 * Manages configuration saving
						 */
						function manageConfig() {
							var keys = sortSuppliers(data);
							// gets all component
							var fields = _.map(keys, toField);
							// Filter all valid fields
							var errors = _.filter(fields, isError);

							// Shows error for invalid fields
							if (errors.length == 0) {
								_.forEach(_.zip(keys, fields), setValue);
								hdaApi.putFarmer(data).then(loadPage);
							} else {
								console.error(errors);
							}
							return false;

							function setValue(kv) {
								var key = kv[0];
								var field = kv[1];
								var value = _.toInteger(field.value());
								data.suppliers[key] = value;
							}

							/*
							 * Converts key to field
							 */
							function toField(key) {
								var f = new builder.Field('#' + key + '-qty');
								f.validate(builder.validateUnsigned);
								return f;
							}

							function isError(field) {
								return !field.validate();
							}
						}
					}

					/*
					 * Returns the image path
					 */
					function imagePath(key) {
						return './img/' + key + '.png';
					}

					/*
					 * Return the product name
					 */
					function productName(key) {
						return hdaApi.productName(key);
					}

					/*
					 * Return the supplier name
					 */
					function supplierName(key) {
						return hdaApi.supplierName(key);
					}

					/*
					 * Clears table
					 */
					function clearTable(name) {
						$('#' + name + ' tr').remove();
					}

					/*
					 * Renders production table
					 */
					function renderProduction(data) {
						clearTable('prodConf');
						var rows = _(hdaApi.productOrder).filter(productExists)
								.map(toUIModel).value();
						renderTableRows($('#prodConf'), rows, renderRow);

						function productExists(key) {
							return key in data.productions
									&& (data.productions[key].fixed + data.productions[key].random) > 0;
						}

						function toUIModel(key) {
							var v = data.productions[key];
							return {
								productName : productName(v.name),
								productImage : imagePath(v.name),
								supplierName : supplierName(v.supplierName),
								supplierImage : imagePath(v.supplierName),
								randomQty : v.random,
								fixedQty : v.fixed
							};
						}

						function renderRow(f) {
							var html = '<td>'
									+ f.supplierName
									+ '</td><td><img src="'
									+ f.supplierImage
									+ '" alt="Image of '
									+ f.supplierName
									+ '" class="img-rounded hda-image"></td><td>'
									+ f.productName
									+ '</td><td><img src="'
									+ f.productImage
									+ '" alt="Image of '
									+ f.productName
									+ '" class="img-rounded hda-image"></td><td>'
									+ f.fixedQty + '</td><td>' + f.randomQty
									+ '</td><td>' + (f.fixedQty + f.randomQty)
									+ '</td>';
							return html;
						}
					}

					/*
					 * Renders forecast table
					 */
					function renderForecasts(data) {
						clearTable('forecasts');
						var rows = _(hdaApi.productOrder).filter(productExists)
								.map(toUIModel).value();

						renderTableRows($('#forecasts'), rows, renderRow);

						function productExists(key) {
							return key in data.consumptions
									&& data.consumptions[key] > 0;
						}

						function toUIModel(key) {
							return {
								name : hdaApi.productName(key),
								image : './img/' + key + '.png',
								qty : data.consumptions[key]
							};
						}

						function renderRow(f) {
							var html = '<td>'
									+ f.name
									+ '</td><td><img src="'
									+ f.image
									+ '" alt="Image of '
									+ f.name
									+ '" class="img-rounded hda-image"></td><td>'
									+ f.qty + '</td>';
							return html;
						}
					}

					/*
					 * Renders market table
					 */
					function renderMarket(data) {
						clearTable('marketTable');
						var keys = sortProducts(data);
						var rows = _.map(keys, toHtml);
						renderTable($('#marketTable'), rows);

						function label(key) {
							return productName(key);
						}

						function image(key) {
							return '<img src="' + imagePath(key)
									+ '" alt="Image of ' + productName(key)
									+ '" class="img-rounded hda-image">';
						}

						function toHtml(key) {
							return [
									label(key),
									image(key),
									builder.createFieldHtml(key + '-val',
											data.values[key], 'Price of '
													+ productName(key)) ];
						}

						function toRenderModel(key) {
							var value = data.values[key];
							return {
								image : './img/' + key + '.png',
								name : hdaApi.productName(key),
								value : value
							};
						}
					}

					function sortSuppliers(data) {
						return _.filter(hdaApi.supplierOrder, supplierExists);

						function supplierExists(key) {
							return key in data.suppliers;
						}
					}

					function sortProducts(data) {
						return _.filter(hdaApi.productOrder, productExists);

						function productExists(name) {
							return name in data.values;
						}
					}

					/*
					 * Renders configuration table
					 */
					function renderConf(data) {
						clearTable('confTable');
						var keys = sortSuppliers(data);
						var rows = _.map(keys, toHtml);
						renderTable($('#confTable'), rows);

						function label(key) {
							return supplierName(key);
						}

						function image(key) {
							return '<img src="' + imagePath(key)
									+ '" alt="Image of ' + supplierName(key)
									+ '" class="img-rounded hda-image">';
						}

						function toHtml(key) {
							return [
									label(key),
									image(key),
									builder.createFieldHtml(key + '-qty',
											data.suppliers[key], 'Number of '
													+ supplierName(key)) ];
						}
					}

					/*
					 * Renders a table
					 */
					function renderTable(target, content) {
						var html = _(content).map(toRows).map(
								prePostFix('<tr>', '</tr>')).reduce(concat);
						target.append(html);

						function toRows(cols) {
							return _(cols).map(prePostFix('<td>', '</td>'))
									.reduce(concat);
						}
					}

					function concat(a, b) {
						return a + b;
					}

					function prePostFix(pre, post) {
						return pp;

						function pp(text) {
							return pre + text + post;
						}
					}

					/*
					 * Renders a table
					 */
					function renderTableRows(target, list, rowRenderer) {
						return $.each(list, rowBuild);

						function rowBuild(i, data) {
							var html = '<tr>' + rowRenderer(data) + '</tr>';
							target.append(html);
						}
					}

				});
