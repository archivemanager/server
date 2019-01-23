$(window).load(function() {
	$('#dg').datagrid({
		url:'/app/indexing/model/search.json?qname='+$.urlParam('qname')        
    });
	$('#field-list').datalist({
        lines: true,
        textField:'name',
        valueField:'id',
        onClickRow:function(index,row) {		        	
        	$('#tabs2').tabs('select', 'field-form');
        }
    });
	$('#source-list').datalist({
        lines: true,
        textField:'name',
        valueField:'id',
        onClickRow:function(index,row) {		        	
        	$('#tabs2').tabs('select', 'association-form');
        }
    });	
	$('#target-list').datalist({
        lines: true,
        textField:'name',
        valueField:'id',
        onClickRow:function(index,row) {		        	
        	$('#tabs2').tabs('select', 'association-form');
        }
    });	
	$('#field-value-list').datalist({
        lines: true,
        textField:'name',
        valueField:'id',
        onClickRow:function(index,row) {		        	
        	//$('#tabs2').tabs('select', 'association-form');
        }
    });
	$('#dg').datagrid('resize',{height:$(window).height()-250});
	$(".loader").fadeOut("slow");
});
function navigate(id) {
	$('#tabs1').tabs('select', id);
}
function addTransaction(){
	$('#dg').datagrid('gotoPage', 1);
	$('#dlg').dialog('open').dialog('center').dialog('setTitle','Add Model');
}
function doSearch() {
	//$('#dg').datagrid('options').url = '/service/search/entity.json?&page=1&rows=20';
	$('#dg').datagrid('load',{
		query: $('#search_type').val()
	});
}
function doEditModel(qname) {
	var record = $('#dg').datagrid('getSelected');
	$.ajax({
        type: "GET",
        url: '/service/dictionary/model/fetch.json?qname='+record.qname,
        success: function(data) {
        	$('#tabs1').tabs('select', 'model-form');
        	var fieldData = [];
        	for(i=0; i < data.fields.length; i++) {
				var attribute = data.fields[i];
				fieldData.push({ id: attribute.qname, name: attribute.name});
			}
        	$('#field-list').datalist('loadData', fieldData);
        	var sourceData = [];
        	for(i=0; i < data.sourceRelations.length; i++) {
				var attribute = data.sourceRelations[i];
				sourceData.push({ id: attribute.qname, name: attribute.name});
			}
        	$('#source-list').datalist('loadData', sourceData);
        	var targetData = [];
        	for(i=0; i < data.targetRelations.length; i++) {
				var attribute = data.targetRelations[i];
				targetData.push({ id: attribute.qname, name: attribute.name});
			}
        	$('#target-list').datalist('loadData', targetData);
        }
	});	
}
function doSaveModel(){
	$.ajax({
		type: "POST",
        url: '/app/indexing/model/save.json',
        data: $("#fm").serialize(),
        success: function(data) {
            if(data.status == 200) {
            	$('#user-msg').toggleClass('success-msg');
            	$('#user-msg').html('user saved successfully');
            } else {
            	$('#user-msg').toggleClass('failure-msg');
            	$('#user-msg').html(data.message);
            }
            setTimeout(function() {
            	$('#user-msg').html('');
            }, 4000);
        }
    });
}
function doAddModel(){	
	var data = $("#fm2").serialize();
	var dateVal = $('#date').val();
	if(dateVal && dateVal.length > 0) {
		var date = dateVal.split("/");
		dateVal = date[2]+date[0]+date[1];
	} else dateVal = 0;
	data += "&date="+dateVal
	$.ajax({
        type: "POST",
        url: '/app/indexing/model/add.json?dictionaryQname='+$.urlParam('qname'),
        data: data,
        success: function(data) {
        	$('#dg').datagrid('gotoPage', 1);
        	$('#dlg').dialog('close');
        }
      });
}
function doRemoveModel(transactionId){
	var record = $('#dg').datagrid('getSelected');
	$.ajax({
        type: "POST",
        url: '/app/indexing/model/remove.json?modelQname='+record.qname,
        success: function(data) {
        	$('#dg').datagrid('gotoPage', 1);
        	$('#dlg').dialog('close');
        }
      });
}
function doIndex(qname){
	$.ajax({
		method: 'POST',
	    type: 'POST',
        url: '/app/indexing/model/index.json?dictionaryQname='+$.urlParam('qname')+'&modelQname='+qname,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
        	$('#dg').datagrid('reload');
        }
    });
}
function doIndexAll(){
	$.ajax({
		method: 'POST',
	    type: 'POST',
        url: '/app/indexing/model/index-all.json?qname='+$.urlParam('qname'),
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
        	$('#dg').datagrid('reload');
        }
    });
}
function doCancel(){
	window.location.replace("/dictionary");
}
function formatIcons(value) {
	return '<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="doIndex(\''+value+'\')"><i class="fa fa-refresh" aria-hidden="true"></i> Index</a>';
}