(function($, ns, channel, window) {
	"use strict";

	window.ACV = window.ACV || {};
	window.ACV.tests = window.ACV.tests || {};
	
	window.ACV.tests.resultCounter = 0;

	/**
	 * Returns the list of possible test files.
	 */
	window.ACV.tests.getFileList = function() {
		var url = window.location.pathname.replace('.html', '.acvfiles.json')
				+ '?root=' + jQuery('[name=path]').val() + '';
		var json = null;
		jQuery.ajax({
			url : url,
			success : function(html) {
				json = html;
			},
			async : false
		});
		if (json.error) {
			window.ACV.tests.showError(json.error);
			return false;
		}
		if (json.warning) {
			window.ACV.tests.showWarning(json.warning);
			return false;
		}
		
		window.ACV.tests.updateFilesTable(json.files);
		var resultContainer = window.ACV.tests.getResultContainer();
		resultContainer.empty();
		jQuery('.acv-results-headline').hide();
		jQuery('.acv-result-table').hide();
	};
	
	/**
	 * Prints a warning message
	 * 
	 * @param message message
	 */
	window.ACV.tests.showWarning = function(message) {
		window.ACV.tests.showDialog(message, 'warning');
	};
	
	/**
	 * Prints an error message
	 * 
	 * @param message message
	 */
	window.ACV.tests.showError = function(message) {
		window.ACV.tests.showDialog(message, 'error');		
	};
	
	/**
	 * Prints a message dialog
	 * 
	 * @param message message
	 * @param variant variant (error, warning)
	 */
	window.ACV.tests.showDialog = function(message, variant) {
		var dialog = new Coral.Dialog().set({
		    variant: variant,
		    closable: 'on',
		    header: {
		      innerHTML: 'Error'
		    },
		    content: {
			  innerHTML: message
			},
			footer: {
			  innerHTML: '<button is="coral-button" variant="primary" coral-close>Ok</button>'
			}
		  });
		  document.body.appendChild(dialog);
		  dialog.show();
	};

	/**
	 * Updates the files in the table.
	 * 
	 * @param files file list
	 */
	window.ACV.tests.updateFilesTable = function(files) {
		var table = jQuery('.acv-tests-selection').first();
		var items = table[0].items;
		items.clear();
		
		for (var file in files) {
			var row = items.add({});
			var selectCell = new Coral.Table.Cell();
			var box = new Coral.Checkbox();
			box.setAttribute('coral-table-rowselect', '');
			box.setAttribute('coral-tr-select', '');
			selectCell.appendChild(box);
			row.appendChild(selectCell);
			var pathCell = new Coral.Table.Cell().set({
			      value: files[file],
			      title: files[file]
			    });
			pathCell.append(files[file]);
			row.appendChild(pathCell);
			row.set({ selected: true });
		}
		window.ACV.tests.setRunButtonDisabled(files.length == 0);
	};
	
	/**
	 * Runs the tests
	 */
	window.ACV.tests.run = function() {
		var table = jQuery('.acv-tests-selection').first();
		var items = table[0].selectedItems;
		if (items.length == 0) {
			window.ACV.tests.showWarning('Please select a file.');
			return;
		}
		window.ACV.tests.setRunButtonDisabled(true);
		var resultContainer = window.ACV.tests.getResultContainer();
		resultContainer.empty();
		jQuery('.acv-results-headline').show();
		var skipsimulation = jQuery('[name=skipsimulation]').is(':checked');
		window.ACV.testResults = [];
		for (var item in items) {
			var file = items[item].cells[1].value;
			window.ACV.tests.runSingleFile(file, skipsimulation);
			window.ACV.showResultSummary();
		}
		window.ACV.tests.setRunButtonDisabled(false);
	}
	
	/**
	 * Sets the run button to disabled or enabled.
	 * 
	 * @param disabled true or false
	 */
	window.ACV.tests.setRunButtonDisabled = function(disabled) {
		var button = jQuery('.acv-tests-run');
		button = button[0];
		button.disabled = disabled;		
	}
	
	/**
	 * Runs a single test file.
	 * 
	 * @param file file
	 * @param skipsimulation skip simulation
	 */
	window.ACV.tests.runSingleFile = function(file, skipsimulation) {
		var resultContainer = window.ACV.tests.getResultContainer();
		var skipParam = skipsimulation ? '1' : '0';
		var url = window.location.pathname.replace('.html', '.acvrun.json')
			+ '?path=' + file + '&skipsimulation=' + skipParam;
		var json = null;
		jQuery.ajax({
			url : url,
			success : function(html) {
				json = html;
			},
			async : false
		});
		if (json.error) {
			window.ACV.tests.showError(json.error);
			return false;
		}
		if (json.warning) {
			window.ACV.tests.showWarning(json.warning);
			return false;
		}
		var template = jQuery('.acv-result-template');
		var templateHTML = template[0].outerHTML;
		var counter = window.ACV.tests.resultCounter;
		window.ACV.tests.resultCounter++;
		var resultId = 'acv-result_' + counter;
		templateHTML = templateHTML.replace('acv-result-template', 'acv-result ' + resultId);
		resultContainer.append(templateHTML);
		// replace label
		var label = jQuery('.' + resultId + ' .acv-result-label');
		label = label[0];
		label.innerText = file;
		window.ACV.tests.updateChart(json.okPercentage.toFixed(2), resultId);
		window.ACV.tests.addToggleListener(resultId);
		window.ACV.tests.addResults(resultId, json.results);
		// replace messages
		var messages = jQuery('.' + resultId + ' .acv-result-messages');
		messages = messages[0];
		messages.innerHTML = "Test runs: " + json.results.length + '<br>';
		messages.innerHTML += "Result ok: " + json.ok + '<br><br>';
		// show result
		var result = jQuery('.' + resultId);
		result.removeClass('acv-hidden');
		// save results
		var result = [];
		result['percentage'] = json.okPercentage;
		result['count'] = json.results.length;
		result['ok'] = json.ok;
		result['okResults'] = json.okResults;
		window.ACV.testResults.push(result);
	}
	
	/**
	 * Updates the success percentage chart.
	 * 
	 * @param percentage percentage
	 * @param resultId resultId of file
	 */
	window.ACV.tests.updateChart = function(percentage, resultId) {
		var percentageLabel = jQuery('.' + resultId + ' .acv-chart-text');
		percentageLabel = percentageLabel[0];
		percentageLabel.innerHTML = percentage + '%';
		var percentageSegment = jQuery('.' + resultId + ' .donut-segment');
		percentageSegment.attr('stroke-dasharray', (percentage + ' ' + (100 - percentage)));		
	}
	
	/**
	 * Adds a listener to toggle the details.
	 * 
	 * @param resultId resultId of file
	 */
	window.ACV.tests.addToggleListener = function(resultId) {
		var toggle = jQuery('.' + resultId + ' .acv-details-toggle');
		toggle.on('click', function() {
			var table = jQuery('.' + resultId + ' .acv-result-details-table');
			table.toggle();
		});
	}
	
	/**
	 * Fills the result table
	 * 
	 * @param resultId result id
	 * @param results test results
	 */
	window.ACV.tests.addResults = function(resultId, results) {
		var table = jQuery('.' + resultId + ' .acv-result-details-table');
		var tbody = table.find('tbody');
		for (var result in results) {
			var testName = results[result].test;
			var params = results[result].params;
			var message = results[result].error;
			var authorizable = results[result].authorizable;
			var isOk = results[result].ok;
			var cssClass = isOk ? 'acv-ok-result' : 'acv-fail-result';
			tbody.append('<tr>' +
					'<td class="' + cssClass + '">' + authorizable + '</td>' +
					'<td class="' + cssClass + '">' + testName + '</td>' +
					'<td class="' + cssClass + '">' + params + '</td>' +
					'<td class="' + cssClass + '">' + message + '</td>' +
					'</tr>');
		}
	}
	
	/**
	 * Returns the container to show the results.
	 */
	window.ACV.tests.getResultContainer = function() {
		var resultContainer = jQuery('.acv-tests-results').first();
		return resultContainer;
	}
	
	/**
	 * Shows the summary.
	 */
	window.ACV.showResultSummary = function() {
		var testCount = 0;
		var isOk = true;
		// update percentage
		var percentageSum = 0;
		var nrOfOkResults = 0;
		
		for (var result in window.ACV.testResults) {
			testCount += window.ACV.testResults[result]['count'];
			percentageSum += window.ACV.testResults[result]['percentage'];
			nrOfOkResults += window.ACV.testResults[result]['okResults'];
			if (!window.ACV.testResults[result]['ok']) {
				isOk = false;
			}
		}
		var percentage = ((100 * Number(nrOfOkResults)) / testCount).toFixed(2); 
		var percentageLabel = jQuery('.acv-summary-result-table .acv-chart-text');
		percentageLabel = percentageLabel[0];
		percentageLabel.innerHTML = percentage + '%';
		// update chart
		var percentageSegment = jQuery('.acv-summary-result-table .donut-segment');
		percentageSegment.attr('stroke-dasharray', percentage + ' ' + (100 - percentage));
		// update result text
		var messages = jQuery('.acv-summary-result-table .acv-result-messages');
		messages = messages[0];
		messages.innerHTML = "Test runs: " + testCount + '<br>';
		messages.innerHTML += "Result ok: " + isOk + '<br><br>';
		// show summary
		jQuery('.acv-results-headline').show();
		jQuery('.acv-summary-result-table').show();
		jQuery('.acv-result-table').show();
	}
	
	/**
	 * Registers events for buttons.
	 */
	$(document).ready(function() {
		jQuery('.acv-tests-select-folder-next').click(function() {
			return window.ACV.tests.getFileList();
		});
		jQuery('.acv-tests-run').click(function() {
			return window.ACV.tests.run();
		});
	});

})(jQuery, Granite.author, jQuery(document), this);
