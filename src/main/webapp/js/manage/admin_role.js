/**
 * 
 */
var ap={
		initTable:function(hasOpera){
			var cols= [
                { label: "id", name: "id", index: "id", hidden: true },
                { label: "角色", name: "roleName", index: "roleName", align:"left" },
                { label: "描述", name: "desc", index: "desc",align:"left" }
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
	            url: common.getContext()+"/manage/adminRole/list",
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
			location.href=common.getContext()+"/manage/adminRole/edit?id="+id;
		},
		search:function(username){
			var queryJson={username:username};
			$("#gridTable").jqGrid('setGridParam', {
				url: common.getContext()+"/manage/adminRole/list",
	            postData: queryJson,
	            page:1
	        }).trigger('reloadGrid');
		}
}