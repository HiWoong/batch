package com.ktds.batch.batch.mapper;

import org.mapstruct.Mapper;

import com.ktds.batch.domain.dto.BatchResDto;
import com.ktds.batch.domain.entity.yugabyte.BatchInfo;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    BatchResDto toBatchResDto(BatchInfo batchInfo);
}
