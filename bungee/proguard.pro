-ignorewarnings
-dontnote

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod,EventHandler,Override

-keepclassmembers class ** {
    @net.md_5.bungee.event.EventHandler *;
}

-keep class com.mongodb.**

-keep class eu.mcone.coresystem.api.**

-keep class ** extends net.md_5.bungee.api.plugin.Plugin {
    public void onEnable(); public void onDisable();
}