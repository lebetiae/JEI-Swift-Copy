package net.vvxzv.jeiswiftcopy.common;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.vvxzv.jeiswiftcopy.compat.kubejs.ScreenMessage;
import net.vvxzv.jeiswiftcopy.utils.KeyMappingUtil;

public class CopyItemHandler {
    public static ItemStack StackBuffer = ItemStack.EMPTY;
    public static int count = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        if (StackBuffer.isEmpty()) return;
        if (count > 0) {
            count--;
        } else {
            StackBuffer = ItemStack.EMPTY;
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().isEmpty()) return;
        StackBuffer = event.getItemStack();
        count = 3;
    }

    @SubscribeEvent
    public static void onKeyInput(ScreenEvent.KeyPressed.Pre event) {
        ItemStack stack = StackBuffer;
        if (stack.isEmpty()) return;
        if (KeyMappingUtil.KEYMAPPING2.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            String itemID = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            String copyId = I18n.get("jei_swift_copy.item.id.format", itemID);
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            minecraft.keyboardHandler.setClipboard(copyId);
            player.displayClientMessage(Component.translatable("jei_swift_copy.message.item.id.success", itemID), false);
        } else if (KeyMappingUtil.KEYMAPPING3 != null &&KeyMappingUtil. KEYMAPPING3.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            ScreenMessage.showItemInfo(Minecraft.getInstance().player, stack);
        }
    }
}
