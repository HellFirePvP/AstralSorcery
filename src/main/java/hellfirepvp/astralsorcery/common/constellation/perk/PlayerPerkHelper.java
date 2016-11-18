package hellfirepvp.astralsorcery.common.constellation.perk;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerPerkHelper
 * Created by HellFirePvP
 * Date: 17.11.2016 / 00:04
 */
public class PlayerPerkHelper {

    @CapabilityInject(IPlayerCapabilityPerks.class)
    public static final Capability<IPlayerCapabilityPerks> PLAYER_PERKS = null;

    @Nullable
    public static IPlayerCapabilityPerks getPerks(EntityPlayer player) {

        if(player.hasCapability(PLAYER_PERKS, EnumFacing.DOWN)) {
            return player.getCapability(PLAYER_PERKS, EnumFacing.DOWN);
        }
        return null;
    }

}
