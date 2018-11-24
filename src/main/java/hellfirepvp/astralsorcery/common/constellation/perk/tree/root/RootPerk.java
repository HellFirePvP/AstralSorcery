/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePointConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RootPerk
 * Created by HellFirePvP
 * Date: 08.07.2018 / 10:25
 */
public class RootPerk extends AttributeModifierPerk {

    protected float expMultiplier = 1.0F;
    private final IMajorConstellation constellation;

    public RootPerk(String name, IMajorConstellation constellation, int x, int y) {
        super(name, x, y);
        setCategory(AbstractPerk.CATEGORY_ROOT);
        this.constellation = constellation;
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, "root_" + name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                expMultiplier = cfg.getFloat("Exp_Multiplier", getConfigurationSection(), expMultiplier, 0F, 1024F,
                        "Sets the exp multiplier exp gained from this root-perk are multiplied by. (So higher multiplier -> more exp)");

                RootPerk.this.loadAdditionalConfigurations(cfg);
            }
        });
    }

    @Override
    protected PerkTreePoint<? extends RootPerk> initPerkTreePoint() {
        return new PerkTreePointConstellation<>(this, getOffset(),
                constellation, PerkTreePointConstellation.ROOT_SPRITE_SIZE);
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        expMultiplier *= multiplier;
    }

    protected void loadAdditionalConfigurations(Configuration cfg) {}

    public IConstellation getConstellation() {
        return constellation;
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, EntityPlayer player) {
        if (progress.hasFreeAllocationPoint(player) && canSee(player, progress)) {
            AbstractPerk core = PerkTree.PERK_TREE.getAstralSorceryPerk("core");
            if (core != null && progress.hasPerkEffect(core)) {
                return true;
            }
        }

        return super.mayUnlockPerk(progress, player);
    }
}
