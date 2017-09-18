/**
 * 
 */
var fp={
		initTable:function(){
			$("#gridTable").jqGrid({
	            datatype: "json",
	            url: common.getContext()+"/manage/favorable/list",
//	            height: $(window).height()-$("#gridTable").offset().top,
	            autowidth: true,
	            colModel: [
	                { label: "id", name: "id", index: "id", hidden: true },
	                { label: "名称", name: "name", index: "name", align:"left" },
	                { label: "开始时间", name: "beginTime", index: "beginTime",align:"left" },
	                { label: "结束时间", name: "endTime", index: "endTime", align: "left" },
	                { label: "赠费比率", name: "rate", index: "rate", align: "left" },
	                { label: "最大充值", name: "maxcharge", index: "maxcharge", align: "left" },
	                { label: "最小充值", name: "mincharge", index: "mincharge", align: "left" },
	                { label: "操作", name: "opera", index: "opera", align: "left" ,
	                	formatter:function(cellvalue, options, rowObject){
	            			eval("var fn="+cellvalue);
	            			return (fn&&fn(cellvalue, options, rowObject))||"";
	            		}
	                }
	            ],
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
			location.href=common.getContext()+"/manage/favorable/edit/"+id;
		},
		cancel:function(dom){
			$.fn.modalConfirm("是否取消当前活动",function(){
				var b=arguments[0];
				var index=arguments[1];
				if(b){//确定
					var id=$(dom).closest("tr").find("td:eq(1)").text();
					$.get(common.getContext()+"/manage/favorable/cancel?id="+id,function(data){
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