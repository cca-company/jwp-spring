package next.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import next.CannotDeleteException;
import next.annotation.LoginUser;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import next.service.QnaService;

@Controller
@RequestMapping("/qna")
public class QnaController {

	private QnaService qnaService = QnaService.getInstance();
	private QuestionDao questionDao = QuestionDao.getInstance();
	private AnswerDao answerDao = AnswerDao.getInstance();
	
	@RequestMapping(value="/show", method=RequestMethod.GET)
	public String show(Long questionId, Model model){
        Question question = questionDao.findById(questionId);
        List<Answer> answers = answerDao.findAllByQuestionId(questionId);
        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
		return "qna/show";
	}

	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String form(@LoginUser User loginUser){
		if (loginUser.isGuestUser()) {
			return "redirect:/users/loginForm";
		}
		return "qna/form";
	}
	
	@RequestMapping(value="/create", method=RequestMethod.PUT)
	public String create(@LoginUser User loginUser, String title, String contents){
		if (loginUser.isGuestUser()) {
			return "redirect:/users/loginForm";
		}
    	User user = loginUser;
    	Question question = new Question(user.getUserId(), title, contents);
    	questionDao.insert(question);
		return "redirect:/";
	}

	@RequestMapping(value="/updateForm", method=RequestMethod.GET)
	public String form(@LoginUser User loginUser, Long questionId, Model model){
		if (loginUser.isGuestUser()) {
			return "redirect:/users/loginForm";
		}
		Question question = questionDao.findById(questionId);
		if (!question.isSameUser(loginUser)) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		model.addAttribute("question", question);
		return "qna/update";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(@LoginUser User loginUser, Long questionId, String title, String contents){
		if (loginUser.isGuestUser()) {
			return "redirect:/users/loginForm";
		}
		Question question = questionDao.findById(questionId);
		if (!question.isSameUser(loginUser)) {
			throw new IllegalStateException("다른 사용자가 쓴 글을 수정할 수 없습니다.");
		}
		Question newQuestion = new Question(question.getWriter(), title, contents);
		question.update(newQuestion);
		questionDao.update(question);
		return "redirect:/";
	}

	@RequestMapping(value="/delete", method=RequestMethod.DELETE)
	public String delete(@LoginUser User loginUser, Long questionId, Model model){
        Question question = questionDao.findById(questionId);
        List<Answer> answers = answerDao.findAllByQuestionId(questionId);
        model.addAttribute("question", question);
        model.addAttribute("answers", answers);
        try {
			qnaService.deleteQuestion(questionId, loginUser);
			return "redirect:/";
		}catch(CannotDeleteException e){
			model.addAttribute("question", qnaService.findById(questionId));
			model.addAttribute("answers", qnaService.findAllByQuestionId(questionId));
			model.addAttribute("errorMessage", e.getMessage());
			return "qna/show";
		}
	}
}
