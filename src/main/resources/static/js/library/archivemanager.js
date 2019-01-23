var entity;
var assocs = [];
var entities = [];
var models = [];
var filter = [];
var id,qname,source,assoc;
$(function() {
	 
});
function select(node, callback) {
	if(!entity || node.id != entity.id) {
		var ajaxReq = $.ajax({
			url: '/service/repository/entity/fetch.json?id=' + node.id,
	        type: 'GET',
	        cache: false,
	        contentType: false,
	        processData: false
	    });
	    ajaxReq.done(function(response) {
	    	assocs = [];
	    	entities = [];
	    	$('#model-associations').show();
	        getModel(response.qname, function(model) {
	        	$('#entityFields').empty();
	        	var formHtml = drawForm('entityForm', model);	          
			    drawAssociations(model, response);
			    if(formHtml.length > 0) $('#entityFields').append(formHtml);
			    
			    drawFormFields('entityForm', model);
	        	
		        entity = response;
		        $('#label-id').html(response.id);
		        if(response.update.length > 0)
		        	$('#label-update').html(response.update);
		        $('#id').val(response.id);  
		        
		        var formData = [];
		        for(i=0; i < response.properties.length; i++) {
		        	var property = response.properties[i];
		        	if(property.value == true) formData[property.qname] = 'on';
		        	else formData[property.qname] = property.value;
		        }		            
		        $('#entityForm').form('load', formData);
		        
		        for(i=0; i < editors.length; i++) {
		    		editors[i].updateSourceElement();
		    	}
		        if(model.item) {
		        	$('#qname').combobox('setValue', response.qname);
		        }
		        $('#application-body').tabs('select', 'form-screen');
		        $('#application-properties').tabs('select', 'associations');
		          
		        $('#mainLayout').layout();
	        });
	        if(callback) callback.call(this);
	    });
	    ajaxReq.fail(function(jqXHR) {
	          message('Entity Selection Failed', 'unable to locate entity for id:' + node.id + ' text:' + node.text);
	    });
	}
}
function drawForm(form, model) {
	var row0 = [];
	var row1 = [];
    var row2 = [];
    var row3 = [];
    var row4 = [];
    var row5 = [];
    var row6 = [];
    var row7 = [];    	
    if(model.item || model.qname == 'openapps_org_repository_1_0_item') {
    	row0.push('<div class="form-cell-50">');
    	row0.push('<select id="qname" name="qname" style="width:100%;">');
      	for(i=0; i < itemQnames.length; i++) {
      		var value = itemQnames[i];
      		if(value.id == entity.qname) row0.push('<option value="'+value.id+'" selected>'+value.name+'</option>');
      		else row0.push('<option value="'+value.id+'">'+value.name+'</option>');
     	}
      	row0.push('</select>');
      	row0.push('</div>');
    }
    if(!model.item && model.qname != 'openapps_org_repository_1_0_item') {
    	row4.push('<div class="form-cell-50">');
    	row4.push('<input id="qname" name="qname" type="text" style="width:100%;" value="'+model.qname+'" />');
    	row4.push('</div>');
    }
    for(var fieldKey in model.fields) {
  	  var field = model.fields[fieldKey];
  	  if(field.format == 'richtext') {
  		  row2.push('<div class="form-row">');
  		  row2.push('<label class="textbox-label textbox-label-top" style="text-align: left;">'+field.label+':</label>');
  		  row2.push('<textarea id="'+form+'_'+field.qname+'" name="'+field.qname+'" style="width:100%;"></textarea>');
  		  row2.push('</div>');
  	  } else if(field.type == 'longtext') {
  		  row2.push('<div class="form-row">');
		  row2.push('<label class="textbox-label textbox-label-top" style="text-align: left;">'+field.label+':</label>');
		  row2.push('<textarea id="'+form+'_'+field.qname+'" name="'+field.qname+'" class="textbox-text" style="width:100%;height:150px;"></textarea>');
		  row2.push('</div>');
  	  } else if(field.qname == 'openapps_org_system_1_0_name') {
  		  row1.push('<div class="form-row">');
  		  row1.push('<input id="'+form+'_'+field.qname+'" name="'+field.qname+'" type="text" style="width:100%;" />');
  		  row1.push('</div>');
  	  } else if(field.type == 'mediumtext') {
		  row3.push('<div class="form-row">');
		  row3.push('<input id="'+form+'_'+field.qname+'" name="'+field.qname+'" type="text" style="width:100%;" />');
		  row3.push('</div>');
  	  } else if(field.type == 'smalltext') {		    	  
  		  if(field.values.length > 0) {
  			  row5.push('<div class="form-cell-50">');
  			  row5.push('<select id="'+form+'_'+field.qname+'" name="'+field.qname+'" style="width:100%;">');
  			  row5.push('<option value=""></option>');
  			  for(var valueKey in field.values) {
  				  var value = field.values[valueKey];
  				  row5.push('<option value="'+value.value+'">'+value.label+'</option>');
  			  }
			  row5.push('</select>');
			  row5.push('</div>');
		  } else {
			  row4.push('<div class="form-cell-50">');
		   	  row4.push('<input id="'+form+'_'+field.qname+'" name="'+field.qname+'" type="text" style="width:100%;" />');
		   	  row4.push('</div>');
		  }
  	  } else if(field.type == 'integer') {
  	  		row6.push('<div class="form-cell-33">');
  	  		row6.push('<input id="'+form+'_'+field.qname+'" name="'+field.qname+'" style="min-width:200px;width:100%;" />');
  	  		row6.push('</div>');
  	  } else if(field.type == 'boolean') {
  	  		row7.push('<div class="form-cell-25">');
  	  		row7.push('<input id="'+form+'_'+field.qname+'" name="'+field.qname+'" type="checkbox" class="easyui-radio" style="width:20px;height:20px;" />');
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
    
    return html;
}
function drawFormFields(form, model) {
	editors = [];
	if(model.item || model.qname == 'openapps_org_repository_1_0_item') {
    	$('#qname').combobox({
     		  label:'Item Type',
     		  labelPosition:'top',
     		  valueField: 'id',
     		  textField: 'name'
     	});
    }
	if(!model.item && model.qname != 'openapps_org_repository_1_0_item') {
    	$('#qname').textbox({
		    label:"QName",
		    labelPosition:"top"
		});
    }
    for(var fieldKey in model.fields) {
    	var field = model.fields[fieldKey];
    	if(field.format == 'richtext') {
    		 CKEDITOR.replace(form+'_'+field.qname);    		
    	} else if(field.format == 'longtext') {
    		
    	} else if(field.format == 'password') {
    	 	$('#'+form+'_'+field.qname).passwordbox({
    	  		prompt: 'Password',
    	  	    showEye: true,
    	  	    label:field.label,
    	  	    labelPosition:'top',
    	  	    valueField: 'value',
    	  	    textField: 'text'
    	  	});
    	} else if(field.type == 'smalltext' || field.type == 'mediumtext') {
    		if(field.values.length > 0) {
    			$('#'+form+'_'+field.qname).combobox({
		    	  label:field.label,
		    	  labelPosition:'top',
		    	  valueField: 'value',
		    	  textField: 'text'
		    	});
		    } else {
		    	$('#'+form+'_'+field.qname).textbox({
			      label:field.label,
			      labelPosition:"top"
			    });
		    }
		} else if(field.type == 'boolean') {
		    $('#'+form+'_'+field.qname).checkbox({
		      label: field.label,
		      labelPosition:'top'
		    });
		} else if(field.type == 'integer') {
		    $('#'+form+'_'+field.qname).numberspinner({
		      label:field.label,
		      labelPosition:"top",
		      min: 0,
		      max: 100,
		      increment:1
		    });
		}    	
    }
}
function drawAssociations(model, entity) {
	var html = '<div id="attribute-accordian" class="easyui-accordion" data-options="fit:true">';
    for(var key in entity.associations) {
    	var relation = getRelation(model, key);
    	var association = entity.associations[key];
    	html += '<div id="association-'+relation.qname+'" title="'+relation.label+' ('+association.length+')" data-options="tools:\'#associations-'+relation.qname+'-toolbar\'">';
    	html += '<ul id="assoc_dl_'+relation.qname+'" class="easyui-datalist attribute-list">';	              	              
    	if(association.length > 0) {
    		for(j=0; j < association.length; j++) {
    			var value = association[j];
    			html += '<li qname="'+value.qname+'" value="'+value.id+'">'+value.name+'</li>';
        		assocs[value.id] = value;
        	}
    	}
    	html += '</ul>';
    	html += '</div>';
    	
    	html += '<div id="associations-'+relation.qname+'-toolbar">';
    	html += '<a href="javascript:void(0)" id="associations-'+relation.qname+'-toolbar-edit" style="margin:0 5px;display:none;" onclick="openEditAssociationWindow(\''+relation.qname+'\')"><i class="fas fa-edit"></i></a>';
    	html += '<a href="javascript:void(0)" id="associations-'+relation.qname+'-toolbar-rem" style="margin:0 5px;display:none;" onclick="removeAssociation(\'assoc_dl_'+relation.qname+'\')"><i class="fas fa-minus"></i></a>';        
    	html += '<a href="javascript:void(0)" id="associations-'+relation.qname+'-toolbar-add" style="margin:0 5px;" onclick="openAssociationWindow(\''+model.qname+'\', \''+relation.qname+'\', \''+relation.target+'\')"><i class="fas fa-plus"></i></a>';
    	html += '</div>';    	
    }
    html += '</div>';
    $('#model-associations').empty();
    $('#model-associations').append(html);
    
    for(var key in entity.associations) {
    	var relation = getRelation(model, key);    	
	    $('#assoc_dl_'+relation.qname).datalist({
	    	lines: true,
	    	showHeader:false,
	    	nowrap:false,
	    	singleSelect:true,
	    	view:assocView,
	    	emptyMsg:'no results',
		    onClickRow:function(index,row) {
		    	var record = assocs[row.value];
		    	for(var key in entity.associations) {
	    	    	var relation = getRelation(model, key);
	    	    	if(record.qname == key) {
			    		if(relation.cascades)
			    			$('#associations-'+relation.qname+'-toolbar-edit').show();
			    		$('#associations-'+relation.qname+'-toolbar-rem').show();
			    	} else {
				    	$('#associations-'+relation.qname+'-toolbar-rem').hide();
				    	$('#associations-'+relation.qname+'-toolbar-edit').hide();
				    }	    	    	
	    		}		    	
		    }
	    });	    
    }
	$('#attribute-accordian').accordion({
		animate:true
	});
}
function drawChildAssociationForm(targetModel) {
	var html = drawForm('association-fields', targetModel);
	$('#association-fields').empty();
	$('#association-fields').append(html);
	$('#association-form').form();
	drawFormFields('association-fields', targetModel);
	$('#childAssociationWindowLayout').layout();		
}
function openEntityWindow(id, qname, relationQname) {
	$('#entityWindow').remove();
	$('#entityWindowButtons').remove();
	getModel(qname, function(model) {
		var formHtml = drawForm('entityWindowForm', model);
		var html = '<div id="entityWindow" style="max-height:800px;width:720px;">';
		html += '<form id="entityWindowForm" class="form">';
		html += formHtml;	
		html += '</form>';		
		html += '</div>';
		$('#windows').append(html);
		var html2 = '<div id="entityWindowButtons" style="width:100%;text-align:right;width:99%;padding:5px;">';
		html2 += '<a id="entityWindowSave" href="javascript:void(0)" class="easyui-linkbutton"><i class="fas fa-save"></i>Save</a>';
		html2 += '<a id="entityWindowCancel" href="javascript:void(0)" class="easyui-linkbutton" style="margin-left:5px;"><i class="fas fa-minus-circle"></i>Cancel</a>';
		html2 += '</div>';
		$('#windows').append(html2); 
		
	    drawFormFields('entityWindowForm', model);
	    $('#entityForm').form();
	    
	    $('#entityWindowSave').linkbutton();
	    $('#entityWindowSave').bind('click', function() {
	    	var record = {};
	    	var source = getSource(qname);
	    	if(source) record['source'] = source;
	    	if(qname == 'openapps_org_repository_1_0_item') {
		    	var q = $('#qname').combobox('getValue');
		    	if(q) record['qname'] = q;
		    } else record['qname'] = qname;
	    	for(i=0; i < editors.length; i++) {
    			editors[i].updateSourceElement();
    		}
    		var data = $('#entityWindowForm').serializeArray();
    		data = data.concat(
    			$('#entityWindowForm input[type=checkbox]:not(:checked)').map(function() {
    				return {"name": this.name, "value": false}
    			}).get()
    		);    		
    		var properties = [];
    		for(i=0; i < data.length; i++) {
    			var formVal = data[i];
    			if(formVal.name && formVal.name != 'qname') {
    				if(formVal.value == 'on')
    					properties.push({qname:formVal.name,value:true});
    				else
    					properties.push({qname:formVal.name,value:formVal.value});
    			}
    		}
    		record['properties'] = properties;
    		
	    	if(id) {	    		
	    		record['id'] =  id;
	    		$.ajax({
	    			type: 'POST',
	    		    contentType : "application/json",
	    		    url: '/service/repository/entity/update.json',
	    		    data: JSON.stringify(record),
	    		    success: function(result) {
	    		     	 $.messager.show({
	    		           	 title: 'Success',
	    		             showType:'show',
	    		             msg: name + ' updated successfully'
	    		        });
	    		     	$('#assoc_dl_'+relationQname).datagrid('selectRecord', id);
	    		     	var selection = $('#assoc_dl_'+relationQname).datagrid('getSelected');
	    		     	var assocId = parseInt(selection.value, 10)
	    		     	var entity = assocs[assocId];
	    		     	for(i=0; i < entity.attributes.length; i++) {
	    		     		for(j=0; j < result.properties.length; j++) {
	    		     			if(entity.attributes[i].qname == result.properties[j].qname)
	    		     				entity.attributes[i].value = result.properties[j].value;
	    		     		}
	    		     	}
	    		     	var index = $('#assoc_dl_'+relationQname).datagrid('getRowIndex', selection);
	    		        $('#assoc_dl_'+relationQname).datagrid('updateRow', {
	    		        	 index:index, 
	    		        	 row: {
	    		        		value:entity.id
	    		        	}
	    		        });
	    		        $('#entityWindow').window('close');
	    		    },
	    		    error: function(result, statusCode, errorMsg) {
	    		        $.messager.alert({
	    		             title: 'Error',
	    		             msg: 'There was an internal error processing this request: ' + statusCode + ': ' + errorMsg
	    		        });
	    		    }	    
	    		});
	    	} else if(source && relationQname) {
	    		$.ajax({
    		        type: 'POST',
    		        contentType : "application/json",
    		        url: '/service/repository/entity/associated/update.json',
    		        data: JSON.stringify(record),
    		        success: function(result) {
    		       	 	entity = result;        	 
    		       	 	$.messager.show({
    		       	 		title: 'Success',
    		       	 		showType:'show',
    		       	 		msg: name + ' updated successfully'
    		       	 	});
    		       	 	assocs[result.id] = result;
    		       	 	var name = getPropertyValue('openapps_org_system_1_0_name');
    		       	 	$('#assoc_dl_' + relationQname).datalist('appendRow', {value: result.id});
    		       	 	doAddEntity();
    		       	 		
    		       	 	$('#entityWindow').window('close');
    		        },
    		        error: function(result, statusCode, errorMsg) {
    		            $.messager.alert({
    		                title: 'Error',
    		                msg: 'There was an internal error processing this request: ' + statusCode + ': ' + errorMsg
    		            });
    		        }
	    		});
	    	} else {	    			    				
	    		$.ajax({
	    			type: 'POST',
	    		    contentType : "application/json",
	    		    url: '/service/repository/entity/update.json',
	    		    data: JSON.stringify(record),
	    		    success: function(result) {
	    		     	entity = result;        	 
	    		    	$.messager.show({
	    		    		title: 'Success',
	    		       	 	showType:'show',
	    		       	 	msg: name + ' updated successfully'
	    		    	});
	    		       	assocs[result.id] = result;
	    		       	if(relationQname) {
	    		       		var name = getPropertyValue('openapps_org_system_1_0_name');
	    		       		$('#assoc_dl_' + relationQname).datalist('appendRow', {value: result.id});
	    		       	}
	    		       	doAddEntity();
	    		       	 		
	    		       	$('#entityWindow').window('close');
	    		    },
	    		    error: function(result, statusCode, errorMsg) {
	    		    	$.messager.alert({
	    		                title: 'Error',
	    		                msg: 'There was an internal error processing this request: ' + statusCode + ': ' + errorMsg
	   		            });
	   		        }
	    	   });
	    	}	    	
	    });
	    $('#entityWindowCancel').linkbutton();
		$('#entityWindowCancel').bind('click', function() {
			$('#entityWindow').dialog('close');
		});
		
		var title = id ? 'Edit '+model.label : 'Add '+model.label;
		$('#entityWindow').window({
			title: title,
		    closed: true,
		    modal:true,
		    maximizable:false,
		    minimizable:false,
		    collapsible:false,
		    footer:'#entityWindowButtons',
		    onClose: function() {}
		});		
		$('#entityWindow').window("center").window('open');
	});
}
function openAssociationWindow(modelQname, relationQname, targetQname) {	
	getModel(modelQname, function(model) {
		var relation = getRelation(model, relationQname);
		if(relation.cascades) return openEntityWindow(id, targetQname, relationQname);
		$('#associationWindow').remove();
		$('#selectedItemsToolBar').remove();
		$('#addAssocSearchToolBar').remove();
		getModel(relation.target, function(targetModel) {
			var height = $(window).height();
			var html = '<div id="associationWindow" style="width:850px;height:'+height+'px;max-height:850px;padding:5px;">';
			html += '<div id="associationWindowLayout" class="easyui-layout" style="width:100%;height:100%;">'
			html += '<div data-options="region:\'east\', collapsible: false, title: \'\', split:true" title="East" style="width:50%;">';
			html += '<table id="selectedItems" style="width:100%;height=100%"></table>';			
			html += '</div>';
			html += '<form id="addAssocSearch"></form>';
			html += '<div data-options="region:\'center\',title:\'\',collapsible:false" style="width:50%">';
			html += '<table id="assocSelectionGrid" style="width:100%;height:100%"></table>';
			html += '</div>';
			html += '</div>';
			html += '</div>';
			$('#windows').append(html);
			
			var html2 = '<div id="selectedItemsToolBar">';
				html2 += '<div style="float:left;">';
					html2 += '<a id="associationWindowRemove1" href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="removeAll(\'selectedItems\')"><i class="fas fa-arrow-alt-circle-left"></i>Remove all</a>';
				html2 += '</div>';
				html2 += '<div style="float:right;">';			
					html2 += '<a id="associationWindowSave" href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="saveAssociations(\''+relationQname+'\')"><i class="fas fa-save"></i>Save</a>';
				html2 += '</div>';
			html2 += '</div>';
			
			html2 += '<div id="addAssocSearchToolBar">';
				html2 += '<div style="float:left;">';
					html2 += '<a id="associationWindowNew" href="javascript:void(0)" class="easyui-linkbutton" plain="true" onclick="openEntityWindow(\'\',\''+targetQname+'\', \''+relationQname+'\')"><i class="fas fa-plus"></i>New</a>';					
				html2 += '</div>';
				html2 += '<div style="float:right;">';
					html2 += '<input id="associationWindowQuery" class="easyui-textbox" style="width:200px;border:1px solid #ccc" onkeypress="handleKeyPress(event)">';
					html2 += '<a id="associationWindowSearch" href="#" class="easyui-linkbutton" plain="true" onclick="doAssocSelectionGridSearch();">Search</a>';
				html2 += '</div>';
			html2 += '</div>';
			
			$('#windows').append(html2);
			
			drawChildAssociationForm(targetModel);
			
			$('#assocSelectionGrid').datagrid({
		         columns: [
		             [
		                 { field: 'openapps_org_system_1_0_name', title: 'Name', width: 250 },
		                 { field: 'id', title: '', width: 50, formatter:formatAssocIcons }
		             ]
		         ],
		         toolbar: '#addAssocSearchToolBar',
		         showHeader:false,
		         pagination:true,
		         pageSize:20,
		         idField: 'id',
		         autoRowHeight: false,
		         striped: true,
		         fit: true,
		         fitColumns: true
			});
			if(relation.fields.length > 0) {
			   $('#selectedItems').datagrid({
			         columns: [
			             [
			            	 { field: 'id', title: '', width: 30, formatter:formatSelectedAssocIcons },
			                 { field: 'openapps_org_system_1_0_name', title: 'Name', width: 300 }
			             ]
			         ],
			         toolbar: '#selectedItemsToolBar',
			         showHeader:false,
			         view: expandView,
			         idField: 'id',
			         autoRowHeight: false,
			         pageSize: 500,
			         striped: true,
			         fit: true,
			         noWrap: false,
			         fitColumns: true,
			         detailFormatter:function(index,row){
			             return '<div class="ddv" style="padding:5px 0"></div>';
			         },
			         onExpandRow: function(index,row){
			             var ddv = $(this).datagrid('getRowDetail',index).find('div.ddv');
			             var content = '<form id="assocPropertyForm'+index+'">';
			             content += drawForm('assocPropertyForm'+index, relation);
			             content += '</form>';
			             ddv.panel({
			                 border:false,
			                 cache:true,
			                 content:content,
			                 onLoad:function(){
			                     $('#dg2').datagrid('fixDetailRowHeight',index);
			                 }
			             });
			             drawFormFields('assocPropertyForm'+index, relation)
			             $('#dg2').datagrid('fixDetailRowHeight',index);
			         }
			   });
			} else {
				$('#selectedItems').datagrid({
			         columns: [
			             [
			            	 { field: 'id', title: '', width: 30, formatter:formatSelectedAssocIcons },
			                 { field: 'openapps_org_system_1_0_name', title: 'Name', width: 300 }
			             ]
			         ],
			         toolbar: '#selectedItemsToolBar',
			         showHeader:false,
			         idField: 'id',
			         autoRowHeight: false,
			         pageSize: 20,
			         striped: true,
			         fit: true,
			         noWrap: false,
			         fitColumns: true			         
			   });
			}
			$('#associationWindow').window({
				title: 'Add ' + relation.label,
			    closed: true,
			    maximizable:false,
			    minimizable:false,
			    collapsible:false,
			    modal:true,
			    //top: 100,       
			    onClose: function() {
			    	$('#selectedItems').datagrid('loadData', { "total": 0, "rows": [] });
			    }
			});
			
			$('#associationWindowRemove1').linkbutton();
			$('#associationWindowRemove2').linkbutton();
			$('#associationWindowSave').linkbutton();
			$('#associationWindowAdd').linkbutton();
			$('#associationWindowNew').linkbutton();
			$('#associationWindowSearch').linkbutton();
			$('#associationWindowQuery').textbox();
			
			$('#associationWindowLayout').layout();
			$('#addAssocSearch').after('<input id="searchQName" type="hidden" value="' + targetQname + '"></input>');
			$('#assocSelectionGrid').datagrid({ url: '/service/repository/entity/search.json', queryParams: { qname: $('#searchQName').val() } });
			
			$('#associationWindow').window('center').window('open');
		});		
	})
}
function openEditAssociationWindow(assocQname) {
	var selection = $('#assoc_dl_' + assocQname).datalist('getSelected');
	var assoc = assocs[selection.value];	
	getModel(assoc.targetName, function(targetModel) {
		$('#childAssociationWindow').window({ title: 'Add ' + targetModel.label });
		drawChildAssociationForm(targetModel);
		
		var formData = [];
	    for(i=0; i < assoc.attributes.length; i++) {
	       	var property = assoc.attributes[i];
	       	formData[property.qname] = property.value;
	    }
	    openEntityWindow(assoc.target, targetModel.qname, assocQname);
	    $('#entityWindowForm').form('load', formData);	        
	    
	})
}
function updateEntity() {
	getModel(entity.qname, function(model) {
	    for(i=0; i < editors.length; i++) {
			editors[i].updateSourceElement();
		}
		var data = $('#entityForm').serializeArray();
		data = data.concat(
			$('#entityForm input[type=checkbox]:not(:checked)').map(function() {
				return {"name": this.name, "value": false}
			}).get()
	    );
		for(i=0; i < data.length; i++) {
			if(data[i].name) {
				if(data[i].name == 'qname') {
					entity.qname = data[i].value;
				} else {
					for(var fieldKey in entity.properties) {
						var field = entity.properties[fieldKey];
					  	if(field.qname == data[i].name) {
					  		if(data[i].value == 'on')
					  			field.value = true;
		    				else
		    					field.value = data[i].value
					  	}
					}
				}
			}
		}
	    $.ajax({
	         type: 'POST',
	         contentType : "application/json",
	         url: '/service/repository/entity/update.json',
	         data: JSON.stringify(entity),
	         success: function(result) {
	        	 entity = result;        	 
	             $.messager.show({
	            	 title: 'Success',
	                 showType:'show',
	                 msg: name + ' updated successfully'
	             });
	             doUpdateEntity();
	         },
	         error: function(result, statusCode, errorMsg) {
	             $.messager.alert({
	                 title: 'Error',
	                 msg: 'There was an internal error processing this request: ' + statusCode + ': ' + errorMsg
	             });
	         }
	     });
	});
}
function removeEntity(id) {
	var entityId = id ? id : entity.id;
    if(entityId) {
    	$.messager.confirm('Confirm', 'Are you sure you want to remove this entity ?', function(r) {
            if(r) {
            	$.post('/service/entity/remove.json', { id: entityId }, function(result) {
                    if (result.status != 0) {
                        $.messager.show({ // show error message
                            title: 'Error',
                            msg: result.errorMsg
                        });
                    } else {                        
                        $.messager.show({
                        	title: 'Success',
                        	showType:'show',
                            msg: result.rows[0].name + ' removed successfully',
                        });
                    }
                    if(id) {
	                    var selectionIndex = $('#assocSelectionGrid').datagrid('getRowIndex', id);
	                    if(selectionIndex > -1) {
		                	$('#assocSelectionGrid').datagrid('selectRow', selectionIndex);
		                	var selectedRecord = $('#assocSelectionGrid').datagrid('getSelected');	                	
		                	$('#assocSelectionGrid').datagrid('deleteRow', selectionIndex);
	                    }
                    }
                    doRemoveEntity();
                }, 'json');
            }
        });
    }
}
function saveAssociations(relationQname) {
	var associationsToAdd = $('#selectedItems').datagrid('getData').rows;
    if(associationsToAdd.length < 1) {
        $.messager.alert({
            title: 'Error',
            msg: 'Nothing to Save. Please Select Items To Add',
            style: {
                right: '',
                bottom: ''
            }
        });
    } else {
    	var source = getSource(relationQname);
    	for(var key in associationsToAdd) {
    		var selection = associationsToAdd[key];
        	var index = $('#selectedItems').datagrid('getRowIndex', selection);
        	var record = {};
        	record['source'] = source; 
        	record['target'] = selection.id;
        	var propertyArr = $('#assocPropertyForm'+index).serializeArray();
        	var properties = [];
        	for(i=0; i < propertyArr.length; i++) {
        		properties.push({qname:propertyArr[i].name,value:propertyArr[i].value});
        	}
        	record['properties'] = properties;
        	$.ajax({
                type: 'POST',
                contentType : "application/json",
                url: '/service/repository/association/add.json',
                data: JSON.stringify(record),
                success: function(result) {
                    entity.associations[relationQname].push(result);                    
                    assocs[result.id] = result;
                    $('#assoc_dl_'+result.qname).datagrid('appendRow',{text:result.name, value:result.id});                    	
                }
            });
        }
    	removeAll('selectedItems');
    }
    $('#addAssociationsWindow').window('close');
}
function addChildAssociation() {
	var id = $('#'+model.qname+'-id').val();
	console.log(id);
}
function removeAssociation(assocDiv) {
	var item = $('#' + assocDiv).datalist('getSelected');
	var record = assocs[item.value];
	$.ajax({
		type: 'POST',
		url: '/service/entity/association/remove.json',
		data: { id: record.id },
		success: function (result) {
			var id = result.rows[0].id;
			var rows = $('#' + assocDiv).datalist('getRows');			
			for(i=0; i < rows.length; i++) {
				var rowId = parseInt(rows[i].value, 10);
				if(id == rowId)
					$('#' + assocDiv).datalist('deleteRow',i);
			}
		}
	}); 
}
function doAssocSelectionGridSearch() {
	$('#assocSelectionGrid').datagrid('load', { 
		query: $('#associationWindowQuery').val(), 
		qname: $('#searchQName').val() 
	});
}
function addToSelectedItems(id) {
	var selectionIndex = $('#assocSelectionGrid').datagrid('getRowIndex', id);
	$('#assocSelectionGrid').datagrid('selectRow', selectionIndex);
	var selectedRecord = $('#assocSelectionGrid').datagrid('getSelected');
	
	$('#assocSelectionGrid').datagrid('deleteRow', selectionIndex);
	
	var rowIndex = $('#selectedItems').datagrid('getRowIndex', id);
	if(rowIndex == -1) {
	  	$('#selectedItems').datagrid('appendRow', {id: selectedRecord.id,openapps_org_system_1_0_name: selectedRecord.openapps_org_system_1_0_name});
	}
}
function removeFromSelectedItems(id) {
	var selectionIndex = $('#selectedItems').datagrid('getRowIndex', id);
	$('#selectedItems').datagrid('selectRow', selectionIndex);
	var selectedRecord = $('#selectedItems').datagrid('getSelected');
	
	$('#selectedItems').datagrid('deleteRow', selectionIndex);
	
	var rowIndex = $('#assocSelectionGrid').datagrid('getRowIndex', id);
	if(rowIndex == -1) {
	  	$('#assocSelectionGrid').datagrid('appendRow', {id: selectedRecord.id,openapps_org_system_1_0_name: selectedRecord.openapps_org_system_1_0_name});
	}
}
function addNewToSelectedItems() {
    var count = $('#selectedItems').datagrid('getData').total;
    var name = $('#assocCreateDlgName').textbox('getText');
    if (count == 0) {
        $('#selectedItems').datagrid({ data: [{ id: 'New', name: name }] });
    } else {
        $('#selectedItems').datagrid('appendRow', {
            id: 'New',
            name: name
        });
    }
    $('#assocCreateDlg').dialog('close');
}
function getModel(qname, callback) {
	var model = models[qname];
	if(model) {
		callback.call(this, model);
		return;
	}
	$.ajax({
		url: '/service/dictionary/model/fetch.json?qname=' + qname,
	    type: 'GET',
	    success:function(m) {
	    	models[m.qname] = m;
	    	callback.call(this, m);
	    }
	});
}
function formatAssocIcons(value) {
	return '<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="removeEntity('+value+')"><i class="fas fa-times-circle"></i></a>' +
	'<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="addToSelectedItems('+value+')"><i class="fas fa-arrow-alt-circle-right"></i></a>';
}
function formatSelectedAssocIcons(value) {
	return '<a href="#" class="easyui-linkbutton" style="margin-right:8px;" plain="true" onclick="removeFromSelectedItems('+value+')"><i class="fas fa-arrow-alt-circle-left"></i></a>';
}