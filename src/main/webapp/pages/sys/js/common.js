/**
 * 工具集合对象
 */
var commonUtils={
	//验证非法字符的正则表达式
	illegalRegex:/[`~!@#\$%\^\&\*\(\)_\+<>\?:"\{\},\.\\\/;'\[\]]/im,
	//邮箱正则表达式
	emailRegex:/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
	//手机号码正则表达式
	mobilePhoneRegex:/^(0|86|17951)?(13[0-9]|15[012356789]|17[0135678]|18[0-9]|14[579])[0-9]{8}$/,
	//获取上下文context
	getContext:function(){
		return $("body").data("context");
	}
}

/**
 * 加载jquery validate自定义验证方法
 */
if(jQuery&&jQuery.validator){
	//验证非法字符
	jQuery.validator.addMethod("illegalChar",function(value,element,param){
		return this.optional(element)||!commonUtils.illegalRegex.test(value);
	},"不能包含非法字符");
	
	//验证手机号
	jQuery.validator.addMethod("mobilePhone",function(value,element,param){
		return this.optional(element)||commonUtils.mobilePhoneRegex.test(value);
	},"手机号码格式不正确");
	
	//验证email
	jQuery.validator.addMethod("email",function(value,element,param){
		return this.optional(element)||commonUtils.emailRegex.test(value);
	},"邮箱格式不正确");
	
	//验证字符长度返回
	jQuery.validator.addMethod("rangeLength",function(value,element,param){
		return this.optional(element)||(value.length>=param[0]&&value.length<=param[1]);
	},"请确保输入的值的长度在{0}-{1}之间");
	
	//设置表单验证的默认值
	jQuery.validator.setDefaults({
		errorElement:"em",
		errorPlacement:function(error,element){
			// Add the `help-block` class to the error element
			error.addClass( "help-block" );

			if ( element.prop( "type" ) === "checkbox" ) {
				error.insertAfter( element.parent( "label" ) );
			} else {
				//如果是输入框组
				if(element.parent().attr("class")=="input-group"){
					error.insertAfter(element.parent());
				}else{
					error.insertAfter( element );
				}
			}
		},
		highlight: function ( element, errorClass, validClass ) {
			$( element ).parents( ".form-group" ).addClass( "has-error" ).removeClass( "has-success" );
		},
		unhighlight: function (element, errorClass, validClass) {
			$( element ).parents( ".form-group" ).addClass( "has-success" ).removeClass( "has-error" );
		}
	});
}
//添加自定义弹出层
if($.fn&&layer){
	//弹出iframe
	$.fn.modalOpen = function (options) {
        var defaults = {
            id: null,
            title: '系统窗口',
            width: "100px",
            height: "100px",
            url: '',
            shade: 0.3,
            btn: ['确认', '关闭'],
            btnclass: ['btn btn-primary', 'btn btn-danger'],
            callBack: null
        };
        var options = $.extend(defaults, options);
        var _width = top.$(window).width() > parseInt(options.width.replace('px', '')) ? options.width : top.$(window).width() + 'px';
        var _height = top.$(window).height() > parseInt(options.height.replace('px', '')) ? options.height : top.$(window).height() + 'px';
        layer.open({
            id: options.id,
            type: 2,
            shade: options.shade,
            title: options.title,
            fix: false,
            area: [_width, _height],
            content: options.url,
            btn: options.btn,
            btnclass: options.btnclass,
            yes: function () {
                options.callBack(options.id)
            }, cancel: function () {
                return true;
            }
        });
    }
	
	/**
	 * 弹出确认框
	 * content 提示内容
	 * callback 点击确定的回调函数
	 */
	$.fn.modalConfirm = function (content,callBack) {
        layer.confirm(content, {
            icon: "fa-exclamation-circle",
            title: "系统提示",
            btn: ['确认', '取消'],
            btnclass: ['btn btn-primary', 'btn btn-danger'],
        }, function (index, layero) {
            callBack(true,index);
        }, function () {
            callBack(false)
        });
    }

	//弹出alert提示框
	$.fn.modalAlert = function (content, type) {
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
            icon: iconType,
            title: "系统提示",
            btn: ['确认'],
            btnclass: ['btn btn-primary'],
        });
    }
	
	//消息提示
	$.fn.modalMsg = function (content, type) {
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
            top.layer.msg(content, { icon: iconType, time: 4000, shift: 5 });
            top.$(".layui-layer-msg").find('i.' + iconType).parents('.layui-layer-msg').addClass('layui-layer-msg-' + type);
        } else {
            top.layer.msg(content);
        }
    }
	
	//
    $.fn.modalClose = function () {
        var index = layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        var $IsdialogClose = top.$("#layui-layer" + index).find('.layui-layer-btn').find("#IsdialogClose");
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

}

/**
 * 字符串去空
 */
String.prototype.trim = function()  
{  
	return this.replace(/(^\s*)|(\s*$)/g, "");  
}  

/**
 * 字符串左去空
 */
String.prototype.ltrim = function()  
{  
	return this.replace(/(^\s*)/g, "");  
}  

/**
 * 字符串右去空
 */
String.prototype.rtrim = function()  
{  
	return this.replace(/(\s*$)/g, "");  
}  