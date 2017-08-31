/**
 * @author Imagine
 */
var wscok_url = "ws://127.0.0.1:8080/cti.ws";// websocket地址
var webSocket;// websocket对象
var callOut_left;// 本机左席呼出对象
var callOut_right;// 本机右席呼出对象

var l_seat = true;// 是否左席，默认为是

var seat_pad = "left";// 当前是左席还是右席点击拨号盘

var l_ph = "";// 左席号码
var r_ph = "";// 右席号码

var meetId;//会议ID
var meetData;//参会人

// 注册
function checkIn(userId) {// userId:请求ID

	if(userId == null || userId == "") {
        modalMsg(2,"您还没有登录！");
        window.location.href = "/";
	} else {
        var reqId = randomString(32);
        var action = ACTION_WEBSOCKET_CONNECT;

        webSocket = new WebSocket(wscok_url + '?userId=' + userId + "&reqId="
            + reqId + "&action=" + action);

        webSocket.onerror = function (event) {
            onError(event);
        };

        webSocket.onopen = function (event) {
            onOpen(event);
        };

        webSocket.onmessage = function (event) {
            onMessage(event);
        };
    }
}

// 收到消息事件
function onMessage(event) {
	console.log("收到服务端发来的websocket消息:\r\n" + event.data);
	var msg = $.parseJSON(event.data);
	if (msg.action == ACTION_WEBSOCKET_CONNECT) {
		l_ph = msg.content.l_ph;
		r_ph = msg.content.r_ph;
		menus_left = msg.content.l_code;
		menus_right = msg.content.r_code;
		init_tab = msg.content.init_tab;

		initDc();
	} else if (msg.action == ACTION_CREATE_MEET_CALL) {
		if (msg.status == SUCCESS) {// 说明呼叫成功

            console.log(msg);
			if (msg.content.caller == l_ph) {

                $("#call_status_left").text("正在呼叫-" + l_ph);
				if ($("#callingpad_left").is(':hidden')) {
					$("#showcallingpad_left").trigger("click");
					$("#hangup_call_left").text("挂断");
				}
                callOut_left = msg.content;
			} else if (msg.content.caller == r_ph) {

                $("#call_status_right").text("正在呼叫-" + r_ph);
				if ($("#callingpad_right").is(':hidden')) {
					$("#showcallingpad_right").trigger("click");
                    $("#hangup_call_right").text("挂断");
				}
                callOut_right = msg.content;
			}

		} else {// 说明呼叫失败
			modalMsg(2, "呼叫失败,原因:" + msg.content);
		}
	} else if (msg.action == ACTION_CALL_STATUS_REPORT) {// 说明是通话状态推送

		if (msg.phnum == l_ph) {// 说明是左席
			$("#call_status_left").text(msg.content);

			if (msg.ci != null) {
				callOut_left = msg.ci;
				$("#u_name_left").text(callOut_left.cName);
				$("#u_duty_left").text(callOut_left.duty);
				$("#u_company_left").text(callOut_left.company);
				$("#u_ctime_left").text(getMyDate(callOut_left.callbeginTime));
			}

			if (msg.status == CALL_STATUS_BYE
					|| msg.status == CALL_STATUS_REJECT
					|| msg.status == CALL_STATUS_CANCEL
					|| msg.status == CALL_STATUS_FAIL) {
				var btn = $("#hangup_call_left");
				btn.text("重拨");
				btn.attr("class", "fa fa-phone");
				callOut_left = null;
				$("#threeCall_left_btn").addClass("disabled");
				$("#hold_left_btn").addClass("disabled");
				$("#rec_left_btn").addClass("disabled");
			} else if (msg.status == CALL_STATUS_ANSWER
					&& msg.caller != msg.phnum) {// 如果加入会场的不是分机，说明是外线应答，加入会议通话中
				$("#threeCall_left_btn").removeClass("disabled");
				$("#hold_left_btn").removeClass("disabled");
				$("#rec_left_btn").removeClass("disabled");
			} else if (msg.status == ACTION_MEET_CALL_HOLD) {// 保持
				$("#hold_left").text("取消");
			} else if (msg.status == ACTION_MEET_CALL_HOLD_CANCEL) {// 取消保持
				$("#hold_left").text("保持");
			} else if (msg.status == ACTION_BEGIN_RECORD) {// 开始录音
				$("#rec_left").text("录音中");
			} else if (msg.status == ACTION_STOP_RECORD) {
				$("#rec_left").text("录音");
			}
		} else if (msg.phnum == r_ph) {// 说明是右席
			$("#call_status_right").text(msg.content);

			if (msg.ci != null) {
				callOut_right = msg.ci;
				$("#u_name_right").text(callOut_right.cName);
				$("#u_duty_right").text(callOut_right.duty);
				$("#u_company_right").text(callOut_right.company);
				$("#u_ctime_right")
						.text(getMyDate(callOut_right.callbeginTime));
			}

			if (msg.status == CALL_STATUS_BYE
					|| msg.status == CALL_STATUS_REJECT
					|| msg.status == CALL_STATUS_CANCEL
					|| msg.status == CALL_STATUS_FAIL) {// 挂机
				var btn = $("#hangup_call_right");
				btn.text("重拨");
				btn.attr("class", "fa fa-phone");
				callOut_right = null;
				$("#threeCall_right_btn").addClass("disabled");
				$("#hold_right_btn").addClass("disabled");
				$("#rec_right_btn").addClass("disabled");
			} else if (msg.status == CALL_STATUS_ANSWER
					&& msg.caller != msg.phnum) {// 如果加入会场的不是分机，说明是外线应答，加入会议通话中
				$("#threeCall_right_btn").removeClass("disabled");
				$("#hold_right_btn").removeClass("disabled");
				$("#rec_right_btn").removeClass("disabled");
			} else if (msg.status == ACTION_MEET_CALL_HOLD) {// 保持
				$("#hold_right").text("取消");
			} else if (msg.status == ACTION_MEET_CALL_HOLD_CANCEL) {// 取消保持
				$("#hold_right").text("保持");
			} else if (msg.status == ACTION_BEGIN_RECORD) {// 开始录音
				$("#rec_right").text("录音中");
			} else if (msg.status == ACTION_STOP_RECORD) {
				$("#rec_right").text("录音");
			}
		}

	} else if (msg.action == ACTION_BYE) {
		if (msg.status == SUCCESS) {
			modalMsg(1, "通话已挂断");
			var btn;
			if (msg.content.caller == l_ph) {
				btn = $("#hangup_call_left");
				callOut_left = null;
			} else if (msg.content.caller == r_ph) {
				btn = $("#hangup_call_right");
				callOut_right = null;
			}
			btn.text("重拨");
			btn.attr("class", "fa fa-phone");
		} else {
			modalMsg(2, "挂机失败,原因:" + msg.content);
		}
	} else if (msg.action == ACTION_POP) {// 说明是弹屏

		if (msg.phnum == l_ph) {

			callOut_left = msg.ci;
			showPop("pop.html","左席新来电-" + msg.caller,msg);

		} else if (msg.phnum == r_ph) {

			callOut_right = msg.ci;
			showPop("pop.html","右席新来电-" + msg.caller,msg);
		}

	} else if(msg.action == ACTION_CREATE_MEET) {
		
		if(msg.status == SUCCESS) {
			if(msg.meetId == null) {//说明是返回创建结果
				meetId = msg.content;
				
				modalMsg(1,"会议创建成功");
				 $('#layui-layer-iframe1')[0].contentWindow.beginGetMeetStatus();
				if(meetData.length > 1) {
					for(var i = 1 ; i < meetData.length ; i++) {
						join_meet(meetData[i].num,meetData[i].uname);
					}
				}
			}
		}
	} else if(msg.action == ACTION_CLOSE_MEET) {
		if(msg.status == SUCCESS) {
			meetId = "";//清除会场信息
		}
	} else if(msg.action == ACTION_GET_MEET_CALLSTATUS) {//会场呼叫信息通知
		 $('#layui-layer-iframe1')[0].contentWindow.getMeetStatus(msg.content);
	}
}

/**
 * 连接事件
 * 
 * @param event
 * @returns
 */
function onOpen(event) {
}

/**
 * websock错误事件
 * 
 * @param event
 * @returns
 */
function onError(event) {
}

/**
 * 发送消息
 * 
 * @param msg
 * @returns
 */
function send(msg) {// msg:发送的消息内容
	webSocket.send(msg);
}

/**
 * 发起呼叫
 * 
 * @param called
 * @returns
 */
function makeCall(called) {
	var caller = ""
	if (seat_pad == "left") {
		caller = l_ph;
		$("#call_pad_name_left").text("呼叫面板-左席" + l_ph);
	} else if (seat_pad == "right") {
		caller = r_ph;
		$("#call_pad_name_right").text("呼叫面板-右席" + r_ph);
	}

	calling(caller, called);
}

/**
 * 呼叫
 * 
 * @param caller
 * @param called
 * @returns
 */
function calling(caller, called) {
	var msg = "{reqId:'" + randomString(32) + "',action:'"
			+ ACTION_CREATE_MEET_CALL + "',caller:'" + caller + "',called:'"
			+ called + "'}";
	send(msg);
}

/**
 * 挂机
 * 
 * @param meetId
 * @param callid
 * @returns
 */
function bye(meetId, callid) {
	var msg = "{reqId:'" + randomString(32) + "',action:'"
			+ ACTION_MEET_CALL_BYE + "',meetId:'" + meetId + "',callId:'"
			+ callid + "'}";
	send(msg)
}

/**
 * 保持
 * 
 * @param meetId
 * @param callid
 * @returns
 */
function hold(meetId, callid) {
	var msg = "{reqId:'" + randomString(32) + "',action:'"
			+ ACTION_MEET_CALL_HOLD + "',meetId:'" + meetId + "',callId:'"
			+ callid + "'}";
	send(msg)
}

/**
 * 取消保持
 * 
 * @param meetId
 * @param callid
 * @returns
 */
function hold_cancel(meetId, callid) {
	var msg = "{reqId:'" + randomString(32) + "',action:'"
			+ ACTION_MEET_CALL_HOLD_CANCEL + "',meetId:'" + meetId
			+ "',callId:'" + callid + "'}";
	send(msg)
}

/**
 * 开始录音
 * 
 * @param meetId
 * @returns
 */
function rec(meetId) {
	var msg = "{reqId:'" + randomString(32) + "',action:'"
			+ ACTION_BEGIN_RECORD + "',meetId:'" + meetId + "'}";
	send(msg)
}

/**
 * 录音取消
 * 
 * @param meetId
 * @returns
 */
function rec_cancel(meetId) {
	var msg = "{reqId:'" + randomString(32) + "',action:'" + ACTION_STOP_RECORD
			+ "',meetId:'" + meetId + "'}";
	send(msg);
}

/**
 * 创建会议
 * @param data
 * @returns
 */
function create_meet(data) {
	meetData = data;
	var d1 = data[0];
	
	var msg = "{reqId:'" + randomString(32) + "',action:'" + ACTION_CREATE_MEET
	+ "',caller:'" + d1.num + "',name:'" + d1.uname + "'}";
	send(msg);
}

function join_meet(caller,name) {

	var msg = "{reqId:'" + randomString(32) + "',meetId:'" + meetId + "',action:'" + ACTION_JOIN_MEET
	+ "',callers:[{caller:'" + caller + "',name:'" + name + "',level:'" + MEET_LEVEL_CHARIMAN + "'}]}";
	send(msg);
	
}

function close_meet() {
	var msg = "{reqId:'" + randomString(32) + "',action:'" + ACTION_CLOSE_MEET
	+ "',meetId:'" + meetId + "'}";
	send(msg);
}

function get_meet_call_status() {
	var msg = "{reqId:'" + randomString(32) + "',action:'" + ACTION_GET_MEET_CALLSTATUS
	+ "',meetId:'" + meetId + "'}";
	send(msg);
}
