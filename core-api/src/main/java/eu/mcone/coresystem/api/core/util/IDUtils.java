/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

import java.util.Random;
import java.util.UUID;

public final class IDUtils {

    public static String generateID() {
        StringBuilder uuid = new StringBuilder();
        String[] uuidArray = UUID.randomUUID().toString().split("-");

        java.util.Random random = new Random(0);

        for (int i = 0; i < uuidArray.length / 3; i++) {
            uuid.append(uuidArray[random.nextInt(uuidArray.length)]);
        }

        return uuid.toString();
    }

}
