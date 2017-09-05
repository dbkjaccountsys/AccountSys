/**
 * 
 */
var urp={
	initTable:function(){
		$("#gridTable").jqGrid({
            datatype: "json",
            url: common.getContext()+"/manage/userReview/list",
//            height: $.fn.getGridHeight(),
            autowidth: true,
            colModel: [
                { label: "id", name: "id", index: "id", hidden: true },
                { label: "userId", name: "userId", index: "userId", hidden: true },
                { label: "账号", name: "username", index: "username",align:"left" },
                { label: "公司名称", name: "companyname", index: "companyname", align: "left" },
                { label: "联系人", name: "contact", index: "contact", align: "left" },
                { label: "联系人电话", name: "contactphone", index: "contactphone", align: "left" },
                { label: "申请时间", name: "modifyTime", index: "modifyTime", align: "left" },
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
	},
	//查询
	search:function(){
		var fromDate=$("#from_datetime>input[type=text]").val();
		var toDate=$("#to_datetime>input[type=text]").val();
		var username=$("#username").val();
		var queryJson={fromDate:fromDate,toDate:toDate,username:username};
		$("#gridTable").jqGrid('setGridParam', {
			url: common.getContext()+"/manage/userReview/list",
            postData: queryJson,
            page:1
        }).trigger('reloadGrid');
	},
	//审核
	review:function(dom){
		var id=$(dom).closest("tr").find("td:eq(1)").text();
		location.href=common.getContext()+"/manage/userReview/userinfo/"+id;
	},
	//获取审核历史记录
	getHistory:function(dom){
		var id=$(dom).closest("tr").find("td:eq(2)").text();
		location.href=common.getContext()+"/manage/userReview/history/"+id;
	}
}