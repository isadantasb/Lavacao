package model;

import javax.persistence.*;


public class Ambiente {

    @Column(name="NM_AMBIENTE")
    private Integer nmAmbiente;

    @Column(name="CD_SETOR")
    private Integer cdSetor;

    @Column(name="CD_PARTE_SALA")
    private Integer cdParteSala;

}
