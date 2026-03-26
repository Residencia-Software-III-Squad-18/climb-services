package com.climb.api.mapper;

import com.climb.api.model.Empresa;
import com.climb.api.model.dto.EmpresaRequestDTO;
import com.climb.api.model.dto.EmpresaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

    EmpresaMapper INSTANCE = Mappers.getMapper(EmpresaMapper.class);

    @Mapping(target = "id", source = "idEmpresa")
    EmpresaResponseDTO toResponseDto(Empresa empresa);

    @Mapping(target = "idEmpresa", ignore = true)
    Empresa toEntity(EmpresaRequestDTO empresaRequestDto);

    List<EmpresaResponseDTO> toResponseDto(List<Empresa> empresas);
}
