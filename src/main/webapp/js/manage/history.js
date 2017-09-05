/**
 * 
 */
var hp={
		initTable:function(uid){
			$("#gridTable").jqGrid({
	            datatype: "json",
	            url: common.getContext()+"/manage/userReview/history/list/"+uid,
//	            height: $.fn.getGridHeight(),
	            autowidth: true,
	            colModel: [
	                { label: "id", name: "id", index: "id", hidden: true },
	                { label: "账号", name: "username", index: "username",align:"left" },
	                { label: "公司名称", name: "companyname", index: "companyname", align: "left" },
	                { label: "联系人", name: "contact", index: "contact", align: "left" },
	                { label: "联系人电话", name: "contactphone", index: "contactphone", align: "left" },
	                { label: "审核时间", name: "checktime", index: "checktime", align: "left" },
	                { label: "审核结果", name: "ispass", index: "ispass", align: "left" },
	                { label: "审核人", name: "checkuser", index: "checkuser", align: "left" },
	                { label: "备注", name: "remark", index: "remark", align: "left" },
	                { label: "操作", name: "id", index: "id", align: "left" ,
	                	formatter:function(cellvalue, options, rowObject){
	            			return "<a href=\""+common.getContext()+"/manage/userReview/history/detail/"+cellvalue+"?uid="+uid+"\" title=\"详情\"><i class=\"fa fa-file-text-o\"></i></a>";
	            		}
	                }
	            ],
	            shrinkToFit: false,
	            altclass: 'altRowsColour'
	        });
			setTimeout(function(){
				var height=$(window).height()-$("#gridTable").offset().top-55;
				console.log(height);
				 $("#gridTable").setGridHeight(height);
			},100);
		}	
}