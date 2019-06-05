package ru.lodes.lincore.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "users")
@Entity
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int playerId;
    @Column(nullable = false)
    protected String name;
    protected List<String> ignored;
    @Column(nullable = false)
    protected String ip;
    protected long lastAlive = System.currentTimeMillis();
    protected boolean messageEnabled;
    @OneToOne(optional = true)
    @JoinColumn(name = "id")
    protected Server server;
}
