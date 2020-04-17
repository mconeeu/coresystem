-ignorewarnings
-dontnote

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod,EventHandler,Override

-keepclassmembers class ** {
    @net.md_5.bungee.event.EventHandler *;
}

-keep class com.mongodb.**

-keep class eu.mcone.coresystem.api.**

-keep class ** extends org.bukkit.plugin.java.JavaPlugin {
    public void onLoad(); public void onEnable(); public void onDisable();
}