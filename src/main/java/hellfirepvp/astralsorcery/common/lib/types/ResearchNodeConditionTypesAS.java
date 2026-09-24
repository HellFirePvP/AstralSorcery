/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.research.condition.ConditionConstellation;
import hellfirepvp.astralsorcery.common.research.condition.ConditionResearchFlag;
import hellfirepvp.astralsorcery.common.research.condition.ResearchNodeCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ResearchNodeConditionTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ResearchNodeConditionTypesAS {

    public static final DeferredRegister<ResearchNodeCondition.Type<?>> RESEARCH_NODE_CONDITION_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_RESEARCH_NODE_CONDITION_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<ResearchNodeCondition.Type<?>, ResearchNodeCondition.Type<ConditionConstellation>> CONSTELLATION_DISCOVERED =
            RESEARCH_NODE_CONDITION_REGISTER.register("constellation_discovered",
                    () -> new ResearchNodeCondition.Type<>(ConditionConstellation.CODEC, ConditionConstellation.STREAM_CODEC));
    public static final DeferredHolder<ResearchNodeCondition.Type<?>, ResearchNodeCondition.Type<?>> RESEARCH_FLAG_SET =
            RESEARCH_NODE_CONDITION_REGISTER.register("research_flag_set",
                    () -> new ResearchNodeCondition.Type<>(ConditionResearchFlag.CODEC, ConditionResearchFlag.STREAM_CODEC));
}
