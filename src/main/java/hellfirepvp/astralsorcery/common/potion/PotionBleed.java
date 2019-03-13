/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.potion;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.util.DamageUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PotionBleed
 * Created by HellFirePvP
 * Date: 18.11.2016 / 01:51
 */
public class PotionBleed extends PotionCustomTexture {

    private static Object texBuffer = null;

    public PotionBleed() {
        super(true, 0x751200);
        setPotionName("effect.as.bleed");
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if(entity instanceof EntityPlayer &&
                !entity.getEntityWorld().isRemote &&
                entity.getEntityWorld() instanceof WorldServer &&
                entity.getEntityWorld().getMinecraftServer().isPVPEnabled()) {
            return;
        }
        int preTime = entity.hurtResistantTime;
        DamageUtil.attackEntityFrom(entity, CommonProxy.dmgSourceBleed, 0.5F * (amplifier + 1));
        entity.hurtResistantTime = Math.max(preTime, entity.hurtResistantTime);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BindableResource getResource() {
        if(texBuffer == null) {
            texBuffer = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "potion_bleed");
        }
        return (BindableResource) texBuffer;
    }
}
