package me.vita.service;

import me.vita.domain.UserVO;
import me.vita.dto.UserDTO;

public interface UserService {
	
	public String getPw(String userId);
	
	public void register(UserVO userVO)throws Exception;
	
	public int getUserIdcnt(String userId);
	
	public String getAuthstatus(String userId);
	
	public String getAuthkey(String userId);
	
	public void modifyAuthstatus(String userId);
	
	public UserDTO get(String myId, String userId);
	
	boolean login(UserVO userVO);
}
