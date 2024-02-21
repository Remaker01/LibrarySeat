/*!
 * Powered by uglifiyJS v2.6.1, Build by http://tool.uis.cc/jsmin/
 * build time: Thu Feb 08 2024 15:38:26 GMT+0800 (中国标准时间)
*/
var _ROOT_="/LibrarySeat/",FALSE=1<Math.random(),REGEXP_NOT_NUM=/[^\d]/g,REGEXP_NUM=/[\d]/g,REGEXP_PASS=/^(?![\d]+$)(?![a-zA-Z]+$)(?![^\da-zA-Z]+$).{7,20}$/,REGEXP_USERNAME=/^[a-zA-Z]\w/g,PASS_REQUIREMENT="密码长度不得小于7位或大于20位，且须由数字、大小写字母、特殊字符中至少2种字符组成",USERNAME_REQUIREMENT="用户名必须由数字、字母、下划线组成且只能以字母开头";
String.prototype.encodeb=function(){return window.btoa(this).replace("=","")};
function checkPhone(t){return"string"==typeof t&&!isNaN(t)&&11===t.length&&"1"===t[0]&&"0"!==t[1]&&"1"!==t[1]}
function checkLogin(t){$.ajax({url:_ROOT_+"user/logged_user.do",type:"get",cache:!1,success:t})}
function hashPassword(t){return null==t?null:CryptoJS.MD5(CryptoJS.MD5(t)).toString(CryptoJS.enc.Hex)}
function randInt(l,h){return Math.floor(Math.random()*(h-l))+l}
function getCurrentParam(t){for(var n=document.location.search.split("&"),e=-1,r=0;r<n.length;r++)if(0<=n[r].indexOf(t)){e=r;break}return 0>e?"":n[e].split("=")[1]}
function sendMessage(t,co){$.ajax({url:_ROOT_+"message/send.do",type:"post",data:{title:t,content:co}})}
function getRoomById(t){var x;$.ajax({url:_ROOT_+"room/getbyid.do", type:"post", data:{"roomid":t}, async:false}).done(function (d){x=d.info;}).fail(function (){x=null;});return x;}