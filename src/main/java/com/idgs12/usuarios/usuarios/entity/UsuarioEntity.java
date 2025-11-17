package com.idgs12.usuarios.usuarios.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String matricula;
    private String correo;
    private String contrasena;
    private String rol;
    //Funcionalidad de habilitar --Maria Fernanda Rosas Briones IDGS12
    @Column(nullable = false)
    private int estatus = 1; 

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProgramaUsuario> programas;

}
