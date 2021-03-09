package com.sbs.springBootService.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.sbs.springBootService.dto.Article;
import com.sbs.springBootService.dto.Board;
import com.sbs.springBootService.dto.GenFile;
import com.sbs.springBootService.dto.ResultData;
import com.sbs.springBootService.service.ArticleService;
import com.sbs.springBootService.service.GenFileService;

@Controller
public class AdmArticleController extends BaseController{
	@Autowired
	private ArticleService articleService;
	@Autowired
	private GenFileService genFileService;

	@RequestMapping("/adm/article/detail")
	@ResponseBody
	public ResultData showDetail(Integer id) {
		if (id == null) {
			return new ResultData("F-1", "아이디를 입력해주세요.");
		}

		Article article = articleService.getForPrintArticle(id);

		if (article == null) {
			return new ResultData("F-2", "존재하지 않는 게시물번호 입니다.");
		}

		return new ResultData("S-1", "성공", "article", article);
	}

	@RequestMapping("/adm/article/list")
	public String showList(HttpServletRequest req, @RequestParam(defaultValue = "1") int boardId, String searchKeywordType, String searchKeyword, @RequestParam(defaultValue = "1") int page) {
		// @RequestParam(defaultValue = "titleAndTitle") String searchKeywordType
		Board board = articleService.getBoard(boardId);
		req.setAttribute("board", board);
		
		if (board == null) {
			return msgAndBack(req, "존재하지 않는 게시판입니다.");
		}


		if (searchKeywordType != null) {
			searchKeywordType = searchKeywordType.trim();
		}

		if (searchKeywordType == null || searchKeywordType.length() == 0) {
			searchKeywordType = "titleAndBody";
		}

		if (searchKeyword != null && searchKeyword.length() == 0) {
			searchKeyword = null;
		}

		if (searchKeyword != null) {
			searchKeyword = searchKeyword.trim();
		}

		if (searchKeyword == null) {
			searchKeywordType = null;
		}

		int itemsInAPage = 20;

		List<Article> articles = articleService.getForPrintArticles(boardId, searchKeywordType, searchKeyword, page,
				itemsInAPage);
		
		for ( Article article : articles ) {
			GenFile genFile = genFileService.getGenFile("article", article.getId(), "common", "attachment", 1);

			if ( genFile != null ) {
				article.setExtra__thumbImg(genFile.getForPrintUrl());
			}
		}

		req.setAttribute("articles", articles);
		
		return "adm/article/list";
	}
	
	@RequestMapping("/adm/article/add")
	public String showAdd(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		return "adm/article/add";
	}
	
	@RequestMapping("/adm/article/doAdd")
	public String doAdd(@RequestParam Map<String, Object> param, HttpServletRequest req, MultipartRequest multipartRequest) {		
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		if (param.get("boardId") == null) {
			return msgAndBack(req, "게시판번호를 입력해주세요.");
		}
		
		if (param.get("title") == null) {
			return msgAndBack(req, "제목을 입력해주세요.");
		}

		if (param.get("body") == null) {
			return msgAndBack(req,"내용을 입력해주세요.");
		}

		param.put("memberId", loginedMemberId);

		ResultData addArticleRd = articleService.addArticle(param);

		int newArticleId = (int) addArticleRd.getBody().get("id");

		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();

		for (String fileInputName : fileMap.keySet()) {
			MultipartFile multipartFile = fileMap.get(fileInputName);

			if ( multipartFile.isEmpty() == false ) {
				genFileService.save(multipartFile, newArticleId);				
			}
		}

		return msgAndReplace(req, String.format("%d번 게시물이 작성되었습니다.", newArticleId), "../article/detail?id=" + newArticleId);
	}

	@RequestMapping("/adm/article/doDelete")
	@ResponseBody
	public ResultData doDelete(Integer id, HttpServletRequest req) {
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		if (id == null) {
			return new ResultData("F-1", "아이디를 입력해주세요.");
		}
		Article article = articleService.getArticle(id);

		if (article == null) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.");
		}

		ResultData actorCanDeleteRd = articleService.getActorCanDeleteRd(article, loginedMemberId);

		if (actorCanDeleteRd.isFail()) {
			return actorCanDeleteRd;
		}

		return articleService.deleteArticle(id);

	}

	@RequestMapping("/adm/article/doModify")
	@ResponseBody
	public ResultData doModify(Integer id, String title, String body, HttpServletRequest req) {
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		if (id == null) {
			return new ResultData("F-1", "아이디를 입력해주세요.");
		}

		Article article = articleService.getArticle(id);

		if (article == null) {
			return new ResultData("F-1", "해당 게시물은 존재하지 않습니다.");
		}

		ResultData actorCanModifyRd = articleService.getActorCanModifyRd(article, loginedMemberId);

		if (actorCanModifyRd.isFail()) {
			return actorCanModifyRd;
		}

		return articleService.modifyArticle(id, title, body);

	}

	@RequestMapping("/adm/article/doAddReply")
	@ResponseBody
	public ResultData doAddReply(@RequestParam Map<String, Object> param, HttpServletRequest req) {
		int loginedMemberId = (int) req.getAttribute("loginedMemberId");

		if (param.get("articleId") == null) {
			return new ResultData("F-1", "게시물번호를 입력해주세요.");
		}

		if (param.get("body") == null) {
			return new ResultData("F-1", "내용을 입력해주세요.");
		}

		param.put("memberId", loginedMemberId);

		return articleService.addReply(param);
	}

}
