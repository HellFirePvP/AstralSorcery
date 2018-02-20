/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.cape.impl;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.block.EffectTranslucentFallingBlock;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapeEffectMineralis
 * Created by HellFirePvP
 * Date: 17.10.2017 / 00:35
 */
public class CapeEffectMineralis extends CapeArmorEffect {

    private static int highlightRange = 8;

    public CapeEffectMineralis(NBTTagCompound cmp) {
        super(cmp, "mineralis");
    }

    @Override
    public IConstellation getAssociatedConstellation() {
        return Constellations.mineralis;
    }

    @Override
    public void playActiveParticleTick(EntityPlayer pl) {
        playConstellationCapeSparkles(pl, 0.15F);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        highlightRange = cfg.getInt(getKey() + "HighlightRange", getConfigurationSection(), highlightRange, 4, 32, "Sets the highlight radius in which the cape effect will search for the block you're holding.");
    }

    @SideOnly(Side.CLIENT)
    public void playClientHighlightTick(EntityPlayer pl) {
        if(rand.nextFloat() > 0.2F) return;

        ItemStack main = pl.getHeldItemMainhand();
        IBlockState check = null;
        if(!main.isEmpty()) {
            try {
                check = ItemUtils.createBlockState(main);
            } catch (Exception e) {}
        }
        if(check == null) {
            main = pl.getHeldItemOffhand();
            try {
                check = ItemUtils.createBlockState(main);
            } catch (Exception e) {}
        }

        if(check != null) {
            Block b;
            int meta;
            try {
                b = check.getBlock();
                meta = b.getMetaFromState(check);
            } catch (Exception e) {
                return;
            }
            List<BlockPos> blocks = MiscUtils.searchAreaFor(pl.world, pl.getPosition(), b, meta, highlightRange);
            if(blocks.isEmpty()) return;

            int index = blocks.size() > 10 ? rand.nextInt(blocks.size()) : rand.nextInt(10);
            if(index >= blocks.size()) {
                return;
            }
            BlockPos at = blocks.get(index);
            IBlockState act = pl.world.getBlockState(at);
            EffectTranslucentFallingBlock bl = EffectHandler.getInstance().translucentFallingBlock(
                    new Vector3(at).add(0.5, 0.5, 0.5), act);
            bl.setDisableDepth(true).setScaleFunction(new EntityComplexFX.ScaleFunction.Shrink<>());
            bl.setMotion(0, 0.02, 0).setAlphaFunction(EntityComplexFX.AlphaFunction.PYRAMID);
            bl.tumble();
            bl.setMaxAge(40 + rand.nextInt(15));
        }
    }

}
