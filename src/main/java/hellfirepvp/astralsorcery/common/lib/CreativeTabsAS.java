/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.ArtifactItem;
import hellfirepvp.astralsorcery.common.item.block.LumenCrystalClusterBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CreativeTabsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CreativeTabsAS {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AstralSorcery.MODID);

    public static DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB_AS = CREATIVE_TAB_REGISTER.register("common", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.astralsorcery"))
            .icon(ItemsAS.TOME::toStack)
            .build());
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB_AS_PAPERS = CREATIVE_TAB_REGISTER.register("papers", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.astralsorcery.papers"))
            .icon(ItemsAS.CONSTELLATION_PAPER::toStack)
            .withTabsBefore(AstralSorcery.key("common"))
            .build());
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB_AS_LUMEN = CREATIVE_TAB_REGISTER.register("lumen", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.astralsorcery.lumen"))
            .icon(() -> LumenCrystalClusterBlockItem.getCluster(LumenAS.AEVITAS, 4))
            .withTabsBefore(AstralSorcery.key("papers"))
            .build());
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB_AS_ATTUNED_CRYSTALS = CREATIVE_TAB_REGISTER.register("attuned_crystals", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.astralsorcery.attuned_crystals"))
            .icon(ItemsAS.CELESTIAL_CRYSTAL::toStack)
            .withTabsBefore(AstralSorcery.key("lumen"))
            .build());
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB_AS_ARTIFACTS = CREATIVE_TAB_REGISTER.register("artifacts", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.astralsorcery.artifacts"))
            .icon(() -> ArtifactItem.defaultStack().orElseGet(ItemsAS.AQUAMARINE::toStack))
            .withTabsBefore(AstralSorcery.key("attuned_crystals"))
            .build());
}
