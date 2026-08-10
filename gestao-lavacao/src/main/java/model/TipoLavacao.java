package model;


import javax.persistence.*;

@Entity
@Table(name="TIPO_LAVACAO")
public class TipoLavacao {

    @Id
    @Column(name="CD_TIPO_LAVACAO")
    private Integer cdTipoLavacao;

    @Column(name="NM_TIPO_LAVACAO")
    private String nmTipoLavacao;


}
