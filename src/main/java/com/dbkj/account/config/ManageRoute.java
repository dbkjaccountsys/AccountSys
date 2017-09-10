package com.dbkj.account.config;

import com.dbkj.account.controller.AdminRoleManageController;
import com.dbkj.account.controller.BackUserManageController;
import com.dbkj.account.controller.FavorableController;
import com.dbkj.account.controller.ManageHomeController;
import com.dbkj.account.controller.ManageLoginController;
import com.dbkj.account.controller.RechargeController;
import com.dbkj.account.controller.FrontUserManageController;
import com.dbkj.account.controller.UserReviewController;
import com.dbkj.account.controller.VoiceReviewController;
import com.jfinal.config.Routes;

public class ManageRoute extends Routes{

	@Override
	public void config() {
		setBaseViewPath("/pages/manage");
		add("/manage",ManageHomeController.class);
		add("/manage/login",ManageLoginController.class);		
		add("/manage/frontUser",FrontUserManageController.class,"/front_user");
		add("/manage/userReview",UserReviewController.class,"/user_review");
		add("/manage/voiceReview",VoiceReviewController.class,"/voice_review");
		add("/manage/favorable",FavorableController.class,"/favorable");
		add("/manage/recharge",RechargeController.class,"/recharge");
		add("/manage/backUser",BackUserManageController.class,"/back_user");
		add("/manage/adminRole",AdminRoleManageController.class,"/admin_role");
	}

}
