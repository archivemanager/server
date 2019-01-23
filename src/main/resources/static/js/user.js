$(window).load(function() {
	$('#passwordWindow').window({
		title: 'Change Password',
	    closed: true,
	    modal:true,
	    maximizable:false,
	    minimizable:false,
	    collapsible:false
	});
	$(".loader").fadeOut("slow");
});
$(function() {	
	$('#error').hide();
	
	$('#application-body').tabs({
		showHeader:false
	});
	$('#application-properties').tabs({
		showHeader:false
	});
	$('#user-navigation').tabs({
		showHeader:false
	});	
	$('#userGrid').datagrid({
		url:'/app/users/search.json',
        fitColumns: true,
        showHeader: false,
        autoRowHeight: false,
        striped: true,
        fit: true,
        noWrap: false,
        pagination:true, 
        rownumbers:false,
        singleSelect:true, 
        pageSize:20,
        columns:[[
            {field:'name',title:'Name',width:'100%'}
        ]],
        queryParams: {
        	qname:'openapps_org_system_1_0_user',
    		query:$('#query').val()
        },
        onSelect:function(index,node) {
        	$('#rem-button').show();
        	$('#password-button').show();
        	$('#save-button').show();
  	  		select(node);
        }
	});
});
function addEntity() {
	openEntityWindow(null, 'openapps_org_system_1_0_user', null);
}
function doAddEntity() {
	var name = getPropertyValue('openapps_org_system_1_0_name');
	if(entity.qname == 'openapps_org_system_1_0_user') {
		$('#userGrid').datagrid('appendRow',
			{id:entity.id,name:name}
		);
		$('#userGrid').datagrid('unselectAll');
	} else {
		$('#assocSelectionGrid').datagrid('appendRow',
			{id:entity.id,openapps_org_system_1_0_name:name}
		);
	}
}
function doUpdateEntity() {
	if(entity.qname == 'openapps_org_system_1_0_user') {
		var name = getPropertyValue('openapps_org_system_1_0_name');
		var repoNode = $('#userGrid').datalist('getSelected');
		var nodeIndex = $('#userGrid').datalist('getRowIndex', repoNode);		
	    if(repoNode.text != name) { 
	    	var newRow = [];
			newRow['name'] = name;
	    	$('#userGrid').datalist('updateRow', {
	       		index : nodeIndex,
	       		row : newRow
	        });
	    }
	}
}
function doRemoveEntity() {
	var selection = $('#userGrid').datagrid('getSelected');
	var index = $('#userGrid').datagrid('getRowIndex', selection);
    if(index > -1) {
    	$('#userGrid').datagrid('deleteRow',index);
    }
	
}
function getSource(qname) {
	if(qname == 'openapps_org_system_1_0_roles') {
		var selected = $('#userGrid').datalist('getSelected');
		if(selected) return selected.id;
	}
	return null;
}
function doSearch() {
	$('#userGrid').datalist('load',{
		qname: 'openapps_org_system_1_0_user', 
    	query: $('#query').val()
	});
}
function changePassword() {
	$('#passwordWindow').window("center").window('open');
}
function doSavePassword() {
	var selected = $('#userGrid').datalist('getSelected');
	$.ajax({
		type: "POST",
	    url: '/app/users/password/save.json',
	    data: {
	    	'id':selected.id,
	    	'password':$('#passwordWindow_password').val()
	    },
	    success: function(data) {
	      	$('#passwordWindow').window('close');
	    }
	});
}
function selectAssociation(id) {
	
}
function formatRoles(val,row) {
	var str = '';
	for ( var i = 0, l = val.length; i < l; i++ ) {
		str += val[i].name + ", ";
	}
	return str.substring(0, str.length-2);
	
}