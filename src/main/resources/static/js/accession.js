$(window).load(function() {	
	$(".loader").fadeOut("slow");
});
$(function() {
	$('#application-body').tabs({
		showHeader:false
	});	
	$('#application-properties').tabs({
		showHeader:false
	});
	$('#collection-tree-navigation').tabs({
		showHeader:false
	});
	$('#classificationGrid').datagrid({
		url:'/classification/search.json',
        fitColumns: true,
        showHeader: false,
        autoRowHeight: false,
        striped: true,
        fit: true,
        noWrap: false,
        onSelect:function(index,node) {
        	$('#rem-button').show();
        	$('#add-corporation-button').hide();
        	$('#add-person-button').hide();
        	$('#add-subject-button').hide();
        	if(node.type == 'openapps_org_classification_1_0_corporation')
        		$('#add-corporation-button').show();
        	if(node.type == 'openapps_org_classification_1_0_person')
        		$('#add-person-button').show();
        	if(node.type == 'openapps_org_classification_1_0_subject')
        		$('#add-subject-button').show();
	        select(node);
        }
	});
});
function addEntityWindow() {
	var qname = $('#qname').val();
	openEntityWindow(null, qname, null);
}
function doUpdateEntity() {
	if(qname == 'openapps_org_classification_1_0_corporation' || 
			qname == 'openapps_org_classification_1_0_person' || 
			qname == 'openapps_org_classification_1_0_subject') {
		var name = getPropertyValue('openapps_org_system_1_0_name');
		var selection = $('#classificationGrid').datagrid('getSelected');  
		var index = $('#classificationGrid').datagrid('getRowIndex', selection);
	    if(selection.text != name) {
	       	$('#classificationGrid').datagrid('updateRow', {
	       		index: index,
	            row: {name:name}
	        });
		}
	}
}
function addEntity() {
	openEntityWindow(null, $('#qname').val(), null);
}
function doAddEntity() {
	var name = getPropertyValue('openapps_org_system_1_0_name');
	var qname = entity.qname;
	if(qname == 'openapps_org_classification_1_0_corporation' || 
			qname == 'openapps_org_classification_1_0_person' || 
			qname == 'openapps_org_classification_1_0_subject') {
		$('#classificationGrid').datagrid('appendRow',
			{id:entity.id,name:name}
		);
	}
}
function doRemoveEntity() {
	var selection = $('#classificationGrid').datagrid('getSelected');
	var index = $('#classificationGrid').datagrid('getRowIndex', selection);
    if(index > -1) {
    	$('#classificationGrid').datagrid('deleteRow',index);
    }
	
}
function getSource(qname) {
	if(qname == 'openapps_org_classification_1_0_corporation' || 
			qname == 'openapps_org_classification_1_0_person' || 
			qname == 'openapps_org_classification_1_0_subject') 
		return null;
	else return $('#classificationGrid').datagrid('getSelected').id;
}
function doSearch() {
	var qname = $('#qname').val();
	var query = $('#query').val();
	$('#classificationGrid').datagrid('load',{
		qname:qname,
		query:query
	});	
}