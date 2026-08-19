package model;


import javax.persistence.*;

@Entity
@Table(name="COLABORADOR")
public class Colaborador {

    @Id
    @Column(name="CD_COLABORADOR")
    private Integer cdColaborador;

    @Column(name="NM_COMPLETO")
    private String nmCompleto;

    @Column(name="NU_CPF",length=11)
    private String nuCpf;

    @Column(name="LT_EMAIL")
    private String ltEmail;

    // Verificar se o mneumonico ta certo
    @Column(name="SE_COLABORADOR")
    private String seColaborador;

    @Column(name="FG_ADMINISTRADOR")
    private Boolean fgAdministrador;

}
