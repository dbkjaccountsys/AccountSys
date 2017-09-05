/**
 * 
 */
var vrl={
		/**
		 * 
		 */
		initTable:function(){
			$("#gridTable").jqGrid({
	            datatype: "json",
	            url: common.getContext()+"/manage/voiceReview/list",
//	            height: $.fn.getGridHeight(),
	            autowidth: true,
	            colModel: [
	                { label: "id", name: "id", index: "id", hidden: true },
	                { label: "账号", name: "username", index: "username",align:"left" },
	                { label: "语音名称", name: "voiceName", index: "voiceName", align: "left" },
	                { label: "语音内容", name: "content", index: "content", align: "left" },
	                { label: "申请时间", name: "updateTime", index: "updateTime", align: "left" },
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
		//查询
		search:function(){
			var fromDate=$("#from_datetime>input[type=text]").val();
			var toDate=$("#to_datetime>input[type=text]").val();
			var username=$("#username").val();
			var queryJson={fromDate:fromDate,toDate:toDate,username:username};
			$("#gridTable").jqGrid('setGridParam', {
				url: common.getContext()+"/manage/voiceReview/list",
	            postData: queryJson,
	            page:1
	        }).trigger('reloadGrid');
		},
		//审核
		review:function(dom){
			var id=$(dom).closest("tr").find("td:eq(1)").text();
			location.href=common.getContext()+"/manage/voiceReview/voiceInfo/"+id;
		},
		//获取审核历史记录
		getHistory:function(dom){
			var id=$(dom).closest("tr").find("td:eq(1)").text();
			location.href=common.getContext()+"/manage/voiceReview/history/"+id;
		}
}