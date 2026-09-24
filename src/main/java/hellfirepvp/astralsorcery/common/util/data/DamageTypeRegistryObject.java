/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DamageTypeRegistryObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record DamageTypeRegistryObject(ResourceKey<DamageType> id, Supplier<DamageType> damageType) {

    public ResourceLocation idLocation() {
        return this.id.location();
    }

    public DamageSource source(Level level) {
        return source(level.damageSources());
    }

    public DamageSource source(DamageSources src) {
        return src.source(this.id());
    }

    public void register(BootstrapContext<DamageType> context) {
        AstralSorcery.assertDataGeneration();
        context.register(this.id(), this.damageType().get());
    }
}
