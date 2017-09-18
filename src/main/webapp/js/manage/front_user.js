/**
 * 
 */
var fp={
		initTable:function(hasOpera){
			var cols= [
                { label: "id", name: "id", index: "id", hidden: true },
                { label: "用户名", name: "username", index: "username", align:"left" },
                { label: "姓名", name: "name", index: "name",align:"left" },
                { label: "手机", name: "phone", index: "phone", align: "left" },
                { label: "email", name: "email", index: "email", align: "left" },
                { label: "余额", name: "charge", index: "charge", align: "left" },
                { label: "认证状态", name: "ispass", index: "ispass", align: "left" },
                { label: "公司", name: "companyName", index: "companyName", align: "left"},
                { label: "对公账号", name: "publicAccount", index: "publicAccount", align: "left"},
                { label: "联系人", name: "contact", index: "contact", align: "left"},
                { label: "联系人电话", name: "contactPhone", index: "contactPhone", align: "left"}
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
	            url: common.getContext()+"/manage/frontUser/list",
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
			location.href=common.getContext()+"/manage/frontUser/edit?id="+id;
		},
		search:function(username){
			var queryJson={username:username};
			$("#gridTable").jqGrid('setGridParam', {
				url: common.getContext()+"/manage/frontUser/list",
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
					$.get(common.getContext()+"/manage/frontUser/delete?id="+id,function(data){
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