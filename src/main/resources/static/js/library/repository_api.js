$.extend($.fn.datagrid.methods, {
	updateRow: function(jq, param){
		return jq.each(function(){
			var target = this;
			var state = $.data(target, 'datagrid');
			var opts = state.options;
			var row = opts.finder.getRow(target, param.index);
			var updated = false;
			for(var field in param.row){
				if (row[field] != param.row[field]){
					updated = true;
					break;
				}
			}
			if (updated){
				if ($.inArray(row, state.insertedRows) == -1){
					if ($.inArray(row, state.updatedRows) == -1){
						state.updatedRows.push(row);
					}
				}
				$.extend(row, param.row);
				opts.view.updateRow.call(opts.view, this, param.index, param.row);
			}
		});
	}
});
function selectRootNode(node, data){
      if ( node == null ){
         node = $('#collectionTree').tree('getRoot');
         $('#collectionTree').tree('select', node.target);
      }
  }
function showAddNodeDialog(type) {
    $('#node-add-content-type').combobox('clear');
    $('#node-add-name').textbox('clear');
    $('#node-add-abstract-note').texteditor('setValue','');
    document.getElementById("nodeAddForm").reset();

    if ( type == null ){
       var node = $('#collectionTree').tree('getSelected');
       var iconCls = node.iconCls;
       if ( iconCls.indexOf("repository") > 0 ){
           showAddNodeDialog('collection');
           return;
       } else if ( iconCls.indexOf("collection") > 0){
           showAddNodeDialog('category');
           return;
       } else {
          $('#divContentType').show();
       }
    } else if (type == 'category') {
        $('#divContentType').hide();
        $('#node-add-content-type').combobox('setValue', 'category');
    } else if (type == 'collection') {
        $('#divContentType').hide();
        $('#node-add-content-type').combobox('setValue', 'collection');
    } else {
        $('#divContentType').show();
    }

    $('#nodeAddDlg').dialog('open').dialog('center').dialog('setTitle', 'Add ');
}

function addNoteToCollection() {
    var node = $('#collectionTree').tree('getSelected');
    $('#note-add-source').val(node.id);
    	$.ajax({
    		type: 'POST',
    		url: '/service/entity/association/add.json',
    		data: $('#noteAddForm').serialize(),
    		success: function (result) {
    			if (result.response.status != 0 ){
    				$.messager.alert({
    					title: 'Error',
    					msg: result.errorMsg
    				});
    			} else {
    				$('#collectionTree').tree('reload',node.target);
    				$('#collectionTree').tree('append', { parent: node.target, data: [{id: result.response.data[0].id, text: result.response.data[0].name}]});
    				var newNode = $('#collectionTree').tree('find',result.response.data[0].id);
    				$('#collectionTree').tree('select', newNode.target);
    			}
    			$('#noteAddDialog').dialog('close');
    		}
    });
}
function addToCollection() {
    var rootNode = $('#collectionTree').tree('getRoot');
    var node = $('#collectionTree').tree('getSelected');
    var contentType = $('#node-add-content-type').val();
    $('#node-add-source').val(node.id);

     var qLocalName = mapContenTypeToAssocQ(contentType);
    $('#node-add-entity_qname').val('openapps_org_repository_1_0_'+contentType);
    if ( qLocalName == 'items'){
        $('#node-add-description').val($('#node-add-abstract-note').texteditor('getValue'));
    }
    $('#node-add-assoc_qname').val('openapps_org_repository_1_0_'+ qLocalName);
        $.ajax({
          type: 'POST',
          url: '/service/entity/association/add.json',
          data: $('#nodeAddForm').serialize(),
          success: function (result) {
                  if (result.response.status != 0 ){
                    $.messager.alert({
                      title: 'Error',
                          msg: result.errorMsg
                      });
                  } else {
                    $.messager.alert({
                      title: 'Success',
                      msg: result.response.messages[0],
                      fn: function(){$('#nodeAddDlg').dialog('close');},
                      style:{
                          right:'',
                          bottom:''
                      }
                    });

                      $('#collectionTree').tree('reload',node.target);

                      $('#collectionTree').tree('append', { parent: node.target, data: [{id: result.response.data[0].id, text: result.response.data[0].name}]});
                      var newNode = $('#collectionTree').tree('find',result.response.data[0].id);
                    $('#collectionTree').tree('select', newNode.target);
                  }
          }
        });

        $('#nodeAddDlg').dialog('close');        // close the dialog
}
function removeNote() {
		 var checkedItems = $('#assoc_dl_notes').datalist('getChecked');
	     checkedItems.forEach(
	         function(item) {
	        	 $.ajax({
	                type: 'POST',
	                url: '/service/entity/remove.json',
	                data: { id: item.id },
	             	success: function (result) {
	             		var id = item.id;
	             		var rows = $('#assoc_dl_notes').datalist('getRows');
	             		for(i=0; i < rows.length; i++) {
	             			if(id == rows[i].id)
	             				$('#assoc_dl_notes').datalist('deleteRow',i);
	             		}
	             	}
	             }); 
	         }
	     );
}
function removeWeblink() {
		 var checkedItems = $('#assoc_dl_weblinks').datalist('getChecked');
	     checkedItems.forEach(
	         function(item) {
	        	 $.ajax({
	                type: 'POST',
	                url: '/service/entity/remove.json',
	                data: { id: item.id },
	             	success: function (result) {
	             		var id = item.id;
	             		var rows = $('#assoc_dl_weblinks').datalist('getRows');
	             		for(i=0; i < rows.length; i++) {
	             			if(id == rows[i].id)
	             				$('#assoc_dl_weblinks').datalist('deleteRow',i);
	             		}
	             	}
	             }); 
	         }
	     );
}
function showCreateWebLinkDialog(){
    $('#webLinkAddDialog').dialog('open').dialog('center').dialog('setTitle', 'Add Web Link');
}

function createWebLink(){
    var node = $('#collectionTree').tree('getSelected');
    $('#web-link-add-source').val(node.id);
    $('#web-link-add-assoc_qname').val('openapps_org_content_1_0_web_links'); //name="assoc_qname" type="hidden" value=""></>
    $('#web-link-add-entity_qname').val('openapps_org_content_1_0_web_link'); //name="entity_qname" type="hidden" value=""></>

    var ajaxReq = $.ajax({
        url: '/service/entity/association/add.json',
        type: 'POST',
        data: $('#webLinkAddForm').serialize()
    });

    ajaxReq.done(function(entity) {
          $('#collectionTree').tree('select', node.target);
          $('#webLinkAddDialog').dialog('close');
    });
}

function showAddNoteDialog(){
  $('#noteAddDialog').dialog('open').dialog('center').dialog('setTitle', 'Add Note');
}
function getAssocQName(title) {
    switch (title) {
        case 'Corporations':
            assoc_qname = 'openapps_org_classification_1_0_named_entities';
            break;
        case 'Digital Objects':
            assoc_qname = 'openapps_org_content_1_0_files';
            break;
        case 'Notes':
            assoc_qname = 'openapps_org_system_1_0_notes';
            break;
        case 'People':
            assoc_qname = 'openapps_org_classification_1_0_named_entities';
            break;
        case 'Permissions':
            assoc_qname = 'openapps_org_system_1_0_permissions';
            break;
        case 'Subjects':
            assoc_qname = 'openapps_org_classification_1_0_subjects';
            break;
        case 'Web Links':
            assoc_qname = 'openapps_org_content_1_0_web_links';
            break;
    }
    return assoc_qname;
}
function getTargetQName(title) {
    switch (title) {
        case 'Corporations':
            assoc_qname = 'openapps_org_classification_1_0_corporation';
            break;
        case 'Digital Objects':
            assoc_qname = 'openapps_org_content_1_0_file';
            break;
        case 'Notes':
            assoc_qname = 'openapps_org_system_1_0_note';
            break;
        case 'People':
            assoc_qname = 'openapps_org_classification_1_0_person';
            break;
        case 'Permissions':
            assoc_qname = 'openapps_org_system_1_0_permission';
            break;
        case 'Subjects':
            assoc_qname = 'openapps_org_classification_1_0_subject';
            break;
        case 'Web Links':
            assoc_qname = 'openapps_org_content_1_0_web_link';
            break;
    }
    return assoc_qname;
}
function getAssocQName(title) {
    switch (title) {
        case 'Corporations':
            assoc_qname = 'openapps_org_classification_1_0_named_entities';
            break;
        case 'Digital Objects':
            assoc_qname = 'openapps_org_content_1_0_files';
            break;
        case 'Notes':
            assoc_qname = 'openapps_org_system_1_0_notes';
            break;
        case 'People':
            assoc_qname = 'openapps_org_classification_1_0_named_entities';
            break;
        case 'Permissions':
            assoc_qname = 'openapps_org_system_1_0_permissions';
            break;
        case 'Subjects':
            assoc_qname = 'openapps_org_classification_1_0_subjects';
            break;
        case 'Web Links':
            assoc_qname = 'openapps_org_content_1_0_web_links';
            break;
    }
    return assoc_qname;
}
function getTargetQName(title) {
    switch (title) {
        case 'Corporations':
            assoc_qname = 'openapps_org_classification_1_0_corporation';
            break;
        case 'Digital Objects':
            assoc_qname = 'openapps_org_content_1_0_file';
            break;
        case 'Notes':
            assoc_qname = 'openapps_org_system_1_0_note';
            break;
        case 'People':
            assoc_qname = 'openapps_org_classification_1_0_person';
            break;
        case 'Permissions':
            assoc_qname = 'openapps_org_system_1_0_permission';
            break;
        case 'Subjects':
            assoc_qname = 'openapps_org_classification_1_0_subject';
            break;
        case 'Web Links':
            assoc_qname = 'openapps_org_content_1_0_web_link';
            break;
    }
    return assoc_qname;
}
function goHome() {
    var urlParams = new URLSearchParams(window.location.search);
    var tabIndex = urlParams.get('tab');
    var pageIndex = urlParams.get('page');
    var rowIndex = urlParams.get('row');

    window.location.replace('/manager');
    $('#tt').tabs('select', tabIndex);
}
function doAssocSearch() {
    clearChecked($('#assocSelectionGrid'));
    $('#assocSelectionGrid').datagrid('load', {
        qname: $('#searchQName').val(),
        query: $('#query').val()
    });
} 
function mapContenTypeToAssocQ(contentType) {
    switch (contentType) {
        case 'category':
            return 'categories';
        case 'collection':
            return 'collections';
        default:
            return "items";
    }
}
function autoHeight() {
 	var c = $('#mainLayout');
    var p = c.layout('panel','center');
    var oldHeight = p.panel('panel').outerHeight();
    p.panel('resize', {height:'auto'});
    var newHeight = p.panel('panel').outerHeight();
    c.layout('resize',{
    	height: (c.height() + newHeight - oldHeight)
    });
}
function openEntityWindow(id, qname) {
	getModel(qname, function(model) {
		$('#entityWindowFields').empty();
		var formHtml = drawForm('entityWindowForm', model);
		if(formHtml.length > 0) {			
			$('#entityWindowFields').append(formHtml);
			drawFormFields('entityWindowForm', model);
			$('#entityWindowLayout').layout();
		}
		var parms = [];
		$('#entityWindowSave').off("click");
		$('#entityWindowSave').click(function(){
			if(id) updateEntity('entityWindowForm');
			else addEntity('entityWindowForm', id, qname);
		});
		$('#entityWindow').window('open');
	});
}