package net.vvxzv.jeiswiftcopy.compat.kubejs;

import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.kubejs.ingredient.NamespaceIngredient;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class ScreenMessage {
    public static void showItemInfo(Player player, ItemStack stack) {
        player.sendSystemMessage(Component.literal("Item in screen:"));
        var holder = stack.getItemHolder();

        var itemRegistry = BuiltInRegistries.ITEM;
        var blockRegistry = BuiltInRegistries.BLOCK;
        var fluidRegistry = BuiltInRegistries.FLUID;
        var tabRegistry = BuiltInRegistries.CREATIVE_MODE_TAB;

        // item info
        // id
        player.sendSystemMessage(copy(((ItemStackKJS)(Object)stack).kjs$toItemString0(player.level().registryAccess().createSerializationContext(NbtOps.INSTANCE)), ChatFormatting.GREEN, "Item ID"));
        // item tags
        var itemTags = holder.tags().toList();
        for (var tag : itemTags) {
            var id = "'#%s'".formatted(tag.location());
            var size = itemRegistry.getTag(tag).map(HolderSet::size).orElse(0);
            player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Item Tag [" + size + " items]"));
        }
        // mod
        player.sendSystemMessage(copy("'@" + ((ItemStackKJS)(Object)stack).kjs$getMod() + "'", ChatFormatting.AQUA, "Mod [" + ((IngredientKJS)(Object)(new NamespaceIngredient(((ItemStackKJS)(Object)stack).kjs$getMod()).toVanilla())).kjs$getStacks().size() + " items]"));

        // creative tab
        for (var tab : tabRegistry) {
            if (tab.contains(stack)) {
                var id = tabRegistry.getKey(tab);
                var count = tab.getDisplayItems().size();
                var searchCount = tab.getSearchTabDisplayItems().size();

                player.sendSystemMessage(copy("'%" + id + "'", ChatFormatting.LIGHT_PURPLE, tab.getDisplayName().copy().append(" [%d/%d items in tab / search tab]".formatted(count, searchCount))));
            }
        }

        // block info
        if (stack.getItem() instanceof BlockItem blockItem) {
            player.sendSystemMessage(Component.literal("Block in screen:"));
            var block = blockItem.getBlock();
            var blockHolder = block.builtInRegistryHolder();
            // id

            player.sendSystemMessage(copy("'" + ((BlockKJS) block).kjs$getId() + "'", ChatFormatting.GREEN, "Block ID"));
            // block tags
            var blockTags = blockHolder.tags().toList();
            for (var tag : blockTags) {
                var id = "'#%s'".formatted(tag.location());
                var size = blockRegistry.getTag(tag).map(HolderSet::size).orElse(0);
                player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Block Tag [" + size + " items]"));
            }
        }

        // fluid info
        var containedFluid = FluidUtil.getFluidContained(stack);
        if (containedFluid.isPresent()) {
            player.sendSystemMessage(Component.literal("Fluid in screen:"));
            var fluid = containedFluid.orElseThrow();
            var fluidHolder = fluid.getFluid().builtInRegistryHolder();
            // id
            player.sendSystemMessage(copy(fluidHolder.key().location().toString(), ChatFormatting.GREEN, "Fluid ID"));
            // fluid tags
            var fluidTags = fluidHolder.tags().toList();
            for (var tag : fluidTags) {
                var id = "'#%s'".formatted(tag.location());
                var size = fluidRegistry.getTag(tag).map(HolderSet::size).orElse(0);
                player.sendSystemMessage(copy(id, ChatFormatting.YELLOW, "Fluid Tag [" + size + " items]"));
            }
        }

    }


    private static Component copy(String s, ChatFormatting col, String info) {
        return copy(Component.literal(s).withStyle(col), info);
    }

    private static Component copy(String s, ChatFormatting col, Component info) {
        return copy(Component.literal(s).withStyle(col), info);
    }

    private static Component copy(Component c, String info) {
        return Component.literal("- ")
                .withStyle(ChatFormatting.GRAY)
                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, c.getString())))
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(info + " (Click to copy)"))))
                .append(c);
    }

    private static Component copy(Component c, Component info) {
        return Component.literal("- ").withStyle(ChatFormatting.GRAY).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, c.getString()))).withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, info.copy().append(" (Click to copy)")))).append(c);
    }
}
