/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.util;

import java.io.*;

public class GenericUtils {

    /**
     * Serializes a object to an byteArray
     *
     * @param object Object
     * @return byte array
     */
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

    /**
     * Deserializes a object to an Object
     *
     * @param byteArray serialized object
     * @return Object
     */

    public static <T> T deserialize(Class<T> clazz, final byte[] byteArray) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
            ObjectInput in = new ObjectInputStream(bis);
            Object obj = in.readObject();
            in.close();
            return clazz.cast(obj);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
