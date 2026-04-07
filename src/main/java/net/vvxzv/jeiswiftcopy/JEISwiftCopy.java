package net.vvxzv.jeiswiftcopy;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.vvxzv.jeiswiftcopy.client.ClientHandler;
import net.vvxzv.jeiswiftcopy.common.CopyItemHandler;

@Mod(JEISwiftCopy.MODID)
public class JEISwiftCopy {
    public static final String MODID = "jei_swift_copy";

    public JEISwiftCopy(IEventBus modEventBus, ModContainer modContainer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientHandler::registerKeyMappings);
            NeoForge.EVENT_BUS.register(CopyItemHandler.class);
        }
    }
}
