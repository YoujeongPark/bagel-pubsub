package com.bagel.main.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

//    private final SimpMessageSendingOperations messageTemplate;
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
//        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(event.getMessage());
//        String username = (String) stompHeaderAccessor.getSessionAttributes().get("username");
//        if(username != null){
//            log.info("User disconnected : {} ", username);
//            var dataMessage = DataMessage.builder()
//                    .type(MessageType.LEAVE)
//                    .sender(username)
//                    .build();
//            messageTemplate.convertAndSend("/topic/public", dataMessage);
//        }
//    }

}
