package dev.gether.getkits;

import dev.gether.getkits.cmd.KitCmd;
import dev.gether.getkits.data.Kit;
import dev.gether.getkits.data.User;
import dev.gether.getkits.file.KitsFile;
import dev.gether.getkits.listener.CloseInvListener;
import dev.gether.getkits.listener.ConnectionListener;
import dev.gether.getkits.listener.InventoryListener;
import dev.gether.getkits.manager.KitManager;
import dev.gether.getkits.manager.UserManager;
import dev.gether.getkits.storage.SQLite;
import dev.gether.getkits.task.AutoSave;
import dev.gether.getkits.utils.ColorFixer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class GetKits extends JavaPlugin {

    private static GetKits instance;

    private UserManager userManager;
    private KitManager kitManager;
    private SQLite sqLite;
    @Override
    public void onEnable() {

        instance = this;
        saveDefaultConfig();

        /*
            DATABASE
         */
        sqLite = new SQLite("getkits", this);
        if(!sqLite.isConnected())
        {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /*
            MANAGER
         */
        kitManager = new KitManager(this);
        userManager = new UserManager(this, kitManager);

        /*
            REGISTER LISTENER
         */
        new InventoryListener(this);
        new ConnectionListener(this);
        new CloseInvListener(this);

        /*
            REGISTER CMD
         */
        new KitCmd(this);


        /*
            IMPLEMENTS ONLINE PLAYERS
         */
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers())
                    sqLite.loadUser(player);
            }
        }.runTaskAsynchronously(this);

        /*
            AUTO SAVE | TASK
         */
        new AutoSave(this).runTaskTimer(this, 20L*300, 20L*300);


    }

    @Override
    public void onDisable() {

        if(sqLite!=null)
        {
            for (Player player : Bukkit.getOnlinePlayers())
                sqLite.updateUser(player);

        }

        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

    }



    public void reloadPlugin()
    {
        reloadConfig();
        KitsFile.loadFile();

        playerLoop:
        for(Player p : Bukkit.getOnlinePlayers())
        {

            for (Kit kit : getKitManager().getKitsData().values())
            {
                if(p.getOpenInventory().getTopInventory().equals(kit.getInventory()))
                {
                    p.closeInventory();
                    continue playerLoop;
                }
            }
            User user = getUserManager().getUserData().get(p.getUniqueId());
            if(user!=null && p.getOpenInventory().getTopInventory().equals(user.getInventory()))
            {
                p.closeInventory();
            }

        }
        kitManager = new KitManager(this);
        userManager.setKitManager(kitManager);

    }
    public static GetKits getInstance() {
        return instance;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public SQLite getSqLite() {
        return sqLite;
    }
}
