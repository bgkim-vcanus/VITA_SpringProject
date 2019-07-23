package me.vita.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.AllArgsConstructor;
import me.vita.service.LoginService;


@Controller
@RequestMapping("/login")
@AllArgsConstructor
public class LoginController {
	
	private LoginService service;
	
	
	//�α����������� �̵�
	@GetMapping("")
	public void login(){}
	
	
	//�α���
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public ModelAndView submit(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mav){
		mav.addObject("id", id);
		mav.addObject("pw", pw);
		String password = service.getPw(id);
		if(password==null){
			mav.setViewName("loginfail");
			mav.addObject("response", "id�� �������� ����");
		}else{
			//���̵� ����
			if(password.equals(pw)){
				//�α��� ����
				if(service.getAuthstatus(id).equals("T")){
					//�̸��� ���� �Ϸ�� ���
					mav.setViewName("loginsuccess");
					mav.addObject("response", "id,pw ��ġ, �̸��������Ϸ�");
				}else{
					//�̸��� ���� �ȵ� ���
					mav.setViewName("emailAuth");
					mav.addObject("id", id);
					mav.addObject("response", "id,pw ��ġ, �̸��������ʿ�");
				}
			}else{
				//�α��� ����
				mav.setViewName("loginfail");
				mav.addObject("response", "id��ġ pw����ġ");
			}
		}
		return mav;
	}
	
	@RequestMapping(value="/authorize", method=RequestMethod.POST)
	public ModelAndView authorize(@RequestParam("id") String id, @RequestParam("authKey") String authKey,ModelAndView mav){

		System.out.println("==================Ȯ��");
		mav.setViewName("authorize");
		System.out.println("==================Ȯ��2");
		System.out.println("authkey: "+service.getAuthkey(id));
		System.out.println(authKey);
		
		if(service.getAuthkey(id).equals(authKey)){
			mav.addObject("response", "�̸��� ������ �Ϸ�Ǿ����ϴ�.");
			service.modifyAuthstatus(id);
		}else{
			mav.addObject("response", "�����ڵ尡 �ٸ��ϴ�.");
		}
		
		
		return mav;
	}
	
	
	//ȸ�������������� �̵�
	@GetMapping(value="/signup")
	public String gosignup(){
		return "signup";
	}

	
	//ȸ������
	@PostMapping(value="/signup")
	public String signup(@RequestParam("id") String id, @RequestParam("password") String pw, @RequestParam("nickname") String nick, @RequestParam("email") String email)throws Exception{
		service.register(id, pw, nick, email, "");
		return "redirect:/login";
	}
	
	@RequestMapping(value="/idcheck", method=RequestMethod.POST, produces ="application/json; charset=UTF-8")
	@ResponseBody
	public Map<Object, Object> idcheck(@RequestBody String userId){
		int count = service.getUserIdcnt(userId);
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("cnt", count);
		
		return map;

		
	}
	
	

	
	
}
