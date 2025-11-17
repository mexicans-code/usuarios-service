package com.idgs12.usuarios.usuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idgs12.usuarios.usuarios.dto.UsuarioDTO;
import com.idgs12.usuarios.usuarios.entity.UsuarioEntity;
import com.idgs12.usuarios.usuarios.services.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public UsuarioEntity crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioService.saveUsuarioConProgramas(usuarioDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> actualizarUsuario(@PathVariable int id, @RequestBody UsuarioDTO usuarioDTO) {
        usuarioDTO.setId(id);
        UsuarioEntity updated = usuarioService.saveUsuarioConProgramas(usuarioDTO);
        return ResponseEntity.ok(updated);
    }

    // Funcionalidad para deshabilitar un usuario
    @Transactional
    @PutMapping("/deshabilitar/{id}")
    public ResponseEntity<Boolean> deshabilitarUsuario(@PathVariable int id) {
        boolean resultado = usuarioService.deshabilitarUsuario(id);
        if (resultado) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
