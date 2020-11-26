package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.chat.MarkdownParser;
import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import eu.mcone.coresystem.api.core.player.GlobalMessenger;
import eu.mcone.coresystem.api.core.translation.CoreTranslationManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class CoreMessenger<S> implements GlobalMessenger<S> {

    protected final GlobalCoreSystem system;
    private final String prefixTranslation;

    public CoreMessenger(GlobalCoreSystem coreSystem, String prefixTranslation) {
        this.system = coreSystem;
        this.prefixTranslation = prefixTranslation;
    }

    //Default
    @Override
    @Deprecated
    public void send(S sender, String message) {
        send(sender, TextLevel.INFO, message);
    }

    @Override
    @Deprecated
    public void send(S sender, BaseComponent... baseComponents) {
        GlobalCorePlayer cp = getCorePlayer(sender);

        TextComponent tc = new TextComponent(system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : CoreTranslationManager.DEFAULT_LANGUAGE
        ));
        for (BaseComponent bc : baseComponents) {
            tc.addExtra(bc);
        }

        dispatchMessage(sender, tc);
    }

    @Override
    @Deprecated
    public void sendSuccess(S sender, String message) {
        send(sender, TextLevel.SUCCESS, message);
    }

    @Override
    @Deprecated
    public void sendInfo(S sender, String message) {
        send(sender, TextLevel.INFO, message);
    }

    @Override
    @Deprecated
    public void sendWarning(S sender, String message) {
        send(sender, TextLevel.WARNING, message);
    }

    @Override
    @Deprecated
    public void sendError(S sender, String message) {
        send(sender, TextLevel.ERROR, message);
    }

    @Override
    @Deprecated
    public void send(S sender, TextLevel level, String message) {
        GlobalCorePlayer cp = getCorePlayer(sender);

        dispatchMessage(sender, system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : CoreTranslationManager.DEFAULT_LANGUAGE)
                + (!level.equals(TextLevel.NONE) ? MarkdownParser.parseMarkdown(message, level) : message)
        );
    }

    @Override
    @Deprecated
    public void sendSimple(S sender, String message) {
        dispatchMessage(sender, message);
    }

    @Override
    @Deprecated
    public void sendSimple(S sender, BaseComponent... baseComponents) {
        dispatchMessage(sender, baseComponents);
    }

    //Translations
    @Override
    public void sendTransl(S sender, String translation) {
        GlobalCorePlayer cp = getCorePlayer(sender);

        dispatchMessage(
                sender,
                system.getTranslationManager().get(prefixTranslation, cp) + system.getTranslationManager().get(translation, cp)
        );
    }

    @Override
    public void sendTransl(S sender, String translationKey, Object... replacements) {
        GlobalCorePlayer cp = getCorePlayer(sender);

        String translation = system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : CoreTranslationManager.DEFAULT_LANGUAGE
        ) + system.getTranslationManager().get(
                translationKey,
                cp != null ? cp.getSettings().getLanguage() : CoreTranslationManager.DEFAULT_LANGUAGE,
                replacements
        );

        dispatchMessage(sender, translation);
    }

    @Override
    public void sendSimpleTransl(S sender, String translation) {
        GlobalCorePlayer cp = getCorePlayer(sender);

        String sb = system.getTranslationManager().get(
                prefixTranslation,
                cp != null ? cp.getSettings().getLanguage() : CoreTranslationManager.DEFAULT_LANGUAGE
        ) + system.getTranslationManager().get(translation, cp != null ? cp.getSettings().getLanguage() : CoreTranslationManager.DEFAULT_LANGUAGE);
        dispatchMessage(sender, sb);
    }

    @Override
    public void sendSimpleTransl(final S sender, String translationKey, Object... replacements) {
        GlobalCorePlayer cp = getCorePlayer(sender);

        String translation = system.getTranslationManager().get(
                translationKey,
                cp != null ? cp.getSettings().getLanguage() : CoreTranslationManager.DEFAULT_LANGUAGE,
                replacements
        );

        dispatchMessage(sender, translation);
    }

    protected abstract void dispatchMessage(S sender, String message);

    protected abstract void dispatchMessage(S sender, BaseComponent... baseComponents);

    protected abstract GlobalCorePlayer getCorePlayer(S sender);

}
