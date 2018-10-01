package com.minecats.cindyk.namehistory;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.*;

/**
 * Created by cindy on 4/6/14.
 */
public class PlayerQueries {

    private NameHistory plugin;

    PlayerQueries(NameHistory plugin)
    {

        this.plugin = plugin;

    }

    public void getNameInfo(String Name, CommandSender pp)
    {
        String database = plugin.getConfig().getString("NameHistory.MySQL.Table_Prefix");
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(
                    "jdbc:MySQL://" +
					plugin.getConfig().getString("NameHistory.MySQL.Server") +
					"/" + plugin.getConfig().getString("NameHistory.MySQL.Database") +
					"?user=" + plugin.getConfig().getString("NameHistory.MySQL.User") +
					"&password=" + plugin.getConfig().getString("NameHistory.MySQL.Password") +
					"&socketTimeout=5000" +
					"&connectTimeout=5000");

            stmt = con.createStatement();
			stmt.setQueryTimeout(5000);

            rs = stmt.executeQuery("SELECT * FROM " + database + " WHERE playername = '" + Name + "' ORDER BY uuid;");

            int i = 1;

            pp.sendMessage( ChatColor.GOLD + "-=-=-=-=-=" + ChatColor.DARK_AQUA + "Name History" + ChatColor.GOLD + "=-=-=-=-=-");

            while (rs.next()) {
                String q = ChatColor.DARK_AQUA + String.valueOf(i) + ". " + ChatColor.GREEN + rs.getString("uuid");
                String name = ChatColor.DARK_AQUA + " -> "+ ChatColor.LIGHT_PURPLE + rs.getString("playername") + ChatColor.DARK_AQUA + " -LastSeen- " + ChatColor.GREEN + rs.getDate("lastseen") + " "+ rs.getTime("lastseen");

                pp.sendMessage( q);
                pp.sendMessage(name);
                i++;
            }
            ;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {


            plugin.close(rs);
            plugin.close(stmt);
            plugin.close(con);
        }

    }

    public void getPlayerInfo( OfflinePlayer pp, CommandSender requester)
    {
        String database = plugin.getConfig().getString("NameHistory.MySQL.Table_Prefix");
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(
                    "jdbc:MySQL://" +
					plugin.getConfig().getString("NameHistory.MySQL.Server") +
					"/" + plugin.getConfig().getString("NameHistory.MySQL.Database") +
					"?user=" + plugin.getConfig().getString("NameHistory.MySQL.User") +
					"&password=" + plugin.getConfig().getString("NameHistory.MySQL.Password") +
					"&socketTimeout=5000" +
					"&connectTimeout=5000");

            stmt = con.createStatement();
			stmt.setQueryTimeout(5000);

            rs = stmt.executeQuery("SELECT * FROM " + database + " WHERE uuid = '" + pp.getUniqueId().toString() + "' ORDER BY lastseen DESC;");

            int i = 1;

            requester.sendMessage( ChatColor.GOLD + "-=-=-=-=-=" + ChatColor.DARK_AQUA + "Player's Name History" + ChatColor.GOLD + "=-=-=-=-=-");
            String q = ChatColor.DARK_AQUA + String.valueOf(i) + ". " + ChatColor.GREEN + pp.getUniqueId().toString();
            requester.sendMessage( q);

            while (rs.next()) {

                String name = ChatColor.DARK_AQUA + " > "+ ChatColor.LIGHT_PURPLE + rs.getString("playername");
                requester.sendMessage(name);
                String dates =   ChatColor.DARK_AQUA + " -->First: " +ChatColor.GREEN +rs.getDate("dateadded") +" "+ rs.getTime("dateadded")  + ChatColor.DARK_AQUA + " *Last: " + ChatColor.GREEN + rs.getDate("lastseen") + " " + rs.getTime("lastseen");
                requester.sendMessage(dates);
                i++;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {


            plugin.close(rs);
            plugin.close(stmt);
            plugin.close(con);
        }


    }

    public void getUUIDInfo( String queryUUID, CommandSender requester)
    {
        String database = plugin.getConfig().getString("NameHistory.MySQL.Table_Prefix");
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(
                    "jdbc:MySQL://" +
					plugin.getConfig().getString("NameHistory.MySQL.Server") +
					"/" + plugin.getConfig().getString("NameHistory.MySQL.Database") +
					"?user=" + plugin.getConfig().getString("NameHistory.MySQL.User") +
					"&password=" + plugin.getConfig().getString("NameHistory.MySQL.Password") +
					"&socketTimeout=5000" +
					"&connectTimeout=5000");

            stmt = con.createStatement();
			stmt.setQueryTimeout(5000);

            rs = stmt.executeQuery("SELECT * FROM " + database + " WHERE uuid = '" + queryUUID + "' ORDER BY lastseen DESC;");

            int i = 1;

            requester.sendMessage( ChatColor.GOLD + "-=-=-=-=-=" + ChatColor.DARK_AQUA + "UUID Name History" + ChatColor.GOLD + "=-=-=-=-=-");
            String q = ChatColor.DARK_AQUA + String.valueOf(i) + ". " + ChatColor.GREEN + queryUUID;
            requester.sendMessage( q);

            while (rs.next()) {

                String name = ChatColor.DARK_AQUA + " > "+ ChatColor.LIGHT_PURPLE + rs.getString("playername");
                requester.sendMessage(name);
                String dates =   ChatColor.DARK_AQUA + " -->First: " +ChatColor.GREEN +rs.getDate("dateadded") +" "+ rs.getTime("dateadded")  + ChatColor.DARK_AQUA + " *Last: " + ChatColor.GREEN + rs.getDate("lastseen") + " " + rs.getTime("lastseen");
                requester.sendMessage(dates);
                i++;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {


            plugin.close(rs);
            plugin.close(stmt);
            plugin.close(con);
        }

    }





}
