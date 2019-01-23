var loaded = false;
var selectedNode;
var type;
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
	$('#valueGrid').datagrid({
		fitColumns: true,
        showHeader: true,
        autoRowHeight: false,
        striped: true,
        fit: true,
        noWrap: false,
        columns:[[
            {field:'label',title:'Label',width:100},
            {field:'value',title:'Value',width:100},
            {field:'id',title:'', width:38,align:'center',formatter:function(value) {
            	var links = '<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="doRemoveValue('+value+')"><i class="fas fa-times-circle"></i></a>';
            	links += '<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="doBump('+value+')"><i class="fas fa-arrow-circle-up"></i></a>';
            	links += '<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="doDrop('+value+')"><i class="fas fa-arrow-circle-down"></i></a>';
            	return links;
            }}
        ]],
        onSelect:function(index,node) {
        	
        }
	});
	$('#repositoryTree').tree({
		title:'',
    	singleSelect:true,
    	method:'get',
    	dnd:true,
    	url:'/app/dictionary/taxonomy.json',
    	onSelect:function(node) {
    		selectedNode = node;
    		var ajaxReq = $.ajax({
				url: '/app/dictionary/fetch.json?id=' + node.id,
		        type: 'GET',
		        cache: false,
		        contentType: false,
		        processData: false
		    });
		    ajaxReq.done(function(response) { 
		    	var row0 = [];
		    	var row1 = [];
		        var row2 = [];
		        var row3 = [];
		        var row4 = [];
		        var row5 = [];
		        var row6 = [];
		        var row7 = [];		    				    
		    	for(var fieldKey in response.properties) {
		    		var field = response.properties[fieldKey];
		    	  	if(field.type == 'longtext' || field.type == 'mediumtext') {
		    			  row3.push('<div class="form-row">');
		    			  row3.push('<input id="'+field.qname+'" name="'+field.qname+'" type="text" style="width:100%;" />');
		    			  row3.push('</div>');
		    	  	} else if(field.type == 'smalltext') {		    	  
		    	  		  if(field.qname == 'openapps_org_dictionary_1_0_type') {
		    	  			  row5.push('<div class="form-cell-50">');
		    	  			  row5.push('<select id="'+field.qname+'" name="'+field.qname+'" class="easyui-combobox" style="width:100%;">');
		    	  			  row5.push('<option value=""></option>');
		    	  			  row5.push('<option value="null">null</option>');
		    	  			  row5.push('<option value="boolean">boolean</option>');
		    	  			  row5.push('<option value="integer">integer</option>');
		    	  			  row5.push('<option value="long">long</option>');
		    	  			  row5.push('<option value="double">double</option>');
		    	  			  row5.push('<option value="date">date</option>');
		    	  			  row5.push('<option value="double">double</option>');
		    	  			  row5.push('<option value="longtext">longtext</option>');
		    	  			  row5.push('<option value="mediumtext">mediumtext</option>');
		    	  			  row5.push('<option value="smalltext">smalltext</option>');
		    	  			  row5.push('<option value="serializable">serializable</option>');		    	  			  
		    				  row5.push('</select>');
		    				  row5.push('</div>');
		    			  } else {
		    				  row4.push('<div class="form-cell-50">');
		    			   	  row4.push('<input id="'+field.qname+'" name="'+field.qname+'" type="text" style="width:100%;" />');
		    			   	  row4.push('</div>');
		    			  }
		    	  	} else if(field.type == 'integer') {
		    	  	  		row6.push('<div class="form-cell-33">');
		    	  	  		row6.push('<input id="'+field.qname+'" name="'+field.qname+'" style="min-width:200px;width:100%;" />');
		    	  	  		row6.push('</div>');
		    	  	} else if(field.type == 'boolean') {
		    	  	  		row7.push('<div class="form-cell-25">');
		    	  	  		row7.push('<input id="'+field.qname+'" name="'+field.qname+'" type="checkbox" class="easyui-radio" style="width:20px;height:20px;" />');
		    	  	  		row7.push('</div>');		      
		    	  	}
		    	}
		    	var html = '';
		        if(row0.length > 0) html += '<div class="form-section">'+row0.join('')+'</div>';
		        if(row1.length > 0) html += '<div class="form-section">'+row1.join('')+'</div>';
		        if(row2.length > 0) html += '<div class="form-section">'+row2.join('')+'</div>';
		        if(row3.length > 0) html += '<div class="form-section">'+row3.join('')+'</div>';
		        if(row4.length > 0) html += '<div class="form-section">'+row4.join('')+'</div>';
		        if(row5.length > 0) html += '<div class="form-section">'+row5.join('')+'</div>';
		        if(row6.length > 0) html += '<div class="form-section">'+row6.join('')+'</div>';
		        if(row7.length > 0) html += '<div class="form-section">'+row7.join('')+'</div>';
		        $('#form').empty();
		        $('#form').append(html);
		        
		        for(var fieldKey in response.properties) {
		    		var field = response.properties[fieldKey];
		        	if(field.type == 'smalltext' || field.type == 'mediumtext' || field.type == 'longtext') {
			    		if(field.qname == 'openapps_org_dictionary_1_0_type') {
			    			$('#'+field.qname).combobox({
			  		    	  label:field.label,
			  		    	  labelPosition:'top',
			  		    	  valueField: 'value',
			  		    	  textField: 'text'
			  		    	});
			    		} else {
		    		    	$('#'+field.qname).textbox({
		    			      label:field.label,
		    			      labelPosition:"top"
		    			    });
		    		    }
		    		} else if(field.type == 'boolean') {
		    		    $('#'+field.qname).checkbox({
		    		      label: field.label,
		    		      labelPosition:'top'
		    		    });
		    		} else if(field.type == 'integer') {
		    		    $('#'+field.qname).numberspinner({
		    		      label:field.label,
		    		      labelPosition:"top",
		    		      min: 0,
		    		      max: 100,
		    		      increment:1
		    		    });
		    		}    	
		        }
		        var formData = [];
		        for(i=0; i < response.properties.length; i++) {
		        	var property = response.properties[i];
		        	if(property.type == 'boolean' && property.value == true) formData[property.qname] = 'on';
		        	else formData[property.qname] = property.value;
		        }		            
		        $('#form').form('load', formData);
			    $('#mainLayout').layout();
		    });
		    $('#add-model-button').hide();
		    $('#add-field-button').hide();
    		$('#add-relation-button').hide();   
		    if(selectedNode.type == 'field') {
		    	if(loaded) {
		    		$('#valueGrid').datagrid('load',{
			    		id: node.id
			    	});
		    	} else {
			    	$('#valueGrid').datagrid({
			    		url: '/app/dictionary/field/search.json',
			    		queryParams: {
			    			id: node.id
			    		}
			    	});
			    	loaded = true;
		    	}		    	
		    	$('#valueGridPanel').show();
		    } else {
		    	$('#valueGridPanel').hide();
		    	if(selectedNode.type == 'model') {
		    		$('#add-field-button').show();
		    		$('#add-relation-button').show();		    		
		    	} else if(selectedNode.type == 'relation') {
		    		$('#add-field-button').show();
		    		
		    	} else if(selectedNode.type == 'dictionary') {
		    		$('#add-model-button').show();
		    	}
		    }		    
		    $('#rem-button').show();		    
		    $('#application-body').tabs('select', 'form-screen');
    	},
    	onDragOver:function(target, source, point){
    		var targetObj = $('#repositoryTree').tree('getNode', target);
			if(targetObj.type == source.type)
				return true;
			else
				return false;
		},
		onDrop: function(target, source, point) {
    		var targetId = $('#repositoryTree').tree('getNode', target).id;
		    $.ajax({
		    	url: '/app/dictionary/switch.json',
		        type: 'post',
		        dataType: 'json',
		        data:{id:source.id,target:targetId},
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
});
function doSaveObject() {
	var data = $("#form").serializeArray();	
	for(i=0; i < data.length; i++) {
    	var property = data[i];
    	if(property.value == 'on') property.value = true;
    }
	data[data.length] = {name:'id',value:selectedNode.id};
	$.ajax({
		type: "POST",
        url: '/app/dictionary/object/save.json',
        data: data,
        success: function(data) {            
            
        }
    });
}
function doAddObject() {
	var url;
	var icon;
	if(type == 'dictionary') {
		url = '/app/dictionary/save.json';
		icon = 'fas fa-bookmark';
	} else if(type == 'field') {
		url = '/app/dictionary/field/save.json?id='+selectedNode.id;
		icon = 'fas fa-box';
	} else if(type == 'model') {
		url = '/app/dictionary/model/save.json?id='+selectedNode.id;
		icon = 'fas fa-database';
	} else if(type == 'relation') {
		url = '/app/dictionary/relation/save.json?id='+selectedNode.id;
		icon = 'fas fa-sitemap';
	}	
	$.ajax({
		type: "POST",
	    url: url,
	    data: $("#add-object-window-form").serialize(),
	    success: function(data) {            
	    	var node = $('#repositoryTree').tree('getSelected');
    	    if(!node || data.type == 'dictionary') node = $('#repositoryTree').tree('getRoot');
	    	if(node) {
    	    	$('#repositoryTree').tree('append', {
    	    		parent: node.target,
    	    		data: {
    	    			id: data.id,
    	    			type:data.type,
    	    			text: data.text,
    	    			state: 'closed',
    	    			iconCls: icon
    	    		}
    	    	});
    	    }
    	    //$('#repositoryTree').tree('reload');
	    	$('#add-object-window').dialog('close');
	    }
	});
}
function doAddValue() {
	$.ajax({
		type: "POST",
        url: '/app/dictionary/value/save.json',
        data: $("#add-value-window-form").serialize()+'&id='+selectedNode.id,
        success: function(data) {            
            $('#add-value-window').dialog('close')
            $('#valueGrid').datagrid('load',{
	    		id: selectedNode.id
	    	});
        }
    });
}
function doBump(id) {
	$.ajax({
		type: "POST",
        url: '/app/dictionary/value/bump.json?id='+id,
        data: 'id='+id,
        success: function(data) {
        	$('#valueGrid').datagrid('load',{
	    		id: selectedNode.id
	    	});
        }
    });
}
function doDrop(id) {
	$.ajax({
		type: "POST",
        url: '/app/dictionary/value/drop.json?id='+id,
        data: 'id='+id,
        success: function(data) {
        	$('#valueGrid').datagrid('load',{
	    		id: selectedNode.id
	    	});           
        }
    });
}
function doRemoveValue(id) {
	$.ajax({
	   	type: "POST",
	    url: '/app/dictionary/object/remove.json?id='+id,
	    success: function(data) {
	    	$('#valueGrid').datagrid('load',{
	    		id: selectedNode.id
	    	}); 	        	
	    }
	});	    
}
function doRemoveSelection() {
	$.ajax({
	   	type: "POST",
	    url: '/app/dictionary/object/remove.json?id='+selectedNode.id,
	    success: function(data) {
	    	$('#repositoryTree').tree('remove',selectedNode.target);
	    	
	    }
	});
}
function openValueWindow() {
	$('#add-value-window-form').form('clear');
	$('#add-value-window').dialog('open')
}
function openObjectWindow(type) {
	this.type = type;
	$('#add-object-window-form').form('clear');
	$('#add-object-window').dialog('open')
}