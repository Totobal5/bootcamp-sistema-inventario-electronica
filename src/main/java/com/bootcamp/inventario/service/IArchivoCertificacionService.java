package com.bootcamp.inventario.service;

import com.bootcamp.inventario.dto.response.ArchivoCertificacionResponse;
import com.bootcamp.inventario.model.enums.TipoArchivo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IArchivoCertificacionService {
    
    /**
     * Obtiene todos los archivos de certificación
     */
    List<ArchivoCertificacionResponse> findAll();
    
    /**
     * Obtiene un archivo por ID
     */
    ArchivoCertificacionResponse findById(Long id);
    
    /**
     * Obtiene archivos de una solicitud de armado
     */
    List<ArchivoCertificacionResponse> findBySolicitudArmadoId(Long solicitudId);
    
    /**
     * Obtiene archivos por tipo
     */
    List<ArchivoCertificacionResponse> findByTipoArchivo(TipoArchivo tipoArchivo);
    
    /**
     * Sube un archivo de certificación (solo ADMIN/OPERADOR)
     * Tipos permitidos: PDF, PNG, JPG, JPEG
     */
    ArchivoCertificacionResponse upload(
            Long solicitudArmadoId,
            TipoArchivo tipoArchivo,
            MultipartFile file
    ) throws IOException;
    
    /**
     * Elimina un archivo de certificación (solo ADMIN)
     */
    void delete(Long id);
}
