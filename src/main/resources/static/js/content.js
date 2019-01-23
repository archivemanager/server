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
		url:'/content/search.json',
        fitColumns: true,
        showHeader: false,
        autoRowHeight: false,
        striped: true,
        fit: true,
        noWrap: false,
        onSelect:function(index,node) {
        	select(node);
        }
	});
});
function addEntityWindow() {
	var qname = $('#qname').val();
	openEntityWindow(null, qname, null);
}
function doUpdateEntity() {
	if(qname == 'openapps_org_content_1_0_digital_content' || 
			qname == 'openapps_org_content_1_0_web_content' || 
			qname == 'openapps_org_content_1_0_web_link' || 
			qname == 'openapps_org_content_1_0_exhibition' ||
			qname == 'openapps_org_content_1_0_event') {
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
	if(qname == 'openapps_org_content_1_0_digital_content' || 
			qname == 'openapps_org_content_1_0_web_content' || 
			qname == 'openapps_org_content_1_0_web_link' || 
			qname == 'openapps_org_content_1_0_exhibition' ||
			qname == 'openapps_org_content_1_0_event') {
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
	if(qname == 'openapps_org_content_1_0_digital_content' || 
			qname == 'openapps_org_content_1_0_web_content' || 
			qname == 'openapps_org_content_1_0_web_link' || 
			qname == 'openapps_org_content_1_0_exhibition' ||
			qname == 'openapps_org_content_1_0_event') {
		return null;
	} else return $('#classificationGrid').datagrid('getSelected').id;
}
function doSearch() {
	var qname = $('#qname').val();
	var query = $('#query').val();
	$('#classificationGrid').datagrid('load',{
		qname:qname,
		query:query
	});	
}