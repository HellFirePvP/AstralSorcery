package hellfirepvp.astralsorcery.common.crafting.recipe.interaction.jei;

import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.InteractionResultRegistry;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultDropItem;
import hellfirepvp.astralsorcery.common.crafting.recipe.interaction.ResultSpawnEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEIInteractionResultRegistry
 * Created by HellFirePvP
 * Date: 31.10.2020 / 14:47
 */
public class JEIInteractionResultRegistry {

    private static final Map<ResourceLocation, JEIInteractionResultHandler> interactionRegistry = new HashMap<>();

    public static void register(ResourceLocation key, JEIInteractionResultHandler interactionResultHandler) {
        interactionRegistry.put(key, interactionResultHandler);
    }

    public static Optional<JEIInteractionResultHandler> get(ResourceLocation key) {
        return Optional.ofNullable(interactionRegistry.get(key));
    }

    static {
        register(InteractionResultRegistry.ID_DROP_ITEM, new JEIHandlerDropItem());
        register(InteractionResultRegistry.ID_SPAWN_ENTITY, new JEIHandlerSpawnEntity());
    }

}
