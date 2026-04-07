package net.vvxzv.jeiswiftcopy.client;

import net.minecraft.client.KeyMapping;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.vvxzv.jeiswiftcopy.utils.KeyMappingUtil;

public class ClientHandler {
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyMappingUtil.KEYMAPPING = new KeyMapping("jei_swift_copy.recipe.json", -1, "jei.key.category.dev.tools"));
        event.register(KeyMappingUtil.KEYMAPPING2 = new KeyMapping("jei_swift_copy.item.id", -1, "jei.key.category.dev.tools"));
        if (ModList.get().isLoaded("kubejs")) {
            event.register(KeyMappingUtil.KEYMAPPING3 = new KeyMapping("jei_swift_copy.item.info", -1, "jei.key.category.dev.tools"));
        }

    }
}
