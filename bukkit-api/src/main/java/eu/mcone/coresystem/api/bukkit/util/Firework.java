package eu.mcone.coresystem.api.bukkit.util;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;

public class Firework {

    private FireworkEffect.Builder fireworkEffect_builder;

    public Firework() {
        this.fireworkEffect_builder = FireworkEffect.builder();
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

    public void spawn(Location location, int power) {
        org.bukkit.entity.Firework firework = location.getWorld().spawn(location, org.bukkit.entity.Firework.class);
        firework.getFireworkMeta().setPower(power);
        firework.getFireworkMeta().addEffect(fireworkEffect_builder.build());
        firework.setFireworkMeta(firework.getFireworkMeta());
    }
}
