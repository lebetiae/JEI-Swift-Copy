package net.vvxzv.jeiswiftcopy.api;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;

public interface ICopyJsonHandler {
    static void handler(MinecraftServer server, Minecraft minecraft, ResourceLocation registryName) {
        var player = minecraft.player;
        var q = server.getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "recipe/" + registryName.getPath() + ".json"));
        if (q.isPresent()) {
            try {
                var reader = new JsonReader(q.get().openAsReader());

                reader.setLenient(true);
                minecraft.keyboardHandler.setClipboard(
                        JsonParser.parseReader(reader)
                                .toString()
                );
                if (player != null) {
                    player.displayClientMessage(Component.translatable("jei_swift_copy.message.recipe.copy.success", registryName.toString()), false);
                }
            } catch (IOException e) {
                if (player != null) {
                    player.displayClientMessage(Component.translatable("jei_swift_copy.message.recipe.copy.failure"), false);
                }
                throw new RuntimeException(e);
            }
        }
    }
}
