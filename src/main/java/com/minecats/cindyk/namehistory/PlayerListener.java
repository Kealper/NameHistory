package com.minecats.cindyk.namehistory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by cindy on 4/2/14.
 */
public class PlayerListener implements Listener {

    NameHistory plugin;

    PlayerListener(NameHistory plugin)
    {
        this.plugin = plugin;

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerJoin(AsyncPlayerPreLoginEvent event)
    {

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {

        //sync event for 10 seconds from now...


        plugin.getLogger().info("Player Login: " + event.getPlayer().getName() + " uuid: " + event.getPlayer().getUniqueId().toString());
        try
        {
             plugin.addData(event.getPlayer().getName(),event.getPlayer().getUniqueId().toString());
        }
        catch(ClassNotFoundException ex)
        {
            plugin.getLogger().info(ex.getMessage());
        }



    }


}
