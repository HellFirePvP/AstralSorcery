/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EvorsioRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 15:41
 */
public class EvorsioRootPerk extends RootPerk {

    public EvorsioRootPerk(int x, int y) {
        super("evorsio", Constellations.evorsio, x, y);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBreak(BlockEvent.BreakEvent event) {
        Side side = event.getPlayer().world.isRemote ? Side.CLIENT : Side.SERVER;
        if (side != Side.SERVER) return;

        EntityPlayer player = event.getPlayer();
        if (player != null && player instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) player)) {
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog == null || !prog.hasPerkUnlocked(this)) {
                return;
            }

            BlockPos at = event.getPos();
            IBlockState broken = event.getState();
            World world = event.getWorld();
            float gainedExp;
            try {
                gainedExp = broken.getPlayerRelativeBlockHardness(player, world, event.getPos());
            } catch (Exception exc) {
                gainedExp = 2F;
            }
            gainedExp /= 4.0F;
            try {
                Explosion exp = new Explosion(world, player, at.getX(), at.getY(), at.getZ(), 2F, true, true);
                gainedExp *= Math.max(broken.getBlock().getExplosionResistance(world, at, player, exp) / 40F, 1F);
            } catch (Exception exc) {}
            gainedExp *= expMultiplier;
            gainedExp = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, gainedExp);
            ResearchManager.modifyExp(player, gainedExp);
        }
    }

}
