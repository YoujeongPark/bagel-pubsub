package com.bagel.main.websocket.handler;

import com.bagel.main.data.ResponseType;
import com.bagel.main.websocket.dto.BagelRequestDTO;
import com.bagel.main.websocket.dto.BagelResponseDTO;
import com.bagel.main.websocket.service.MessageQueueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.text.MessageFormat;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketDataHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final MessageQueueService messageQueueService;

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("payload {}" , payload);

        try {
            var requestMessage = objectMapper.readValue(payload, BagelRequestDTO.class);
            var type = requestMessage.getType();
            var topic = requestMessage.getTopic();

            switch(type) {
                case PUBLISH -> messageQueueService.addToQueue(topic, requestMessage.getMessage());
                case SUBSCRIBE -> messageQueueService.subscribe(topic, session);
                case UNSUBSCRIBE -> messageQueueService.unsubscribe(topic, session);
            }

            sendResponseMessage(session, topic, MessageFormat.format("{0} is succeeded", type));

        } catch (Exception e) {
            sendResponseMessage(session, "", "failed to serialize response");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("welcome!!!"));
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        messageQueueService.unsubscribeAll(session);
    }

    private void sendResponseMessage(WebSocketSession session, String topic, String message) {
        try {
            var bagelResponse = BagelResponseDTO.builder()
                    .type(ResponseType.RECEIPT)
                    .topic(topic)
                    .message(message)
                    .build();

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(bagelResponse)));
        } catch(Exception e) {
            log.error("Error sending response message", e);
        }
    }
}