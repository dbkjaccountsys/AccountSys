/**
 * Home Page
 */
var hp={
		loadMenu:function(){
			$.ajax({
				type:"post",
				url:common.getContext()+"/manage/adminMenu",
				dataType:"json",
				success:function(data){
					rmfc(data)
					$(".sidebar-menu").sidebarMenu({data:data});
					console.log(data);
					hp.loadDefaultTab(data);
				}
			});
			
			function rmfc(nodes){
				for(var i=0,len=nodes.length;i<len;i++){
					var node=nodes[i];
					if(node.url){
						node.url=node.url.substring(1);
					}else{
						if(node.children){
							rmfc(node.children);
						}
					}
					
				}
			}
		},
		loadDefaultTab:function(nodes){
			for(var i=0,len=nodes.length;i<len;i++){
				var node=nodes[i];
				if(node.url){
					addTabs({id:node.id,title:node.text,url:node.url});
					return;
				}else{
					if(node.children){
						this.loadDefaultTab(node.children);
						return;
					}
				}
			}
		}
}