/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.util.Provider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMatcherRegistry
 * Created by HellFirePvP
 * Date: 02.12.2018 / 00:58
 */
public class StructureMatcherRegistry {

    public static final StructureMatcherRegistry INSTANCE = new StructureMatcherRegistry();

    private Map<ResourceLocation, Provider<StructureMatcher>> matcherRegistry = Maps.newHashMap();

    private StructureMatcherRegistry() {}

    public void register(Provider<StructureMatcher> matchProvider) {
        StructureMatcher match = matchProvider.provide();
        this.matcherRegistry.put(match.getRegistryName(), matchProvider);
    }

    @Nullable
    public StructureMatcher provideNewMatcher(ResourceLocation key) {
        Provider<StructureMatcher> provider = this.matcherRegistry.get(key);
        return provider == null ? null : provider.provide();
    }

}
