package com.ktds.batch.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BatchSttus {
    AVAILABLE("001", "EXECUTION_AVAILABLE"),
    STOP("002", "EXECUTION_STOP")
    ;

    private String code;
    private String description;

}
