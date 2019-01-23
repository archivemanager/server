var model;
var uploaded = false;
$(window).load(function() {
	$(".loader").fadeOut("slow");
});
$(function() {
	$('#qnames').combobox({
		onChange:function(newValue, oldValue) {
			getModel(newValue, function(model) {
				drawForm(model);
				doSearch(false);
			});
		}
	});	
	$('#reports').combobox({
		onChange:function(newValue, oldValue) {
			
		}
	});
	$('#reporting-grid').datagrid({ 
		view: dataview,
        toolbar: '#toolbar',        
        idField: 'id',
        autoRowHeight: false,
        pageSize: 20,
        striped: true,
        pagination:true,
        fitColumns: true,
        showHeader: false,
        columns:[[
            {field:'id',title:'ID'}
        ]]
	});
	$('#data-navigation').tabs();
	
	getModel($('#qnames').val(), function(m) {
		model = m
		drawForm(model);
		doSearch(false);
	});
})
function doSearch(uploaded) {
	this.uploaded = uploaded;
	var report = $('#reports').val();
	var qname = $('#qnames').val();
	var query = $('#query').val();
	getModel(qname, function(m) {
		model = m;
		var data = {};
		data['report'] = report;
		data['qname'] = qname;
		data['query'] = query;
		if(uploaded) data['uploaded'] = uploaded;
		for(var fieldKey in model.fields) {
			var field = model.fields[fieldKey];
			var value = $('#'+field.qname).val();
			if(value) data[field.qname] = value;
		}		
		$('#reporting-grid').datagrid({
			url: '/app/data/search.json',
			queryParams: data
		});
		if(uploaded) $('#upload-save').show();
    	else $('#upload-save').hide();
		//drawForm(model);
	});			
}
function openExcel() {
	var report = $('#reports').val();
	var qname = $('#qnames').val();
	var query = $('#query').val();
	getModel(qname, function(m) {
		model = m;
		var data = {};
		data['qname'] = qname;
		data['query'] = query;
		data['report'] = report;
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
    $.ajax({
		url: '/app/data/excel/generate.json',
	    type: 'GET',
	    data: params,
	    success:function(response) {
	    	
	    	window.open('/app/data/fetch/'+response);
	    }
	});        
}
function doUpload() {
	var form = new FormData($("#upload-form")[0]);
	$.ajax({
		method: 'POST',
        type: 'POST',
        url: '/app/data/upload.json',
        data: form,
        cache: false,
        contentType: false,
        processData: false,
        success: function(result) {
        	doSearch(true);
        	$('#save-import-button').show();
        },
        error: function(result, statusCode, errorMsg) {
            $.messager.alert({
                title: 'Error',
                msg: 'There was an internal error processing this request: ' + statusCode + ': ' + errorMsg
            });
        }
    });
}
function doSaveImport() {
	$.ajax({
		method: 'POST',
        type: 'POST',
        url: '/app/data/upload/save.json',
        success: function(result) {	
        	$.messager.show({
           	 title: 'Success',
                showType:'show',
                msg: 'collection saved successfully'
            });
        }
	});
}
function editEntity(id) {
	var ajaxReq = $.ajax({
		url: '/service/repository/entity/fetch.json?id=' + id,
        type: 'GET',
        cache: false,
        contentType: false,
        processData: false
    });
    ajaxReq.done(function(response) {
    	$('#model-associations').show();
    	openEntityWindow('', response.qname, '');   
    });
    ajaxReq.fail(function(jqXHR) {
        message('Entity Selection Failed', 'unable to locate entity for id:' + node.id + ' text:' + node.text);
    });
}
function removeEntity(id) {
	var ajaxReq = $.ajax({
		url: '/service/repository/entity/fetch.json?id=' + id,
        type: 'GET',
        cache: false,
        contentType: false,
        processData: false
    });	
}