package fr.skitroz.testPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    public static Main INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        EventsTest.onCreateMessageConfig();

        getCommand("scoreboard").setExecutor(new CommandsTest());
        getCommand("permission").setExecutor(new CommandsTest());
        getCommand("display").setExecutor(new CommandsTest());
        getCommand("remove-display").setExecutor(new CommandsTest());
        this.getServer().getPluginManager().registerEvents(new EventsTest(), this);
    }

    @Override
    public void onDisable() {
    }
}
