/**
 * 添加会议呼叫号码
 * 
 * @param called
 *            要添加的号码
 * @returns
 */
function addcall(called) {
	if (called != '') {
		var uname = $("#c_name").text();
		var dept = $("#c_company").text();
		var duty = $("#c_duty").text();

		addCallToTable(uname, called, dept, duty);
	}
}

/**
 * 手动添加号码
 * 
 * @returns
 */
function addnewcall() {

	var called = $("#p_num").val();

	if (called != '') {

		if (vailExists(called)) {
			$("#err_txt").text(called + "已存在于参会列表");
		} else {

			var uname = $("#p_name").val();

			if (uname == '') {// 如果名称为空，则置为号码
				uname = called;
			}

			var dept = $("#p_company").val();
			var duty = $("#p_duty").val();
			$("#err_txt").text("");
			addCallToTable(uname, called, dept, duty);

			$("#close_add").trigger("click");
			$("#p_num").val("");
			$("#p_name").val("");
			$("#p_company").val("");
			$("#p_duty").val("");
		}
	} else {
		$("#err_txt").text("号码不能为空");
	}
}

/**
 * 把号码添加进表格
 * 
 * @returns
 */
function addCallToTable(uname, called, dept, duty) {

	var id = $("#table tr").length;

	var added = [ {
		"id" : id,
		"num" : called,
		"state" : "1",
		"uname" : uname,
		"duty" : duty,
		"dept" : dept
	} ];
	$('#table').bootstrapTable("append", added);

	$(window).resize();
		
	//如果当前正在会议，则加人时直接呼叫
	if(window.parent.meetId != null && window.parent.meetId != "") {
		window.parent.join_meet(called,uname);
	}
}

function vailExists(num) {
	var trs = $("#table").bootstrapTable("getData");

	for (var i = 0; i < trs.length; i++) {
		if (num == trs[i].num) {
			return true;
		}
	}
}

function addseat(s) {

	var ph = "";
	var name = "";
	if(s == 'left') {
		ph = parent.l_ph;
		name = "左席";
	} else if(s == 'right') {
		ph = parent.r_ph;
        name = "右席";
	}

    if(!vailExists(ph)) {
        var added = [ {
            "id" : "1",
            "num" : ph,
            "state" : "1",
            "uname" : name,
            "duty" : "会议主持人",
            "dept" : "坐席"
        } ];
        $('#table').bootstrapTable("append", added);

        $(window).resize();
    }
}

function add2call() {
	if(!vailExists("17757172406")) {
		addCallToTable("测试", "17757172406", "测试部门", "测试号码");
	} else {
		$("#group_err_txt").text("17757172406已存在于参会列表");
	}
	
	if(!vailExists("18672234888")) {
		addCallToTable("测试1", "18672234888", "测试部门", "测试号码");
	} else {
		$("#group_err_txt").text("18672234888已存在于参会列表");
	}
}

function add3call() {
	if(!vailExists("18922115520")) {
		addCallToTable("林广日", "18922115520", "华亨测试", "测试号码");
	} else {
		$("#group_err_txt").text("18922115520已存在于参会列表");
	}
	
	if(!vailExists("18923092888")) {
		addCallToTable("张植华", "18923092888", "华亨测试", "测试号码");
	} else {
		$("#group_err_txt").text("18923092888已存在于参会列表");
	}
	
	if(!vailExists("18926163799")) {
		addCallToTable("邓顺林", "18926163799", "华亨测试", "测试号码");
	} else {
		$("#group_err_txt").text("18926163799已存在于参会列表");
	}
}

function add1call() {
	if(!vailExists("17757172406")) {
		addCallToTable("测试", "17757172406", "测试部门", "测试号码");
	} else {
		$("#group_err_txt").text("17757172406已存在于参会列表");
	}
}

function beginMeet() {
	
	var btn = $("#meet_btn");
	
	if(btn.text() == "开始会议") {
		var data = $("#table").bootstrapTable("getData");
		window.parent.create_meet(data);
		btn.text("结束会议");
	} else {
		window.parent.close_meet();
		btn.text("开始会议");
	}
}

function beginGroupCall() {
	
	var btn = $("#meet_btn");
	
	if(btn.text() == "开始群呼") {
		var data = $("#table").bootstrapTable("getData");
		window.parent.create_meet(data);
		btn.text("结束群呼");
	} else {
		window.parent.close_meet();
		btn.text("开始群呼");
	}
}

function beginVoiceCall() {
	
	var btn = $("#meet_btn");
	
	if(btn.text() == "开始通播") {
		var data = $("#table").bootstrapTable("getData");
		window.parent.create_meet(data);
		btn.text("结束通播");
	} else {
		window.parent.close_meet();
		btn.text("开始通播");
	}
}

function getMeetStatus(msg) {
	if(typeof msg === 'object') {
		
	} else {
		
	}
}

function beginGetMeetStatus() {
	setTimeout(window.parent.get_meet_call_status(),5000);
}