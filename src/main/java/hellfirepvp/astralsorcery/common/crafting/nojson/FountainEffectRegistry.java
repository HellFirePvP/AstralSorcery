package hellfirepvp.astralsorcery.common.crafting.nojson;

import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffect;
import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffectLiquid;
import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffectVortex;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FountainEffectRegistry
 * Created by HellFirePvP
 * Date: 01.11.2020 / 09:37
 */
public class FountainEffectRegistry {

    public static final FountainEffect<?> EFFECT_LIQUID = new FountainEffectLiquid();
    public static final FountainEffect<?> EFFECT_VORTEX = new FountainEffectVortex();

    private static final Map<ResourceLocation, FountainEffect<?>> fountainEffectRegistry = new HashMap<>();

    private FountainEffectRegistry() {}

    public static void register(FountainEffect<?> effect) {
        fountainEffectRegistry.put(effect.getId(), effect);
    }

    @Nullable
    public static FountainEffect<?> getEffect(ResourceLocation key) {
        return fountainEffectRegistry.get(key);
    }

    static {
        register(EFFECT_LIQUID);
        register(EFFECT_VORTEX);
    }
}
