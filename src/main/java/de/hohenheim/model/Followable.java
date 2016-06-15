package de.hohenheim.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by bunyaminbilgin on 08.06.16.
 */
@Entity
@Inheritance
//Seiten und Benutzer werden hierduch in eine Tabelle abgelegt, der Rest (Spalten und co.) mir NULL gefüllt.
@DiscriminatorColumn(name = "PAGE_TYPE")
//Die Tabelle, auf der Alles gespeichert werden soll - der Name ist immer optional bzw. individuell anpassbar.
@Table(name = "FOLLOWABLE_USER")
//Die abstrakte Klasse hat die ID und wird somit auch durch USER spezialisiert.
public abstract class Followable {

    @Id
    @GeneratedValue(generator = "generator")
    @GenericGenerator(name = "generator", strategy = "increment")
    //Jedes der Objekte hat hierdurch ihre eigene ID
    @Column(name = "ID")
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



}
