package com.sbs.springBootService.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sbs.springBootService.dao.ArticleDao;
import com.sbs.springBootService.dto.Article;
import com.sbs.springBootService.dto.Board;
import com.sbs.springBootService.dto.GenFile;
import com.sbs.springBootService.dto.ResultData;
import com.sbs.springBootService.util.Util;

@Service // Conponent
public class ArticleService {
	@Autowired
	private GenFileService genFileService;
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

		changeInputFileRelIds(param, id);

		return new ResultData("S-1", "성공", "id", id);
	}

	public ResultData deleteArticle(int id) {
		articleDao.deleteArticle(id);

		genFileService.deleteGenFiles("article", id);

		return new ResultData("S-1", "삭제하였습니다.", "id", id);
	}

	public ResultData modifyArticle(Map<String, Object> param) {
		articleDao.modifyArticle(param);

		int id = Util.getAsInt(param.get("id"), 0);

		return new ResultData("S-1", "게시물을 수정하였습니다.", "id", id);
	}

	private void changeInputFileRelIds(Map<String, Object> param, int id) {
		String genFileIdsStr = Util.ifEmpty((String) param.get("genFileIdsStr"), null);

		if (genFileIdsStr != null) {
			List<Integer> genFileIds = Util.getListDividedBy(genFileIdsStr, ",");

			// 파일이 먼저 생성된 후에, 관련 데이터가 생성되는 경우에는, file의 relId가 일단 0으로 저장된다.
			// 그것을 뒤늦게라도 이렇게 고처야 한다.
			for (int genFileId : genFileIds) {
				genFileService.changeRelId(genFileId, id);
			}
		}
	}

	public ResultData getActorCanModifyRd(Article article, int actorId) {
		if (article.getMemberId() == actorId) {
			return new ResultData("S-1", "가능합니다.");
		}

		if (memberService.isAdmin(actorId)) {
			return new ResultData("S-2", "가능합니다.");
		}

		return new ResultData("F-1", "권한이 없습니다.");
	}

	public ResultData getActorCanDeleteRd(Article article, int actorId) {
		return getActorCanModifyRd(article, actorId);
	}

	public Article getForPrintArticle(int id) {
		return articleDao.getForPrintArticle(id);
	}

	public List<Article> getForPrintArticles(int boardId, String searchKeywordType, String searchKeyword, int page,
			int itemsInAPage) {
		int limitStart = (page - 1) * itemsInAPage;
		int limitTake = itemsInAPage;

		List<Article> articles = articleDao.getForPrintArticles(boardId, searchKeywordType, searchKeyword, limitStart,
				limitTake);
		List<Integer> articleIds = articles.stream().map(article -> article.getId()).collect(Collectors.toList());
		Map<Integer, Map<String, GenFile>> filesMap = genFileService.getFilesMapKeyRelIdAndFileNo("article", articleIds,
				"common", "attachment");

		for (Article article : articles) {
			Map<String, GenFile> mapByFileNo = filesMap.get(article.getId());

			if (mapByFileNo != null) {
				article.getExtraNotNull().put("file__common__attachment", mapByFileNo);
			}
		}

		return articles;
	}

	public Board getBoard(int id) {
		return articleDao.getBoard(id);
	}

	public ResultData addReply(Map<String, Object> param) {
		articleDao.addReply(param);

		int id = Util.getAsInt(param.get("id"), 0);

		return new ResultData("S-1", "성공", "id", id);
	}
}
