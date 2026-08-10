package model;


import javax.persistence.*;

@Entity
@Table(name="USUARIO")
public class Usuario {

    @Id
    @Column(name="CD_USUARIO")
    private Integer cdUsuario;

    @Column(name="NM_COMPLETO")
    private String nmCompleto;

    @Column(name="NU_CPF",length=11)
    private String nuCpf;

    @Column(name="LT_EMAIL")
    private String ltEmail;

    // Verificar se o mneumonico ta certo
    @Column(name="SE_USUARIO")
    private String seUsuario;

    @Column(name="FG_ADMINISTRADOR")
    private Boolean fgAdministrador;

}
