var lp={
		initTable:function(){
			var cols= [
                { label: "id", name: "id", index: "id", hidden: true },
                { label: "操作用户", name: "username", index: "username", align:"left" },
                { label: "用户类型", name: "userType", index: "userType",align:"left" },
                { label: "IP地址", name: "ip", index: "ip",align:"left" },
                { label: "操作时间", name: "time", index: "time",align:"left" },
                { label: "操作内容", name: "content", index: "content",align:"left" },
                { label: "操作类型", name: "operaType", index: "operaType",align:"left" },
                { label: "操作结果", name: "operaResult", index: "operaResult",align:"left" },
                { label: "异常信息", name: "exceptionMsg", index: "exceptionMsg",align:"left" }
            ];
			
			$("#gridTable").jqGrid({
	            datatype: "json",
	            url: common.getContext()+"/manage/log/list",
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
				url: common.getContext()+"/manage/log/list",
	            postData: queryJson,
	            page:1
	        }).trigger('reloadGrid');
		},
		getStartDate:function(month){
			var year=month.substr(0,4);
			var month=month.substr(4);
			return year+"-"+month+"-"+"01"+" 00:00";
		},
		getEndDate:function(month){
			var year=month.substr(0,4);
			var month=month.substr(4);
			var date=new Date(parseInt(year),parseInt(month),0);
			return year+"-"+month+"-"+date.getDate()+" 23:59";
		}
}