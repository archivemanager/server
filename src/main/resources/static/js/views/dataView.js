var dataview = $.extend({}, $.fn.datagrid.defaults.view, {
	renderRow: function(target, fields, frozen, rowIndex, rowData){
		var cc = [];
        cc.push('<td colspan=' + fields.length + ' style="padding:10px 5px;border:0;">');
        if(!frozen) {
        	cc.push('<div id="report-element-'+rowIndex+'">');        	
        	for(var i=0; i < rowData.length; i++) { 
            	var row = rowData[i];
            	if(row.name && row.name.length > 0) {
	            	cc.push('<div class="attribute-row">');
					cc.push('<div class="attribute-label">'+row.id+'</div>');
			  		cc.push('<div class="attribute-value">'+row.name+'</div>');
			  		cc.push('</div>');
            	}
            }
            if(!uploaded) {
            	cc.push('<div class="attribute-row">');
            	cc.push('<div class="attribute-label">&nbsp;</div>');
            	cc.push('<div class="attribute-value" style="line-height:30px;vertical-align:middle;">');
            	//cc.push('<a href="#" style="margin-right:8px;" plain="true" onclick="editEntity('+rowData.id+')"><i class="fas fa-edit" style="margin-right:2px;"></i> edit</a>');
            	//cc.push('<a href="#" style="margin-right:8px;" plain="true" onclick="removeEntity('+rowData.id+')"><i class="fas fa-times-circle" style="margin-right:2px;"></i> remove</a>');
            	cc.push('</div>');
            	cc.push('</div>');
        	}	
            cc.push('</div>');
        }
        cc.push('</td>');
        return cc.join('');
	}
});