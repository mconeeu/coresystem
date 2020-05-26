/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

public final class Firework extends ExtendedItemBuilder<FireworkMeta> {

    /**
     * creates Firework instance of ItemStack amount 1
     *
     * @param effects firework effects that should be bound to this item
     */
    public Firework(FireworkEffect... effects) {
        this(1, effects);
    }

    /**
     * creates Firework instance
     *
     * @param amount  amount of items in ItemStack
     * @param effects firework effects that should be bound to this item
     */
    public Firework(int amount, FireworkEffect... effects) {
        super(new ItemStack(Material.FIREWORK, amount));

        if (effects.length > 0) {
            addEffects(effects);
        }
    }

    private Firework(ItemStack item) {
        super(item);
    }

    /**
     * wraps an existing ItemStack which must be of Material type FIREWORK
     *
     * @param firewokItem ItemStack
     * @return new Firework instance
     * @throws ClassCastException if ItemStack has a conflicting Material
     */
    public static Firework wrap(ItemStack firewokItem) throws ClassCastException {
        FireworkMeta meta = (FireworkMeta) firewokItem.getItemMeta();
        return new Firework(firewokItem);
    }

    /**
     * Add another effect to this firework.
     *
     * @param effect The firework effect to add
     * @return this
     * @throws IllegalArgumentException effect is null
     */
    public Firework addEffect(FireworkEffect effect) throws IllegalArgumentException {
        meta.addEffect(effect);
        return this;
    }

    /**
     * Add several effects to this firework.
     *
     * @param effects The firework effects to add
     * @return this
     * @throws IllegalArgumentException If effect is null
     * @throws IllegalArgumentException If any effect is null (may be thrown after changes have occurred)
     */
    public Firework addEffects(FireworkEffect... effects) throws IllegalArgumentException {
        meta.addEffects(effects);
        return this;
    }

    /**
     * Add several firework effects to this firework.
     *
     * @param effects An iterable object whose iterator yields the desired firework effects
     * @return
     * @throws IllegalArgumentException If effects is null
     * @throws IllegalArgumentException If any effect is null (may be thrown after changes have occurred)
     */
    public Firework addEffects(Iterable<FireworkEffect> effects) throws IllegalArgumentException {
        meta.addEffects(effects);
        return this;
    }

    /**
     * Remove an effect from this firework.
     *
     * @param index The index of the effect to remove
     * @return this
     * @throws IndexOutOfBoundsException If index < 0 or index > getEffectsSize()
     */
    public Firework removeEffect(int index) throws IndexOutOfBoundsException {
        meta.removeEffect(index);
        return this;
    }

    /**
     * Remove all effects from this firework.
     *
     * @return this
     */
    public Firework clearEffects() {
        meta.clearEffects();
        return this;
    }

    /**
     * Sets the approximate power of the firework. Each level of power is half a second of flight time.
     *
     * @param power the power of the firework, from 0-128
     * @return this
     */
    public Firework setPower(int power) {
        meta.setPower(power);
        return this;
    }

    /**
     * Get the effects in this firework.
     *
     * @return An immutable list of the firework effects
     */
    public List<FireworkEffect> getEffects() {
        return meta.getEffects();
    }

    /**
     * Get the number of effects in this firework.
     *
     * @return The number of effects
     */
    public int getEffectsSize() {
        return meta.getEffectsSize();
    }

    /**
     * Get whether this firework has any effects.
     *
     * @return true if it has effects, false if there are no effects
     */
    public boolean hasEffects() {
        return meta.hasEffects();
    }

    /**
     * Gets the approximate height the firework will fly.
     *
     * @return approximate flight height of the firework.
     */
    public int getPower() {
        return meta.getPower();
    }

}
