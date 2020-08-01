/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreeConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.util.DiminishingMultiplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RootPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:14
 */
public abstract class RootPerk extends AttributeModifierPerk {

    private final IMajorConstellation constellation;
    private Map<UUID, DiminishingMultiplier> dimReturns = new HashMap<>();

    private final Config config;

    public RootPerk(ResourceLocation name, IMajorConstellation constellation, int x, int y) {
        super(name, x, y);
        this.constellation = constellation;
        this.config = new Config(name.getPath());
        this.setCategory(CATEGORY_ROOT);
        this.setRequireDiscoveredConstellation(this.constellation);
    }

    @Override
    protected PerkTreePoint<? extends RootPerk> initPerkTreePoint() {
        return new PerkTreeConstellation<>(this, getOffset(),
                this.constellation, PerkTreeConstellation.ROOT_SPRITE_SIZE);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    public IMajorConstellation getConstellation() {
        return constellation;
    }

    @Override
    public void clearCaches(LogicalSide side) {
        super.clearCaches(side);

        if (side.isServer()) {
            this.dimReturns.clear();
        }
    }

    protected double getExpMultiplier() {
        return this.applyMultiplierD(this.config.expMultiplier.get());
    }

    protected float getDiminishingReturns(PlayerEntity player) {
        UUID playerUUID = player.getUniqueID();
        return this.dimReturns.computeIfAbsent(playerUUID, uuid -> createMultiplier()).getMultiplier();
    }

    @Nonnull
    protected abstract DiminishingMultiplier createMultiplier();

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue expMultiplier;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.expMultiplier = cfgBuilder
                    .comment("Defines the general exp multiplier for this root perk. Can be used for balancing in a pack environment.")
                    .translation(translationKey("expMultiplier"))
                    .defineInRange("expMultiplier", 1F, 0.1F, 20F);
        }
    }
}
