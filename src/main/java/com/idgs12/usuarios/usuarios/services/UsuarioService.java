package com.idgs12.usuarios.usuarios.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idgs12.usuarios.usuarios.dto.UsuarioDTO;

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
    //Funcionalidad de habilitar --Maria Fernanda Rosas Briones IDGS12
    @Transactional
    public boolean habilitarUsuario(int id) {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return false;
        }

        usuario.setEstatus(1); // Habilitado
        usuarioRepository.save(usuario);

        return true;
    }    
}
