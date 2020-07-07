package eu.mcone.coresystem.bukkit.npc.nms.player.controller;

import eu.mcone.coresystem.bukkit.npc.nms.player.EntityHuman;

public class ControllerJump {
    private EntityHuman b;
    protected boolean a;

    public ControllerJump(EntityHuman var1) {
        this.b = var1;
    }

    public void a() {
        this.a = true;
    }

    public void b() {
        this.b.i(this.a);
        this.a = false;
    }
}
