package com.bagel.main.data;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class DataController {

    @MessageMapping("/data.sendMessage")
    @SendTo("/topic/public")
    public DataMessage sendMessage(@Payload DataMessage dataMessage){
        return dataMessage;
    }

    @MessageMapping("/data.addUser")
    @SendTo("/topic/public")
    public DataMessage addUser(@Payload DataMessage dataMessage, SimpMessageHeaderAccessor headerAccessor){
        headerAccessor.getSessionAttributes().put("username",dataMessage.getSender());
        return dataMessage;

    }

}
