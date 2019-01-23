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
function doSaveUser(){
	$.ajax({
		type: "POST",
        url: '/user/profile/save.json',
        data: $("#fm").serialize(),
        success: function(data) {
            if(data.status == 200) {
            	$('#success-msg').show();
            } else {
            	$('#error-msg').show();
            }
            setTimeout(function() {
            	$('#success-msg').hide();
            	$('#error-msg').hide();
            }, 4000);
        }
    });
}
function changePassword() {
	$('#passwordWindow').window("center").window('open');
}
function doSavePassword() {
	$.ajax({
		type: "POST",
	    url: '/users/password/save.json',
	    data: {
	    	'password':$('#passwordWindow_password').val()
	    },
	    success: function(data) {
	      	$('#passwordWindow').window('close');
	    }
	});
}