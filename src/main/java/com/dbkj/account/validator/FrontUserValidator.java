package com.dbkj.account.validator;

import com.dbkj.account.service.FrontUserManageService;
import com.dbkj.account.util.FileUtil;
import com.dbkj.account.util.ValidateUtil;
import com.dbkj.account.vo.FrontUserFormVo;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import com.jfinal.validate.Validator;

public class FrontUserValidator extends Validator{
	
	private FrontUserManageService frontUserManageService=new FrontUserManageService();
	private static final String[] ALLOWED_ARRAY={"jpg","jpeg","png","bmp"};
	private static final double FILE_SIZE=3.0D;//默认最大上传图片大小
	
	private final String updateAction="/manage/frontUser/update";
	private final String addAction="/manage/frontUser/save";

	@Override
	protected void validate(Controller c) {
		String actionKey=getActionKey();
		UploadFile license = c.getFile("license");
		UploadFile idcard=c.getFile("idcard");
		UploadFile safety=c.getFile("safety");
		
		FrontUserFormVo fvo=c.getBean(FrontUserFormVo.class,"f");
		fvo.setLicenseFile(license);
		fvo.setIdcardFile(idcard);
		fvo.setSafetyFile(safety);
		
		Res res=I18n.use("zh_CN");
		if(updateAction.equals(actionKey)&&fvo.getId()==null){
			addError("idMsg", res.get("frontuser.id.empty"));
		}
		
		if(addAction.equals(actionKey)){
			String phone=fvo.getPhone();
			String phoneMsg="phoneMsg";
			if(StrKit.isBlank(phone)){
				addError(phoneMsg, res.get("frontuser.phone.empty"));
			}else if(!ValidateUtil.validateMobilePhone(phone)){
				addError(phoneMsg, res.get("frontuser.phone.format.incorrect"));
			}else if(frontUserManageService.existsPhone(phone)){
				addError(phoneMsg, res.get("frontuser.phone.exists"));
			}
		}
		
		String password=fvo.getPassword();
		String passwordMsg="passwordMsg";
		if(StrKit.isBlank(password)){
			addError(passwordMsg, res.get("frontuser.password.empty"));
		}else if(!ValidateUtil.validatePassword(password)){
			addError(passwordMsg, res.get("frontuser.password.format.incorrect"));
		}
		String confirmPassword=fvo.getConfirmPassword();
		String confirmPasswordMsg="confirmPasswordMsg";
		if(StrKit.isBlank(confirmPassword)){
			addError(confirmPasswordMsg, res.get("frontuser.confirm.password.empty"));
		}else if(!confirmPassword.equals(password)){
			addError(confirmPasswordMsg, res.get("frontuser.confirm.password.not.equal.password"));
		}
		String companyName=fvo.getCompanyName();
		String companyNameMsg="companyNameMsg";
		if(StrKit.isBlank(companyName)){
			addError(companyNameMsg, res.get("frontuser.company.name.empty"));
		}else if(ValidateUtil.validateSpecialString(companyName)){
			addError(companyNameMsg, res.get("illegal.char.msg"));
		}
		
		String contact=fvo.getContact();
		String contactMsg="contactMsg";
		if(StrKit.isBlank(contact)){
			addError(contactMsg, res.get("frontuser.contact.empty"));
		}else if(ValidateUtil.validateSpecialString(contact)){
			addError(contactMsg, res.get("illegal.char.msg"));
		}
		
		String contactPhone=fvo.getContactPhone();
		String contactPhoneMsg="contactPhoneMsg";
		if(StrKit.isBlank(contactPhone)){
			addError(contactPhoneMsg, res.get("frontuser.contact.phone.empty"));
		}else if(!ValidateUtil.validatePhoneAndMobileNum(contactPhone)){
			addError(contactPhoneMsg, res.get("phone.format.incorrect"));
		}
		
		String publicAccount=fvo.getPublicAccount();
		String publicAccountMsg="publicAccountMsg";
		if(StrKit.isBlank(publicAccount)){
			addError(publicAccountMsg, res.get("frontuser.public.account.empty"));
		}
		
		String licenseMsg="licenseMsg";
		if(addAction.equals(actionKey)&&license==null){
			addError(licenseMsg, res.get("frontuser.license.empty"));
		}
		if(license!=null){
			if(!isAllowed(license.getContentType())){
				license.getFile().delete();
				addError(licenseMsg, res.get("img.format.incorrect"));
			}else {
				long fileSize=FileUtil.getFileSize(license.getFile());
				double size = fileSize/(1024*1024d);
				if(size>FrontUserValidator.FILE_SIZE){
					license.getFile().delete();
					addError(licenseMsg, res.get("img.file.size.too.large"));
				}
				
			}
		}
		
		String idcardMsg="idcardMsg";
		if(addAction.equals(actionKey)&&idcard==null){
			addError(idcardMsg, res.get("frontuser.license.empty"));
		}
		if(idcard!=null){
			if(!isAllowed(idcard.getContentType())){
				idcard.getFile().delete();
				addError(idcardMsg, res.get("img.format.incorrect"));
			}else {
				long fileSize=FileUtil.getFileSize(idcard.getFile());
				double size = fileSize/(1024*1024d);
				if(size>FrontUserValidator.FILE_SIZE){
					license.getFile().delete();
					addError(licenseMsg, res.get("img.file.size.too.large"));
				}
			}
		}
		
		String safetyMsg="safetyMsg";
		if(addAction.equals(actionKey)&&safety==null){
			addError(safetyMsg, res.get("frontuser.license.empty"));
		}
		if(safety!=null){
			if(!isAllowed(safety.getContentType())){
				safety.getFile().delete();
				addError(safetyMsg, res.get("img.format.incorrect"));
			}else {
				long fileSize=FileUtil.getFileSize(safety.getFile());
				double size = fileSize/(1024*1024d);
				if(size>FrontUserValidator.FILE_SIZE){
					license.getFile().delete();
					addError(licenseMsg, res.get("img.file.size.too.large"));
				}
			}
		}
		
		c.setAttr("f", fvo);
	}

	@Override
	protected void handleError(Controller c) {
		c.keepBean(FrontUserFormVo.class,"f");
		String actionKey=getActionKey();
		if(addAction.equals(actionKey)){
			c.render("add.html");
		}else{
			c.render("edit.html");
		}
	}

	private boolean isAllowed(String contentType){
		contentType=contentType.substring(contentType.indexOf("/")+1);
		for(int i=0;i<FrontUserValidator.ALLOWED_ARRAY.length;i++){
			if(FrontUserValidator.ALLOWED_ARRAY[i].equals(contentType)){
				return true;
			}
		}
		return false;
	}
}
