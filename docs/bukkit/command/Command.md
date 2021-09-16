With the CoreCommand or CorePlayerCommand class you can easily create a command.
There are two command classes: CoreCommand and CorePlayerCommand. 
The CoreCommand is only for console sender and player and the CorePlayerCommand only for player.

**Create a Command that can execute a Console sender and Player**
```java
public class ConsoleCommand extends CoreCommand {

    public ConsoleCommand() {
        super("test", //The name of the command.
                "system.test", //The permission you ned to execute this command.
                "tes", "t" //The aliases with you can also call the command.
        );
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] strings) {
        return false;
    }
}
```

**Create a Command that can execute only Players**
```java
public class PlayerCommand extends CorePlayerCommand {

    public PlayerCommand() {
        super("test", //The name of the command
                "system.test", //The permission you ned to execute this command
                "tes", "t" //The aliases with you can also call the command
        );
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] strings) {
        return false;
    }
}
```

All commands that are created must also be registered.
You can register the command with `CoreSystem.getInstance().registerCommands(new ConsoleCommand(), new PlayerCommand());`.
This registers the ConsoleCommand and PlayerCommand.
