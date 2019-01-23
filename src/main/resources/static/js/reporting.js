var model;
$(window).load(function() {
	$(".loader").fadeOut("slow");
});
$(function() {
	$('#qnames').combobox({
		onChange:function(newValue, oldValue) {
			getModel(newValue, function(model) {
				drawForm(model);
				doSearch();
			});
		}
	});
	$('#reporting-grid').datagrid({        
        view: expandView,
        toolbar: '#toolbar',        
        idField: 'id',
        autoRowHeight: false,
        pageSize: 20,
        striped: true,
        pagination:true,
        fitColumns: true,
        detailFormatter:function(index,row){
            return '<div class="ddv" style="padding:5px 0"></div>';
        },
        onExpandRow: function(index,row){
            var ddv = $(this).datagrid('getRowDetail',index).find('div.ddv');
            var content = '<div id="report-element-'+index+'">';            
            for(var fieldKey in model.fields) {
        	  	var field = model.fields[fieldKey];
        	  	var property = row[field.qname];
			  	if(!field.hidden && property) {
			  		content += '<div class="attribute-row">';
			  		content += '<div class="attribute-label">'+field.label+'</div>';
			  		if(property) content += '<div class="attribute-value">'+property+'</div>';
			  		else content += '<div class="attribute-value">&nbsp;</div>';
			  		content += '</div>';
			  	}        	  	
            }            
            content += '</div>';
            ddv.panel({
                border:false,
                cache:true,
                content:content,
                onLoad:function(){
                    $('#reporting-grid').datagrid('fixDetailRowHeight',index);
                }
            });
            $('#reporting-grid').datagrid('fixDetailRowHeight',index);
        }
	});
	getModel($('#qnames').val(), function(m) {
		model = m
		drawForm(model);
		doSearch();
	});
})
function doSearch() {
	var qname = $('#qnames').val();
	var query = $('#query').val();
	getModel(qname, function(m) {
		model = m;
		var data = {};
		data['qname'] = qname;
		data['query'] = query;
		for(var fieldKey in model.fields) {
			var field = model.fields[fieldKey];
			var value = $('#'+field.qname).val();
			if(value) data[field.qname] = value;
		}
		$('#reporting-grid').datagrid({
			url: '/service/search/structured.json',
			queryParams: data
		});
	});			
}
function openExcel() {
	var qname = $('#qnames').val();
	var query = $('#query').val();
	getModel(qname, function(m) {
		model = m;
		var data = {};
		data['qname'] = qname;
		data['query'] = query;
		for(var fieldKey in model.fields) {
			var field = model.fields[fieldKey];
			var value = $('#'+field.qname).val();
			if(value) data[field.qname] = value;
		}
		toExcel(model, data);
	});
}
function drawForm(model) {
	var html = [];
	for(var fieldKey in model.fields) {
		var field = model.fields[fieldKey];
		if(field.searchable) {
			if(field.type == 'boolean') {
				html.push('<div class="form-row">');
				html.push('<input id="'+field.qname+'" name="'+field.qname+'" type="checkbox" class="easyui-radio" style="width:20px;height:20px;" />');
				html.push('</div>');		      
	  	  	} else {
	  	  		html.push('<div class="form-row">');
	  	  		html.push('<select id="'+field.qname+'" name="'+field.qname+'" style="width:150px;" editable="false">');
	  	  		html.push('<option value=""></option>');
	  	  		for(var valueKey in field.values) {
	  	  			var value = field.values[valueKey];
	  	  			html.push('<option value="'+value.value+'">'+value.label+'</option>');
	  	  		}
	  	  		html.push('</select>');
	  	  		html.push('</div>');
	  	  	}
		}
	}
	$('#reportFields').empty();
	$('#reportFields').append(html.join(''));
	
	for(var fieldKey in model.fields) {
    	var field = model.fields[fieldKey];
    	if(field.searchable) {
    		if(field.type == 'boolean') {
    		    $('#'+field.qname).checkbox({
    		      label: field.label,
    		      labelPosition:'top'
    		    });
    		} else {
	    		if(field.values.length > 0) {
	    			$('#'+field.qname).combobox({
			    	  label:field.label,
			    	  labelPosition:'left',
			    	  valueField: 'value',
			    	  textField: 'text'
			    	});
			    }
    		}
    	}
    }
}
function toExcel(model, params){
    var filename = 'archivemanager-'+Math.random().toString(36).slice(2);
    var worksheet = 'Worksheet';       
    var uri = 'data:application/vnd.ms-excel;base64,'
    , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body>{table}</body></html>'
    , base64 = function (s) { return window.btoa(unescape(encodeURIComponent(s))) }
    , format = function (s, c) { return s.replace(/{(\w+)}/g, function (m, p) { return c[p]; }) }
    $.ajax({
		url: '/service/search/structured.json',
	    type: 'GET',
	    data: params,
	    success:function(response) {
	    	var table = toHtml(model, response.rows);
	        var ctx = { worksheet: worksheet, table: table };
	        var data = base64(format(template, ctx));
	        if (window.navigator.msSaveBlob){
	            var blob = b64toBlob(data);
	            window.navigator.msSaveBlob(blob, filename);
	        } else {
	            var alink = $('<a style="display:none"></a>').appendTo('body');
	            alink[0].href = uri + data;
	            alink[0].download = filename;
	            alink[0].click();
	            alink.remove();
	        }
	    }
	});        
}
function toHtml(model, rows){
    var data = ['<table border="1" rull="all" style="border-collapse:collapse">'];
    var trStyle = 'height:32px';
    var tdStyle0 = 'vertical-align:middle;padding:0 4px';
    data.push('<tr style="'+trStyle+'">');
    data.push('<th style="'+tdStyle+'">ID</th>');
    for(var i=0; i<model.fields.length; i++){
        var tdStyle = tdStyle0 + ';width:200px;';
        data.push('<th style="'+tdStyle+'">'+model.fields[i].label+'</th>');
    }
    data.push('</tr>');
    $.map(rows, function(row){
        data.push('<tr style="'+trStyle+'">');
        data.push('<td style="'+tdStyle0+'">'+row.id+'</td>');
        for(var i=0; i<model.fields.length; i++){
            var field = model.fields[i];
            var value = row[field.qname];
            if(value) data.push('<td style="'+tdStyle0+'">'+value+'</td>');
            else data.push('<td style="'+tdStyle0+'"></td>');
        }
        data.push('</tr>');
    });
    data.push('</table>');
    return data.join('');
}