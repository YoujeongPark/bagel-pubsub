package com.bagel.main.websocket.service;

import com.bagel.main.data.ResponseType;
import com.bagel.main.websocket.dto.BagelResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RequiredArgsConstructor
@Service

public class MessageQueueService {
    private final ObjectMapper objectMapper;
    private final Map<String, Queue<String>> topicQueues = new ConcurrentHashMap<>();
    private final Map<String, Boolean> topicProcessFlags = new ConcurrentHashMap<>();
    private final Map<String, List<WebSocketSession>> topicSubscribers = new ConcurrentHashMap<>();

    @Autowired
    private AsyncTaskExecutor taskExecutor;

    public void subscribe(String topic, WebSocketSession session) {
        topicSubscribers.putIfAbsent(topic, new CopyOnWriteArrayList<>());
        topicSubscribers.get(topic).add(session);
    }

    public void unsubscribe(String topic, WebSocketSession session) {
        if(!topicSubscribers.containsKey(topic))
            return;

        topicSubscribers.get(topic).remove(session);
    }

    public void unsubscribeAll(WebSocketSession session) {
        topicSubscribers.forEach((topic, sessions) -> sessions.remove(session));
    }

    public void addToQueue(String topic, String message) {
        topicQueues.putIfAbsent(topic, new ConcurrentLinkedQueue<>());
        topicQueues.get(topic).add(message);

        var isProcessing = Optional.ofNullable(topicProcessFlags.get(topic))
                                   .orElse(false);

        if (!isProcessing) {
            taskExecutor.submit(() -> sendMessages(topic));
        }

    }

    private void sendMessages(String topic) {
        if(topic == null) {
            log.error("topic is null!");
            return;
        }

        var topicQueue = topicQueues.get(topic);
        if (topicQueue == null) {
            log.error("topicQueue is null!");
            return;
        }

        topicProcessFlags.put(topic, true);
        var subscribers = topicSubscribers.get(topic);
        while (!topicQueue.isEmpty()) {
            var message = topicQueue.poll();
            if (subscribers == null) {
                log.error("subscribers are null!");
                continue;
            }

            if (message == null) {
                log.error("message is null!");
                continue;
            }

            var bagelResponse = BagelResponseDTO.builder()
                    .type(ResponseType.MESSAGE)
                    .topic(topic)
                    .message(message)
                    .build();

            try {
                var bagelMessage = objectMapper.writeValueAsString(bagelResponse);
                for (WebSocketSession webSocketSession : subscribers) {
                    try {
                        webSocketSession.sendMessage(new TextMessage(bagelMessage));
                    } catch (IOException e) {
                        log.error(Arrays.toString(e.getStackTrace()));
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("failed to serialize response");
            }
        }
        topicProcessFlags.put(topic, false);
    }
}
