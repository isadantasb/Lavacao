package model;


import javax.persistence.*;

@Entity
@Table(name="PARTE_SALA")
public class ParteSala {

    @Id
    @Column(name="CD_PARTE_SALA")
    private Integer cdParteSala;

    @Column(name="NM_PARTE_SALA")
    private String nmParteSala;

}
