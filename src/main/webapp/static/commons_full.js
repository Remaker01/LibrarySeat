var _ROOT_ = "/LibrarySeat/",FALSE = (Math.random()>1),REGEXP_NOT_NUM = /[^\d]/g,REGEXP_NUM = /[\d]/g,REGEXP_PASS=/^(?![\d]+$)(?![a-zA-Z]+$)(?![^\da-zA-Z]+$).{7,20}$/;
var REGEXP_USERNAME=/^[a-zA-Z]\w/g;
var PASS_REQUIREMENT = "密码长度不得小于7位或大于20位，且须由数字、大小写字母、特殊字符中至少2种字符组成",USERNAME_REQUIREMENT="用户名必须由数字、字母、下划线组成且只能以字母开头";
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
function sendMessage(title, content) {
    $.ajax(
        {url: _ROOT_+"message/send.do",type: "post", data: {"title":title,content:content}}
    );
}
function getRoomById(id){
    var x;
    $.ajax({
        url:_ROOT_+"room/getbyid.do", type:"post", data:{"roomid":id}, async:false
    }).done(function (d){
        x=d.info;
    }).fail(function (){
        x=null;
    });
    return x;
}
function timeDiff(before,after){
    var _typeofDate=typeof new Date();
    if(typeof before !== _typeofDate||typeof after !== _typeofDate)
        throw new TypeError();
    var diffTimeStamp=Math.floor((after.getTime()-before.getTime())/1000);
    var result={}
    var level2 = diffTimeStamp % (3600);
    // result.date=Math.floor(diffTimeStamp / (24 * 3600)); // 计算出天数
    result.hours=Math.floor(diffTimeStamp / (3600));
    result.minutes=Math.floor(level2 / (60));
    result.seconds=Math.floor(level2%60);
    return result;
}

/**
 * 将表格排序
 * @param table 表格元素
 * @param columnName 要排序的列名
 */
function sortTable(table,columnName) {
    ///获取比较函数。
    function getCmp() {
        var lastOrderd = $(table).attr("data-ordered-col")
        ,sortOrder = $(table).attr("data-order-"+columnName)||''; //获取现在的顺序标记。在th上打标记会比较困难，就直接在table上
        if (sortOrder === '' || sortOrder === 'd' || lastOrderd !== columnName) { //这一列还没排过序，或已降序排序，或上次不是按这一列排序
            $(table).attr("data-order-" + columnName, 'a').attr("data-ordered-col", columnName) //记录上一次
                .find("th img").attr("src",""); //1.将当前列标记为升序，2.设置排序标签，3.清空所有表头的图像
            $(table).find("th img[data-title='"+columnName+"']")[0].src=_ROOT_+"img/caret-up.png"; //4.设置图像
            return function (a, b) {
                var c = $(a).find("td[data-title='" + columnName + "']").text(),
                    d = $(b).find("td[data-title='" + columnName + "']").text();
                var cDate = Date.parse(c),dDate = Date.parse(d);
                return !isNaN(cDate) && !isNaN(dDate) ? cDate - dDate : (!isNaN(c) && !isNaN(d) ? (c - d) : c.localeCompare(d, "zh"));
            }
        }
        if (sortOrder === 'a') { //已经升序排序
            $(table).attr("data-order-" + columnName, 'd').attr("data-ordered-col", columnName)
                .find("th img").attr("src",""); //1.将当前列标记为升序，2.设置排序标签，3.清空所有表头的图像
            $(table).find("th img[data-title='"+columnName+"']")[0].src=_ROOT_+"img/caret-down.png"; //4.设置图像
            return function (a, b) {
                var c = $(a).find("td[data-title='" + columnName + "']").text(),
                    d = $(b).find("td[data-title='" + columnName + "']").text();
                var cDate = Date.parse(c),dDate = Date.parse(d);
                return !isNaN(cDate) && !isNaN(dDate) ? dDate - cDate : (!isNaN(c) && !isNaN(d) ? (d - c) : d.localeCompare(c, "zh"));
            }
        }
        return null;
    }
    var $trs = $(table).find("tbody>tr"),$tbody=$($trs[0].parentNode);
    $trs.sort(getCmp());
    console.log($trs);
    $tbody.html('').append($trs);
}