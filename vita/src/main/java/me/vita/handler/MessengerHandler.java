package me.vita.handler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.vita.domain.MessengerVO;
import me.vita.domain.UserVO;
import me.vita.dto.MessengerDTO;
import me.vita.service.MessengerService;

public class MessengerHandler extends TextWebSocketHandler {

	// 키:유저아이디 값:유저세션
	Map<String, Object> userSessions = new HashMap<String, Object>();

	@Autowired
	MessengerService service;

	// 연결완료시
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if (getUser(session) == null) {
			session.close();
			return;
		}

		String userId = getUser(session).getUserId();

		// Map에 유저아이디, 유저세션을 put
		userSessions.put(userId, session);
	}

	// 메세지 받고 보낼때
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// json으로 보낸 메세지 받기
		String requestText = message.getPayload();
		JsonParser jsonParse = new JsonParser();
		JsonObject jobj = (JsonObject) jsonParse.parse(requestText);

		// json객체에서 인스턴스 추출
		String type = jobj.get("type").getAsString();
		switch (type) {
		case "message":
			sendMessage(session, jobj);
			break;
		case "check":
			checkMessage(session, jobj);
			break;
		}
	}

	private void checkMessage(WebSocketSession session, JsonObject jobj) throws IOException {
		String reqId = jobj.get("contactUser").getAsString();
		String resId = getUser(session).getUserId();
		Integer msgNo = jobj.get("msgNo").getAsInt();

		WebSocketSession responseSession = (WebSocketSession) userSessions.get(reqId);
		int re = service.modify(reqId, resId, msgNo);
		if (re > 0) {
			jobj.addProperty("count", re);
			session.sendMessage(new TextMessage(new Gson().toJson(jobj)));
			if (responseSession != null) {
				responseSession.sendMessage(new TextMessage(new Gson().toJson(jobj)));
			}
		}
	}

	private void sendMessage(WebSocketSession session, JsonObject jobj) throws IOException {
		String msg = jobj.get("msg").getAsString();
		String reqId = getUser(session).getUserId();
		String resId = jobj.get("resId").getAsString();

		if (resId == null) { // 사용자 선택 안할시의 예외처리
			return;
		}

		MessengerVO sendMsg = new MessengerVO();
		sendMsg.setMsg(msg);
		sendMsg.setReqId(reqId);
		sendMsg.setResId(resId);
		sendMsg.setMsgDate(new Date());

		WebSocketSession responseSession = (WebSocketSession) userSessions.get(resId);
		if (service.register(sendMsg)) { // 메시지 입력 성공
			if (responseSession != null) { // 상대방 존재시
				MessengerDTO responseDTO = service.get(sendMsg.getMsgNo());
				JsonObject responseJobj = (JsonObject) new Gson().toJsonTree(responseDTO);

				responseJobj.addProperty("type", "message");
				responseSession.sendMessage(new TextMessage(new Gson().toJson(responseJobj)));
			}
			// 나에게 메시지 성공 전송
			jobj.addProperty("msgNo", sendMsg.getMsgNo());
			jobj.addProperty("msgDate", sendMsg.getMsgDate().getTime());
			jobj.addProperty("msgChk", sendMsg.getMsgChk());
			jobj.addProperty("msgChk", "F");
			jobj.addProperty("reqId", reqId);
			jobj.addProperty("type", "success");
			session.sendMessage(new TextMessage(new Gson().toJson(jobj)));
		} else { // 메시지 입력 실패
			jobj.addProperty("type", "sendError");
			session.sendMessage(new TextMessage(new Gson().toJson(jobj)));
		}

	}

	// 연결 종료시
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		if (getUser(session) == null) {
			session.close();
			return;
		}
		String userId = getUser(session).getUserId();

		// Map에 유저아이디, 유저세션을 remove
		userSessions.remove(userId);

	}

	public UserVO getUser(WebSocketSession session) {
		Map<String, Object> httpSession = session.getAttributes();
		UserVO user = (UserVO) httpSession.get("authUser");

		return user;
	}
}
