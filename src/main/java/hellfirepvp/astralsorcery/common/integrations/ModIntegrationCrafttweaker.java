/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations;

import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.tweaks.*;
import hellfirepvp.astralsorcery.common.integrations.mods.crafttweaker.network.SerializeableRecipe;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncMinetweakerChanges;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.util.IEventHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ModIntegrationCrafttweaker
 * Created by HellFirePvP
 * Date: 27.02.2017 / 00:45
 */
public class ModIntegrationCrafttweaker {

    public static ModIntegrationCrafttweaker instance = new ModIntegrationCrafttweaker();
    public static List<SerializeableRecipe> recipeModifications = new LinkedList<>();

    private ModIntegrationCrafttweaker() {}

    public void load() {
        MineTweakerAPI.registerClass(InfusionRecipe.class);
        MineTweakerAPI.registerClass(RitualMineralis.class);
        MineTweakerAPI.registerClass(LightTransmutations.class);
        MineTweakerAPI.registerClass(AltarRecipe.class);
        MineTweakerAPI.registerClass(WellRecipe.class);

        MineTweakerImplementationAPI.onReloadEvent(AstralRecipeReloadHandlerPre.instance);
        MineTweakerImplementationAPI.onPostReload(AstralRecipeReloadHandlerPost.instance);
    }

    private static class AstralRecipeReloadHandlerPre implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {

        static AstralRecipeReloadHandlerPre instance = new AstralRecipeReloadHandlerPre();

        private AstralRecipeReloadHandlerPre() {}

        @Override
        public void handle(MineTweakerImplementationAPI.ReloadEvent reloadEvent) {
            recipeModifications.clear();
        }

    }

    private static class AstralRecipeReloadHandlerPost implements IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {

        static AstralRecipeReloadHandlerPost instance = new AstralRecipeReloadHandlerPost();

        private AstralRecipeReloadHandlerPost() {}

        @Override
        public void handle(MineTweakerImplementationAPI.ReloadEvent reloadEvent) {
            CraftingAccessManager.clearModifications();

            for (SerializeableRecipe modification : recipeModifications) {
                modification.applyServer();
            }

            CraftingAccessManager.compile();

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && Loader.instance().hasReachedState(LoaderState.SERVER_ABOUT_TO_START)) {
                PacketChannel.CHANNEL.sendToAll(compileRecipeChangePacket());
            }
        }
    }

    public static IMessage compileRecipeChangePacket() {
        PktSyncMinetweakerChanges.Compound compound = new PktSyncMinetweakerChanges.Compound();
        for (SerializeableRecipe modification : recipeModifications) {
            PktSyncMinetweakerChanges change = new PktSyncMinetweakerChanges(modification);
            compound.parts.add(change);
        }
        return compound;
    }

}
