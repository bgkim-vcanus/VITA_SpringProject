package me.vita.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import lombok.extern.log4j.Log4j;
import me.vita.domain.DeletedFeedVO;
import me.vita.domain.UserVO;
import me.vita.domain.WarnVO;
import me.vita.dto.WarnDetailDTO;
import me.vita.dto.WarnSimpleDTO;
import me.vita.security.Auth;
import me.vita.security.Auth.Role;
import me.vita.service.WarnService;

@Controller
@Log4j
@RequestMapping("/warn")
public class WarnController {
	
	@Autowired
	WarnService service;
	
	@GetMapping("/list/{page}")
	@ResponseBody
	@Auth(Role.ADMIN)
	public List<WarnSimpleDTO> getlist(@PathVariable("page") Integer page) {
		return service.getList(page);
	}
	
	@GetMapping("/{feedNo}")
	@ResponseBody
	@Auth(Role.ADMIN)
	public WarnDetailDTO get(@PathVariable("feedNo") Integer feedNo) {
		return service.get(feedNo);
	}
	
	// delete이지만 수정으로 delete 처리
	@DeleteMapping("/{feedNo}")
	@ResponseBody
	@Auth(Role.ADMIN)
	public ResponseEntity<String> remove(@PathVariable("feedNo") Integer feedNo, @RequestBody DeletedFeedVO deletedFeedVO) {
		if(service.remove(feedNo, deletedFeedVO)) {
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{feedNo}")
	@ResponseBody
	@Auth(Role.ADMIN)
	public ResponseEntity<String> modify(@PathVariable("feedNo") Integer feedNo) {
		if(service.modify(feedNo)) {
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/new")
	@Auth
	public ResponseEntity<String> register(@SessionAttribute("authUser") UserVO user, @RequestBody WarnVO warnVO){
		warnVO.setUserId(user.getUserId());
		if(service.register(warnVO)) {
			return new ResponseEntity<String>("success", HttpStatus.OK);
		}else {
			return new ResponseEntity<String>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
}
