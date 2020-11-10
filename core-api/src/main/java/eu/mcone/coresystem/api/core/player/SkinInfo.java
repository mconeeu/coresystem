/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SkinInfo {

    public enum SkinType {
        DATABASE, PLAYER, CUSTOM
    }

    private String name, value, signature;
    private SkinType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkinInfo skinInfo = (SkinInfo) o;
        return Objects.equals(name, skinInfo.name) &&
                value.equals(skinInfo.value) &&
                signature.equals(skinInfo.signature) &&
                type == skinInfo.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, signature, type);
    }

}
