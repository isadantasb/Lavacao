package model;

import javax.persistence.*;

@Entity
@Table(name="TESTE_ATP")
public class TesteATP {

    @Id
    @Column(name="CD_TESTE_ATP")
    private Integer cdTesteAtp;

    @Column(name="CD_LIMPEZA")
    private Integer cdLimpeza;

    @Column(name="NU_RESULTADO")
    private Integer nuResultado;
}
