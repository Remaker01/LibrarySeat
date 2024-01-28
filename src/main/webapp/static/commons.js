var _ROOT_="/LibrarySeat/",FALSE=(Math.random()>1);
var REGEXP_NOT_NUM=/[^\d]/g,REGEXP_NUM=/[\d]/g;
function checkPhone(str){if(typeof str!=="string")return false;if(isNaN(str)||str.length!==11)return false;return str[0]==='1'&&str[1]!=='0'&&str[1]!=='1'}
String.prototype.encodeb=function(){return window.btoa(this).replace('=','')}
function checkLogin(callback){$.ajax({url:_ROOT_+"user/logged_user.do",type:"get",cache:false,success:callback})}
function hashPassword(pswd){return CryptoJS.MD5(CryptoJS.MD5(pswd)).toString(CryptoJS.enc.Hex)}
function randInt(l,h){return Math.floor(Math.random()*(h-l))+l}