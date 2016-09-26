package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.light.EffectLightbeam;
import hellfirepvp.astralsorcery.client.effect.texture.TextureSpritePlane;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.AbstractCacheableRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: DiscoveryRecipe
* Created by HellFirePvP
* Date: 25.09.2016 / 23:58
*/
public class DiscoveryRecipe extends AbstractAltarRecipe {

    public DiscoveryRecipe(AbstractCacheableRecipe recipe) {
        this(recipe.make());
    }

    public DiscoveryRecipe(IRecipe recipe) {
        super(TileAltar.AltarLevel.DISCOVERY, recipe);
        setPassiveStarlightRequirement(600);
    }

    @Override
    public int craftingTickTime() {
        return 100;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);

        if(rand.nextInt(8) == 0) {
            Vector3 from = new Vector3(altar).add(0.5, 0.3, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 0.4F);
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(4 + rand.nextInt(2)), from, 1);
            lightbeam.setMaxAge(64);
        }
        if(rand.nextInt(10) == 0) {
            Vector3 from = new Vector3(altar).add(0.5, -0.6, 0.5);
            MiscUtils.applyRandomOffset(from, rand, 1.8F);
            from.setY(altar.getPos().getY() - 0.6 + 1 * rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
            EffectLightbeam lightbeam = EffectHandler.getInstance().lightbeam(from.clone().addY(5 + rand.nextInt(3)), from, 1);
            lightbeam.setMaxAge(64);
        }
    }
}
