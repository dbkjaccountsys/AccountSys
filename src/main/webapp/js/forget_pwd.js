/**
 * 
 */
var fp={
		/**
		 * 
		 */
		setErrorMsg:function(dom,msg){
			var $parent=$(dom).closest(".input-group");
			if($parent.next().get(0)){
				$parent.next().text(msg);
			}else{
				$parent.after("<em id=\"errorMsg\" class=\"error help-block\" style=\"color:#a94442;\">"+msg+"</em>");
			}
		}
}