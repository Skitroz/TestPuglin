package fr.skitroz.testPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.permissions.PermissionAttachment;

public class CommandsTest implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

            if(sender instanceof Player) {
                Player player = (Player) sender;
                BossBar bossBar;

                bossBar = Bukkit.createBossBar("Test", BarColor.BLUE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);

                if (cmd.getName().equalsIgnoreCase("scoreboard")) {
                    bossBar.addPlayer(player);
                    player.sendMessage("Voici le scoreboard");
                    return true;
                } else if (cmd.getName().equalsIgnoreCase("permission")) {

                    if (!player.hasPermission("testplugin.scoreboard")) {
                        PermissionAttachment attachment = player.addAttachment(Main.INSTANCE);
                        attachment.setPermission("testplugin.scoreboard", true);
                        player.sendMessage("Permission ajoutée");
                    } else {
                        PermissionAttachment attachment = player.addAttachment(Main.INSTANCE);
                        attachment.setPermission("testplugin.scoreboard", false);
                        player.sendMessage("Permission enlevé");
                    }
                    return true;
                } else if (cmd.getName().equalsIgnoreCase("display") && args.length > 0) {
                    Location location = player.getLocation();
                    World world = player.getWorld();
                    TextDisplay textDisplay = world.spawn(location, TextDisplay.class);

                    String[] colors = args[2].split(",");

                    ColorDisplay colorEnumeration = ColorDisplay.getColorDisplay(args[2].toUpperCase());

                    int RGB_1;
                    int RGB_2;
                    int RGB_3;

                    if (!colorEnumeration.equals(ColorDisplay.NULL)) {
                        RGB_1 = colorEnumeration.getRGB_1();
                        RGB_2 = colorEnumeration.getRGB_2();
                        RGB_3 = colorEnumeration.getRGB_3();
                    } else {
                        RGB_1 = Integer.valueOf(colors[0]);
                        RGB_2 = Integer.valueOf(colors[1]);
                        RGB_3 = Integer.valueOf(colors[2]);
                    }

                    textDisplay.setRotation(50, 50);
                    
                    textDisplay.setText(args[0]);
                    textDisplay.setBillboard(Display.Billboard.valueOf(args[1].toUpperCase()));
                    textDisplay.setBackgroundColor(Color.fromRGB(RGB_1, RGB_2, RGB_3));

                    player.sendMessage("Le texte : " + textDisplay.getText() + " a été correctement affiché à la position : " + "X: " + (int)location.getX() + "Y: " + (int)location.getY() + "Z: " + (int)location.getZ());
                    return true;
                } else if (cmd.getName().equalsIgnoreCase("remove-display")) {
                    Location location = player.getLocation();
                    World world = player.getWorld();
                    TextDisplay textDisplay = world.spawn(location, TextDisplay.class);
                    textDisplay.remove();
                    player.sendMessage("Le texte : "+ textDisplay.getText() + " a été correctement supprimé");
                    return true;
                }



            }

                return false;
        }



        private enum ColorDisplay {

            NULL("NULL", 0,0,0),
            RED("RED", 255, 0, 0),
            BLUE("BLUE", 0, 0, 255);

            private String name;
            private int RGB_1;
            private int RGB_2;
            private int RGB_3;

            private ColorDisplay(String name, int RGB_1, int RGB_2, int RGB_3) {
                this.name = name;
                this.RGB_1 = RGB_1;
                this.RGB_2 = RGB_2;
                this.RGB_3 = RGB_3;
            }

            public String getName() {
                return name;
            }

            public int getRGB_1() {
                return RGB_1;
            }

            public int getRGB_2() {
                return RGB_2;
            }

            public int getRGB_3() {
                return RGB_3;
            }

            public static ColorDisplay getColorDisplay(String nameTo) {
                for (ColorDisplay colorDisplay : ColorDisplay.values()) {
                    if (colorDisplay.name.equals(nameTo)) return colorDisplay;
                }
                return ColorDisplay.NULL;
                 }

        }


}