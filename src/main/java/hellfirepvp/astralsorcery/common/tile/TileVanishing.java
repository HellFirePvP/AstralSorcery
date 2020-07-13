/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileVanishing
 * Created by HellFirePvP
 * Date: 11.03.2020 / 21:16
 */
public class TileVanishing extends TileEntityTick {

    private static final AxisAlignedBB SEARCH_BOX = new AxisAlignedBB(-4,0, -4, 4, 3, 4);

    public TileVanishing() {
        super(TileEntityTypesAS.VANISHING);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isRemote() && this.getTicksExisted() % 5 == 0) {
            boolean removeBlock = true;

            List<PlayerEntity> players = getWorld().getEntitiesWithinAABB(PlayerEntity.class, SEARCH_BOX.offset(getPos()));
            for (PlayerEntity player : players) {
                if (ItemMantle.getEffect(player, ConstellationsAS.aevitas) != null) {
                    double yDiff = player.getPosY() - this.getPos().getY();

                    //Standing on top of this block
                    if (player.onGround && yDiff >= 0.95 && yDiff <= 1.15) {
                        if (player.isSneaking()) { //Indicating they want to drop down
                            break; //Remove the block
                        }

                        removeBlock = false;
                    } else if (player.isSneaking() && yDiff >= 0.95 && yDiff <= 2.15) {
                        removeBlock = false;
                    }
                }
            }

            if (removeBlock) {
                this.getWorld().removeBlock(getPos(), false);
            }
        }

        if (this.getWorld().isRemote()) {
            this.tickClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickClient() {
        for (int i = 0; i < 3; i++) {
            if (rand.nextFloat() < 0.07F) {
                Vector3 at = new Vector3(pos).add(0.5F, 0.5F, 0.5F).add(Vector3.random());
                FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                        .alpha(VFXAlphaFunction.PYRAMID)
                        .setMaxAge(40 + rand.nextInt(10));
                if (rand.nextBoolean()) {
                    p.color(VFXColorFunction.WHITE);
                } else {
                    p.color(VFXColorFunction.constant(ColorsAS.RITUAL_CONSTELLATION_AEVITAS));
                }
            }
        }
    }
}
