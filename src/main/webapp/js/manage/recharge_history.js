/**
 * 
 */
var hp={
		initTable:function(){
			var cols= [
                { label: "id", name: "id", index: "id", hidden: true },
                { label: "用户名称", name: "username", index: "username", align:"left" },
                { label: "公司名称", name: "companyName", index: "companyName",align:"left" },
                { label: "充值金额", name: "charge", index: "charge",align:"left" },
                { label: "实际充值金额", name: "realCharge", index: "realCharge",align:"left" },
                { label: "充值类型", name: "chargeType", index: "chargeType",align:"left" },
                { label: "序列号", name: "serialNum", index: "serialNum",align:"left" },
                { label: "充值人", name: "chargeUser", index: "chargeUser",align:"left" },
                { label: "操作时间", name: "time", index: "time",align:"left" }
            ];
			
			$("#gridTable").jqGrid({
	            datatype: "json",
	            url: common.getContext()+"/manage/recharge/history/list",
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
		search:function(queryJson){
			$("#gridTable").jqGrid('setGridParam', {
				url: common.getContext()+"/manage/recharge/history/list",
	            postData: queryJson,
	            page:1
	        }).trigger('reloadGrid');
		}
}