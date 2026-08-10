package model;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="LIMPEZA")
public class Limpeza {

    @Id
    @Column(name="CD_LIMPEZA")
    private Integer cdLimpeza;

    @Column(name="DT_LIMPEZA")
    @Temporal(TemporalType.DATE)
    private Date dtLimpeza;

    @Column(name="CD_AMBIENTE")
    private Integer cdAmbiente;

    @Column(name="CD_TIPO")
    private Integer cdTipo;

    // Não tenho ctz dos mneumonicos a seguir:

    @Column(name="LT_ENCARREGADO")
    private String ltEncarregado;

    @Column(name="LT_EQUIPE")
    private String ltEquipe;

    @Column(name="LT_OBSERVACAO")
    private String ltObservacao;

    // ^^


    @Column(name="FG_ATP")
    private Boolean fgAtp;

}
