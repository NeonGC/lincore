package ru.lodes.lincore.data;

import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.lodes.lincore.api.enums.ServerType;

@Data
@Table(name = "servers")
@Entity
@NoArgsConstructor
public class Server implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(nullable = false)
    protected ServerType type;
    @Column(name = "b_name", nullable = false)
    protected String name;
    @Column(nullable = false)
    protected String ip;
    @Column(nullable = false)
    protected int port;
}
