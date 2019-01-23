$.extend($.fn.validatebox.defaults.rules, {
    isNumberAndLength: {
        validator: function(value, param){
            return !isNaN(value) && value.length == 4;
        },
        message: 'Please enter number at between 0001 - 9999'
    }
});

$.extend($.fn.validatebox.defaults.rules, {
    isDateAndFormat: {
        validator: function(value, param){
            return !isNaN(value) && value.length == 4;
        },
        message: 'Please enter a date in format \'yyyy-mm-dd\''
    }
});

$.fn.datebox.defaults.parser = function(s){
    alert(this);
	var t = Date.parse(s);
	if (!isNaN(t)){
		return new Date(t);
	} else {
		return new Date();
	}
}
function removeChecked(dataGrid) {
	var rows = $('#'+dataGrid).datagrid('getChecked');
    var cnt = rows.length;
    var ids = [];
    for (i = 0; i < rows.length; i++) {
    	ids.push(rows[i].id);
    }
    for(i = 0; i < ids.length; i++) {
    	var id = ids[i];
        var rowIndex = $('#'+dataGrid).datagrid('getRowIndex', id);
        $('#'+dataGrid).datagrid('deleteRow', rowIndex);
    }
} 
function removeSelected(dataGrid) {
	var rows = $('#'+dataGrid).datagrid('getSelected');
    var cnt = rows.length;
    var ids = [];
    for (i = 0; i < rows.length; i++) {
    	ids.push(rows[i].id);
    }
    for(i = 0; i < ids.length; i++) {
    	var id = ids[i];
        var rowIndex = $('#'+dataGrid).datagrid('getRowIndex', id);
        $('#'+dataGrid).datagrid('deleteRow', rowIndex);
    }
} 
function removeAll(dataGrid) {
	var rows = $('#'+dataGrid).datagrid('getRows');
    var cnt = rows.length;
    var ids = [];
    for (i = 0; i < rows.length; i++) {
    	ids.push(rows[i].id);
    }
    for(i = 0; i < ids.length; i++) {
    	var id = ids[i];
        var rowIndex = $('#'+dataGrid).datagrid('getRowIndex', id);
        $('#'+dataGrid).datagrid('deleteRow', rowIndex);
    }
} 
function handleKeyPress(e) {
     var key = e.keyCode || e.which;
     if (key == 13) {
         doAssocSearch();
     }
 }
 function handleKeyPressCollectionSearch(e) {
     var key = e.keyCode || e.which;
     if (key == 13) {
         doSearch();
     }
}
function roundUp(x){
	var y = Math.pow(10, x.toString().length-1);
    x = (x/y);
    x = Math.ceil(x);
    x = x*y;
    return x;
}
function getAttribute(entity, name) {
	for(i=0; i < entity.attributes.length; i++) {
		var attribute = entity.attributes[i];
		if(attribute.localName == name)
			return attribute.value;
	}
} 

function customDateFormatter(date) {
     var y = date.getFullYear();
     var m = date.getMonth() + 1;
     var d = date.getDate();
     return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
}
 function customDateParser(s) {
     if (!s) return new Date();
     if (s.length == 10 && isValidDate(s)) {
         var ss = (s.split('-'));
         var y = parseInt(ss[0], 10);
         var m = parseInt(ss[1], 10);
         var d = parseInt(ss[2], 10);
         if (!isNaN(y) && !isNaN(m) && !isNaN(d)) {
             return new Date(y, m - 1, d);
         } else {
             return new Date();
         }
     } else if (s.length == 10 && !isValidDate(s)) {
         $.messager.alert({
             title: 'Error',
             icon: 'error',
             msg: 'Please enter a validate date must be in format yyyy-mm-dd'

         });
         $('#' + this.id).datebox('setValue', '');
         $('#' + this.id).datebox('calendar').focus();

     }
 }
 function isValidDate(dateString) {
     var regEx = /^\d{4}-\d{2}-\d{2}$/;
     var validDate = false;
     if (!dateString.match(regEx)) return false; // Invalid format
     var d = new Date(dateString);
     if (Number.isNaN(d.getTime())) return false; // Invalid date
     return d.toISOString().slice(0, 10) === dateString;
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
 function convertNullString(inString) {
     if (inString == "null") {
         return "";
     } else {
         return inString;
     }
 }
 function goHome() {
     var urlParams = new URLSearchParams(window.location.search);
     var tabIndex = urlParams.get('tab');
     var pageIndex = urlParams.get('page');
     var rowIndex = urlParams.get('row');

     window.location.replace('/manager');
     $('#tt').tabs('select', tabIndex);
 }
 function endsWith(str, suffix) {
     return str.indexOf(suffix, str.length - suffix.length) !== -1;
 }
function displayHelp(){
    $('#helpDialog').dialog('open').dialog('center').dialog('setTitle', 'Help');
}
function getLocalName(qname) {
	var n = qname.lastIndexOf("_");
	return qname.substring(n+1);
}
function getPropertyValue(qname) {
	for(var fieldKey in entity.properties) {
		var property = entity.properties[fieldKey];
		if(property.qname == qname)
			return property.value;
	}
}
function getRelation(model, qname) {
	for(var fieldKey in model.relations) {
  	  	var relation = model.relations[fieldKey];
  	  	if(relation.qname == qname)
  	  		return relation;
	}
}
function doAssocSearch() {
	clearChecked($('#assocSelectionGrid'));
    $('#assocSelectionGrid').datagrid('load', {
    	qname: $('#searchQName').val(),
        query: $('#query').val()
    });
} 
function customDateFormatter(date) {
	var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    return y + '-' + (m < 10 ? ('0' + m) : m) + '-' + (d < 10 ? ('0' + d) : d);
}
function customDateParser(s) {
	if(!s) return new Date();
    if(s.length == 10 && isValidDate(s)) {
    	var ss = (s.split('-'));
        var y = parseInt(ss[0], 10);
        var m = parseInt(ss[1], 10);
        var d = parseInt(ss[2], 10);
        if(!isNaN(y) && !isNaN(m) && !isNaN(d)) {
            return new Date(y, m - 1, d);
        } else {
        	return new Date();
        }
     } else if (s.length == 10 && !isValidDate(s)) {
         $.messager.alert({
             title: 'Error',
             icon: 'error',
             msg: 'Please enter a validate date must be in format yyyy-mm-dd'
         });
         $('#' + this.id).datebox('setValue', '');
         $('#' + this.id).datebox('calendar').focus();
     }
 }
 function isValidDate(dateString) {
     var regEx = /^\d{4}-\d{2}-\d{2}$/;
     var validDate = false;
     if (!dateString.match(regEx)) return false; // Invalid format
     var d = new Date(dateString);
     if (Number.isNaN(d.getTime())) return false; // Invalid date
     return d.toISOString().slice(0, 10) === dateString;
 } 
 function convertNullString(inString) {
     if (inString == "null") {
         return "";
     } else {
         return inString;
     }
 } 
 function endsWith(str, suffix) {
     return str.indexOf(suffix, str.length - suffix.length) !== -1;
 }
 function displayHelp(){
	 $('#helpDialog').dialog('open').dialog('center').dialog('setTitle', 'Help');
 }
function formatDate(val,row) {
	if(val == 0) return "";
	return val.toString().substring(4,6)+'/'+val.toString().substring(6,8)+'/'+val.toString().substring(0,4);
}
function message(title,message) {
	$.messager.show({
        title:title,
        msg:message,
        showType:'slide',
        style:{
            right:'',
            top:document.body.scrollTop+document.documentElement.scrollTop,
        	bottom:''
    	}
    });
}
function b64toBlob(data){
    var sliceSize = 512;
    var chars = atob(data);
    var byteArrays = [];
    for(var offset=0; offset<chars.length; offset+=sliceSize){
        var slice = chars.slice(offset, offset+sliceSize);
        var byteNumbers = new Array(slice.length);
        for(var i=0; i<slice.length; i++){
            byteNumbers[i] = slice.charCodeAt(i);
        }
        var byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }
    return new Blob(byteArrays, {
        type: ''
    });
}