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
function timeDiff(before,after){var _typeofDate=typeof new Date();if(typeof before!==_typeofDate||typeof after!==_typeofDate)throw new TypeError();var diffTimeStamp=Math.floor((after.getTime()-before.getTime())/1000);var result={};var level2=diffTimeStamp%3600;result.hours=Math.floor(diffTimeStamp/3600);result.minutes=Math.floor(level2/60);result.seconds=Math.floor(level2%60);return result}
function sortTable(t,a){function r(){var r=$(t).attr("data-ordered-col"),d=$(t).attr("data-order-"+a)||"";return""===d||"d"===d||r!==a?($(t).attr("data-order-"+a,"a").attr("data-ordered-col",a).find("th img").attr("src",""),$(t).find("th img[data-title='"+a+"']")[0].src=_ROOT_+"img/caret-up.png",function(t,r){var d=$(t).find("td[data-title='"+a+"']").text(),e=$(r).find("td[data-title='"+a+"']").text(),i=Date.parse(d),n=Date.parse(e);return isNaN(i)||isNaN(n)?isNaN(d)||isNaN(e)?d.localeCompare(e,"zh"):d-e:i-n}):"a"===d?($(t).attr("data-order-"+a,"d").attr("data-ordered-col",a).find("th img").attr("src",""),$(t).find("th img[data-title='"+a+"']")[0].src=_ROOT_+"img/caret-down.png",function(t,r){var d=$(t).find("td[data-title='"+a+"']").text(),e=$(r).find("td[data-title='"+a+"']").text(),i=Date.parse(d),n=Date.parse(e);return isNaN(i)||isNaN(n)?isNaN(d)||isNaN(e)?e.localeCompare(d,"zh"):e-d:n-i}):null}var d=$(t).find("tbody>tr"),e=$(d[0].parentNode);d.sort(r()),e.html("").append(d)}