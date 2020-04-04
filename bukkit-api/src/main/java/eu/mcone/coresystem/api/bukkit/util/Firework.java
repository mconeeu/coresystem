package eu.mcone.coresystem.api.bukkit.util;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

public class Firework {

    private org.bukkit.entity.Firework firework;
    private FireworkEffect.Builder fireworkEffect_builder;
    private FireworkMeta fireworkMeta;

    public Firework(Location location) {
//        (org.bukkit.entity.Firework) new EntityFireworks(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), null).getBukkitEntity();
        this.firework = (org.bukkit.entity.Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        this.fireworkEffect_builder = FireworkEffect.builder();
        this.fireworkMeta = this.firework.getFireworkMeta();
    }

    public Firework color(Color color) {
        this.fireworkEffect_builder.withColor(color);
        return this;
    }

    public Firework color(Color... colors) {
        this.fireworkEffect_builder.withColor(colors);
        return this;
    }

    public Firework flicker(boolean flicker) {
        this.fireworkEffect_builder.flicker(flicker);
        return this;
    }

    public Firework trail(boolean trail) {
        this.fireworkEffect_builder.trail(trail);
        return this;
    }

    public Firework fade(Color color) {
        this.fireworkEffect_builder.withFade(color);
        return this;
    }

    public Firework fade(Color... colors) {
        this.fireworkEffect_builder.withFade(colors);
        return this;
    }

    public Firework with(FireworkEffect.Type with) {
        this.fireworkEffect_builder.with(with);
        return this;
    }

    public Firework power(int power) {
        this.fireworkMeta.setPower(power);
        return this;
    }

    public org.bukkit.entity.Firework build() {
        this.firework.setFireworkMeta(this.fireworkMeta);
        return firework;
    }

    public int getPower() {
        return this.fireworkMeta.getPower();
    }

    public List<FireworkEffect> getEffects() {
        return this.fireworkMeta.getEffects();
    }
}
