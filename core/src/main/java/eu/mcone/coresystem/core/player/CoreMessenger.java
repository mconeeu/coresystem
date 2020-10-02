package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.chat.MarkdownParser;
import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import eu.mcone.coresystem.api.core.player.GlobalMessenger;
import eu.mcone.coresystem.api.core.translation.TranslationManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class CoreMessenger<P, Cs> implements GlobalMessenger<P, Cs> {

    protected final GlobalCoreSystem system;
    private final String prefixTranslation;

    public CoreMessenger(GlobalCoreSystem coreSystem, String prefixTranslation) {
        this.system = coreSystem;
        this.prefixTranslation = prefixTranslation;
    }

    /*
     * Player
     */

    //Default
    @Override
    @Deprecated
    public void send(P player, String message) {
        send(player, TextLevel.INFO, message);
    }

    @Override
    @Deprecated
    public void send(P player, BaseComponent... baseComponents) {
        TextComponent tc = new TextComponent(system.getTranslationManager().get(prefixTranslation, getCorePlayer(player)));
        for (BaseComponent bc : baseComponents) {
            tc.addExtra(bc);
        }

        dispatchMessage(player, tc);
    }

    @Override
    @Deprecated
    public void sendSuccess(P player, String message) {
        send(player, TextLevel.SUCCESS, message);
    }

    @Override
    @Deprecated
    public void sendInfo(P player, String message) {
        send(player, TextLevel.INFO, message);
    }

    @Override
    @Deprecated
    public void sendWarning(P player, String message) {
        send(player, TextLevel.WARNING, message);
    }

    @Override
    @Deprecated
    public void sendError(P player, String message) {
        send(player, TextLevel.ERROR, message);
    }

    @Override
    @Deprecated
    public void send(P player, TextLevel level, String message) {
        GlobalCorePlayer cp = getCorePlayer(player);

        dispatchMessage(player, new TextComponent(TextComponent.fromLegacyText(system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ) + (!level.equals(TextLevel.NONE) ? MarkdownParser.parseMarkdown(message, level) : message))));
    }

    @Override
    @Deprecated
    public void sendSimple(P player, String message) {
        dispatchMessage(player, message);
    }

    @Override
    @Deprecated
    public void sendSimple(P player, BaseComponent... baseComponents) {
        dispatchMessage(player, baseComponents);
    }

    //Translations
    @Override
    public void sendTransl(final P player, String... translation) {
        GlobalCorePlayer cp = getCorePlayer(player);

        StringBuilder sb = new StringBuilder(system.getTranslationManager().get(prefixTranslation, cp));
        for (String s : translation) {
            sb.append(system.getTranslationManager().get(s, cp));
        }

        dispatchMessage(player, sb.toString());
    }

    @Override
    public void sendTransl(final P player, String translationKey, Object... replacements) {
        GlobalCorePlayer cp = getCorePlayer(player);
        String translation = system.getTranslationManager().get(prefixTranslation, cp, replacements)
                + system.getTranslationManager().get(translationKey, cp, replacements);

        dispatchMessage(player, translation);
    }

    @Override
    public void sendSimpleTransl(final P player, String... translation) {
        GlobalCorePlayer cp = getCorePlayer(player);

        StringBuilder sb = new StringBuilder();
        for (String s : translation) {
            sb.append(system.getTranslationManager().get(s, cp));
        }

        dispatchMessage(player, sb.toString());
    }

    @Override
    public void sendSimpleTransl(final P player, String translationKey, Object... replacements) {
        GlobalCorePlayer cp = getCorePlayer(player);
        String translation = system.getTranslationManager().get(translationKey, cp, replacements);

        dispatchMessage(player, translation);
    }

    protected abstract void dispatchMessage(P player, String message);

    protected abstract void dispatchMessage(P player, BaseComponent... baseComponents);

    protected abstract GlobalCorePlayer getCorePlayer(P player);


    /*
     * Command Sender
     */

    //Default
    @Override
    @Deprecated
    public void sendSender(Cs sender, String message) {
        sendSender(sender, TextLevel.NONE, message);
    }

    @Override
    @Deprecated
    public void sendSenderInfo(Cs sender, String message) {
        sendSender(sender, TextLevel.INFO, message);
    }

    @Override
    @Deprecated
    public void sendSenderSuccess(Cs sender, String message) {
        sendSender(sender, TextLevel.SUCCESS, message);
    }

    @Override
    @Deprecated
    public void sendSenderWarning(Cs sender, String message) {
        sendSender(sender, TextLevel.WARNING, message);
    }

    @Override
    @Deprecated
    public void sendSenderError(Cs sender, String message) {
        sendSender(sender, TextLevel.ERROR, message);
    }

    @Override
    @Deprecated
    public void sendSender(Cs sender, TextLevel level, String message) {
        GlobalCorePlayer cp = getCorePlayerFromSender(sender);

        dispatchSenderMessage(sender, system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE)
                + (!level.equals(TextLevel.NONE) ? MarkdownParser.parseMarkdown(message, level) : message)
        );
    }

    @Override
    @Deprecated
    public void sendSenderSimple(Cs sender, String message) {
        dispatchSenderMessage(sender, message);
    }

    @Override
    @Deprecated
    public void sendSenderSimple(final Cs sender, final BaseComponent... baseComponents) {
        dispatchSenderMessage(sender, baseComponents);
    }

    @Override
    public void sendSenderTransl(final Cs sender, String... translation) {
        GlobalCorePlayer cp = getCorePlayerFromSender(sender);

        StringBuilder sb = new StringBuilder(system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ));
        for (String s : translation) {
            sb.append(system.getTranslationManager().get(s, cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE));
        }

        dispatchSenderMessage(sender, sb.toString());
    }

    @Override
    public void sendSenderTransl(final Cs sender, String translationKey, Object... replacements) {
        GlobalCorePlayer cp = getCorePlayerFromSender(sender);

        String translation = system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE
        ) + system.getTranslationManager().get(
                translationKey,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE,
                replacements
        );

        dispatchSenderMessage(sender, translation);
    }

    @Override
    public void sendSenderSimpleTransl(final Cs sender, String... translation) {
        GlobalCorePlayer cp = getCorePlayerFromSender(sender);

        StringBuilder sb = new StringBuilder();
        for (String s : translation) {
            sb.append(system.getTranslationManager().get(s, cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE));
        }

        dispatchSenderMessage(sender, sb.toString());
    }

    @Override
    public void sendSenderSimpleTransl(final Cs sender, String translationKey, Object... replacements) {
        GlobalCorePlayer cp = getCorePlayerFromSender(sender);

        String translation = system.getTranslationManager().get(
                translationKey,
                cp != null ? cp.getSettings().getLanguage() : TranslationManager.DEFAULT_LANGUAGE,
                replacements
        );

        dispatchSenderMessage(sender, translation);
    }

    protected abstract void dispatchSenderMessage(Cs sender, String message);

    protected abstract void dispatchSenderMessage(Cs sender, BaseComponent... baseComponents);

    private GlobalCorePlayer getCorePlayerFromSender(Cs sender) {
        GlobalCorePlayer cp;
        try {
            cp = getCorePlayer((P) sender);
        } catch (ClassCastException e) {
            cp = null;
        }

        return cp;
    }

}
