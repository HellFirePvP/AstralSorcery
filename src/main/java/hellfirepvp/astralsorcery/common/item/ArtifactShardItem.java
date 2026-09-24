/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.artifact.ArtifactType;
import hellfirepvp.astralsorcery.common.component.ArtifactComponent;
import hellfirepvp.astralsorcery.common.component.ArtifactTypeComponent;
import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.CreativeTabsAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactShardItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactShardItem extends ItemCustom {

    public ArtifactShardItem() {
        super(new Properties()
                .stacksTo(16));
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeTabsAS.CREATIVE_TAB_AS_ARTIFACTS.get();
    }

    @Override
    public void fillCreativeTab(Consumer<ItemStack> tabItems) {
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.forEach(type -> {
            tabItems.accept(createShard(type));
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level instanceof ServerLevel) {
            if (!stack.has(DataComponentsAS.ARTIFACT_TYPE)) {
                stack.shrink(stack.getCount());
            }
        }
    }

    public static ItemStack createShard(ArtifactType type) {
        ItemStack stack = ItemsAS.ARTIFACT_SHARD.toStack();
        stack.set(DataComponentsAS.ARTIFACT_TYPE, new ArtifactTypeComponent(type));
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.has(DataComponentsAS.ARTIFACT_TYPE)) {
            ArtifactType type = stack.get(DataComponentsAS.ARTIFACT_TYPE).type();
            return Component.translatable("item.astralsorcery.artifact_shard.format", type.getName());
        }
        return super.getName(stack);
    }
}
