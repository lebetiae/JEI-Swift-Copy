package net.vvxzv.jeiswiftcopy.api;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ICopyJsonHandler {
    Logger LOGGER = LoggerFactory.getLogger(ICopyJsonHandler.class);

    static void handler(MinecraftServer server, Minecraft minecraft, ResourceLocation registryName) {
        var player = minecraft.player;
        var resource = server.getResourceManager().getResource(
                ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "recipe/" + registryName.getPath() + ".json"));
        if (resource.isEmpty()) {
            if (player != null) {
                player.displayClientMessage(Component.translatable("jei_swift_copy.message.recipe.copy.failure"), false);
            }
            return;
        }
        // try-with-resources ensures the reader (and the underlying input stream) is always closed.
        try (var reader = new JsonReader(resource.get().openAsReader())) {
            reader.setLenient(true);
            minecraft.keyboardHandler.setClipboard(JsonParser.parseReader(reader).toString());
            if (player != null) {
                player.displayClientMessage(Component.translatable("jei_swift_copy.message.recipe.copy.success", registryName.toString()), false);
            }
        } catch (Exception e) {
            // Never let a clipboard-copy failure crash the game.
            LOGGER.error("Failed to copy recipe JSON: {}", registryName, e);
            if (player != null) {
                player.displayClientMessage(Component.translatable("jei_swift_copy.message.recipe.copy.failure"), false);
            }
        }
    }
}
