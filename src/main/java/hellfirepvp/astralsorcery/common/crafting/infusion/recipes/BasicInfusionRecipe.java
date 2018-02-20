/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.infusion.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.crafting.infusion.AbstractInfusionRecipe;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BasicInfusionRecipe
 * Created by HellFirePvP
 * Date: 11.12.2016 / 18:11
 */
public class BasicInfusionRecipe extends AbstractInfusionRecipe {

    public BasicInfusionRecipe(ItemStack output, String oreDictInput) {
        this(output, new ItemHandle(oreDictInput));
    }

    public BasicInfusionRecipe(ItemStack output, ItemStack input) {
        this(output, new ItemHandle(input));
    }

    public BasicInfusionRecipe(ItemStack output, ItemHandle input) {
        super(output, input);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileStarlightInfuser infuser, long tick, Random rand) {
        super.onCraftClientTick(infuser, tick, rand);

        if(rand.nextInt(10) == 0) {
            Vector3 from = new Vector3(infuser).add(0.5, 0.3, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 0.4F);
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
            lightbeam.setMaxAge(64);
        }

        BlockPos randPos = TileStarlightInfuser.offsetsLiquidStarlight[rand.nextInt(TileStarlightInfuser.offsetsLiquidStarlight.length)];
        Vector3 from = new Vector3(infuser).add(randPos);
        from.add(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        Vector3 dir = new Vector3(infuser).add(0.5, 1.6, 0.5).subtract(from);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(from.getX(), from.getY(), from.getZ());
        p.setColor(Color.WHITE).scale(0.2F + rand.nextFloat() * 0.1F).gravity(0.004).motion(dir.getX() / 40D, dir.getY() / 40D, dir.getZ() / 40D);
    }

}
