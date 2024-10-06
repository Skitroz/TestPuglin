package fr.skitroz.testPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public class EventsTest implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Action action = event.getAction();

        if(action.equals(Action.RIGHT_CLICK_BLOCK) && block.getType().equals(Material.CARVED_PUMPKIN)) {

            Location originBlock = block.getLocation();
            Location blockToVerify = new Location(originBlock.getWorld(), originBlock.getX(), originBlock.getY() - 1, originBlock.getZ());

            if(blockToVerify.getBlock().getType().equals(Material.HONEYCOMB_BLOCK)){
                originBlock.getWorld().strikeLightning(originBlock);
                return;
            }
        }
    }

    public static void onCreateMessageConfig() {
        File file = new File(Main.INSTANCE.getDataFolder(), "message.yml");
        FileConfiguration messageConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(file);

        if(!file.exists()) {
            messageConfig.set("joinMessage", "&2[&a+&2] &a%player%");
            messageConfig.set("quitMessage", "&4[&c-&4] &c%player%");
            try {
                messageConfig.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        File file = new File(Main.INSTANCE.getDataFolder(), "message.yml");
        FileConfiguration messageConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) return;

        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', messageConfig.getString("joinMessage").replace("%player%", event.getPlayer().getName())));//event.setJoinMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "+" + ChatColor.DARK_GREEN + "] " + ChatColor.GREEN + event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

        File file = new File(Main.INSTANCE.getDataFolder(), "message.yml");
        FileConfiguration messageConfig = (FileConfiguration)YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) return;

        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', messageConfig.getString("quitMessage").replace("%player%", event.getPlayer().getName())));
    }
}
