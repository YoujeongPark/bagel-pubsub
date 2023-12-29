package com.bagel.main.data;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMessage {
    private String content;
    private String sender;
    private MessageType type;
}
