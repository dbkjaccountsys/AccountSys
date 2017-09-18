/**
 * 
 */
var rp={
		initTable:function(hasOpera){
			var cols= [
                { label: "id", name: "id", index: "id", hidden: true },
                { label: "账号", name: "username", index: "username",align:"left" },
                { label: "公司", name: "companyName", index: "companyName", align: "left" },
                { label: "账户余额", name: "charge", index: "charge", align: "left" }
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
	            url: common.getContext()+"/manage/recharge/list",
//	            height: $.fn.getGridHeight(),
	            autowidth: true,
	            colModel: cols,
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
			var uid=$("#companyName").val();
			var queryJson={uid:uid};
			$("#gridTable").jqGrid('setGridParam', {
				url: common.getContext()+"/manage/recharge/list",
	            postData: queryJson,
	            page:1
	        }).trigger('reloadGrid');
		},
		charge:function(dom){
			var id=$(dom).closest("tr").find("td:eq(1)").text();
			location.href=common.getContext()+"/manage/recharge/charging?uid="+id;
		}
}