package next.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import next.annotation.LoginUser;
import next.dao.UserDao;
import next.model.User;

@Controller
@RequestMapping("/users")
public class UsersController {
	private static final Logger log = LoggerFactory.getLogger(UsersController.class);
	private UserDao userDao = UserDao.getInstance();
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public String index(@LoginUser User loginUser, Model model) throws SQLException{
    	if (loginUser.isGuestUser()) {
			return "redirect:/users/loginForm";
		}
    	
        model.addAttribute("users", userDao.findAll());
        return "user/list";
	}
	
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String form(){
		return "user/form";
	}

	@RequestMapping(value="/create", method=RequestMethod.PUT)
	public String create(User user){
		log.debug("create user : {"+user+"}");
		userDao.insert(user);
		return "redirect:/";
	}
	
	@RequestMapping(value="/loginForm", method=RequestMethod.GET)
	public String loginForm(){
		return "user/login";
	}

	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String login(HttpSession session, String userId, String password){
        User user = userDao.findByUserId(userId);
        
        if (user == null) {
            throw new NullPointerException("사용자를 찾을 수 없습니다.");
        }
        
        if (user.matchPassword(password)) {
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            throw new IllegalStateException("비밀번호가 틀립니다.");
        }
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpSession session){
		session.removeAttribute("user");
    	return "redirect:/qna/list";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.GET)
	public String profile(String userId, Model model){
        model.addAttribute("user", userDao.findByUserId(userId));
        return "user/profile";
	}
	
	@RequestMapping(value="/updateForm", method=RequestMethod.GET)
	public String updateForm(@LoginUser User loginUser, String userId, Model model){
		User user = userDao.findByUserId(userId);
    	if (!loginUser.isSameUser(user)) {
        	throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }
        model.addAttribute("user", user);
        return "user/updateForm";
	}

	@RequestMapping(value="/update", method=RequestMethod.POST)
	public String update(@LoginUser User loginUser, String userId, String password, String name, String email){	
		User user = userDao.findByUserId(userId);
        if (!loginUser.isSameUser(user)) {
        	throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }
        User updateUser = new User(userId, password, name, email);
        
		log.debug("update user : {"+user+"}");
		userDao.update(updateUser);
		return "redirect:/";
	}
	
	
}
