package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import net.md_5.bungee.api.chat.BaseComponent;

public interface GlobalMessenger<S> {

    /**
     * send message with prefix to sender
     * use one of the send methods with TextLevel instead
     *
     * @param sender  sender
     * @param message message
     */
    @Deprecated
    void send(S sender, String message);

    @Deprecated
    void send(S sender, BaseComponent... baseComponents);

    /**
     * send message with TextLevel success with prefix to sender
     * use the sendSenderTransl method instead
     *
     * @param sender  sender
     * @param message message
     */
    @Deprecated
    void sendSuccess(S sender, String message);

    /**
     * send message with TextLevel info with prefix to sender
     * use the sendSenderTransl method instead
     *
     * @param sender  sender
     * @param message message
     */
    @Deprecated
    void sendInfo(S sender, String message);

    /**
     * send message with TextLevel warning with prefix to sender
     * use the sendSenderTransl method instead
     *
     * @param sender  sender
     * @param message message
     */
    @Deprecated
    void sendWarning(S sender, String message);

    /**
     * send message with TextLevel error with prefix to sender
     * use the sendSenderTransl method instead
     *
     * @param sender  sender
     * @param message message
     */
    @Deprecated
    void sendError(S sender, String message);

    /**
     * send message with specified TextLevel with prefix to sender
     * use this if you want no TextLevel (TextLevel.NONE)
     * use the sendTransl method instead
     *
     * @param sender  sender
     * @param message message
     */
    @Deprecated
    void send(S sender, TextLevel level, String message);

    /**
     * send message without prefix to sender
     * use the sendSimpleTransl method instead
     *
     * @param sender  sender
     * @param message message
     */
    @Deprecated
    void sendSimple(S sender, String message);

    /**
     * send message without prefix to sender
     * use the sendSimpleTransl method instead
     *
     * @param sender        sender
     * @param baseComponents text component
     */
    @Deprecated
    void sendSimple(S sender, BaseComponent... baseComponents);

    /**
     * send translations with prefix to sender
     *
     * @param sender      sender
     * @param translation translation name/key
     */
    void sendTransl(S sender, String translation);

    /**
     * send translation with replacements and prefix to sender
     *
     * @param sender         sender
     * @param translationKey translation name/key
     * @param replacements   replacements
     */
    void sendTransl(S sender, String translationKey, Object... replacements);

    /**
     * send translations without prefix to sender
     *
     * @param sender      sender
     * @param translation translation name/key
     */
    void sendSimpleTransl(S sender, String translation);

    /**
     * send translation with replacements and without prefix to sender
     *
     * @param sender         sender
     * @param translationKey translation name/key
     * @param replacements   replacements
     */
    void sendSimpleTransl(S sender, String translationKey, Object... replacements);

}
