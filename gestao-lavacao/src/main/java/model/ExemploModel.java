package model;


import javax.persistence.*;

@Entity
@Table(name="Exemplo")
public class ExemploModel {
    @Id
    private Integer id;

    @Column(name="NM_EXEMPLO")
    private String nmExemplo;

}
