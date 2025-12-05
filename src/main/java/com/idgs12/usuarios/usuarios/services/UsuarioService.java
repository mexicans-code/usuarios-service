package com.idgs12.usuarios.usuarios.services;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.idgs12.usuarios.usuarios.dto.UsuarioDTO;

import com.idgs12.usuarios.usuarios.dto.*;
import com.idgs12.usuarios.usuarios.repository.UsuarioRepository;
import com.idgs12.usuarios.usuarios.repository.ProgramaUsuarioRepository;
import com.idgs12.usuarios.usuarios.FeignClient.ProgramaFeignClient;
import com.idgs12.usuarios.usuarios.entity.UsuarioEntity;
import com.idgs12.usuarios.usuarios.entity.ProgramaUsuario;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProgramaUsuarioRepository programaUsuarioRepository;

    @Autowired
    private ProgramaFeignClient programaFeignClient;

    /**
     * Obtiene todos los usuarios y los convierte a DTOs, incluyendo la información de los programas asociados
     */
    public List<UsuarioResponseDTO> getAllUsuariosDTO() {
        return usuarioRepository.findAll().stream().map(usuario -> {
            // Mapear campos básicos del usuario
            UsuarioResponseDTO dto = new UsuarioResponseDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setApellidoPaterno(usuario.getApellidoPaterno());
            dto.setApellidoMaterno(usuario.getApellidoMaterno());
            dto.setCorreo(usuario.getCorreo());
            dto.setRol(usuario.getRol());
            dto.setMatricula(usuario.getMatricula());

            // Verifica si el usuario tiene programas asociados
            if (usuario.getProgramas() != null && !usuario.getProgramas().isEmpty()) {
                // Obtiene solo los IDs de los programas del usuario
                List<Long> programaIds = usuario.getProgramas().stream()
                        .map(ProgramaUsuario::getProgramaId) // Por cada relación ProgramaUsuario, toma el ID del programa
                        .collect(Collectors.toList()); // Convierte el stream a una lista de IDs

                // Llama al microservicio de programas para obtener los detalles de cada programa por sus IDs
                List<ProgramaDTO> programas = programaFeignClient.obtenerProgramasPorIds(programaIds)
                        .stream()
                        .map(p -> { // Por cada programa recibido, crea un DTO
                            ProgramaDTO pdto = new ProgramaDTO();
                            pdto.setId(p.getId());       // Asigna el ID del programa
                            pdto.setNombre(p.getNombre()); // Asigna el nombre del programa
                            return pdto; // Devuelve el DTO simplificado
                        }).collect(Collectors.toList()); // Convierte el stream a lista de DTOs

                // Asigna la lista de programas al DTO del usuario que será devuelto
                dto.setProgramas(programas);
            }


            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public UsuarioEntity saveUsuarioConProgramas(UsuarioDTO usuarioDTO) {
        UsuarioEntity usuario;

        if (usuarioDTO.getId() != 0) {
            usuario = usuarioRepository.findById(usuarioDTO.getId()).orElse(new UsuarioEntity());
        } else {
            usuario = new UsuarioEntity();
        }

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellidoPaterno(usuarioDTO.getApellidoPaterno());
        usuario.setApellidoMaterno(usuarioDTO.getApellidoMaterno());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setRol(usuarioDTO.getRol());
        usuario.setMatricula(usuarioDTO.getMatricula());

        UsuarioEntity savedUsuario = usuarioRepository.save(usuario);

        programaUsuarioRepository.deleteByUsuario_Id(savedUsuario.getId());

        if (usuarioDTO.getProgramaIds() != null && !usuarioDTO.getProgramaIds().isEmpty()) {
            List<ProgramaUsuario> relaciones = usuarioDTO.getProgramaIds().stream()
                    .map(programaId -> {
                        ProgramaUsuario pu = new ProgramaUsuario();
                        pu.setUsuario(savedUsuario);
                        pu.setProgramaId(programaId);
                        return pu;
                    })
                    .collect(Collectors.toList());

            programaUsuarioRepository.saveAll(relaciones);
        }

        return savedUsuario;
    }

    public UsuarioResponseDTO getUsuarioDTOById(int id) {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null)
            return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellidoPaterno(usuario.getApellidoPaterno());
        dto.setApellidoMaterno(usuario.getApellidoMaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setRol(usuario.getRol());
        dto.setMatricula(usuario.getMatricula());

        if (usuario.getProgramas() != null && !usuario.getProgramas().isEmpty()) {
            List<Long> programaIds = usuario.getProgramas().stream()
                    .map(ProgramaUsuario::getProgramaId)
                    .collect(Collectors.toList());

            List<ProgramaDTO> programas = programaFeignClient.obtenerProgramasPorIds(programaIds)
                    .stream()
                    .map(p -> {
                        ProgramaDTO pdto = new ProgramaDTO();
                        pdto.setId(p.getId());
                        pdto.setNombre(p.getNombre());
                        return pdto;
                    }).collect(Collectors.toList());

            dto.setProgramas(programas);
        }

        return dto;
    }

    /*Funcionalidad de habilitar --Maria Fernanda Rosas Briones IDGS12
    @Transactional
    public boolean habilitarUsuario(int id) {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElse(null);


    // Método para deshabilitar usuario
    @Transactional
    public boolean deshabilitarUsuario(int id) {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return false;
        }

        usuario.setEstatus(1); // Habilitado
        usuarioRepository.save(usuario);

        return true;
    }    
        usuario.setActivo(false); // Cambiamos el estado a false
        usuarioRepository.save(usuario);

        return true;
    } */
    
}
