$(window).load(function() {
	$('#openapps_org_repository_1_0_collection_id').datagrid({
		url:'/service/search/attribute/values.json',
	    fitColumns: true,
	    showHeader: false,
	    autoRowHeight: false,
	    striped: true,
	    fit: true,
	    noWrap: false,
	    pagination:true,
	    pageSize:20,
	    columns:[[
            {field:'name',title:'Name'},
            {field:'count',title:'Count',align:'center'}
        ]],
	    queryParams: {
	       	qname:'openapps_org_repository_1_0_collection_id'
	    },
	    onSelect:function(index,node) {
	    	window.location = '/app/search?query='+node.query;
	    }
	});
	$('#openapps_org_classification_1_0_subjects').datagrid({
		url:'/service/search/attribute/values.json',
	    fitColumns: true,
	    showHeader: false,
	    autoRowHeight: false,
	    striped: true,
	    fit: true,
	    noWrap: false,
	    pagination:true,
	    pageSize:20,
	    columns:[[
            {field:'name',title:'Name'},
            {field:'count',title:'Count',align:'center'}
        ]],
	    queryParams: {
	       	qname:'openapps_org_classification_1_0_subjects'
	    },
	    onSelect:function(index,node) {
	    	window.location = '/app/search?query='+node.query;
	    }
	});
	$('#openapps_org_classification_1_0_people').datagrid({
		url:'/service/search/attribute/values.json',
	    fitColumns: true,
	    showHeader: false,
	    autoRowHeight: false,
	    striped: true,
	    fit: true,
	    noWrap: false,
	    pagination:true,
	    pageSize:20,
	    columns:[[
            {field:'name',title:'Name'},
            {field:'count',title:'Count',align:'center'}
        ]],
	    queryParams: {
	       	qname:'openapps_org_classification_1_0_people'
	    },
	    onSelect:function(index,node) {
	    	window.location = '/app/search?query='+node.query;
	    }
	});
	$('#openapps_org_classification_1_0_corporations').datagrid({
		url:'/service/search/attribute/values.json',
	    fitColumns: true,
	    showHeader: false,
	    autoRowHeight: false,
	    striped: true,
	    fit: true,
	    noWrap: false,
	    pagination:true,
	    pageSize:20,
	    columns:[[
            {field:'name',title:'Name'},
            {field:'count',title:'Count',align:'center'}
        ]],
	    queryParams: {
	       	qname:'openapps_org_classification_1_0_corporations'
	    },
	    onSelect:function(index,node) {
	    	window.location = '/app/search?query='+node.query;
	    }
	});
	$(".loader").fadeOut("slow");
});
function doSearch(grid, sort) {
	$('#'+grid).datagrid('load',{
		sort: sort,
		qname: grid
	});
}
