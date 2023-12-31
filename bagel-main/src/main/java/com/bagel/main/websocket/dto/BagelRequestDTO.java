package com.bagel.main.websocket.dto;

import com.bagel.main.data.RequestType;
import lombok.Value;

/*
{
    "type":"SUBSCRIBE",
    "topic":"topic1"
}

{
    "type":"UNSUBSCRIBE",
    "topic":"topic1"
}

{
    "type":"PUBLISH",
    "topic":"topic1",
    "message":"Hello, World!"
}
*/

@Value
public class BagelRequestDTO {
    RequestType type;
    String topic;
    String message;
}
