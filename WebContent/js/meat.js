function action(url, type, dataType, success, error, before, data) {
	$.ajax({
		async : true,
		url : url, // 跨域URL
		type : type,
		dataType : dataType,
		data : data,
		timeout : 5000,
		crossdomain : true,
		success : success,
		error : error,
		beforeSend : before
	});
}

function clearHeader(){
	$(document).find('.eleme-header').find('.header-title').remove();
	$(document).find('.eleme-header').find('.header-helper').remove();
}