package net.vvxzv.jeiswiftcopy.mixin;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.vvxzv.jeiswiftcopy.api.ICopyJsonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * JEI 19.57 already ships a "copy recipe id" action ({@code handleCopyRecipeId}) wired to its own
 * key binding. We hook that method instead of {@code handleUserInput}, so we never touch the input
 * routing that handles ESC — exiting the recipe screen stays intact.
 */
@Mixin(targets = "mezz.jei.gui.recipes.RecipeLayoutWithButtons$RecipeLayoutUserInputHandler", remap = false)
public class RecipeLayoutUserInputHandlerMixin<R> {

    @Inject(
            method = "handleCopyRecipeId",
            at = @At("HEAD"),
            cancellable = true
    )
    private void jeiSwiftCopy$copyRecipeJson(IRecipeLayoutDrawable<R> recipeLayout, boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        // Keep JEI's own simulate behavior (claim the key without doing work yet).
        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            cir.setReturnValue(true);
            return;
        }

        LocalPlayer player = minecraft.player;
        IRecipeCategory<R> category = recipeLayout.getRecipeCategory();
        R recipe = recipeLayout.getRecipe();
        ResourceLocation registryName = category.getRegistryName(recipe);
        if (registryName != null) {
            ICopyJsonHandler.handler(server, minecraft, registryName);
        } else if (player != null) {
            player.displayClientMessage(Component.translatable("jei_swift_copy.message.recipe.copy.failure"), false);
        }

        cir.setReturnValue(true);
    }
}
