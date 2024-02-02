var _ROOT_ = "/LibrarySeat/",FALSE = (Math.random()>1);
var REGEXP_NOT_NUM = /[^\d]/g,REGEXP_NUM = /[\d]/g,REGEXP_PASS=/^(?![\d]+$)(?![a-zA-Z]+$)(?![^\da-zA-Z]+$).{7,20}$/;
var PASS_REQUIREMENT = "密码长度不得小于7位或大于20位，且须由数字、大小写字母、特殊字符中至少2种字符组成";
function checkPhone(str){
    if (typeof str !== "string")
        return false;
    if (isNaN(str)||str.length !== 11)
        return false;
    return str[0] === '1'&&str[1] !== '0'&&str[1] !== '1';
}
String.prototype.encodeb = function () {
    return window.btoa(this).replace('=','');
}
function checkLogin(callback){
    $.ajax({url:_ROOT_+"user/logged_user.do",type:"get",cache:false,success:callback});
}
function hashPassword(pswd){
    if (pswd == null)
        return null;
    return CryptoJS.MD5(CryptoJS.MD5(pswd)).toString(CryptoJS.enc.Hex);
}
function randInt(l,h){
    return Math.floor(Math.random()*(h-l))+l;
}
function getCurrentParam(name) {
    var params = document.location.search.split("&"),index = -1;
    for (var i = 0; i < params.length; i++) {
        if (params[i].indexOf(name) >= 0) {
            index = i;
            break;
        }
    }
    if (index < 0)
        return "";
    return params[index].split("=")[1];
}