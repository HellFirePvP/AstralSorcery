package hellfirepvp.astralsorcery.common.crafting.altar.recipes;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.INighttimeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.OreDictAlias;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.init.Items;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttunementAltarRecipe
 * Created by HellFirePvP
 * Date: 07.12.2016 / 13:47
 */
public class AttunementAltarRecipe extends AttunementRecipe implements INighttimeRecipe {

    private static Vector3[] offsetPillars = new Vector3[] {
        new Vector3( 3, 2,  3),
        new Vector3(-3, 2,  3),
        new Vector3( 3, 2, -3),
        new Vector3(-3, 2, -3)
    };

    public AttunementAltarRecipe(boolean celestial) {
        super(new ShapedRecipe(BlocksAS.attunementAltar)
                .addPart(celestial ? ItemsAS.celestialCrystal : ItemsAS.rockCrystal,
                        ShapedRecipeSlot.UPPER_CENTER)
                .addPart(OreDictAlias.ITEM_GOLD_INGOT,
                        ShapedRecipeSlot.LEFT,
                        ShapedRecipeSlot.RIGHT)
                .addPart(BlocksAS.attunementRelay,
                        ShapedRecipeSlot.LOWER_CENTER)
                .addPart(BlockMarble.MarbleBlockType.RUNED.asStack(),
                        ShapedRecipeSlot.LOWER_LEFT,
                        ShapedRecipeSlot.LOWER_RIGHT));
        setAttItem(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),
                AltarSlot.UPPER_LEFT,
                AltarSlot.UPPER_RIGHT);
        setAttItem(BlockMarble.MarbleBlockType.RUNED.asStack(),
                AltarSlot.LOWER_LEFT,
                AltarSlot.LOWER_RIGHT);
    }

    @Override
    public void onCraftClientTick(TileAltar altar, int tick, Random rand) {
        super.onCraftClientTick(altar, tick, rand);

        Vector3 randomPos = new Vector3(altar);
        randomPos.add(rand.nextFloat() * 7 - 3, 0.1, rand.nextFloat() * 7 - 3);
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(randomPos.getX(), randomPos.getY(), randomPos.getZ());
        p.scale(0.7F).gravity(0.02);
        if(tick % 50 == 0) {
            Vector3 vec = new Vector3(altar).add(0.5, 0.5, 0.5);
            for (Vector3 offset : offsetPillars) {
                EffectHandler.getInstance().lightbeam(offset.clone().add(altar.getPos()).add(0.5, 0.5, 0.5), vec, 1.2F);
                        //.setColorOverlay(127F / 255F, 127F / 255F, 1F, 1F);
            }
        }
    }
}
