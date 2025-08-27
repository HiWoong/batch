package com.ktds.batch.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TriggerSttus {
    START("001", "BATCH_START"),
    END("002", "BATCH_END"),
    ERROR("003", "BATCH_ERROR"),
    REJECT("004", "BATCH_REJECT"),
    FAIL("005", "BATCH_FAIL"),
    ;

    private String code;
    private String description;
}
