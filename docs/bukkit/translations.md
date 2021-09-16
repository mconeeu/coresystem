# Translation Manager

Whereever you send an message to the player keep in mind to use the Translation Manager!
Also dont use `p.sendMessage()`. Instead use the Coresystem [Messenger](messenger.md).

Utilizing the Translation Manager is easy.   
{- \`Coresystem.getInstance().getMessenger.send(p, "§aMy text!")\` -}   
{+ \`Coresystem.getInstance().getMessenger.sendTransl(p, "my.translation")\` +}

For working translations the tranlation key with the translations has to be put in the database with a specific category.
A database translation document can look like this:
```json
{
    "key" : "my.translation", 
    "category" : "myplugin", 
    "DE" : "§aMein Test!", 
    "EN" : "§aMy text!", 
    ...
}
```

Maybe you noticed the `category` field. Here you can put a plugin name. 
This would cause the translation to only be loaded when the plugin `myplugin` is launched!

For further features utilize the TranslationManager instance:
```java
TranslationManager translationManager = Coresystem.getInstance().getTranslationManager();
```
Here you have the following methods:

### `getLoadedLanguages()`
returns all loaded Languages.
*Languages will be loaded on demand if a player join which has a language that is not already loaded. 
German and English are default loaded.*

### `getLoadedCategories()`
returns all loaded Categories.
*Categories depend on the plugin name. On every server start the CoreSystem tries to Categories from all plugins*

### `loadAdditionalLanguages(Language... languages)`
loads additional Languages

### `loadAdditionalCategories(Language... categories)`
loads additional Categories

### `getTranslations(Strng key)`
returns translations in all lanugages for a specific key

### `get(String key)`
Gets the translation of a specific key in the default language.
> Do not use this method unless you know what youre doing! This Method will return the default language (german) only!

### `get(String key, Language language)`
returns the translation of a specific key in the specific language

### `get(String key, Language language, Object... replace)`
The Translation manager allows replace options. If youre translation looks like `Hello {0}. Welcome to {1}!`, then you can use this method with:
```java
String message = translationManager.get("myplugin.hello", cp.getSettings().getLanguage(), cp.getName(), "the server");
```
You noticed: the {0} and {1} will be replaced with the players name and "the server".

### `get(String key, CorePlayer player)`
returns the tranlation of a specific key in the players language

### `get(String key, CorePlayer player, Object... replace)`
returns the tranlation of a specific key in the players language with replace options
