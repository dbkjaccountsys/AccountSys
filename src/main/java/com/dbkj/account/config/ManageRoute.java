package com.dbkj.account.config;

import com.dbkj.account.controller.ManageHomeController;
import com.dbkj.account.controller.ManageLoginController;
import com.dbkj.account.controller.UserManageController;
import com.dbkj.account.controller.UserReviewController;
import com.dbkj.account.controller.VoiceReviewController;
import com.jfinal.config.Routes;

public class ManageRoute extends Routes{

	@Override
	public void config() {
		setBaseViewPath("/pages/manage");
		add("/manage",ManageHomeController.class);
		add("/manage/login",ManageLoginController.class);		
		add("/manage/user",UserManageController.class,"/user");
		add("/manage/userReview",UserReviewController.class,"/user_review");
		add("/manage/voiceReview",VoiceReviewController.class,"/voice_review");
	}

}
