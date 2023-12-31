package com.bagel.main.websocket.dto;

import com.bagel.main.data.ResponseType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BagelResponseDTO {
    private ResponseType type;
    private String topic;
    private String message;
}
