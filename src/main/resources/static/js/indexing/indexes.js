$(function() {	
	$('#error').hide();
});
$(window).load(function() {
	//$('#dg').datagrid('resize',{height:$(window).height()-250});
	$(".loader").fadeOut("slow");
});
function doSearch() {
	//$('#dg').datagrid('options').url = '/service/search/entity.json?&page=1&rows=20';
	$('#dg').datagrid('load',{});
}
function add(){
	$('#dlg').dialog('open').dialog('center').dialog('setTitle','Add Dictionary');
    $('#fm').form('clear');
}
function edit(){
	var record = $('#dg').datagrid('getSelected');
	window.location.href = "/app/indexing/edit?qname="+record.qname;
}

function destroy(){
	var row = $('#dg').datagrid('getSelected');
    if(row){
    	$.messager.confirm('Confirm','Are you sure you want to destroy this?',function(r){
    		if(r) {
	        	$.ajax({
	        		type: "POST",
	        	    url: '/app/indexing/remove.json?id='+row.id,
	        	    success: function(data) {
	        	    	$('#dg').datagrid('gotoPage', 1);
	        	    }
	        	});
	        }
    	});
    }
}
function doCancel() {
	$('#error').hide();
	$('#fm').form('clear');
	$('#dlg').dialog('close')
}
function formatRoles(val,row) {
	var str = '';
	for ( var i = 0, l = val.length; i < l; i++ ) {
		str += val[i].name + ", ";
	}
	return str.substring(0, str.length-2);
	
}