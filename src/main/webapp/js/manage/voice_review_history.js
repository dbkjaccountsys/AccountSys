/**
 * 语音审核历史列表界面
 */
var vhp={
		initTable:function(vid){
			$("#gridTable").jqGrid({
	            datatype: "json",
	            url: common.getContext()+"/manage/voiceReview/history/list/"+vid,
//	            height: $.fn.getGridHeight(),
	            autowidth: true,
	            colModel: [
	            	{ label: "id", name: "id", index: "id", hidden: true },
		            { label: "账号", name: "username", index: "username",align:"left" },
		            { label: "语音名称", name: "voiceName", index: "voiceName", align: "left" },
		            { label: "语音内容", name: "content", index: "content", align: "left" },
		            { label: "审核结果", name: "status", index: "status", align: "left" },
		            { label: "审核人", name: "checkUser", index: "checkUser", align: "left" },
		            { label: "申请时间", name: "updateTime", index: "updateTime", align: "left" },
	                { label: "操作", name: "id", index: "id", align: "left" ,
	                	formatter:function(cellvalue, options, rowObject){
	            			return "<a href=\""+common.getContext()+"/manage/voiceReview/history/detail/"+cellvalue+"?vid="+vid+"\" title=\"详情\"><i class=\"fa fa-file-text-o\"></i></a>";
	            		}
	                }
	            ],
	            shrinkToFit: false,
	            altclass: 'altRowsColour'
	        });
		}	
}