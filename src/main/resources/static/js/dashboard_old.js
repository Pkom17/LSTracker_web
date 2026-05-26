$(document).ready(function() {
	$
		.get(
			"/dashboard/sample_status_by_sample_type" + window.location.search,
			function(data) {
				Highcharts
					.chart(
						'sample_trend', {
						chart: {
							type: 'column'
						},
						title: {
							text: "Echantillons collectés, Transmis, Rejetés"
						},
						xAxis: {
							//categories: data.categories,
							categories: data.categories,
							crosshair: true
						},
						yAxis: {
							title: {
								text: "Nombre d'échantillons"
							}
						},
						legend: {
							enabled: true
						},
						plotOptions: {
							series: {
								allowPointSelect: true,
								dataLabels: {
									enabled: true,
								}
							}
						},
						tooltip: {
							headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
							pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
								'<td style="padding:0"><b>{point.y:.0f} </b></td></tr>',
							footerFormat: '</table>',
							shared: true,
							useHTML: true
						},

						series: [{
							name: "Echantillons collectés",
							data: data.collected,
							color: "#66F"
						},
						{
							name: "Echantillons Transmis",
							data: data.delivered,
							color: "#6E9"
						},
						{
							name: "Echantillons rejetés",
							data: data.nonConform,
							color: "#EE5599"
						}, {
							name: "Analyses Terminées",
							data: data.analysisDone,
							color: "#0C97D4"
						},
						{
							name: "Résultats Collectés",
							data: data.resultCollected,
							color: "#E3E912"
						},
						{
							name: "Résultats Déposés",
							data: data.resultOnSite,
							color: "#1AF4C3"
						}
						]
					});
			});

});

(function() {
	function pad(n) { return (n < 10 ? '0' : '') + n; }

	// Convertit "dd/mm/yy" ou "dd/mm/yyyy" en "yyyy-MM-dd"
	function uiToISO(dmy) {
		if (!dmy || !dmy.trim()) return '';
		const parts = dmy.split(/[\/\-\.]/); // gère "dd/mm/yy" ou "dd-mm-yy"
		if (parts.length < 3) return '';
		let [dd, mm, yy] = parts;
		dd = parseInt(dd, 10);
		mm = parseInt(mm, 10);
		let yyyy = (yy.length === 2) ? (2000 + parseInt(yy, 10)) : parseInt(yy, 10);
		if (!dd || !mm || !yyyy) return '';
		return `${yyyy}-${pad(mm)}-${pad(dd)}`;
	}

	// Si la page est rendue avec des ISO (thymeleaf) et que tu veux afficher en dd/mm/yy :
	function isoToUI(iso) {
		if (!iso) return '';
		const d = new Date(iso);
		if (isNaN(d.getTime())) return '';
		return pad(d.getDate()) + '/' + pad(d.getMonth() + 1) + '/' + d.getFullYear();
	}

	function fetchJSON(url, params) {
		const qs = new URLSearchParams(params).toString();
		return fetch(url + '?' + qs, { credentials: 'same-origin' }).then(r => r.json());
	}

	function toSeries(name, data) {
		return {
			name: name,
			data: (data || []).map(pt => [Date.parse(pt.day), Number(pt.cnt) || 0]),
			type: 'line',
			marker: { enabled: false }
		};
	}

	function renderTimeSeries(params) {
		fetchJSON('/dashboard/data/series', params).then(res => {
			Highcharts.chart('chart_series', {
				title: { text: null },
				xAxis: { type: 'datetime' },
				yAxis: { title: { text: 'Nombre' }, allowDecimals: false },
				legend: { enabled: true },
				credits: { enabled: false },
				series: [
					toSeries('Collectés', res.collected),
					toSeries('Déposés', res.deposited),
					toSeries('Analysés', res.analysed),
					toSeries('Livrés', res.delivered)
				],
				tooltip: { shared: true }
			});
		});
	}

	function renderDurations(params) {
		fetchJSON('/dashboard/data/step-durations', params).then(rows => {
			const steps = [
				'collecte→dépôt',
				'dépôt→réception',
				'réception→analyse',
				'analyse→résultat prêt',
				'résultat prêt→livraison'
			];
			const byType = {};
			rows.forEach(r => {
				if (!byType[r.sampleType]) byType[r.sampleType] = {};
				byType[r.sampleType][r.step] = Number(r.medianDays) || 0;
			});
			const categories = steps;
			const series = Object.keys(byType).sort().map(type => ({
				name: type,
				type: 'column',
				data: categories.map(step => byType[type][step] || 0),
				tooltip: { valueSuffix: ' j' }
			}));

			Highcharts.chart('chart_durations', {
				title: { text: null },
				xAxis: { categories },
				yAxis: {
					title: { text: 'Jours (médiane)' },
					allowDecimals: false,
					min: 0
				},
				legend: { enabled: true },

				plotOptions: {
					series: {
						allowPointSelect: true,
						dataLabels: {
							enabled: true,
						}
					}
				},
				tooltip: {
					headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
						'<td style="padding:0"><b>{point.y:.0f} </b></td></tr>',
					footerFormat: '</table>',
					shared: true,
					useHTML: true
				},


				credits: { enabled: false },
				tooltip: { shared: true },
				series
			});
		});
	}

	// Exposés globalement pour tes boutons
	window.filterData = function() {
		const startUI = document.getElementById('input_start_date').value || '';
		const endUI = document.getElementById('input_end_date').value || '';
		const startDate = uiToISO(startUI);
		const endDate = uiToISO(endUI);

		const regionId = document.getElementById('select_region').value || '';
		const districtId = document.getElementById('select_district').value || '';
		const siteId = document.getElementById('select_site').value || '';

		const params = { startDate, endDate, regionId, districtId, siteId };
		renderTimeSeries(params);
		renderDurations(params);
	};

	window.resetFilter = function() {
		// réinitialise les champs visuels
		document.getElementById('input_start_date').value = '';
		document.getElementById('input_end_date').value = '';
		document.getElementById('select_region').value = '';
		document.getElementById('select_district').value = '';
		document.getElementById('select_site').value = '';
		filterData(); // appellera les endpoints sans dates → défauts côté serveur
	};

	document.addEventListener('DOMContentLoaded', function() {
		// Si Thymeleaf a injecté des ISO, on les reformate pour l’UI
		const sEl = document.getElementById('input_start_date');
		const eEl = document.getElementById('input_end_date');
		if (sEl && /^\d{4}-\d{2}-\d{2}$/.test(sEl.value)) sEl.value = isoToUI(sEl.value);
		if (eEl && /^\d{4}-\d{2}-\d{2}$/.test(eEl.value)) eEl.value = isoToUI(eEl.value);

		// Initialise les datepickers (format dd/mm/yy)
		$("#input_start_date").datepicker({ changeMonth: true, changeYear: true, dateFormat: "dd/mm/yy" });
		$("#input_end_date").datepicker({ changeMonth: true, changeYear: true, dateFormat: "dd/mm/yy" });

		// Premier rendu
		filterData();
	});
})();