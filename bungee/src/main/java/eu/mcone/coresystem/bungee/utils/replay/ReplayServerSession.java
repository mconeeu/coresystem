package eu.mcone.coresystem.bungee.utils.replay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.Server;

@Getter
@Setter
@AllArgsConstructor
public class ReplayServerSession {

    private long registered;
    private String replayID;
    private Server server;
    private String gamemode;

}
