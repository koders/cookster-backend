package lv.cookster.entity;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Rihards on 15.06.2014.
 */
@Entity
@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Level implements Serializable {

    private static final long serialVersionUID = 5307014824202765952L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
