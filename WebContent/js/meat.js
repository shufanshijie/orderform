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

function setCookie(name,value)
{
    var Days = 1;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();

    
//    var strsec = getsec(time);
//    var exp = new Date();
//    exp.setTime(exp.getTime() + strsec*1);
//    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

//读取cookies
function getCookie(name)
{
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return (arr[2]);
    else
        return null;
}
//删除cookies
function delCookie(name)
{
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null)
        document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}