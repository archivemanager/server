package org.archivemanager.web.service;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.archivemanager.ActionMessageListener;
import org.archivemanager.ActionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;



public class MessagingController {
	private static final Logger log = Logger.getLogger(MessagingController.class.getName());	
	private ActionMessageListener listener;
	@Autowired private ObjectMapper objMapper;
	
	@RequestMapping(value="/inbound", method = RequestMethod.POST,consumes = "text/plain;charset=UTF-8",produces = "text/plain;charset=UTF-8")
    public void inboundPost(@RequestBody String data, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		ActionMessage message = (ActionMessage)objMapper.readValue(data, ActionMessage.class);
		try {
			listener.onMessage(message);
		} catch(Exception e) {
			log.log(Level.SEVERE, data, e);
		}
	}

	public void setListener(ActionMessageListener listener) {
		this.listener = listener;
	}
		
}
