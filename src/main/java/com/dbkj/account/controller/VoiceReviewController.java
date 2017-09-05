package com.dbkj.account.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbkj.account.dic.Constant;
import com.dbkj.account.dto.Page;
import com.dbkj.account.dto.VoiceReviewDto;
import com.dbkj.account.model.Admin;
import com.dbkj.account.service.VoiceReviewService;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class VoiceReviewController extends Controller{
	
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	private VoiceReviewService voiceReviewService=new VoiceReviewService();

	public void index(){
		render("index.html");
	}
	
	public void list(){
		Page<VoiceReviewDto> page=new Page<VoiceReviewDto>();
		int pageNum=getParaToInt("page",1);
		page.setCurrentPage(pageNum);
		int rows=getParaToInt("rows",20);
		page.setPageSize(rows);
		
		String fromDate=getPara("fromDate");
		String toDate=getPara("toDate");
		String username=getPara("username");
		
		Admin admin=getSessionAttr(Constant.CURRENT_USER);
		voiceReviewService.getPage(page, fromDate, toDate, username, admin.getRoleId());
		renderJson(page);
	}
	
	public void voiceInfo(){
		long id=getParaToLong();
		VoiceReviewDto voiceReviewDto=voiceReviewService.getVoiceInfo(id);
		setAttr("v", voiceReviewDto);
		render("review.html");
	}
	
	public void review(){
		long uid=((Admin)getSessionAttr(Constant.CURRENT_USER)).getId();
		VoiceReviewDto voiceReviewDto=getBean(VoiceReviewDto.class,"v");
		String result=voiceReviewService.validateReview(voiceReviewDto);
		if(null==result){
			boolean flag = voiceReviewService.review(voiceReviewDto,getRequest(), uid);
			if(flag){
				redirect("/manage/voiceReview");
				return;
			}else{
				setAttr("errorMsg", "操作失败！");
			}
		}else{//验证不通过
			setAttr("errorMsg", result);
		}
		VoiceReviewDto v = voiceReviewService.getVoiceInfo(voiceReviewDto.getId());
		v.setStatus(voiceReviewDto.getStatus());
		v.setReason(voiceReviewDto.getReason());
		setAttr("v",v);
		render("review.html");
	}
	
	public void history(){//用户资料审核历史
		long vid=getParaToLong();
		setAttr("vid", vid);
		render("history.html");
	}
	
	@ActionKey("/manage/voiceReview/history/list")
	public void historyList(){
		long vid=getParaToLong();
		List<VoiceReviewDto> list=voiceReviewService.getHistoryList(vid);
		setAttr("vid", vid);
		renderJson(list);
	}
	
	@ActionKey("/manage/voiceReview/history/detail")
	public void historyDetail(){
		long id=getParaToLong();
		VoiceReviewDto dto=voiceReviewService.getHistoryDetail(id);
		setAttr("v", dto);
		render("history_detail.html");
	}
	
	@ActionKey("/manage/voiceReview/history/voice")
	public void getHistoryVoice(){
		String name=getPara("name");
		File file=voiceReviewService.getHistoryVoice(name);
		if(file==null||!file.exists()){//文件不存在
			HttpServletResponse response = getResponse();
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(),e);
				}
			}
			renderNull();
		}else{
			renderFile(file);
		}
	}
}
