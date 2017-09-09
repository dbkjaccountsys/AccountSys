/**
 * 
 */
var bp={
		initTable:function(hasOpera){
			var cols= [
                { label: "id", name: "id", index: "id", hidden: true },
                { label: "用户名", name: "username", index: "username", align:"left" },
                { label: "姓名", name: "name", index: "name",align:"left" },
                { label: "手机", name: "phone", index: "phone", align: "left" },
                { label: "email", name: "email", index: "email", align: "left" },
                { label: "用户角色", name: "roleName", index: "roleName", align: "left"},
                { label: "创建时间", name: "createTime", index: "contactPhone", align: "left"}
            ];
			if(hasOpera=="yes"){
				cols.push({ label: "操作", name: "opera", index: "opera", align: "left" ,
                	formatter:function(cellvalue, options, rowObject){
            			eval("var fn="+cellvalue);
            			return (fn&&fn(cellvalue, options, rowObject))||"";
            		}
                });
			}
			$("#gridTable").jqGrid({
	            datatype: "json",
	            url: common.getContext()+"/manage/backUser/list",
//	            height: $(window).height()-$("#gridTable").offset().top,
	            autowidth: true,
	            colModel:cols,
	            shrinkToFit: false,
	            altclass: 'altRowsColour',
	            rowNum: "10000",
	            rownumbers: true,
	            pager: "#gridPager",
	            rowList: [20, 50, 100, 500, 1000],
	            rowNum: "20",
	            jsonReader:{
	            	root: "data",
	                page: "currentPage",
	                total: "totalCount",
	                records: "records"
	            }
	        });
			setTimeout(function(){
				var height=$(window).height()-$("#gridTable").offset().top-55;
				console.log(height);
				 $("#gridTable").setGridHeight(height);
			},100);
		},
		edit:function(dom){
			var id=$(dom).closest("tr").find("td:eq(1)").text();
			location.href=common.getContext()+"/manage/backUser/edit?id="+id;
		},
		search:function(username){
			var queryJson={username:username};
			$("#gridTable").jqGrid('setGridParam', {
				url: common.getContext()+"/manage/backUser/list",
	            postData: queryJson,
	            page:1
	        }).trigger('reloadGrid');
		},
		del:function(dom){
			$.fn.modalConfirm("是否删除当前用户",function(){
				var b=arguments[0];
				var index=arguments[1];
				if(b){//确定
					var id=$(dom).closest("tr").find("td:eq(1)").text();
					$.get(common.getContext()+"/manage/backUser/delete?id="+id,function(data){
						layer.close(index);
						if(data.success){
							$("#gridTable").trigger("reloadGrid");
						}else{
							$.fn.Alert("操作失败！","error");
						}
					});
				}
			});
		}
}