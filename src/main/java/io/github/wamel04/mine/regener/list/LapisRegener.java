package io.github.wamel04.mine.regener.list;

import io.github.wamel04.mine.regener.Regener;
import io.github.wamel04.mine.util.SimpleItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class LapisRegener extends Regener {

    public LapisRegener(Material blockType, int period, SimpleItem dropItem, int minDrop, int maxDrop, boolean multiplicable, @Nullable Integer exchangeAmount, @Nullable SimpleItem exchangeItem) {
        super(Material.LAPIS_ORE, 20 * 1,
                new SimpleItem(new ItemStack(Material.INK_SACK, 0, (short) 4)),
                4, 9, true, 128,
                new SimpleItem(Material.LAPIS_BLOCK, 1, null, "§9응축된 청금석", null));
    }

}
