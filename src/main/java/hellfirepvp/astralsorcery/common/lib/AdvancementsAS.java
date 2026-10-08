/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.advancement.ConstellationDiscoveryTrigger;
import hellfirepvp.astralsorcery.common.advancement.FocalPointDiscoveryTrigger;
import hellfirepvp.astralsorcery.common.advancement.GemSocketTrigger;
import hellfirepvp.astralsorcery.common.advancement.ItemAttunementTrigger;
import hellfirepvp.astralsorcery.common.advancement.LumenDiscoveryTrigger;
import hellfirepvp.astralsorcery.common.advancement.PerkLevelTrigger;
import hellfirepvp.astralsorcery.common.advancement.PlayerAttunementTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AdvancementsAS
 * Created by HellFirePvP
 * Date: 07.10.2026 / 23:38
 */
public class AdvancementsAS {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_REGISTER =
            DeferredRegister.create(Registries.TRIGGER_TYPE, AstralSorcery.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, PerkLevelTrigger> PERK_LEVEL =
            TRIGGER_REGISTER.register("perk_level", PerkLevelTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, PlayerAttunementTrigger> PLAYER_ATTUNEMENT =
            TRIGGER_REGISTER.register("player_attunement", PlayerAttunementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, ItemAttunementTrigger> ITEM_ATTUNEMENT =
            TRIGGER_REGISTER.register("item_attunement", ItemAttunementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, ConstellationDiscoveryTrigger> CONSTELLATION_DISCOVERY =
            TRIGGER_REGISTER.register("constellation_discovery", ConstellationDiscoveryTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, LumenDiscoveryTrigger> LUMEN_DISCOVERY =
            TRIGGER_REGISTER.register("lumen_discovery", LumenDiscoveryTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, FocalPointDiscoveryTrigger> FOCAL_POINT_DISCOVERY =
            TRIGGER_REGISTER.register("focal_point_discovery", FocalPointDiscoveryTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, GemSocketTrigger> GEM_SOCKET =
            TRIGGER_REGISTER.register("gem_socket", GemSocketTrigger::new);
}
