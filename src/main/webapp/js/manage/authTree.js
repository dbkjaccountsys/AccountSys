//权限树
var authTree=function(options){
	//加载权限树结构
	//加载到id为cid元素内部
	//把权限值存进id为vid的隐藏域
	//url为获取权限json数据url
	var dv=options.cid;
	var v=options.vid;
	var url=options.url;
	var chkv=[];
//	var id=options.id?"/"+options.id:"";
	$.ajax({
		type:"post",
		url:common.getContext()+url,
		dataType:"json",
		success:function(data){
			var s=loadNode(data,true);
			$("#"+v).val(chkv.join(","));
			$("#"+dv).html(s);
		}
	});
	
	function loadNode(data,flag){
		if(data){
			var content="<ul class=\"list-unstyled\" ";
			if(!flag){
				content+="style=\"margin-left:35px;\"";
			}
			content+=">";
			for(var i=0,len=data.length;i<len;i++){
				
				if(data[i].children&&data[i].children.length>0){
					content+="<li>";
					content+="<i class=\"fa fa-minus-square\"></i>";
				}else{
					content+="<li style=\"padding-left: 10px;\">";
				}
				content+="&nbsp;&nbsp;<input type=\"checkbox\" style=\"position:relative;top:3px\" value=\""+data[i].id+"\" ";
				if(data[i].checked){
					content+="checked=\"checked\"";
					chkv.push(data[i].id);
				}
				content+="/>&nbsp;&nbsp;";
				content+=data[i].name;
				content+=loadNode(data[i].children,false);
				content+="</li>";
			}
			content+="</ul>";
			return content;
		}
		return "";
	}
	//绑定事件
	$("#"+dv).on("click","i.fa-minus-square",function(){
		$(this).attr("class","fa fa-plus-square");
		var uls = $(this).closest("li").find("ul");
		uls&&$(uls[0]).hide();
	});
	$("#"+dv).on("click","i.fa-plus-square",function(){
		$(this).attr("class","fa fa-minus-square");
		var uls=$(this).closest("li").find("ul");
		uls&&$(uls[0]).show();
	});
	$("#"+dv).on("change","input[type=checkbox]",function(){
		var chked=$(this).get(0).checked;
		if(chked){
			checkParents(this);
		}else{
			uncheckChildren(this);
		}
	});
	//选中所有父节点
	function checkParents(dom){
		addVal(dom);
		var $li=$(dom).closest("ul").closest("li");
		if($li&&$li.get(0)){
			var chkboxs=$li.find("input[type=checkbox]");
			if(chkboxs&&chkboxs[0]){
				chkboxs[0].checked=true;
				checkParents(chkboxs[0]);
			}
		}
		
	}
	//将选择的权限的id存进隐藏域中
	function addVal(dom){				
		var val=$("#"+v).val();
		if(val){
			arr=val.split(",");
		}else{
			arr=[];
		}
		var n=$(dom).val();
		if(n&&"null"!=n){
			var flag=true;
			for(var i=0,len=arr.length;i<len;i++){
				if(arr[i]==n){
					flag=false;
					break;
				}
			}
			if(flag){
				arr.push(n);
			}
		}
		$("#"+v).val(arr.join(","));
	}
	//反选所有子节点
	function uncheckChildren(dom){
		removeVal(dom);
		$(dom).closest("li").children().find("input[type=checkbox]").each(function(){
			$(this).removeAttr("checked");
			//
			removeVal(this);
		});
		
	}
	function removeVal(dom){
		var arr;
		var val=$("#"+v).val();
		if(val){
			arr=val.split(",");
		}else{
			arr=[];
		}
		var n=$(dom).val();
		for(var i=0,len=arr.length;i<len;i++){
			if(arr[i]==n){
				arr.splice(i,1);
				break;
			}
		}
		$("#"+v).val(arr.join(","));
	}
}