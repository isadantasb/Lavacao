package model;

import javax.persistence.*;


@Entity
@Table(name="SETOR")
public class Setor {

    @Id
    @Column(name="CD_SETOR")
    private Integer cdSetor;

    @Column(name="NM_SETOR")
    private String nmSetor;

}
