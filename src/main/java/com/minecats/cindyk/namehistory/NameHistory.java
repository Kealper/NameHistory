package com.minecats.cindyk.namehistory;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.logging.Level;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * Created by cindy on 4/2/14.
 *
 * UUID Support now:  UtilUUID.b lets you add them back.
 * public static UUID getUUID(String uuid) for Bungee.
 *
 */
public class NameHistory extends JavaPlugin implements CommandExecutor {

    private MiniConnectionPoolManager pool;
    PlayerListener pl;
    PlayerQueries plQueries;

    @Override
    public void onEnable() {
        super.onEnable();

        loadConfig();

        //Initialize Classes
        pl = new PlayerListener(this);
        plQueries = new PlayerQueries(this);

        //Register Command
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(pl, this);

        getCommand("history").setExecutor(this);

        //Setup SQL
        try{
            connect();
        }catch(ClassNotFoundException cex)
        {
            this.getLogger().log(Level.SEVERE," Error:" + cex.getMessage());
        }
        catch(SQLException ex)
        {
            this.getLogger().log(Level.SEVERE," Error:" + ex.getMessage());
        }

        if(getConfig().getBoolean("NameHistory.MySQL.Enabled")==false)
        {
            getLogger().info("Disabling plugin, you have NOT setup SQL yet in the Config. Its required.");
            this.getPluginLoader().disablePlugin(this);
        }
        else
        {
            getLogger().info("=^..^=  Setting Up SQL");
            setUpSQL();
        }



    }

    @Override
    public void onDisable() {


        super.onDisable();

        getLogger().info("Closing SQL");
        //Close SQL
        close();

        getLogger().info("Disabled!");
    }



    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
    {
        if (command.getName().equalsIgnoreCase("history"))
            try {
                   handleCommands(sender,args);

            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return true;
    }
    private boolean handleCommands(CommandSender sender, String[] args) throws ClassNotFoundException
    {


        if (!sender.hasPermission("namehistory.command"))
        {
			sender.sendMessage("[NameHistory] You do not have permission to run this command.");
            return true;
        }
        else
        {
            //Select playername, dateadded, lastseen from namehistory where uuid = player.uuid.tostring();
            if(args.length ==2 )
            {

                switch(args[0])
                {
                    case "name":
                        plQueries.getNameInfo(args[1], sender);

                    break;

                    case "player":

                        if( getServer().getOfflinePlayer(args[1]) != null)
                            plQueries.getPlayerInfo(getServer().getOfflinePlayer(args[1]), sender);
                        else
                            sender.sendMessage("Player " + args[1] + " has to be online for this query.");
                    break;
                    case "uuid":

                        plQueries.getUUIDInfo(args[1], sender);
                    break;

                }
            }
            else
            {
                sender.sendMessage("/history [name/player/uuid] [playername/uuid] ");
                sender.sendMessage(" for player, you might need to use the last name they joined the server with. ");
            }

        }

        return true;
    }

    private void setUpSQL()
    {

            this.getLogger().info("Connecting to SQL database!");
            {
                Connection connection = null;
                Statement st = null;
                int rs = 0;
                try
                {
                    connection = pool.getValidConnection();
                    /*connection =
							DriverManager.getConnection("jdbc:MySQL://" + plugin.getConfig().getString("VoteSQL.MySQL.Server") + "/" + plugin.getConfig().getString("VoteSQL.MySQL.Database"), plugin.getConfig().getString("VoteSQL.MySQL.User"), plugin.getConfig().getString("VoteSQL.MySQL.Password"));*/					st = connection.createStatement();
                    st.setQueryTimeout(5000);
					rs = st.executeUpdate("CREATE TABLE IF NOT EXISTS "
                            + this.getConfig().getString("NameHistory.MySQL.Table_Prefix")
                            + "( uuid VARCHAR(36) NOT NULL, playername VARCHAR(32), dateadded DATETIME, lastseen DATETIME, PRIMARY KEY (uuid,playername))");
                    this.getLogger().info("SQL database connected!");
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    this.getLogger().log(Level.SEVERE," Error:" + rs);
                }
                catch(MiniConnectionPoolManager.TimeoutException te)
                {
                    this.getLogger().log(Level.SEVERE,"Timeout Exception Error:" + te.getMessage());
                }
            }
            return;
        }

    public synchronized void connect() throws ClassNotFoundException, SQLException {

            Class.forName("com.mysql.jdbc.Driver");
             getLogger().info("MySQL driver loaded");
            MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
            dataSource.setDatabaseName(this.getConfig().getString("NameHistory.MySQL.Database"));
            dataSource.setServerName(this.getConfig().getString("NameHistory.MySQL.Server"));
            dataSource.setPort(3306);
            dataSource.setUser(this.getConfig().getString("NameHistory.MySQL.User"));
            dataSource.setPassword(this.getConfig().getString("NameHistory.MySQL.Password"));
			dataSource.setConnectTimeout(5000);
			dataSource.setSocketTimeout(5000);
            pool = new MiniConnectionPoolManager(dataSource, 1, 5);
             getLogger().info("Connection pool ready");

    }



    private void loadConfig() {

        String path2 = "NameHistory.MySQL.Enabled";
        String path3 = "NameHistory.MySQL.Server";
        String path4 = "NameHistory.MySQL.Database";
        String path5 = "NameHistory.MySQL.User";
        String path6 = "NameHistory.MySQL.Password";
        String path7 = "NameHistory.MySQL.Table_Prefix";

        getConfig().addDefault(path2, false);
        getConfig().addDefault(path3, "Server Address eg.Localhost");
        getConfig().addDefault(path4, "Place Database name here");
        getConfig().addDefault(path5, "Place User of MySQL Database here");
        getConfig().addDefault(path6, "Place User password here");
        getConfig().addDefault(path7, "NameHistory");

        getConfig().options().copyDefaults(true);
        saveConfig();
    }


    public void addData(String name, String strUUID ) throws ClassNotFoundException
    {
        PreparedStatement pst = null;
        Connection con = null;
        Statement uuidstmt = null;
        ResultSet rs = null;
       // int num = 1;

        if(strUUID == null)
        {
            getLogger().info("Player UUID is MISSING!! " + name);
            return;
        }


        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            getLogger().info("MySQL driver loaded");
            MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
            dataSource.setDatabaseName(getConfig().getString("NameHistory.MySQL.Database"));
            dataSource.setServerName(getConfig().getString("NameHistory.MySQL.Server"));
            dataSource.setPort(3306);
            dataSource.setUser(getConfig().getString("NameHistory.MySQL.User"));
            dataSource.setPassword(getConfig().getString("NameHistory.MySQL.Password"));
			dataSource.setConnectTimeout(5000);
			dataSource.setSocketTimeout(5000);

            pool = new MiniConnectionPoolManager(dataSource, 10, 5);

            con = pool.getValidConnection();
			/*con = DriverManager.getConnection(
					"jdbc:MySQL://"
							+ plugin.getConfig().getString("VoteSQL.MySQL.Server")
							+ "/"
							+ plugin.getConfig().getString("VoteSQL.MySQL.Database"),
							plugin.getConfig().getString("VoteSQL.MySQL.User"),
							plugin.getConfig().getString("VoteSQL.MySQL.Password"));*/

            String database = getConfig().getString("NameHistory.MySQL.Table_Prefix");

            getLogger().info("Looking for UUID :  " + strUUID );
            uuidstmt = con.createStatement();
			uuidstmt.setQueryTimeout(5000);
            if(uuidstmt.execute("SELECT * FROM " + database + " WHERE uuid = '" +strUUID+"'"))
            {
                rs = uuidstmt.getResultSet();
                boolean bFound = false;

                java.util.Date today = new java.util.Date();
                java.sql.Timestamp timestamp = new java.sql.Timestamp(today.getTime());


                while (rs.next())
               // if (!rs.next())
                {


                        if(rs.getString("playername").compareToIgnoreCase(name)==0)
                        {
                            bFound = true;
                            //HAS A NEW NAME!
                            //TELL PLAYER THEY ARE SCREWED!
                            getLogger().info("Player: " + name + " has been here before.");

                            //Update LastSeen!


                            //INSERT INTO DATABASE.
                            pst = con.prepareStatement("Update " + database+ " Set lastseen = ? where (uuid = ? and playername = ? ) ");

                            pst.setTimestamp(1, timestamp);
                            pst.setString(2, strUUID);
                            pst.setString(3, name);

                            pst.executeUpdate();
                            getLogger().info("Updated Player");


                        }

                }

                if(!bFound)
                {

                    //INSERT INTO DATABASE.
                    pst = con.prepareStatement("INSERT INTO " + database
                            + "(uuid,playername,dateadded,lastseen) VALUES(?, ? , ? , ?)");


                    pst.setString(1, strUUID);
                    pst.setString(2, name);
                    pst.setTimestamp(3, timestamp);
                    pst.setTimestamp(4, timestamp);

					pst.setQueryTimeout(5000);
                    pst.executeUpdate();
                    getLogger().info("Inserted Player");
                    //System.out.print("inserted");
                }
            }


        }
        catch (SQLException ex)
        {
            System.out.print(ex);
        } finally {
            close(rs);
            close(uuidstmt);
            close(con);
        }
    }
    public synchronized void close() {
        try {
            pool.dispose();
        } catch (SQLException ex) {
            getLogger().info(ex.getMessage());
        }
    }

    public void reload() {
    }


    public  void close(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                getLogger().info(ex.getMessage());
            }
        }
    }

    public  void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                getLogger().info(ex.getMessage());
            }
        }
    }

    public  void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                getLogger().info(ex.getMessage());
            }
        }
    }



}


