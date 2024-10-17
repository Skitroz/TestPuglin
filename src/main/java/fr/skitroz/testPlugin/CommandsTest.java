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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandsTest implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player) {
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
                    player.sendMessage("Permission enlevée");
                }
                return true;
            } else if (cmd.getName().equalsIgnoreCase("display")) {
                Location location = player.getLocation();
                World world = player.getWorld();

                // Vérification du nombre minimum d'arguments requis
                if (args.length < 3) {
                    player.sendMessage("Usage incorrect de la commande display. Format attendu : /display <texte> <billboard> <couleur R,G,B> [x] [y] [z]");
                    return true;
                }

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
                    if (colors.length != 3) {
                        player.sendMessage("La couleur doit être définie sous forme de 'R,G,B'");
                        return true;
                    }
                    try {
                        RGB_1 = Integer.parseInt(colors[0]);
                        RGB_2 = Integer.parseInt(colors[1]);
                        RGB_3 = Integer.parseInt(colors[2]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("Les valeurs RGB doivent être des entiers.");
                        return true;
                    }
                }

                if (args.length == 6) {
                    try {
                        double x = Double.parseDouble(args[3]);
                        double y = Double.parseDouble(args[4]);
                        double z = Double.parseDouble(args[5]);
                        location = new Location(world, x, y, z);
                        player.sendMessage("Coordonnées valides");
                    } catch (NumberFormatException e) {
                        player.sendMessage("Les coordonnées fournies ne sont pas valides");
                        return true;
                    }
                } else if (args.length == 4 || args.length == 5) {
                    player.sendMessage("Vous devez spécifier les trois coordonnées X, Y et Z.");
                    return true;
                }

                TextDisplay textDisplay = world.spawn(location, TextDisplay.class);
                textDisplay.setText(args[0]);
                try {
                    textDisplay.setBillboard(Display.Billboard.valueOf(args[1].toUpperCase()));
                } catch (IllegalArgumentException e) {
                    player.sendMessage("Type de billboard invalide");
                    return true;
                }
                textDisplay.setBackgroundColor(Color.fromRGB(RGB_1, RGB_2, RGB_3));

                player.sendMessage("Le texte : " + textDisplay.getText() + " a été correctement affiché à la position : " +
                        "X: " + (int) location.getX() + " Y: " + (int) location.getY() + " Z: " + (int) location.getZ());
                return true;
            } else if (cmd.getName().equalsIgnoreCase("remove-display")) {
                Location eyeLocation = player.getEyeLocation();
                Vector direction = eyeLocation.getDirection().normalize();
                World world = player.getWorld();
                double maxDistance = 50.0; // Distance maximale pour détecter les TextDisplays
                double radius = 0.5; // Rayon autour de chaque point de la trajectoire pour vérifier la présence d'un TextDisplay

                boolean found = false;

                // Parcours de la trajectoire dans la direction du regard du joueur
                for (double i = 0; i < maxDistance; i += 0.5) {
                    Location checkLocation = eyeLocation.clone().add(direction.clone().multiply(i));

                    // Parcourt tous les TextDisplays dans le monde
                    for (TextDisplay textDisplay : world.getEntitiesByClass(TextDisplay.class)) {
                        // Vérifie si le TextDisplay est dans le rayon autour de la position actuelle
                        if (textDisplay.getLocation().distance(checkLocation) <= radius) {
                            textDisplay.remove();
                            player.sendMessage("Le texte affiché que vous regardiez a été supprimé.");
                            found = true;
                            break; // Quitte la boucle une fois qu'un TextDisplay est trouvé et supprimé
                        }
                    }

                    if (found) {
                        break; // Quitte la boucle principale si un TextDisplay a été supprimé
                    }
                }

                if (!found) {
                    player.sendMessage("Aucun texte affiché n'a été trouvé dans votre champ de vision.");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("display")) {
            if (args.length == 1) {
                suggestions.add("<texte>");
            } else if (args.length == 2) {
                suggestions.addAll(Arrays.asList("center", "horizontal", "vertical", "fixed"));
            } else if (args.length == 3) {
                suggestions.addAll(Arrays.asList("red", "green", "blue", "yellow", "orange", "purple", "pink", "white", "black", "gray", "brown", "cyan", "magenta", "lime", "maroon", "navy", "olive", "teal", "silver", "sky", "tan", "aqua", "fuchsia", "limegreen", "lightblue", "lightcoral", "lightcyan", "lightgoldenrodyellow", "lightgray", "lightgreen", "lightpink", "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightsteelblue", "lightyellow", "darkred", "darkblue", "darkgreen", "darkyellow", "darkorange", "darkpurple", "darkpink", "darkwhite", "darkblack", "darkgray", "darkbrown", "darkcyan", "darkmagenta", "darklime", "darkmaroon", "darknavy", "darkolive", "darkteal", "darksilver", "darksky", "darktan", "darkaqua", "darkfuchsia"));
            } else if (args.length == 4) {
                suggestions.add("<x>");
            } else if (args.length == 5) {
                suggestions.add("<y>");
            } else if (args.length == 6) {
                suggestions.add("<z>");
            }
        }

        return suggestions;
    }

    private enum ColorDisplay {
        NULL("NULL", 0, 0, 0),
        RED("RED", 255, 0, 0),
        BLUE("BLUE", 0, 0, 255),
        GREEN("GREEN", 0, 255, 0),
        YELLOW("YELLOW", 255, 255, 0),
        ORANGE("ORANGE", 255, 165, 0),
        PURPLE("PURPLE", 128, 0, 128),
        PINK("PINK", 255, 192, 203),
        WHITE("WHITE", 255, 255, 255),
        BLACK("BLACK", 0, 0, 0),
        GRAY("GRAY", 128, 128, 128),
        BROWN("BROWN", 165, 42, 42),
        CYAN("CYAN", 0, 255, 255),
        MAGENTA("MAGENTA", 255, 0, 255),
        LIME("LIME", 0, 255, 0),
        MAROON("MAROON", 128, 0, 0),
        NAVY("NAVY", 0, 0, 128),
        OLIVE("OLIVE", 128, 128, 0),
        TEAL("TEAL", 0, 128, 128),
        SILVER("SILVER", 192, 192, 192),
        SKY("SKY", 135, 206, 235),
        TAN("TAN", 210, 180, 140),
        AQUA("AQUA", 0, 255, 255),
        FUCHSIA("FUCHSIA", 255, 0, 255),
        LIMEGREEN("LIMEGREEN", 50, 205, 50),
        LIGHTBLUE("LIGHTBLUE", 173, 216, 230),
        LIGHTCORAL("LIGHTCORAL", 240, 128, 128),
        LIGHTCYAN("LIGHTCYAN", 224, 255, 255),
        LIGHTGOLDENRODYELLOW("LIGHTGOLDENRODYELLOW", 250, 250, 210),
        LIGHTGRAY("LIGHTGRAY", 211, 211, 211),
        LIGHTGREEN("LIGHTGREEN", 144, 238, 144),
        LIGHTPINK("LIGHTPINK", 255, 182, 193),
        LIGHTSALMON("LIGHTSALMON", 255, 160, 122),
        LIGHTSEAGREEN("LIGHTSEAGREEN", 32, 178, 170),
        LIGHTSKYBLUE("LIGHTSKYBLUE", 135, 206, 250),
        LIGHTSLATEGRAY("LIGHTSLATEGRAY", 119, 136, 153),
        LIGHTSTEELBLUE("LIGHTSTEELBLUE", 176, 196, 222),
        LIGHTYELLOW("LIGHTYELLOW", 255, 255, 224),
        DARKRED("DARKRED", 139, 0, 0),
        DARKBLUE("DARKBLUE", 0, 0, 139),
        DARKGREEN("DARKGREEN", 0, 100, 0),
        DARKYELLOW("DARKYELLOW", 139, 139, 0),
        DARKORANGE("DARKORANGE", 255, 140, 0),
        DARKPURPLE("DARKPURPLE", 128, 0, 128),
        DARKPINK("DARKPINK", 255, 20, 147),
        DARKWHITE("DARKWHITE", 255, 255, 255),
        DARKBLACK("DARKBLACK", 0, 0, 0),
        DARKGRAY("DARKGRAY", 169, 169, 169),
        DARKBROWN("DARKBROWN", 139, 69, 19),
        DARKCYAN("DARKCYAN", 0, 139, 139),
        DARKMAGENTA("DARKMAGENTA", 139, 0, 139),
        DARKLIME("DARKLIME", 50, 205, 50),
        DARKMAROON("DARKMAROON", 128, 0, 0),
        DARKNAVY("DARKNAVY", 0, 0, 128),
        DARKOLIVE("DARKOLIVE", 85, 107, 47),
        DARKTEAL("DARKTEAL", 0, 128, 128),
        DARKSILVER("DARKSILVER", 192, 192, 192),
        DARKSKY("DARKSKY", 135, 206, 235),
        DARKTAN("DARKTAN", 210, 180, 140),
        DARKAQUA("DARKAQUA", 0, 139, 139),
        DARKFUCHSIA("DARKFUCHSIA", 139, 0, 139);

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
                if (colorDisplay.name.equalsIgnoreCase(nameTo)) return colorDisplay;
            }
            return ColorDisplay.NULL;
        }
    }
}
