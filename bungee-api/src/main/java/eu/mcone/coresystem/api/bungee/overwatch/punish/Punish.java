package eu.mcone.coresystem.api.bungee.overwatch.punish;

import eu.mcone.coresystem.api.core.overwatch.punish.PunishTemplate;
import eu.mcone.coresystem.api.core.util.IDUtils;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.UUID;

@Getter
@BsonDiscriminator
public class Punish {

    private final String punishID;
    private final UUID punished, teamMember;
    private final PunishTemplate template;
    private final String reason;
    private BanEntry banEntry;
    private MuteEntry muteEntry;

    public Punish(UUID punished, UUID teamMember, PunishTemplate template, String reason) {
        this.punishID = IDUtils.generateID();
        this.punished = punished;
        this.teamMember = teamMember;
        this.template = template;
        this.reason = reason;
    }

    @BsonCreator
    public Punish(@BsonProperty("punishID") String punishID, @BsonProperty("punished") UUID punished, @BsonProperty("teamMember") UUID teamMember,
                  @BsonProperty("template") PunishTemplate template, @BsonProperty("reason") String reason,
                  @BsonProperty("banEntry") BanEntry banEntry, @BsonProperty("muteEntry") MuteEntry muteEntry) {
        this.punishID = punishID;
        this.punished = punished;
        this.teamMember = teamMember;
        this.template = template;
        this.reason = reason;
        this.banEntry = banEntry;
        this.muteEntry = muteEntry;
    }


    public void addBanEntry(long end) {
        banEntry = new BanEntry(end);
    }

    public void addMuteEntry(long end, String chatLogID) {
        muteEntry = new MuteEntry(end, chatLogID);
    }

    public boolean isBanned() {
        return banEntry != null;
    }

    public boolean isMuted() {
        return muteEntry != null;
    }

    public void unBan() {
        if (banEntry != null) {
            banEntry.setUnPunished(System.currentTimeMillis() / 1000);
        }
    }

    public void unMute() {
        if (muteEntry != null) {
            muteEntry.setUnPunished(System.currentTimeMillis() / 1000);
        }
    }
}
