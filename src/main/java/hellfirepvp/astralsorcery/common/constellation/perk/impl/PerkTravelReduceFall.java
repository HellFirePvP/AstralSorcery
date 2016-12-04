package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTravelReduceFall
 * Created by HellFirePvP
 * Date: 04.12.2016 / 14:28
 */
public class PerkTravelReduceFall extends ConstellationPerk {

    private static float fallDmgReduction = 0.3F;

    public PerkTravelReduceFall() {
        super("TRV_FALLRED", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        if(source.getDamageType().toLowerCase().contains("fall")) {
            dmgIn *= fallDmgReduction;
        }
        return dmgIn;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        fallDmgReduction = cfg.getFloat(getKey() + "FallReduction", getConfigurationSection(), 0.3F, 0F, 1F, "Defines the multiplier for fall damage if the player has this perk.");
    }

}
