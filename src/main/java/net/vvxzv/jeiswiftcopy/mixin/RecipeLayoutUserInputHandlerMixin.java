package net.vvxzv.jeiswiftcopy.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.input.IUserInputHandler;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.vvxzv.jeiswiftcopy.api.ICopyJsonHandler;
import net.vvxzv.jeiswiftcopy.utils.KeyMappingUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(targets = "mezz.jei.gui.recipes.RecipeLayoutWithButtons$RecipeLayoutUserInputHandler", remap = false)
public class RecipeLayoutUserInputHandlerMixin<R> {
    @Shadow
    @Final
    private IRecipeLayoutDrawable<R> recipeLayout;

    @Inject(
            method = "handleUserInput",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/common/Internal;getKeyMappings()Lmezz/jei/common/input/IInternalKeyMappings;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void test(Screen screen, UserInput input, IInternalKeyMappings keyBindings, CallbackInfoReturnable<Optional<IUserInputHandler>> cir) {
        InputConstants.Key key = input.getKey();
        if(!KeyMappingUtil.KEYMAPPING.isActiveAndMatches(key)) {
            return;
        }
        boolean simulate = input.isSimulate();
        if (simulate) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        IRecipeCategory<R> recipeCategory = recipeLayout.getRecipeCategory();
        R recipe = recipeLayout.getRecipe();
        ResourceLocation registryName = recipeCategory.getRegistryName(recipe);
        if (registryName != null) {
            ICopyJsonHandler.handler(server, minecraft, registryName);
            cir.setReturnValue(Optional.of((IUserInputHandler) this));
            return;
        }
        if (player != null) {
            player.displayClientMessage(Component.translatable("jei_swift_copy.message.recipe.copy.failure"), false);
        }
        cir.setReturnValue(Optional.empty());
    }
}
