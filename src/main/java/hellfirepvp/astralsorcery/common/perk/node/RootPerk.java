/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.node.key.KeyCore;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreeConstellation;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RootPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:14
 */
public class RootPerk extends AttributeModifierPerk {

    protected float expMultiplier = 1.0F;
    private final IMajorConstellation constellation;

    public RootPerk(ResourceLocation name, IMajorConstellation constellation, int x, int y) {
        super(name, x, y);
        this.constellation = constellation;
        this.setCategory(CATEGORY_ROOT);
        this.setRequireDiscoveredConstellation(this.constellation);
    }

    @Override
    protected PerkTreePoint<? extends RootPerk> initPerkTreePoint() {
        return new PerkTreeConstellation<>(this, getOffset(),
                this.constellation, PerkTreeConstellation.ROOT_SPRITE_SIZE);
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        expMultiplier *= multiplier;
    }

    public IMajorConstellation getConstellation() {
        return constellation;
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        if (progress.hasFreeAllocationPoint(player) && canSee(player, progress)) {
            AbstractPerk core = PerkTree.PERK_TREE.getPerk(KeyCore.NAME);
            if (core != null && progress.hasPerkEffect(core)) {
                return true;
            }
        }

        return super.mayUnlockPerk(progress, player);
    }
}
