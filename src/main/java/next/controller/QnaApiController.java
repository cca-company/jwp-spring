package next.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import core.jdbc.DataAccessException;
import next.CannotDeleteException;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.Result;
import next.model.User;
import next.service.QnaService;

@RestController
@RequestMapping("/api/qna")
public class QnaApiController {
	private QnaService qnaService = QnaService.getInstance();
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	private static final Logger log = LoggerFactory.getLogger(QnaApiController.class);
	
	@RequestMapping(value="/deleteQuestion", method=RequestMethod.DELETE)
	public Result deleteQuestion(HttpSession session, Long questionId){

    	if (!UserSessionUtils.isLogined(session)) {
			return Result.fail("Login is required");
		}
		
		try {
			qnaService.deleteQuestion(questionId, UserSessionUtils.getUserFromSession(session));
			return Result.ok();
		} catch (CannotDeleteException e) {
			return Result.fail(e.getMessage());
		}
	}

	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<Question> list(){
		return questionDao.findAll();
	}

	@RequestMapping(value="/addAnswer", method=RequestMethod.PUT)
	public Map<String,Object> addAnswer(HttpSession session, Long questionId, String contents){
		Map<String,Object> result = new HashMap<String,Object>();
		if (!UserSessionUtils.isLogined(session)) {
			result.put("result", Result.fail("Login is required"));
			return result;
		}
    	
    	User user = UserSessionUtils.getUserFromSession(session);
		Answer answer = new Answer(user.getUserId(), 
				contents, 
				questionId);
		log.debug("answer : {}", answer);
		
		Answer savedAnswer = answerDao.insert(answer);
		questionDao.updateCountOfAnswer(savedAnswer.getQuestionId());
		
		result.put("answer", savedAnswer);
		result.put("result", Result.ok());
		return result;
	}


	@RequestMapping(value="/deleteAnswer", method=RequestMethod.DELETE)
	public Result deleteAnswer(HttpSession session, Long answerId){
        Result result;
		try {
			answerDao.delete(answerId);
			result = Result.ok();
		} catch (DataAccessException e) {
			result = Result.fail(e.getMessage());
		}
		return result;
	}	
	
}
