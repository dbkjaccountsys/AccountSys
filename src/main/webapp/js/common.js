/**
 * 工具集合对象
 */
var common={
	//验证非法字符的正则表达式
	illegalRegex:/[`~!@#\$%\^\&\*\(\)_\+<>\?:"\{\},\.\\\/;'\[\]]/im,
	//邮箱正则表达式
	emailRegex:/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
	//手机号码正则表达式
	mobilePhoneRegex:/^1[3|4|5|7|8]\d{9}$/,
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
		return this.optional(element)||!common.illegalRegex.test(value);
	},"不能包含非法字符");
	
	//验证手机号
	jQuery.validator.addMethod("mobilePhone",function(value,element,param){
		return this.optional(element)||common.mobilePhoneRegex.test(value);
	},"手机号码格式不正确");
	
	//验证email
	jQuery.validator.addMethod("email",function(value,element,param){
		return this.optional(element)||common.emailRegex.test(value);
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
				if(element.parent().attr("class").indexOf("input-group")!=-1){
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
if($.fn&&undefined!==layer){
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
            yes: function (index, layero) {
                options.callBack&&options.callBack(index)
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

//ajax操作全局监测，用户session失效
jQuery&&jQuery(function ($) {
    //备份jquery的ajax方法
    var _ajax=$.ajax;
    //重写ajax方法，先判断登陆再执行success函数
    $.ajax=function (opt) {
    	var _success=opt&&opt.success||function(a,b){};
        var _error=opt&&opt.error||function (a,b,c) {};
        var _complete=opt&&opt.complete||function(a,b){};
        var _opt=$.extend(opt,{
        	success:function(data,textStatus){
        		_success(data,textStatus);
        	},
            error:function (xhr,textstatus,errorThrown) {
                try{
                    console.log(textstatus);
                    if(xhr.status==403){//登陆过期或者没有权限
                    	$.fn.modalAlert("登陆过期或无此权限");
                    }
                }catch (e){
                    console.log(e);
                }
                _error(xhr,textstatus,errorThrown);
            },
            complete:function(xhr,textStatus){
            	console.log(textStatus);
            	_complete(xhr,textStatus);
            }
        });
        return _ajax(_opt);
    };
});

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

/**
 * 将日期转为指定格式字符串
 * 如：format:'yyyy-MM-dd h:m:s
 */
Date.prototype.format = function(format) {
    var date = {
       "M+": this.getMonth() + 1,
       "d+": this.getDate(),
       "h+": this.getHours(),
       "m+": this.getMinutes(),
       "s+": this.getSeconds(),
       "q+": Math.floor((this.getMonth() + 3) / 3),
       "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
       format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
       if (new RegExp("(" + k + ")").test(format)) {
           format = format.replace(RegExp.$1, RegExp.$1.length == 1
              ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
       }
    }
    return format;
}