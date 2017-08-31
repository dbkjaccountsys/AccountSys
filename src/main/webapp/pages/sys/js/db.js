/**
 * @author Imagine
 */
var menus_left = "";// 左席菜单Json
var menus_right = "";// 右席菜单Json
var init_tab = "";
var uId;

var recPath = "http://192.168.100.101:8080/rec/"

/**
 * 打开页面框
 */
function dialOpen(url,title) {
    $.fn.dialOpen({
        id : "meet",
        title : title,
        url : url,
        width : "1200px",
        height : "1000px",
        callBack : function(iframeId) {
            top.frames[iframeId].AcceptClick();
        }
    });
}

/**
 * 打开页面框
 */
function addOpen(url,title) {
    $.fn.dialOpen({
        id : "add",
        title : title,
        url : url,
        width : "600px",
        height : "860px",
        callBack : function(iframeId) {
            top.frames[iframeId].AcceptClick();
        }
    });
}

/**
 * 打开页面框
 */
function importOpen(url,title) {
    $.fn.dialOpen({
        id : "import",
        title : title,
        url : url,
        width : "480px",
        height : "220px",
        callBack : function(iframeId) {
            top.frames[iframeId].AcceptClick();
        }
    });
}

var popSize = 0;
/**
 * 弹出消息框
 * @returns
 */
function showPop(url,title,msg) {

	var left = 300 + (popSize * 100);

	console.log("left:" + left + ";popSize:" + popSize);

	var newWin = window.open(url, msg.ci.callid, 'height=650, width=540, top=200, left=' + left + ', toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no');
	
	window.setTimeout(function(){

		popSize += 1;

		newWin.document.title = title;
		
		var btn = newWin.$("#hangup_call_left");
		newWin.$("#callednum").text(msg.caller);
		btn.text("挂断");
		btn.attr("class", "glyphicon glyphicon-phone-alt");
		
		btn.click(function(event) {
			bye(msg.ci.meetId,msg.ci.callid);
			newWin.close();
		});

		if (msg.ci != null) {
			callOut_left = msg.ci;
			newWin.$("#u_name").text(msg.ci.cName);
			newWin.$("#u_duty").text(msg.ci.duty);
			newWin.$("#u_company").text(msg.ci.company);
			newWin.$("#u_ctime").text(getMyDate(msg.ci.callbeginTime));
		}
		
	},500);

	var popLoop = window.setInterval(function() {
		if(newWin.closed) {
			clearInterval(popLoop);
			popSize -= 1;
		}
	},1000);
}

/**
 * 显示提示框
 * 
 * @param type
 * @param str
 * @returns
 */
function modalMsg(type, str) {
	switch (type) {
	case 1:
		$.fn.modalMsg(str, "success");
		return;
	case 2:
		$.fn.modalMsg(str, "error");
		return;
	case 3:
		$.fn.modalMsg(str, "warning");
		return;
	}
}

/**
 * 按键
 * 
 * @param key
 * @returns
 */
function pad_press(key) {
	var num = $("#callnum").text() + key;
	$("#callnum").text(num);

}

/**
 * 清空
 * 
 * @returns
 */
function pad_clear() {
	$("#callnum").text("");
}

/**
 * 呼叫
 * 
 * @returns
 */
function pad_call() {
	var num = $("#callnum").text();
	if (num == null || num == "") {
		modalMsg(2, "请输入您要拨的号码");
	} else {
		$("#closecallpad").trigger("click");
		if (seat_pad == "left") {
			$("#callednum_left").text(num);
		} else {
			$("#callednum_right").text(num);
		}
		modalMsg(1, "正在发起呼叫:" + num);
		makeCall(num);
	}
}

/**
 * 退格键
 * 
 * @returns
 */
function pad_backspace() {
	var num = $("#callnum").text();
	if (num != null && num != "") {
		num = num.substr(0, num.length - 1);
		$("#callnum").text(num);
	}
}

/**
 * 重拨
 * 
 * @returns
 */
function pad_redial() {
	modalMsg(2, "当前没有可重拨的号码");
}

/**
 * 显示联系人详情
 * 
 * @param tr
 * @param id
 * @returns
 */
function showmodal(tr, id) {

	if (tr != null) {

		$("#c_name").text(tr[2].title);
		$("#c_duty").text(tr[3].title);
		$("#c_company").text(tr[4].title);

		$("#c_mobile").text(tr[7].title);
		$("#c_mobile2").text(tr[11].title);

		$("#c_office_phone").text(tr[5].title);
		$("#c_office_phone2").text(tr[9].title);

		$("#c_home_phone").text(tr[6].title);
		$("#c_home_phone2").text(tr[10].title);

		$("#c_hj_office_phone").text(tr[13].title);
		$("#c_hj_home_phone").text(tr[14].title);
	}

	$("#" + id).trigger("click");
}

/**
 * 显示联系人详情
 *
 * @param tr
 * @param id
 * @returns
 */
function showclogmodal(tr, id) {

    if (tr != null) {

        $("#callType").text(tr[2].title);
        $("#caller").text(tr[3].title);
        $("#callee").text(tr[7].title);

        $("#callerBeginTime").text(tr[4].title);
        $("#callerRingTime").text(tr[5].title);
        $("#callerAnswerTime").text(tr[6].title);

        $("#calledBeginTime").text(tr[8].title);
        $("#calledRingTime").text(tr[9].title);
        $("#calledAnswerTime").text(tr[10].title);

        $("#hangupTime").text(tr[11].title);

        $("#callerIsAnswer").text(tr[12].title);
        $("#calledIsAnswer").text(tr[13].title);

        $("#callTime").text(tr[14].title);

        var rPath = tr[15].title;
        if(rPath != "") {

            var src = recPath + tr[15].title;
        	var audioHtml = "<audio src='" + src + "' controls='controls'></audio>"

            $("#rec_play").html(audioHtml);
		}

        $("#cName").text(tr[16].title);
        $("#company").text(tr[17].title);
        $("#duty").text(tr[18].title);
        $("#remark").text(tr[19].title);

    }

    $("#" + id).trigger("click");
}

/**
 * 呼叫号码
 * 
 * @param txt
 * @returns
 */
function call(txt) {
	if (txt != null && txt != "") {
		var btn;
		if (l_seat) {
			btn = $("#hangup_call_left");
			$("#callednum_left").text(txt);
		} else {
			btn = $("#hangup_call_right");
			$("#callednum_right").text(txt);
		}
		btn.text("挂断");
		btn.attr("class", "glyphicon glyphicon-phone-alt");
		makeCall(txt);
	}
}

/**
 * 初始化顶部模块
 * 
 * @returns
 */
function initTab() {
	eval(init_tab);
}

/**
 * 初始化左侧菜单
 * 
 * @returns
 */
function initLeftMenu() {

	var l_menus = $.parseJSON(menus_left);
	$('.sidebar-menu-left').sidebarMenu({
		data : l_menus,
		param : {
			strUser : 'admin'
		}
	});
}

/**
 * 初始化右侧菜单
 * 
 * @returns
 */
function initRightMenu() {

	var r_menus = $.parseJSON(menus_right);
	$('.sidebar-menu-right').sidebarMenu({
		data : r_menus,
		param : {
			strUser : 'admin'
		}
	});
}

/**
 * 切换左右席
 */
function change_seat(seat) {

	if (seat != null && seat != "") {
		if (seat == "right" && l_seat) {
			modalMsg(1, "当前是左席，切换到右席");
			l_seat = false;
			setUse(seat);
		} else if (seat == "left" && !l_seat) {
			modalMsg(1, "当前是右席，切换到左席");
			l_seat = true;
			setUse(seat);
		}
	}
}

/**
 * 设置坐席使用
 * 
 * @param seat
 * @returns
 */
function setUse(seat) {
	if (seat != null && seat != "") {
		if (seat == "left") {
			$("#l_ph").html("<b style='color: red'>" + l_ph + "</b>");
			$("#l_ph_btn").addClass("disabled");
			$("#r_ph").html(r_ph);
			$("#r_ph_btn").removeClass("disable");
		} else if (seat == "right") {
			$("#r_ph").html("<b style='color: red'>" + r_ph + "</b>");
			$("#r_ph_btn").addClass("disabled");
			$("#l_ph").html(l_ph);
			$("#l_ph_btn").removeClass("disabled");
		}
	}
}

/**
 * 初始化呼入线路列表
 */
function init_line() {
	$("#line1").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 1
					+ "</b></button>");
	$("#line2").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 2
					+ "</b></button>");
	$("#line3").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 3
					+ "</b></button>");
	$("#line4").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 4
					+ "</b></button>");
	$("#line5").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 5
					+ "</b></button>");
	$("#line6").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 6
					+ "</b></button>");
	$("#line7").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 7
					+ "</b></button>");
	$("#line8").html(
			"<button type='button' class='btn btn-block btn-default'><b>呼入" + 8
					+ "</b></button>");
}

/**
 * 初始化界面
 * 
 * @returns
 */
function initDc() {

	$("#l_ph").html("<b style='color: red'>" + l_ph + "</b>");
	$("#l_ph_btn").addClass("disabled");
	$("#r_ph").html(r_ph);

	document.getElementById("cid").click();
	init_line();

	initTab();

	initLeftMenu();
	initRightMenu();
}

/**
 * 初始化websocket
 * 
 * @returns
 */
function initWebSock() {
	uId = getUrlParam("uId")
	checkIn(uId);
}

/**
 * 获取指定长度的随机字符串
 * @param len 字符串长度
 * @returns {string}
 */
function randomString(len) {
	len = len || 32;
	var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
	/** **默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1*** */
	var maxPos = $chars.length;
	var pwd = '';
	for (i = 0; i < len; i++) {
		pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
	}
	return pwd;
}

// 获得年月日 得到日期oTime
function getMyDate(str) {
	var oDate = new Date(str), oYear = oDate.getFullYear(), oMonth = oDate
			.getMonth() + 1, oDay = oDate.getDate(), oHour = oDate.getHours(), oMin = oDate
			.getMinutes(), oSen = oDate.getSeconds(), oTime = oYear + '-'
			+ getzf(oMonth) + '-' + getzf(oDay) + ' ' + getzf(oHour) + ':'
			+ getzf(oMin) + ':' + getzf(oSen);// 最后拼接时间
	return oTime;
}

// 补0操作
function getzf(num) {
	if (parseInt(num) < 10) {
		num = '0' + num;
	}
	return num;
}

/**
 * 话务操作
 * 
 * @param type
 * @param msg
 * @returns
 */
function callOper(type, seat) {
	if (type == 1) {// 说明是挂机

		var btn;
		if (seat == "left") {
			btn = $("#hangup_call_left");

			if (btn.text() == "挂断") {
				if (callOut_left != null) {
					bye(callOut_left.meetId,callOut_left.callid);
				} else {
					modalMsg(2, "当前没有通话");
				}
			} else if (btn.text() == "重拨") {
				makeCall($("#callednum_left").text());
				btn.text("挂断");
				btn.attr("class", "glyphicon glyphicon-phone-alt");
			}

		} else if (seat == "right") {
			btn = $("#hangup_call_right");

			if (btn.text() == "挂断") {
				if (callOut_right != null) {
					bye(callOut_right.meetId,callOut_right.callid);
				} else {
					modalMsg(2, "当前没有通话");
				}
			} else if (btn.text() == "重拨") {
				makeCall($("#callednum_right").text());
				btn.text("挂断");
				btn.attr("class", "glyphicon glyphicon-phone-alt");
			}

		}
	} else if (type == 2) {// 转多方

		var btn;
		if(seat == "left") {

			btn = $("#threeCall_left_btn");

            if(!btn.hasClass("disabled")) {

                modalMsg(1, "准备转左席多方通话");
            }

		} else if(seat == "right") {

            btn = $("#threeCall_right_btn");
            if(!btn.hasClass("disabled")) {

                modalMsg(1, "准备转右席多方通话");
            }
		}

	} else if (type == 3) {// 保持

		var btn;
		if (seat == "left") {
			btn = $("#hold_left_btn");

            if(!btn.hasClass("disabled")) {
                if ($("#hold_left").text() == "保持") {
					hold(callOut_left.meetId, callOut_left.callid);
                } else if ($("#hold_left").text() == "取消") {
                    hold_cancel(callOut_left.meetId, callOut_left.callid);
                }
            }

		} else if (seat == "right") {
			btn = $("#hold_right_btn");

			if(!btn.hasClass("disabled")) {

                if ($("#hold_right").text() == "保持") {
                    hold(callOut_right.meetId, callOut_right.callid);
                } else if ($("#hold_right").text() == "取消") {
                    hold_cancel(callOut_right.meetId, callOut_right.callid);
                }
            }
		}
		
	} else if (type == 4) {// 录音
		var btn;
		if (seat == "left") {
			btn = $("#rec_left_btn");

            if(!btn.hasClass("disabled")) {
                if ($("#rec_left").text() == "录音") {
                    if (callOut_left != null) {
                        rec(callOut_left.meetId);
                    }
                } else if ($("#rec_left").text() == "录音中") {
                    rec_cancel(callOut_left.meetId, callOut_left.callid);
                }
            }

		} else if (seat == "right") {
			btn = $("#rec_right_btn");

            if(!btn.hasClass("disabled")) {
                if ($("#rec_right").text() == "录音") {
                    if (callOut_right != null) {
                        rec(callOut_right.meetId);
                    }
                } else if ($("#rec_right").text() == "录音中") {
                    rec_cancel(callOut_left.meetId, callOut_left.callid);
                }
            }
		}
	}
}

function hangup(seat) {
	if (seat == "left") {
		if (callOut_left != null) {
			bye(callOut_left.meetId,callOut_left.callid);
		} else {
			modalMsg(2, "左席当前没有通话");
		}
	} else if (seat == "right") {
		if (callOut_right != null) {
			bye(callOut_right.meetId,callOut_right.callid);
		} else {
			modalMsg(2, "右席当前没有通话");
		}
	}
}

function showCallPad(seat) {

	seat_pad = seat;

	if (seat == "left") {// 说明是左席操作
		if (callOut_left != null) {
			$("#showcallingpad_left").trigger("click");
		} else {
			$("#dial_pad_name").text("呼叫面板-左席:" + l_ph);
			$("#showcallpad").trigger("click");
			$("#callnum").text("");
		}
	} else if (seat == "right") {// 说明是右席操作
		if (callOut_right != null) {
			$("#showcallingpad_right").trigger("click");
		} else {
			$("#dial_pad_name").text("呼叫面板-右席:" + r_ph);
			$("#showcallpad").trigger("click");
			$("#callnum").text("");
		}
	}
}

/**
 * 获取URL指定参数
 * @param paramName
 */
function getUrlParam(paramName) {
    paramValue = "", isFound = !1;
    if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) {
        arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&"), i = 0;
        while (i < arrSource.length && !isFound) arrSource[i].indexOf("=") > 0 && arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase() && (paramValue = arrSource[i].split("=")[1], isFound = !0), i++
    }

    return paramValue == "" && (paramValue = null), paramValue
}

/*
 @function     JsonSort 对json排序
 @param        json     用来排序的json
 @param        key      排序的键值
 */
function jsonSort(json,key){
    for(var j=1,jl=json.length;j < jl;j++){
        var temp = json[j],
            val  = temp[key],
            i    = j-1;
        while(i >=0 && json[i][key]>val){
            json[i+1] = json[i];
            i = i-1;
        }
        json[i+1] = temp;

    }
    return json;
}

