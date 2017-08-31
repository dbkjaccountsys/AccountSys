
function modalConfirm() {
	$.fn.modalConfirm("确认要提交么？", function(status) {
		alert("您选择的是" + status);
	});
}
function modalAlert(type) {
	switch (type) {
	case 1:
		$.fn.modalAlert("提交成功", "success");
		return;
	case 2:
		$.fn.modalAlert("错误", "error");
		return;
	case 3:
		$.fn.modalAlert("警告", "warning");
		return;
	}

}
function modalMsg(type) {
	switch (type) {
	case 1:
		$.fn.modalMsg("提交成功", "success");
		return;
	case 2:
		$.fn.modalMsg("错误", "error");
		return;
	case 3:
		$.fn.modalMsg("警告", "warning");
		return;
	}
}

$.fn.modalOpen = function(options) {
	var defaults = {
		id : null,
		title : '系统窗口',
		width : "100px",
		height : "100px",
		url : '',
		shade : 0.3,
		btn : [ '确认', '关闭' ],
		btnclass : [ 'btn btn-primary', 'btn btn-danger' ],
		callBack : null
	};
	var options = $.extend(defaults, options);
	var _width = top.$(window).width() > parseInt(options.width.replace('px',
			'')) ? options.width : top.$(window).width() + 'px';
	var _height = top.$(window).height() > parseInt(options.height.replace(
			'px', '')) ? options.height : top.$(window).height() + 'px';
	layer.open({
		id : options.id,
		type : 2,
		shade : options.shade,
		title : options.title,
		fix : false,
		area : [ _width, _height ],
		content : options.url,
		btn : options.btn,
		btnclass : options.btnclass,
		yes : function() {
			options.callBack(options.id)
		},
		cancel : function() {
			return true;
		}
	});
}

$.fn.modalConfirm = function(content, callBack) {
	layer.confirm(content, {
		icon : "fa-exclamation-circle",
		title : "系统提示",
		btn : [ '确认', '取消' ],
		btnclass : [ 'btn btn-primary', 'btn btn-danger' ],
	}, function() {
		callBack(true);
	}, function() {
		callBack(false)
	});
}
$.fn.modalAlert = function(content, type) {
	var icon = "";
	var iconType = 0;
	if (type == 'success') {
		icon = "fa-check-circle";
		iconType = 1;
	}
	if (type == 'error') {
		icon = "fa-times-circle";
		iconType = 2;
	}
	if (type == 'warning') {
		icon = "fa-exclamation-circle";
		iconType = 3;
	}
	top.layer.alert(content, {
		icon : iconType,
		title : "系统提示",
		btn : [ '确认' ],
		btnclass : [ 'btn btn-primary' ],
	});
}
$.fn.modalMsg = function(content, type) {
	var iconType = 0;
	if (type != undefined) {
		var icon = "";
		if (type == 'success') {
			icon = "fa-check-circle";
			iconType = 1;
		}
		if (type == 'error') {
			icon = "fa-times-circle";
			iconType = 2;
		}
		if (type == 'warning') {
			icon = "fa-exclamation-circle";
			iconType = 3;
		}
		top.layer.msg(content, {
			icon : iconType,
			time : 4000,
			shift : 5
		});
		top.$(".layui-layer-msg").find('i.' + iconType).parents(
				'.layui-layer-msg').addClass('layui-layer-msg-' + type);
	} else {
		top.layer.msg(content);
	}
}
$.fn.modalClose = function() {
	var index = layer.getFrameIndex(window.name); // 先得到当前iframe层的索引
	var $IsdialogClose = top.$("#layui-layer" + index).find('.layui-layer-btn')
			.find("#IsdialogClose");
	var IsClose = $IsdialogClose.is(":checked");
	if ($IsdialogClose.length == 0) {
		IsClose = true;
	}
	if (IsClose) {
		layer.close(index);
	} else {
		location.reload();
	}
}

$.fn.dialOpen = function(options) {
	var defaults = {
		id : null,
		title : '系统窗口',
		width : "100px",
		height : "100px",
		url : '',
		shade : 0.3,
		callBack : null
	};
	var options = $.extend(defaults, options);
	var _width = top.$(window).width() > parseInt(options.width.replace('px',
			'')) ? options.width : top.$(window).width() + 'px';
	var _height = top.$(window).height() > parseInt(options.height.replace(
			'px', '')) ? options.height : top.$(window).height() + 'px';
	layer.open({
		id : options.id,
		type : 2,
		shade : options.shade,
		title : options.title,
		fix : false,
		area : [ _width, _height ],
		content : options.url,
		btn : options.btn,
		btnclass : options.btnclass,
		yes : function() {
			options.callBack(options.id)
		},
		cancel : function() {
			return true;
		}
	});
}