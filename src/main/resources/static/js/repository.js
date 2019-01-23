var editors = [];
var itemQnames;
var view = 'repository';
$(window).load(function() {	
	var ajaxReq = $.ajax({
        url: '/service/repository/item/qnames.json',
        type: 'GET',
        cache: false,
        contentType: false,
        processData: false
    });
    ajaxReq.done(function(qnames) {
    	itemQnames = qnames;
    });    
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
	$('#repositoryTree').tree({		
    	singleSelect:true,
    	method:'get',
    	dnd:true,
    	url:'/app/repository/taxonomy.json',
    	onSelect:function(node) {    		
	        select(node, function() {
		    	$('#rem-button').show();
		    	if(node.iconCls == 'icon-collection') {
			       	$('#add-repository-button').show();
			      	$('#add-collection-button').hide();
			      	$('#add-category-button').hide();
			      	$('#add-item-button').hide();
			      	$('#edit-button').show();
			      	$('#rem-button').show();
			      	$('#import-button').show();
			    } else {
			       	$('#add-repository-button').show();
			      	$('#add-collection-button').show();
			      	$('#add-category-button').hide();
			      	$('#add-item-button').hide();
			      	$('#edit-button').hide();
			      	$('#rem-button').show();
			      	$('#import-button').hide();
			    }
		    	$('#save-button').show();
			    $('#rem-button').show();
		    });
    	},
    	onDragOver:function(target, source, point){
    		var targetObj = $('#repositoryTree').tree('getNode', target)
			if(targetObj.iconCls == 'icon-repository')
				return true;
			else
				return false;
		},
		onDrop: function(target, source, point) {
    		var targetId = $('#repositoryTree').tree('getNode', target).id;
		    $.ajax({
		    	url: '/service/repository/collection/repository/switch.json',
		        type: 'post',
		        dataType: 'json',
		        data:{id:source.id,source:targetId},
		        success: function(result) {
		        	if(result.status != 200) {
		        		$.messager.alert({
		        			title: 'Error',
		                    msg: result.errorMsg
		                });
		            } else {
		              	entity = result;
		            }
		        }                
    		});
    	}
	});
	$('#collectionTree').tree({
		animate:true,		
		method:'get',
		dnd:true,
		onSelect:function(node) {
			select(node, function() {
				if(node.iconCls == 'icon-collection') {
					$('#add-repository-button').hide();
			      	$('#add-collection-button').hide();
			      	$('#add-category-button').show();
			      	$('#add-item-button').show();
			      	$('#edit-button').hide();
			      	$('#rem-button').hide();
				} else if(node.iconCls == 'icon-category') {
					$('#add-repository-button').hide();
			  	   	$('#add-collection-button').hide();
			  	   	$('#add-category-button').show();
			  	   	$('#add-item-button').show();
			  	   	$('#edit-button').hide();
			  	   	$('#rem-button').show();
				} else {
					$('#add-repository-button').hide();
			  	   	$('#add-collection-button').hide();
			  	   	$('#add-category-button').hide();
			  	   	$('#add-item-button').hide();
			  	   	$('#edit-button').hide();
			  	   	$('#rem-button').show();
				}
				$('#rem-button').show();
				$('#application-body').tabs('select', 'form-screen');
		    });					
    	},
    	onDragOver:function(target, source, point){
    		var targetObj = $('#collectionTree').tree('getNode', target)
			if(targetObj.iconCls == 'icon-repository' || targetObj.iconCls == 'icon-collection' || targetObj.iconCls == 'icon-category')
				return true;
			else
				return false;
		},
		onDrop: function(target, source, point) {
    		var targetId = $('#collectionTree').tree('getNode', target).id;
		    $.ajax({
		    	url: '/service/repository/entity/category/switch.json',
		        type: 'post',
		        dataType: 'json',
		        data:{id:source.id,source:targetId},
		        success: function(result) {
		        	if(result.status != 200) {
		        		$.messager.alert({
		        			title: 'Error',
		                    msg: result.errorMsg
		                });
		            } else {
		              	entity = result;
		            }
		        }                
    		});
    	}
	});	
    //$('#mainLayout').layout();    
});
function uploadCollection() {
	$('#application-body').tabs('select', 'upload-screen');
	$('#save-button').hide();
	$('#import-button').hide();
}
function doCollectionUpload() {
	var form = new FormData($("#upload-form")[0]);
	$.ajax({
		method: 'POST',
        type: 'POST',
        url: '/app/repository/collection/upload.json',
        data: form,
        cache: false,
        contentType: false,
        processData: false,
        success: function(result) {
        	$('#importNavigation').tree({		
            	singleSelect:true,
            	method:'get',
            	nowrap:false,
            	url:'/app/repository/collection/upload/fetch.json',
            	onSelect:function(node) {    		
        	        $.ajax({
        				url: '/app/repository/collection/upload/entity/fetch.json?id=' + node.id,
        		        type: 'GET',
        		        cache: false,
        		        contentType: false,
        		        processData: false,
        		        success: function(result) {
        		        	var content = '<table id="report-element">';        		        	
        		        	for(i=0; i < result.properties.length; i++) {
        			        	var property = result.properties[i];
        			        	content += '<tr class="attribute-row">';
        				  		content += '<td class="attribute-label">'+property.label+'</td>';
        				  		if(property && property.value != null) content += '<td class="attribute-value">'+property.value+'</td>';
        				  		else content += '<td class="attribute-value">&nbsp;</td>';
        				  		content += '</tr>';
        			        }
        		        	content += '</table>';
        		        	$('#importDisplay').empty();
        		        	$('#importDisplay').append(content);
        		        }
        		    });
            	}
            });
       		$('#import-viewer').show(); 
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
	var node = $('#repositoryTree').tree('getSelected');
	if(!node) {
		$.messager.show({
          	 title: 'Missing Name',
               showType:'show',
               msg: 'please enter a collection name'
           });
	} else {
		$.ajax({
			method: 'POST',
	        type: 'POST',
	        data:'collectionId='+node.id,
	        url: '/app/repository/collection/upload/save.json',
	        success: function(result) {	
	        	$.messager.show({
	           	 title: 'Success',
	                showType:'show',
	                msg: 'collection saved successfully'
	            });
	        }
		});
	}
}
function doAddEntity() {
	var name = getPropertyValue('openapps_org_system_1_0_name');
	if(entity.qname == 'openapps_org_repository_1_0_repository') {				
		var node = $('#repositoryTree').tree('getRoot');
	    if(node) {
	    	$('#repositoryTree').tree('insert', {
	    		before: node.target,
	    		data: {
	    			id: entity.id,
	    			text: name,
	    			state: 'open',
	    			iconCls: 'icon-repository'
	    		}
	    	});
	    } else $('#repositoryTree').tree('reload');
	    
	} else if(entity.qname == 'openapps_org_repository_1_0_collection') {
		var node = $('#repositoryTree').tree('getSelected');
		$('#repositoryTree').tree('append', {
    		parent: node.target,
    		data: {
    			id: entity.id,
    			text: name,
    			state: 'open',
    			iconCls: 'icon-collection'
    		}
    	});
	} else {
		var node = $('#collectionTree').tree('getSelected');
		$('#collectionTree').tree('append', {
    		parent: node.target,
    		data: {
    			id: entity.id,
    			text: name,
    			state: 'open',
    			iconCls: 'icon-'+getLocalName(entity.qname)
    		}
    	});
	}
	$('#entityWindow').window('close');
}
function doUpdateEntity() {
	var name = getPropertyValue('openapps_org_system_1_0_name');
	if(entity.qname == 'openapps_org_repository_1_0_repository' || entity.qname == 'openapps_org_repository_1_0_collection') {				
	    var repoNode = $('#repositoryTree').tree('find', entity.id);                    	 
        if(repoNode.text != name) {
        	$('#repositoryTree').tree('update', {
                target: repoNode.target,
                text: name
            });
        }
	} else {
		var repoNode = $('#collectionTree').tree('find', entity.id);                    	 
        if(repoNode.text != name || repoNode.qname != entity.qname) {
        	var localName = getLocalName(entity.qname);
        	$('#collectionTree').tree('update', {
                target: repoNode.target,
                iconCls: 'icon-'+localName,
                text: name
            });
        }
	}
}
function doRemoveEntity() {
	var repoNode = $('#repositoryTree').tree('find', entity.id);
    if(repoNode) {
    	$('#collectionTree').tree('remove',repoNode.target);
    }
    var collNode = $('#collectionTree').tree('find', entity.id);
    if(collNode) {
    	$('#repositoryTree').tree('remove',collNode.target);
    }
    $('#rem-button').hide();
    $('#add-collection-button').hide();
}
function doSearch() {
	var query = $('#query').val();
	$('#repositoryTree').tree({
		url:'/app/repository/taxonomy.json?query='+query
	});
}
function getSource() {
	if(view == 'repository') {
		 var selection = $('#repositoryTree').tree('getSelected');
		 return selection.id;
	} else {
		var selection = $('#collectionTree').tree('getSelected');
		return selection.id;
	}
}
function navigate(name) {
	var node = $('#repositoryTree').tree('getSelected');
	var url = '/app/repository/collection/taxonomy.json?collection='+node.id
	$('#collectionTree').tree({'url': url});
    if(name == 'repository') {    	
    	$('#add-repository-button').hide();
	    $('#add-collection-button').hide();
	    $('#add-category-button').show();
	    $('#add-item-button').show();
	    $('#edit-button').hide();
	    $('#rem-button').hide();
	    view = 'repository';
    	$('#collection-tree-navigation').tabs('select', 'repository-tree-panel');
    } else {
    	$('#add-repository-button').hide();
	    $('#add-collection-button').hide();
	    $('#add-category-button').show();
	    $('#add-item-button').show();
	    $('#edit-button').hide();
	    $('#rem-button').show();
	    view = 'collection';
    	$('#collection-tree-navigation').tabs('select', 'collection-tree-panel');
    }
}

  