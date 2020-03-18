/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

import java.io.*;

public class GenericUtils {

    public static byte[] serialize(final Object object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            byte[] array = bos.toByteArray();
            bos.close();
            return array;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserialize(final byte[] byteArray) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            ObjectInput in = new ObjectInputStream(bis);
            Object obj = in.readObject();
            in.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
