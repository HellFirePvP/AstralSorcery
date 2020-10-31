package hellfirepvp.astralsorcery.common.crafting.recipe.interaction;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InteractionResultRegistry
 * Created by HellFirePvP
 * Date: 31.10.2020 / 13:56
 */
public class InteractionResultRegistry {

    public static final ResourceLocation ID_DROP_ITEM = AstralSorcery.key("drop_item");
    public static final ResourceLocation ID_SPAWN_ENTITY = AstralSorcery.key("spawn_entity");

    private static final Map<ResourceLocation, Supplier<InteractionResult>> interactionRegistry = new HashMap<>();

    private InteractionResultRegistry() {}

    public static void register(ResourceLocation key, Supplier<InteractionResult> supplier) {
        interactionRegistry.put(key, supplier);
    }

    public static Collection<ResourceLocation> getKeys() {
        return interactionRegistry.keySet();
    }

    public static Collection<String> getKeysAsStrings() {
        return interactionRegistry.keySet().stream().map(ResourceLocation::toString).collect(Collectors.toList());
    }

    @Nullable
    public static InteractionResult create(ResourceLocation key) {
        if (!interactionRegistry.containsKey(key)) {
            return null;
        }
        return interactionRegistry.get(key).get();
    }

    static {
        register(ID_DROP_ITEM, ResultDropItem::new);
        register(ID_SPAWN_ENTITY, ResultSpawnEntity::new);
    }
}
