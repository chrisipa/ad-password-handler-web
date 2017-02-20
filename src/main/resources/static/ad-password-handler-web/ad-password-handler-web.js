AdPasswordHandlerWeb = (function () {
	
	function init() {
		
		$(document).ready(function () {
			
			$(document).ajaxStart(function() {
				$.mobile.loading('show');
			});
			
			$(document).ajaxStop(function() {
				$.mobile.loading('hide');
			});
			
			$(document).on("pagechange", function(event) {
				$("input[type=text]:visible").first().focus();
			});
			
			$.i18n.properties({
				name: 'translations', 
				path: Settings.bundlePath, 
				mode: 'both',
				language: $.i18n.browserLang(),
				callback: function () {
					showPasswordChangePage();
				}
			});
		});
	}
	
	function showPasswordChangePage() {
		
		var passwordChangePage = $("<div id='passwordChangePage' data-role='page'>");
		
		$.mobile.pageContainer.html(passwordChangePage);
		
		var header = $("<div data-role='header'></div>"); 
		
		passwordChangePage.append(header);
		
		var headline = $("<h1></h1>").html(Settings.name + " (" + Settings.version + ")");
		
		header = header.append(headline);
		
		var content = $("<div data-role='content'></div>");
		
		header.after(content);
		
		var passwordChangeForm = $("<form id='pw-change'></form>");
		passwordChangeForm.append($("<input type='text' id='username' />").attr("Placeholder", $.i18n.prop("input.username.label")));
		passwordChangeForm.append($("<input type='password' id='password' />").attr("Placeholder", $.i18n.prop("input.password.label")));
		passwordChangeForm.append($("<input type='password' id='newPassword' />").attr("Placeholder", $.i18n.prop("input.new.password.label")));
		passwordChangeForm.append($("<input type='password' id='newPasswordConfirm' />").attr("Placeholder", $.i18n.prop("input.new.password.confirm.label")));
		passwordChangeForm.append($("<input type='submit' />").attr("value", $.i18n.prop("button.password.change.label")));
		passwordChangeForm.submit(function (e) {

			e.preventDefault();

			$.ajax({
				type: "POST",
				contentType: "application/x-www-form-urlencoded; charset=utf-8",
				url: "pw-change",
				data: {
					"username": $("#username").val(),
					"password": $("#password").val(),
					"newPassword": $("#newPassword").val(),
					"newPasswordConfirm": $("#newPasswordConfirm").val()
				},
				success: function (response) {
					$("#username").val("");
					$("#password").val("");
					$("#newPassword").val("");
					$("#newPasswordConfirm").val("");
					alert($("<div />").html($.i18n.prop("password.change.success.message")).text());
				},
				error: function(request,status,errorThrown) {
					$("#password").val("");
					$("#newPassword").val("");
					$("#newPasswordConfirm").val("");
					alert($("<div />").html($.i18n.prop("password.change.error.message")).text());
				}
			});
		});
		
		content.append(passwordChangeForm);
		
		$.mobile.changePage(passwordChangePage);
	}

	return {
        init: init,
	}
}()).init();
