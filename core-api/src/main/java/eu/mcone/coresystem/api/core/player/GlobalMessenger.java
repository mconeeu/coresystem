package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import net.md_5.bungee.api.chat.BaseComponent;

public interface GlobalMessenger<P, Cs> {

    /**
     * send message with prefix to player
     * use one of the send methods with TextLevel instead
     *
     * @param player  player
     * @param message message
     */
    @Deprecated
    void send(P player, String message);

    @Deprecated
    void send(P player, BaseComponent... baseComponents);

    /**
     * send message with TextLevel success with prefix to player
     * use the sendSenderTransl method instead
     *
     * @param player  player
     * @param message message
     */
    @Deprecated
    void sendSuccess(P player, String message);

    /**
     * send message with TextLevel info with prefix to player
     * use the sendSenderTransl method instead
     *
     * @param player  player
     * @param message message
     */
    @Deprecated
    void sendInfo(P player, String message);

    /**
     * send message with TextLevel warning with prefix to player
     * use the sendSenderTransl method instead
     *
     * @param player  player
     * @param message message
     */
    @Deprecated
    void sendWarning(P player, String message);

    /**
     * send message with TextLevel error with prefix to player
     * use the sendSenderTransl method instead
     *
     * @param player  player
     * @param message message
     */
    @Deprecated
    void sendError(P player, String message);

    /**
     * send message with specified TextLevel with prefix to player
     * use this if you want no TextLevel (TextLevel.NONE)
     * use the sendTransl method instead
     *
     * @param player  player
     * @param message message
     */
    @Deprecated
    void send(P player, TextLevel level, String message);

    /**
     * send message without prefix to player
     * use the sendSimpleTransl method instead
     *
     * @param player  player
     * @param message message
     */
    @Deprecated
    void sendSimple(P player, String message);

    /**
     * send message without prefix to player
     * use the sendSimpleTransl method instead
     *
     * @param player        player
     * @param baseComponents text component
     */
    @Deprecated
    void sendSimple(P player, BaseComponent... baseComponents);

    /**
     * send translations with prefix to player
     *
     * @param player      player
     * @param translation translation name/key
     */
    void sendTransl(P player, String... translation);

    /**
     * send translation with replacements and prefix to player
     *
     * @param player         player
     * @param translationKey translation name/key
     * @param replacements   replacements
     */
    void sendTransl(P player, String translationKey, Object... replacements);

    /**
     * send translations without prefix to player
     *
     * @param player      player
     * @param translation translation name/key
     */
    void sendSimpleTransl(P player, String... translation);

    /**
     * send translation with replacements and without prefix to player
     *
     * @param player         player
     * @param translationKey translation name/key
     * @param replacements   replacements
     */
    void sendSimpleTransl(P player, String translationKey, Object... replacements);

    /**
     * send message with prefix to command sender
     * use one of the send methods with TextLevel instead
     *
     * @param sender  command sender
     * @param message message
     */
    @Deprecated
    void sendSender(Cs sender, String message);

    /**
     * send message with TextLevel info with prefix to command sender
     * use the sendSenderTransl method instead
     *
     * @param sender  command sender
     * @param message message
     */
    @Deprecated
    void sendSenderInfo(Cs sender, String message);

    /**
     * send message with TextLevel success with prefix to command sender
     * use the sendSenderTransl method instead
     *
     * @param sender  command sender
     * @param message message
     */
    @Deprecated
    void sendSenderSuccess(Cs sender, String message);

    /**
     * send message with TextLevel warning with prefix to command sender
     * use the sendSenderTransl method instead
     *
     * @param sender  command sender
     * @param message message
     */
    @Deprecated
    void sendSenderWarning(Cs sender, String message);

    /**
     * send message with TextLevel error with prefix to command sender
     * use the sendSenderTransl method instead
     *
     * @param sender  command sender
     * @param message message
     */
    @Deprecated
    void sendSenderError(Cs sender, String message);


    /**
     * send message with specified TextLevel with prefix to command sender
     * use this if you want no TextLevel (TextLevel.NONE)
     * use the sendSenderTransl method instead
     *
     * @param sender  command sender
     * @param message message
     */
    @Deprecated
    void sendSender(Cs sender, TextLevel level, String message);

    /**
     * send message without prefix to command sender
     * use the sendSenderSimpleTransl method instead
     * @param sender  command sender
     * @param baseComponents text component
     */
    @Deprecated
    void sendSenderSimple(Cs sender, BaseComponent... baseComponents);

    /**
     * send message without prefix to command sender
     * use the sendSenderSimpleTransl method instead
     *
     * @param sender  command sender
     * @param message message
     */
    @Deprecated
    void sendSenderSimple(Cs sender, String message);

    /**
     * send translations without prefix to command sender
     *
     * @param sender      command sender
     * @param translation translation name/key
     */
    void sendSenderTransl(Cs sender, String... translation);

    /**
     * send translation with replacements and prefix to command sender
     *
     * @param sender         command sender
     * @param translationKey translation name/key
     * @param replacements   replacements
     */
    void sendSenderTransl(Cs sender, String translationKey, Object... replacements);

    /**
     * send translation with replacements and without prefix to player
     *
     * @param sender         command sender
     * @param translationKey translation name/key
     * @param replacements   replacements
     */
    void sendSenderSimpleTransl(Cs sender, String translationKey, Object... replacements);

    /**
     * send translations without prefix to player
     *
     * @param sender      command sender
     * @param translation translation name/key
     */
    void sendSenderSimpleTransl(Cs sender, String... translation);

}
