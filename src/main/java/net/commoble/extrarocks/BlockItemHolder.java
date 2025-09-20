package net.commoble.extrarocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public record BlockItemHolder(DeferredHolder<Block, ? extends Block> block, DeferredHolder<Item, ? extends BlockItem> item)
{

}
