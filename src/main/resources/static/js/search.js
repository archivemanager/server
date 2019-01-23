var query = $.urlParam('query');
$(window).load(function() {
	$('#dg').datagrid('options').url = '/service/search/results.json';
	$('#attribute').datagrid('options').url = '/service/search/attribute/values.json';
	$('#attribute-qname').combobox({
		onChange:function(newValue,oldValue) {
			$('#attribute').datagrid('load',{
				qname:newValue
			});
		}
	});
	doSearch();
	$(".loader").fadeOut("slow");
});
$(function(){
	$('#dg').datagrid({
		view: cardview,
		striped:true,
		pagination:true, 
		fit:true,
		fitColumns:true,
		singleSelect:true,
		pageSize:20,
		onLoadSuccess: function(data) {
			if(data.attributes && data.attributes.length > 0) {
				$('#attribute').datagrid('load',{
					qname:$('#attribute-qname').val(),
					query:$('#att-query').val()
				});
			}
			var breadcrumbHtml = '';
			for(i=0; i < data.breadcrumb.length; i++) {
				var crumb = data.breadcrumb[i];
				if(crumb.query) breadcrumbHtml += '<a class="breadcrumb-entry" href="#" onclick="doClickCrumb(\''+crumb.query+'\');">'+crumb.label+'</a>';
				else breadcrumbHtml += '<a class="breadcrumb-entry" href="#" onclick="doClickCrumb();">'+crumb.label+'</a>';
				if(i < data.breadcrumb.length-1) breadcrumbHtml += '<svg aria-hidden="true" data-prefix="fac" data-icon="dot" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 512" class="svg-inline--fa fa-dot fa-w-20"><path fill="currentColor" d="M256,176c44.2,0,80,35.8,80,80s-35.8,80-80,80s-80-35.8-80-80S211.8,176,256,176z" class=""></path></svg>';
			}
			$('#breadcrumb').empty();
			$('#breadcrumb').append(breadcrumbHtml);
		}
    });
	$('#attribute').datagrid({		
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
	       	qname:$('#attribute-qname').val()
	    },
	    onSelect:function(index,node) {
	    	if(query && query.length > 0)
	    		query = query + ' ' + node.query;
	    	else
	    		query = node.query;
	    	doSearch();
	    }
	});
	$('#search-body').tabs({
		showHeader:false,
		fit:true
	});
	$('#att-query').textbox({
		onChange(newValue, oldValue) {
			$('#attribute').datagrid('load',{
				qname:$('#attribute-qname').val(),
				query:$('#att-query').val()
			});
		}
	})
});
function doAttribute(sort) {
	$('#attribute').datagrid('load',{
		sort: sort,
		qname: $('#attribute-qname').val()
	});
}
function doSearch() {	
	$('#dg').datagrid('load', {
		sort: $('#sort').val(),
		direction: $('#direction').val(),
        qname: 'openapps_org_repository_1_0_item',
        query: query
    });
	$('#search-body').tabs('select', 'search-results-panel');
}
function doClickCrumb(crumbQuery) {
	query = crumbQuery;
	doSearch();
}
function doSearchQuery() {
	if(query && query.length > 0)query = query+' '+$('#query').val();
	else query = $('#query').val();
	doSearch();
}
function doSelectAttribute(newQuery) {
	if(query && query.length > 0)query = query+' '+newQuery;
	else query = newQuery;
	doSearch();
}
function doViewDetail(id) {
	var html = '<ul class="easyui-datalist">';
	$.ajax({
        type: "GET",
        url: '/app/search/detail.json?id='+id,
        success: function(data) {
        	$('#detail-name').html(data.name);
        	
        	if(data.collectionName && data.collectionName != 'null') $('#detail-collection').html(data.collectionName);
        	else $('#detail-collection-row').hide();
        	        	
        	if(data.dateExpression) $('#detail-date-expression').html(data.dateExpression);
        	else $('#detail-date-expression-row').hide();
        	
        	if(data.contentType) $('#detail-content-type').html(data.contentType);
        	else $('#detail-content-type-row').hide();
        	
        	if(data.language) $('#detail-language').html(data.language);
        	else $('#detail-language-row').hide();
        	
        	if(data.container) $('#detail-container').html(data.container);
        	else $('#detail-container-row').hide();

        	if(data.description && data.description != 'null') $('#detail-description').html(data.description);
        	else $('#detail-description-row').hide();

        	if(data.summary) $('#detail-summary').html(data.summary);
        	else $('#detail-summary-row').hide();
        	
        	if(data.abstractNote) $('#detail-abstract-note').html(data.abstractNote);
        	else $('#detail-abstract-note-row').hide();
        	
        	$('#search-detail-path').empty();
			$('#search-detail-path').append(writePath(data.data));
			
        	if((data.subjects && data.subjects.length > 0) || (data.people && data.people.length > 0)
        			|| (data.corporations && data.corporations.length > 0) || (data.weblinks && data.weblinks.length > 0)
        			|| (data.digitalObjects && data.digitalObjects.length > 0)) {
	        	var detailAttributeHtml = '<div id="detail-attribute-accordian">';	        	
				if(data.subjects && data.subjects.length > 0) {
					detailAttributeHtml += '<div style="padding:10px 0;" data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="Subjects" style="width:100%;">';
		        	for(i=0; i < data.subjects.length; i++) {
						var attribute = data.subjects[i];					
						detailAttributeHtml += '<li>'+attribute.name+'</li>';										
					}
		        	detailAttributeHtml += '</ul></div>';
				}
				if(data.people && data.people.length > 0) {
					detailAttributeHtml += '<div style="padding:10px 0;" data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="People" style="width:100%;">';
		        	for(i=0; i < data.people.length; i++) {
						var attribute = data.people[i];					
						detailAttributeHtml += '<li>'+attribute.name+'</li>';										
					}
		        	detailAttributeHtml += '</ul></div>';
				}
				if(data.corporations && data.corporations.length > 0) {
					detailAttributeHtml += '<div style="padding:10px 0;" data-options="collapsed:false,collapsible:false"><ul class="detail-attribute-list" class="easyui-datalist" title="Corporations" style="width:100%;">';
		        	for(i=0; i < data.corporations.length; i++) {
						var attribute = data.corporations[i];					
						detailAttributeHtml += '<li>'+attribute.name+'</li>';										
					}
		        	detailAttributeHtml += '</ul></div>';
				}
				detailAttributeHtml += '</div>';				
				$('#collection-detail-attributes').empty();
				$('#collection-detail-attributes').append(detailAttributeHtml);
				
				$('.detail-attribute-list').datalist({
			        lines: true
			    });
				
				var viewerNavigationHtml = '<div id="viewer-navigation-accordian">';
			    if((data.weblinks && data.weblinks.length > 0) || (data.digitalObjects && data.digitalObjects.length > 0)) {
			    	viewerNavigationHtml += '<div data-options="collapsed:false,collapsible:false"><ul class="viewer-attribute-list" class="easyui-datalist" style="width:100%;">';
		        	for(i=0; i < data.weblinks.length; i++) {
						var attribute = data.weblinks[i];					
						viewerNavigationHtml += '<li url="'+attribute.url+'">'+attribute.url+'</li>';									
					}
		        	for(i=0; i < data.digitalObjects.length; i++) {
						var attribute = data.digitalObjects[i];					
						viewerNavigationHtml += '<li url="'+attribute.url+'">'+attribute.url+'</li>';										
					}
		        	viewerNavigationHtml += '</ul></div>';
				}
				viewerNavigationHtml += '</div>';
				$('#detail-object-navigation').empty();
				$('#detail-object-navigation').append(viewerNavigationHtml);
				
				$('#detail-object-viewer').empty();
			    $('#detail-object-viewer').html('<img style="display:block;margin-top:130px;margin-bottom:130px;margin-right:auto;margin-left:auto;" src="/images/logo/ArchiveManager200.png" />');
			    
				$('.viewer-attribute-list').datalist({
			        lines: true,
			        showHeader:false,
			        onSelect:function(index,node) {
			        	doViewDigitalObject(node.value);
			        }
			    });			    
        	}
        }
    });
	$('#search-body').tabs('select', 'search-detail-panel');
}
function doViewDigitalObject(url) {
	var html = '';
	if(endsWith(url, ".pdf")) {
		html += '<object data="'+url+'" type="application/pdf" style="height:100%;width:100%;"></object>';				
	} else if(endsWith(url, ".mp4")) {
		html += '<video id="my_video_1" class="video-js" style="height:100%;width:100%;" controls="" preload="auto" data-setup="{}"> <source src="'+url+'" type="video/mp4"> </video>';
	} else if(endsWith(url, ".jpg")) {
		html += '<img style="height:100%;width:100%;" src="'+url+'" />';
	} else {
		html += '<iframe style="width:100%;height:100%;border:0;" src="'+url+'"></iframe>';
	}
	$('#detail-object-viewer').empty();
    $('#detail-object-viewer').html(html);
}
var cardview = $.extend({}, $.fn.datagrid.defaults.view, {
	renderRow: function(target, fields, frozen, rowIndex, rowData){
		var cc = [];
        //cc.push('<td colspan=' + fields.length + '>');
        if (!frozen && rowData.id) {
        	cc.push('<table class="search-result">');
        	cc.push('<tr>');
        	cc.push('<td><span style="float:left;margin-right:8px;font-size:12px;">('+rowData.id+')</span><div class="name" onclick="doViewDetail(\''+rowData.id+'\');">'+rowData.openapps_org_system_1_0_name+'</div></td>');
        	cc.push('<td style=""><div class="dateExpression">'+sanitize(rowData.openapps_org_repository_1_0_date_expression)+'</div></td>');
        	cc.push('</tr>');        	
        	cc.push('<tr><td colspan="2"><div class="description">'+sanitize(rowData. openapps_org_repository_1_0_general_note)+'</div></td></tr>');
        	cc.push('<tr>');
        	cc.push('<td colspan="2">');
        	cc.push(writePath(rowData));
        	cc.push('</td>');
        	cc.push('</tr>');
        	cc.push('</table');
        }
        //cc.push('</td>');
        return cc.join('');
	}
});
function writePath(rowData) {
	var cc = [];
	if(rowData.openapps_org_system_1_0_path && rowData.openapps_org_system_1_0_path_labels) {        		
		cc.push('<div class="entity-path">');        		
		var labels = rowData.openapps_org_system_1_0_path_labels.split('__');
		for(i=0; i < labels.length; i++) {
			if(i < labels.length-1 && labels[i].length > 0) {
    			if(i == 0) cc.push('<i class="far fa-folder-open"></i>');
    			else cc.push('<i class="fas fa-circle"></i>');
			}
			cc.push('<span class="entity-path-value">'+labels[i]+'</span>');
		}
		cc.push('</div>');        		
	}
	return cc.join('');
}
function sanitize(input) {
	if(input) return input;
	return '&nbsp;';
}
function handleAttributeKeyPress(event) {
	var key = e.keyCode || e.which;
    if (key == 13) {
    	$('#attribute').datagrid('load',{
			qname:$('#attribute-qname').val(),
			query:$('#att-query').val()
		});
    }
}