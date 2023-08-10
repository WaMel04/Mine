package io.github.wamel04.mine.regener;

import io.github.wamel04.mine.BukkitInitializer;
import io.github.wamel04.mine.util.SimpleItem;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

public class Regener implements Listener {

    protected static BukkitInitializer plugin = BukkitInitializer.getInstance();

    protected Material blockType;
    protected int period;
    protected SimpleItem dropItem;
    protected int minDrop;
    protected int maxDrop;
    protected boolean multiplicable;
    protected Integer exchangeAmount;
    protected SimpleItem exchangeItem;

    protected BukkitTask task;
    protected ArrayList<Location> regenLocations = new ArrayList<>();

    protected Random random = new Random();

    public Regener(Material blockType, int period, SimpleItem dropItem, int minDrop, int maxDrop, boolean multiplicable, @Nullable Integer exchangeAmount, @Nullable SimpleItem exchangeItem) {
        this.blockType = blockType;
        this.period = period;
        this.dropItem = dropItem;
        this.minDrop = minDrop;
        this.maxDrop = maxDrop;
        this.multiplicable = multiplicable;
        this.exchangeAmount = exchangeAmount;
        this.exchangeItem = exchangeItem;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Location loc : (ArrayList<Location>) regenLocations.clone()) {
                    loc.getBlock().setType(blockType);
                    regenLocations.remove(loc);
                }
            }
        }.runTaskTimer(plugin, 0, period);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();

        if (block.getType().equals(blockType) && !player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setDropItems(false);

            int amount = random.nextInt(maxDrop - minDrop + 1) + minDrop;

            if (tool != null && tool.getType().name().contains("PICKAXE") && multiplicable) {
                int level = tool.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

                if (level > 0) {
                    int chanceNumber = random.nextInt(100);
                    int multipleChance = 2/(level + 2);

                    if (chanceNumber > multipleChance) {
                        amount *= random.nextInt(level + 1 - 2) + 2;
                    }
                }
            }
            try {
                ItemStack item = dropItem.getItemStack();
                item.setAmount(amount);
                player.getInventory().addItem(item);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (exchangeAmount != null && exchangeItem != null) {
                int dropItemAmount = 0;

                if (dropItem.getItemStack().getItemMeta() != null && dropItem.getItemStack().getItemMeta().getDisplayName() != null) {
                    for (ItemStack item : player.getInventory().getStorageContents()) {
                        if (item != null && item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null) {
                            if (item.getItemMeta().getDisplayName().equals(dropItem.getItemStack().getItemMeta().getDisplayName())) {
                                dropItemAmount += item.getAmount();
                            }
                        }
                    }
                } else {
                    for (ItemStack item : player.getInventory().getStorageContents()) {
                        if (item != null && item.getType() != null) {
                            if (item.getType().equals(dropItem.getItemStack().getType())) {
                                dropItemAmount += item.getAmount();
                            }
                        }
                    }
                }
                while (dropItemAmount - exchangeAmount >= 0) {
                    ItemStack dpItem = dropItem.getItemStack();
                    dpItem.setAmount(exchangeAmount);

                    ItemStack exItem = exchangeItem.getItemStack();

                    player.getInventory().removeItem(dpItem);
                    player.getInventory().addItem(exItem);

                    dropItemAmount -= exchangeAmount;
                }
            }

            regenLocations.add(event.getBlock().getLocation());
        }
    }
}
