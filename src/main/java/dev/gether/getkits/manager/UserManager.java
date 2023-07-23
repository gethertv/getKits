package dev.gether.getkits.manager;

import dev.gether.getkits.GetKits;
import dev.gether.getkits.data.AdminUser;
import dev.gether.getkits.data.Kit;
import dev.gether.getkits.data.User;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {

    private final GetKits plugin;
    private final KitManager kitManager;
    private HashMap<UUID, User> userData = new HashMap<>();
    private HashMap<UUID, AdminUser> adminEditKit = new HashMap<>();

    public UserManager(GetKits plugin, KitManager kitManager)
    {
        this.plugin = plugin;
        this.kitManager = kitManager;

    }

    public void openInv(Player player)
    {
        User user = userData.get(player.getUniqueId());
        if(user==null)
        {
            user = new User();
            userData.put(player.getUniqueId(),user);
        }
        user.openInv(player, kitManager);
    }

    public HashMap<UUID, AdminUser> getAdminEditKit() {
        return adminEditKit;
    }

    public HashMap<UUID, User> getUserData() {
        return userData;
    }
}
