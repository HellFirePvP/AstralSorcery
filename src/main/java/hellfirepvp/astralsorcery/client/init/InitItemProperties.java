/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.init;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.ArtifactType;
import hellfirepvp.astralsorcery.common.block.tile.CelestialCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.block.tile.GemCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.block.tile.LumenCrystalClusterBlock;
import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.component.StoredPlayerProgressComponent;
import hellfirepvp.astralsorcery.common.item.StardewItem;
import hellfirepvp.astralsorcery.common.item.block.CelestialCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.item.block.GemCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.item.block.LumenCrystalClusterBlockItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InitItemProperties
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InitItemProperties {

    public static void init() {
        ItemProperties.register(ItemsAS.BLOCK_CELESTIAL_CRYSTAL_CLUSTER.asItem(), AstralSorcery.key("stage"), (stack, level, entity, seed) -> {
            return (float) CelestialCrystalClusterBlockItem.getStage(stack) / CelestialCrystalClusterBlock.STAGE.getPossibleValues().size();
        });
        ItemProperties.register(ItemsAS.BLOCK_GEM_CRYSTAL_CLUSTER.asItem(), AstralSorcery.key("stage"), (stack, level, entity, seed) -> {
            return (float) GemCrystalClusterBlockItem.getStage(stack).ordinal() / GemCrystalClusterBlock.STAGE.getPossibleValues().size();
        });
        ItemProperties.register(ItemsAS.BLOCK_LUMEN_CRYSTAL_CLUSTER.asItem(), AstralSorcery.key("stage"), (stack, level, entity, seed) -> {
            return (float) LumenCrystalClusterBlockItem.getStage(stack) / LumenCrystalClusterBlock.STAGE.getPossibleValues().size();
        });
        ItemProperties.register(ItemsAS.KNOWLEDGE_SHARE.asItem(), AstralSorcery.key("written"), (stack, level, entity, seed) -> {
            StoredPlayerProgressComponent cmp = stack.getOrDefault(DataComponentsAS.STORED_PLAYER_PROGRESS, StoredPlayerProgressComponent.EMPTY);
            return cmp.creative() || cmp.progress().isPresent() ? 1F : 0F;
        });
        ItemProperties.register(ItemsAS.ARTIFACT.asItem(), AstralSorcery.key("artifact_type"), (stack, level, entity, seed) -> {
            if (!stack.has(DataComponentsAS.ARTIFACT)) return 0F;
            ArtifactType artifactType = stack.get(DataComponentsAS.ARTIFACT).artifactType();
            return RegistriesAS.REGISTRY_ARTIFACT_TYPES.getId(artifactType) / (float) RegistriesAS.REGISTRY_ARTIFACT_TYPES.size();
        });
        ItemProperties.register(ItemsAS.ARTIFACT_SHARD.asItem(), AstralSorcery.key("artifact_type"), (stack, level, entity, seed) -> {
            if (!stack.has(DataComponentsAS.ARTIFACT_TYPE)) return 0F;
            ArtifactType artifactType = stack.get(DataComponentsAS.ARTIFACT_TYPE).type();
            return RegistriesAS.REGISTRY_ARTIFACT_TYPES.getId(artifactType) / (float) RegistriesAS.REGISTRY_ARTIFACT_TYPES.size();
        });
        ItemProperties.register(ItemsAS.LUMEN_CRYSTAL.asItem(), AstralSorcery.key("prismatic"), (stack, level, entity, seed) -> {
            return stack.getOrDefault(DataComponentsAS.LUMEN, LumenComponent.EMPTY).lumen().is(LumenAS.PRISMATIC.getKey()) ? 1F : 0F;
        });
        ItemProperties.register(ItemsAS.STARDEW.asItem(), AstralSorcery.key("filled"), (stack, level, entity, seed) -> {
            if (!StardewItem.isWater(stack) && StardewItem.isEmpty(stack)) return 0F;
            return (4 - stack.getDamageValue()) / 4F;
        });
    }
}
