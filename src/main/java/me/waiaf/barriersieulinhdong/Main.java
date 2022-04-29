package me.waiaf.barriersieulinhdong;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Main extends JavaPlugin implements Listener {

    HashMap<Player, Block> playerLastBlock = new HashMap<>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event){

        Player player = event.getPlayer();
        if (player.isFlying()) return;
        Block block = player.getLocation().getBlock();
        Location playerLocation = player.getLocation();
        if (block.getType().equals(Material.WATER)) return;

        playerLastBlock.put(player, block);

        if (player.isSneaking()){

            for (Player otherPlayers : Bukkit.getOnlinePlayers()) {
                if (otherPlayers.getName().equals(player.getName())) return;
                if (otherPlayers.getLocation().getY() - 1 == player.getLocation().getY() && otherPlayers.getNearbyEntities(0.2,1.25,0.2).contains(player)) return;

                Bukkit.getScheduler().runTask(this, () -> {
                    if (playerLastBlock.containsKey(player) && playerLastBlock.get(player) != null){
                        otherPlayers.sendBlockChange(player.getLocation(), playerLastBlock.get(player).getType().createBlockData());
                    }
                    otherPlayers.sendBlockChange(player.getLocation(), Material.BARRIER.createBlockData());
                });


            }

        } else {
            if (playerLastBlock.containsKey(player) && playerLastBlock.get(player) != null){
                for (Player otherPlayers : Bukkit.getOnlinePlayers()){
                    if (playerLastBlock.get(player) == null || otherPlayers.getName().equals(player.getName()))
                        continue;
                    Bukkit.getScheduler().runTask(this, () -> {
                        otherPlayers.sendBlockChange(playerLastBlock.get(player).getLocation(), block.getType().createBlockData());
                        otherPlayers.sendBlockChange(player.getLocation(), playerLastBlock.get(player).getType().createBlockData());
                    });
                }
            } else {
                for (Player otherPlayers : Bukkit.getOnlinePlayers()) {
                    if (otherPlayers.getName().equals(player.getName())) continue;
                    Bukkit.getScheduler().runTask(this, () -> otherPlayers.sendBlockChange(player.getLocation(), block.getType().createBlockData()));
                }
            }
        }
    }

}
