// /static/js/dashboard.js


(function() {



	function isISO(s) {
		return /^\d{4}-\d{2}-\d{2}$/.test(s || '');
	}

	// Accepts either "dd/mm/yy|yyyy" or "yyyy-MM-dd" and returns ISO "yyyy-MM-dd"
	function normalizeToISO(s) {
		if (!s) return '';
		if (isISO(s)) return s;      // already ISO
		return uiToISO(s);           // convert from UI to ISO
	}



	function pad(n) { return (n < 10 ? '0' : '') + n; }

	// "dd/mm/yy|yyyy" -> "yyyy-MM-dd"
	function uiToISO(dmy) {
		if (!dmy || !dmy.trim()) return '';
		const p = dmy.split(/[\/\-\.]/);
		if (p.length < 3) return '';
		let [dd, mm, yy] = p;
		dd = parseInt(dd, 10); mm = parseInt(mm, 10);
		const yyyy = (yy.length === 2) ? 2000 + parseInt(yy, 10) : parseInt(yy, 10);
		if (!dd || !mm || !yyyy) return '';
		return `${yyyy}-${pad(mm)}-${pad(dd)}`;
	}

	// "yyyy-MM-dd" -> "dd/mm/yyyy" (pour afficher dans les inputs)
	function isoToUI(iso) {
		if (!iso) return '';
		const d = new Date(iso);
		if (isNaN(d.getTime())) return '';
		return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()}`;
	}

	function qsGet(name) {
		const u = new URLSearchParams(window.location.search);
		const v = u.get(name);
		return (v === null || v === '' || v === '0') ? '' : v;
	}

	// Read params from URL (region/district/site + dates ISO)
	function getParamsFromURL() {
		return {
			startDate: qsGet('startDate'),
			endDate: qsGet('endDate'),
			region: qsGet('region'),
			district: qsGet('district'),
			site: qsGet('site'),
			lab: qsGet('lab'),
			regionId: qsGet('regionId') || qsGet('region'),
			districtId: qsGet('districtId') || qsGet('district'),
			siteId: qsGet('siteId') || qsGet('site'),
			labId: qsGet('labId') || qsGet('labId')
		};
	}

	function fetchJSON(url, params) {
		const qs = new URLSearchParams(params || {}).toString();
		return fetch(url + (qs ? ('?' + qs) : ''), { credentials: 'same-origin' })
			.then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); });
	}

	// ------- CARDS -------
	function setText(sel, val) {
		const el = document.querySelector(sel);
		if (el) el.textContent = (val == null ? '0' : ('' + val));
	}

	function updateCards(p) {
		return fetchJSON('/dashboard/data/summary', {
			startDate: p.startDate, endDate: p.endDate,
			regionId: p.regionId || '', districtId: p.districtId || '', siteId: p.siteId || '', labId: p.labId || ''
		}).then(sum => {
			setText('#card_all', sum.all_count);
			setText('#card_in_transit', sum.in_transit);
			const delivered = Number(sum.received || 0) + Number(sum.accepted || 0);
			setText('#card_delivered', delivered);
			setText('#card_rejected', sum.rejected);
			setText('#card_result_ready', sum.result_ready);
			setText('#card_result_collected', sum.result_collected);
			setText('#card_result_on_site', sum.result_on_site);
			setText('#card_failed', sum.failed);
		}).catch(() => { });
	}

	// ------- CHART 1 (status par type) -------
	function renderSampleTrend(p) {
		fetchJSON('/dashboard/sample_status_by_sample_type', {
			startDate: p.startDate, endDate: p.endDate,
			region: p.region || '', district: p.district || '', site: p.site || '', labId: p.labId || ''
		}).then(data => {
			Highcharts.chart('sample_trend', {
				chart: { type: 'column' },
				title: { text: "Échantillons (par type) : collectés / transmis / rejetés" },
				xAxis: { categories: data.categories || [], crosshair: true },
				yAxis: { title: { text: "Nombre d'échantillons" }, allowDecimals: false, min: 0 },
				legend: { enabled: true },
				credits: { enabled: false },
				plotOptions: { series: { allowPointSelect: true, dataLabels: { enabled: true } } },
				tooltip: {
					headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
						'<td style="padding:0"><b>{point.y:.0f}</b></td></tr>',
					footerFormat: '</table>', shared: true, useHTML: true
				},
				series: [
					{ name: "Collectés", data: data.collected || [], color: "#3366FF" },
					{ name: "Transmis/Déposés", data: data.delivered || [], color: "#33CC99" },
					{ name: "Rejetés", data: data.nonConform || [], color: "#EE5599" },
					{ name: "Analyses terminées", data: data.analysisDone || [], color: "#0C97D4" },
					{ name: "Résultats collectés", data: data.resultCollected || [], color: "#E3E912" },
					{ name: "Résultats déposés", data: data.resultOnSite || [], color: "#1AF4C3" }
				]
			});
		}).catch(() => { });
	}

	// ------- CHART 2 (séries) -------

	// --- Helpers d’agrégation ---
	function toUTC(ts) {
		// ts peut être '2025-09-01' ou un timestamp
		return typeof ts === 'string' ? Date.UTC(...ts.split('-').map((n, i) => i === 1 ? Number(n) - 1 : Number(n))) : ts;
	}
	function startOfWeekUTC(d) {
		// Semaine ISO (lundi). On travaille en UTC pour rester cohérent avec Highcharts.
		const day = new Date(d); // d = timestamp ms
		const dow = (day.getUTCDay() + 6) % 7; // 0 = lundi
		day.setUTCDate(day.getUTCDate() - dow);
		day.setUTCHours(0, 0, 0, 0);
		return day.getTime();
	}
	function startOfMonthUTC(d) {
		const day = new Date(d);
		return Date.UTC(day.getUTCFullYear(), day.getUTCMonth(), 1);
	}
	function bucketize(data, granularity) {
		// data = [{ day: 'YYYY-MM-DD', cnt: number }, ...]
		const map = new Map();
		for (const pt of (data || [])) {
			const ts = toUTC(pt.day);
			let key;
			if (granularity === 'week') key = startOfWeekUTC(ts);
			else if (granularity === 'month') key = startOfMonthUTC(ts);
			else key = new Date(ts).setUTCHours(0, 0, 0, 0); // day
			const prev = map.get(key) || 0;
			map.set(key, prev + (Number(pt.cnt) || 0));
		}
		// retourne tableau trié [ [timestamp, sum], ... ]
		return Array.from(map.entries()).sort((a, b) => a[0] - b[0]);
	}

	function toSeries(name, data, granularity) {
		return {
			name,
			type: 'line',
			marker: { enabled: false },
			data: bucketize(data, granularity)
		};
	}

	// --- Rendu + interaction ---
	let _cache = null; // on garde les données brutes pour re-rendu rapide
	let _lastParams = null;

	function renderTimeSeries(p, granularity = 'day') {
		_lastParams = p;
		const doRender = (res) => {
			Highcharts.chart('chart_series', {
				title: { text: null },
				credits: { enabled: false },
				xAxis: { type: 'datetime' },
				yAxis: { title: { text: 'Nombre' }, allowDecimals: false },
				legend: { enabled: true },
				tooltip: { shared: true },
				series: [
					toSeries('Collectés', res.collected, granularity),
					toSeries('Déposés', res.deposited, granularity),
					toSeries('Analysés', res.analysed, granularity),
					toSeries('Livrés', res.delivered, granularity)
				]
			});
		};

		if (_cache && _cache.key === JSON.stringify(p)) {
			doRender(_cache.data);
		} else {
			fetchJSON('/dashboard/data/series', {
				startDate: p.startDate, endDate: p.endDate,
				regionId: p.regionId || '', districtId: p.districtId || '', siteId: p.siteId || '', labId: p.labId || ''
			}).then(res => {
				_cache = { key: JSON.stringify(p), data: res };
				doRender(res);
			}).catch(() => { });
		}
	}

	document.getElementById('granularitySelect')?.addEventListener('change', (e) => {
		const gran = e.target.value;
		if (_lastParams) {
			renderTimeSeries(_lastParams, gran);
		}
	});


	// ------- CHART 3 (durées) -------
	function renderDurations(p) {
		fetchJSON('/dashboard/data/step-durations', {
			startDate: p.startDate, endDate: p.endDate,
			regionId: p.regionId || '', districtId: p.districtId || '', siteId: p.siteId || '', labId: p.labId || ''
		}).then(rows => {
			const steps = [
				'collecte→dépôt',
				'dépôt→réception',
				'réception→analyse',
				'analyse→résultat prêt',
				'résultat prêt→collecte résultat',
				'collecte résultat→dépôt résultat'
			];
			const byType = {};
			(rows || []).forEach(r => {
				if (!byType[r.sampleType]) byType[r.sampleType] = {};
				byType[r.sampleType][r.step] = Number(r.medianDays) || 0;
			});

			const series = Object.keys(byType).sort().map(type => ({
				name: type, type: 'column',
				data: steps.map(step => byType[type][step] || 0),
				tooltip: { valueSuffix: ' j' }
			}));

			Highcharts.chart('chart_durations', {
				title: { text: null }, credits: { enabled: false },
				xAxis: { categories: steps },
				yAxis: { title: { text: 'Jours (médiane)' }, min: 0, allowDecimals: false },
				legend: { enabled: true },
				plotOptions: { series: { allowPointSelect: true, dataLabels: { enabled: true } } },
				tooltip: {
					headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
						'<td style="padding:0"><b>{point.y:.0f} j</b></td></tr>',
					footerFormat: '</table>', shared: true, useHTML: true
				},
				series
			});
		}).catch(() => { });
	}

	// --------- Rendu global (lecture URL) ----------
	function renderAllFromURL() {
		const p = getParamsFromURL();
		updateCards(p);
		renderSampleTrend(p);
		renderTimeSeries(p, 'day');
		renderDurations(p);
	}

	// --------- Actions boutons : MAJ URL + reload ----------
	function buildUrlFromInputs() {
		const startUI = document.getElementById('input_start_date').value || '';
		const endUI = document.getElementById('input_end_date').value || '';
		const startDate = uiToISO(startUI);
		const endDate = uiToISO(endUI);

		const region = document.getElementById('select_region').value || '';
		const district = document.getElementById('select_district').value || '';
		const site = document.getElementById('select_site').value || '';
		const lab = document.getElementById('select_lab').value || '';

		const params = new URLSearchParams();
		if (startDate) params.set('startDate', startDate);
		if (endDate) params.set('endDate', endDate);
		if (region) params.set('region', region);
		if (district) params.set('district', district);
		if (site) params.set('site', site);
		if (lab) params.set('lab', lab);

		const base = window.location.pathname.replace(/\/+$/, '') || '/dashboard';
		return base + (params.toString() ? ('?' + params.toString()) : '');
	}

	// Exposés globalement
	window.filterData = function() {
		const url = buildUrlFromInputs();
		window.location.assign(url); // recharge avec les params
	};

	window.resetFilter = function() {
		window.location.assign('/dashboard'); // sans params
	};

	// --------- Init page ----------
	$(document).ready(function() {
		const sEl = document.getElementById('input_start_date');
		const eEl = document.getElementById('input_end_date');
		if (sEl && isISO(sEl.value)) sEl.value = isoToUI(sEl.value);
		if (eEl && isISO(eEl.value)) eEl.value = isoToUI(eEl.value);

		// Init datepickers
		$("#input_start_date").datepicker({ changeMonth: true, changeYear: true, dateFormat: "dd/mm/yy" });
		$("#input_end_date").datepicker({ changeMonth: true, changeYear: true, dateFormat: "dd/mm/yy" });

		// Pré-remplir inputs depuis URL (Thymeleaf met déjà selectedX, mais on unifie)
		const p = getParamsFromURL();
		if (p.startDate) $('#input_start_date').val(isoToUI(p.startDate));
		if (p.endDate) $('#input_end_date').val(isoToUI(p.endDate));
		if (p.region) $('#select_region').val(p.region).trigger('change');
		if (p.district) $('#select_district').val(p.district).trigger('change');
		if (p.site) $('#select_site').val(p.site).trigger('change');
		if (p.lab) $('#select_lab').val(p.lab).trigger('change');

		// Rendu initial (depuis URL)
		renderAllFromURL();
	});
})();