package com.ktds.batch.batch.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ktds.batch.domain.dto.BatchResDto;
import com.ktds.batch.domain.entity.yugabyte.BatchInfo;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(source = "nm", target = "nm")
    @Mapping(source = "jobClassNm", target = "jobClassNm")
    @Mapping(source = "triggerNm", target = "triggerNm")
    @Mapping(source = "cronExpression", target = "cronExpression")
    @Mapping(source = "sttus", target = "sttus")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "lastExecutionTime", target = "lastExecutionTime")
    @Mapping(source = "nextExecutionTime", target = "nextExecutionTime")
    BatchResDto toBatchResDto(BatchInfo batchInfo);
}
