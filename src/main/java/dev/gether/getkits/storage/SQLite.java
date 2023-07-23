package dev.gether.getkits.storage;

import dev.gether.getkits.GetKits;
import dev.gether.getkits.data.User;
import dev.gether.getkits.manager.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class SQLite {

    private Connection connection;
    private String database;
    private String table = "get_kits";

    private final GetKits plugin;
    private String createTableSQL = "CREATE TABLE IF NOT EXISTS "+table+" (" +
            "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "    uuid VARCHAR(100) NOT NULL," +
            "    username VARCHAR(100) NOT NULL," +
            "    kit VARCHAR(100) NOT NULL," +
            "    last_usage VARCHAR(20)," +
            "    UNIQUE (uuid, kit) ON CONFLICT REPLACE" +
            ");";

    public SQLite(String database, GetKits plugin){
        this.database = database;
        this.plugin = plugin;
        openConnection();
        createTable(createTableSQL);
    }

    public void update(String paramString) {
        try {
            Connection connection = getConnection();
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                statement.executeUpdate(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong update : '" + paramString + "'!");
        }
    }

    public void openConnection() {
        File dataFolder = new File(plugin.getDataFolder(), database+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+database+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                this.connection = connection;
            }
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
    }

    public void createTable(String sqlCreate) {

        update(sqlCreate);
    }

    public Connection getConnection() {
        return connection;
    }

    public void loadUser(Player player) {
        if(!playerExists(player.getUniqueId()))
        {
            createUser(player);
            plugin.getUserManager().getUserData().put(player.getUniqueId(), new User());
            return;
        }

        String str = "SELECT * FROM "+table+" WHERE uuid = '" + player.getUniqueId() + "'";
        try {
            ResultSet resultSet = getResult(str);
            HashMap<String, Long> kitsCooldown = new HashMap<>();
            while (resultSet.next()) {
                String kit = resultSet.getString("kit");
                String lastUsage = resultSet.getString("last_usage");
                kitsCooldown.put(kit, (lastUsage!=null) ? Long.parseLong(lastUsage) : 0L);
            }

            plugin.getUserManager().getUserData().put(
                    player.getUniqueId(), new User(kitsCooldown));

        } catch (SQLException | NullPointerException sQLException) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    System.out.println(sQLException.getMessage());
                    player.kickPlayer("Bląd! Zgłoś sie na discord!");
                }
            }.runTask(plugin);
        }
    }

    public void updateUser(Player player) {
        User user = plugin.getUserManager().getUserData().get(player.getUniqueId());
        if(user==null)
            return;

        try {
            Connection connection = getConnection();
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                user.getCooldown().forEach((name, cooldown) -> {
                    String update = "INSERT INTO "+table+" (uuid, username, kit, last_usage) VALUES ('"+player.getUniqueId()+"', '"+player.getName()+"', '"+name+"', '"+cooldown+"')";
                    try {
                        statement.addBatch(update);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                statement.executeBatch();
                statement.close();
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong update : '" + sQLException.getMessage() + "'!");
        }

    }

    public void createUser(Player player)
    {
        update("INSERT INTO "+table+" (uuid, username) VALUES ('"+player.getUniqueId()+"', '"+player.getName()+"')");
    }


    public boolean playerExists(UUID uuid) {
        return (getPlayerID(uuid) != 0);
    }

    private int getPlayerID(UUID uuid) {
        return getInt("id", "SELECT id FROM "+table+" WHERE uuid='" + uuid.toString() + "'");
    }
    private int getInt(String paramString1, String paramString2) {
        try {
            ResultSet resultSet = getResult(paramString2);
            if (resultSet.next()) {
                int i = resultSet.getInt(paramString1);
                resultSet.close();
                return i;
            }
        } catch (SQLException sQLException) {
            return 0;
        }
        return 0;
    }

    public ResultSet getResult(String paramString) {
        ResultSet resultSet = null;
        Connection connection = getConnection();
        try {
            if (connection != null) {
                Statement statement = getConnection().createStatement();
                resultSet = statement.executeQuery(paramString);
            }
        } catch (SQLException sQLException) {
            System.out.println("[mysql] wrong when want get result: '" + paramString + "'!");
        }
        return resultSet;
    }
    public boolean isConnected() {
        return (getConnection() != null);
    }


}