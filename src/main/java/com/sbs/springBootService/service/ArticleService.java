package com.sbs.springBootService.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.springBootService.dao.ArticleDao;
import com.sbs.springBootService.dto.Article;
import com.sbs.springBootService.dto.ResultData;
import com.sbs.springBootService.util.Util;

@Service // Conponent
public class ArticleService {
	@Autowired
	private ArticleDao articleDao;
	@Autowired
	private MemberService memberService;

	public Article getArticle(int id) {
		return articleDao.getArticle(id);
	}

	public List<Article> getArticles(String searchKeywordType, String searchKeyword) {
		return articleDao.getArticles(searchKeywordType, searchKeyword);
	}

	public ResultData addArticle(Map<String, Object> param) {
		articleDao.addArticle(param);

		int id = Util.getAsInt(param.get("id"), 0);

		return new ResultData("S-1", "성공", "id", id);
	}

	public ResultData deleteArticle(int id) {
		articleDao.deleteArticle(id);

		return new ResultData("S-1", "삭제하였습니다.", "id", id);
	}

	public ResultData modifyArticle(int id, String title, String body) {
		articleDao.modifyArticle(id, title, body);

		return new ResultData("S-1", "게시물을 수정하였습니다.", "id", id);
	}

	public ResultData getActorCanModifyRd(Article article, int actorId) {
		if (article.getMemberId() == actorId) {
			return new ResultData("S-1", "가능합니다.");
		}
		
		if(memberService.isAdmin(actorId)) {
			return new ResultData("S-2", "가능합니다.");
		}
		
		return new ResultData("F-1","권한이 없습니다.");
	}

	public ResultData getActorCanDeleteRd(Article article, int actorId) {
		return getActorCanModifyRd(article,actorId);
	}
}
