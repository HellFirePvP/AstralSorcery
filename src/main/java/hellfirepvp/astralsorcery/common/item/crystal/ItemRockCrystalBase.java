package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalBase
 * Created by HellFirePvP
 * Date: 08.05.2016 / 21:38
 */
public class ItemRockCrystalBase extends Item {

    public ItemRockCrystalBase() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if(worldIn.isRemote) {
            EffectHandler.getInstance().texturePlane(AssetLoader.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1"), Axis.persisentRandomAxis()).
                    setPosition(new Vector3(playerIn)).setMaxAge(50);
        }
        return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE, 100, 100));
        subItems.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip);
    }

    public static ItemStack createRandomBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandom());
        return crystal;
    }

}
