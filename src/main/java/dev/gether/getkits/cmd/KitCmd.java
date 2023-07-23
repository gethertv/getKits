package dev.gether.getkits.cmd;

import dev.gether.getkits.GetKits;
import dev.gether.getkits.data.AdminUser;
import dev.gether.getkits.data.Kit;
import dev.gether.getkits.file.KitsFile;
import dev.gether.getkits.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class KitCmd implements CommandExecutor, TabCompleter {
    private final GetKits plugin;
    public KitCmd(GetKits plugin)
    {
        this.plugin = plugin;
        plugin.getCommand("getkit").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        if(args.length==1)
        {
            if(player.hasPermission("getkits.admin"))
            {
                if(args[0].equalsIgnoreCase("reload")) {
                    plugin.reloadPlugin();
                    player.sendMessage(ColorFixer.addColors("&aPomyslnie przeladowano plugin!"));
                    return true;
                }
            }
        }
        if(args.length==2)
        {
            if(player.hasPermission("getkits.admin"))
            {
               if(args[0].equalsIgnoreCase("create"))
               {
                   if(KitsFile.getConfig().isSet("kits."+args[1]))
                   {
                       player.sendMessage(ColorFixer.addColors("&cPodany zestaw juz istnieje!"));
                       return false;
                   }
                   createKit(player, args[1]);
                   return true;
               }
                if(args[0].equalsIgnoreCase("edit"))
                {
                    Kit kit = plugin.getKitManager().getKitsData().get(args[1]);
                    if(kit==null)
                    {
                        player.sendMessage(ColorFixer.addColors("&cPodany zestaw nie istnieje!"));
                        return true;
                    }
                    editKit(player, kit);
                    return true;
                }
                if(args[0].equalsIgnoreCase("delete"))
                {
                    Kit kit = plugin.getKitManager().getKitsData().get(args[1]);
                    if(kit==null)
                    {
                        player.sendMessage(ColorFixer.addColors("&cPodany zestaw nie istnieje!"));
                        return true;
                    }
                    deleteKit(player, kit);
                    return true;
                }
            }
        }
        if(!player.hasPermission("getkits.use"))
        {
            player.sendMessage(ColorFixer.addColors(plugin.getConfig().getString("lang.no-permission")));
            return false;
        }
        plugin.getUserManager().openInv(player);
        return false;
    }

    private void deleteKit(Player player, Kit kit) {
        FileConfiguration config = KitsFile.getConfig();
        config.set("kits."+kit.getKeyName(), null);
        KitsFile.save();
        plugin.reloadPlugin();
        player.sendMessage(ColorFixer.addColors("&aPomyslnie usunieto zestaw &2"+kit.getKeyName()+"&a!"));

    }

    private void editKit(Player player, Kit kit) {
        Inventory inv = Bukkit.createInventory(null, kit.getInventory().getSize(), ColorFixer.addColors("&0Edytowanie - "+kit.getKeyName()));
        kit.getKitItems().forEach(itemDrop -> inv.setItem(itemDrop.getSlot(), itemDrop.getItemStack()));
        plugin.getUserManager().getAdminEditKit().put(player.getUniqueId(), new AdminUser(kit.getKeyName(), inv));
        player.openInventory(inv);
    }

    private void createKit(Player player, String name) {
        FileConfiguration config = KitsFile.getConfig();
        config.set("kits."+name+".name", "&a"+name);
        config.set("kits."+name+".cooldown", 60);
        config.set("kits."+name+".permission", "kit."+name);
        config.set("kits."+name+".inv.title", "&0Zestaw "+name);
        config.set("kits."+name+".inv.size", 27);
        config.set("kits."+name+".inv.backgrounds.1.material", "BLACK_STAINED_GLASS_PANE");
        config.set("kits."+name+".inv.backgrounds.1.displayname", "&7");
        config.set("kits."+name+".inv.backgrounds.1.lore", new ArrayList<>());
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 27; i++) {
            slots.add(i);
        }
        config.set("kits."+name+".inv.backgrounds.1.slots", slots);
        config.set("kits."+name+".item.slot", 10);

        config.set("kits."+name+".item.cooldown.material", "WOODEN_PICKAXE");
        config.set("kits."+name+".item.cooldown.displayname", "&eZestaw "+name);
        config.set("kits."+name+".item.cooldown.lore", Arrays.asList("&7", "&fMusisz odczekac &c{time}", "&7"));

        config.set("kits."+name+".item.available.material", "WOODEN_PICKAXE");
        config.set("kits."+name+".item.available.displayname", "&eZestaw "+name);
        config.set("kits."+name+".item.available.lore", Arrays.asList("&7", "&fKliknij, aby odebrac zestaw", "&7"));

        config.set("kits."+name+".item.no-available.material", "BARRIER");
        config.set("kits."+name+".item.no-available.displayname", "&eZestaw "+name);
        config.set("kits."+name+".item.no-available.lore", Arrays.asList("&7", "&cBrak uprawnien!", "&7"));
        config.set("kits."+name+".items", new HashMap<>());

        KitsFile.save();
        player.sendMessage(ColorFixer.addColors("&aPomyslnie stworzono zestaw &2"+name+"&a!"));
        plugin.reloadPlugin();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!sender.hasPermission("getkits.admin"))
            return null;
        if(args.length==1)
        {
            return Arrays.asList("create", "edit", "reload", "delete");
        }
        if(args.length==2)
        {
            if(args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete"))
                return new ArrayList<>(plugin.getKitManager().getKitsData().keySet());
        }
        return null;
    }
}
