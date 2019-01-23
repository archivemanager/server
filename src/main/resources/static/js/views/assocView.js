var assocView = $.extend({}, $.fn.datagrid.defaults.view, {
    renderRow: function(target, fields, frozen, rowIndex, rowData) {
        var cc = [];
        if(rowData.value) {
	          var entity = assocs[rowData.value];
	          if(!frozen){
	        	  cc.push('<td field="text">');
	        	  	cc.push('<div style="white-space:normal;height:auto;" class="datagrid-cell datagrid-cell-c4-text">');
	        	  if(entity.view) {
	        		  if(entity.view == 'view1') {
	        			  for(i=0; i < entity.attributes.length; i++) {
	        				  var attribute = entity.attributes[i];
	        				  if(attribute) {
	        					  if(i == 0) {
	        						  cc.push('<div class="attribute-name">');
	        						  cc.push(entity.target+' - '+attribute.value);
	        					  } else {
	        						  cc.push('<div class="attribute-'+attribute.label+'">');
	        						  cc.push(attribute.value);
	        					  }		    			          
		    			          cc.push('</div>');
	        				  }
	        			  }
	        		  } else if(entity.view == 'view2') {
	        			  	cc.push('<div class="attribute-name">');
	        			  		cc.push(entity.name);
	        			  	cc.push('</div>');
	        			  	for(i=0; i < entity.properties.length; i++) {
	        			  		var property = entity.properties[i];
	        			  		if(property) {
	        			  			cc.push('<div class="attribute-label">'+property.label+'</div>');
	        			  			if(property.value) cc.push('<div class="attribute-value">'+property.value+'</div>');
	        			  			else cc.push('<div class="attribute-value">&nbsp;</div>');
	        			  		}
	        			  	}
	        		  } else if(entity.view == 'view3') {
	        			  	cc.push('<div class="attribute-name">');
	        			  		cc.push(entity.name);
	        			  	cc.push('</div>');
	        			  	for(i=0; i < entity.attributes.length; i++) {
	        			  		var property = entity.attributes[i];
	        			  		if(property && property.value && property.qname != 'openapps_org_system_1_0_name') {
	        			  			cc.push('<div class="attribute-row">');
	        			  			cc.push('<div class="attribute-label">'+property.label+'</div>');
	        			  			cc.push('<div class="attribute-value">'+property.value+'</div>');
	        			  			cc.push('</div>');
	        			  		}
	        			  	}
	        		  } else {
	        			  for(i=0; i < entity.attributes.length; i++) {
	        				  var attribute = entity.attributes[i];
	        				  if(attribute && attribute.qname == entity.view) {
	        					  cc.push('<div class="attribute-name">');
		    			           	cc.push(attribute.target+' - '+attribute.value);
		    			          cc.push('</div>');
	        				  }
	        			  }   
	        		  }
	        	  } else {
	        		 cc.push('<div class="attribute-name">');
		             	cc.push(entity.target+' - '+entity.name);
		             cc.push('</div>');
	        	  }
	        	  cc.push('</div>');
	        	  cc.push('</td>');
	          } else {
	        	  cc.push('<td field="_ck" style="padding:5px;vertical-align: top;">');
	        	  cc.push('<div class="datagrid-cell-check" style="float:left;width:25px;">');
	        	  	cc.push('<input type="checkbox" name="_ck" value="" />');
	        	  cc.push('</div>');
	        	  cc.push('</td>');
	          }	          
        }
        return cc.join('');
    }
});