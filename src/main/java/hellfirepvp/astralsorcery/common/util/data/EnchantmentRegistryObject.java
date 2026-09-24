/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentRegistryObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record EnchantmentRegistryObject(ResourceKey<Enchantment> id) {

    public ResourceLocation idLocation() {
        return this.id.location();
    }

    public Holder<Enchantment> enchantmentHolder(Level level) {
        return level.holderOrThrow(this.id());
    }

    public Enchantment enchantment(Level level) {
        return enchantmentHolder(level).value();
    }

    public void register(BootstrapContext<Enchantment> context, Enchantment.Builder definition) {
        AstralSorcery.assertDataGeneration();
        context.register(this.id(), definition.build(this.idLocation()));
    }
}
