/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.util.FileStorageUtil;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientScreenshotCache
 * Created by HellFirePvP
 * Date: 21.04.2017 / 17:03
 */
public class ClientScreenshotCache {

    private static ResourceLocation nullEntry = new ResourceLocation(AstralSorcery.MODID, "NULL-NONACCESS");

    private static String addrContext = null;
    private static Map<Integer, List<Tuple<BlockPos, ResourceLocation>>> clientScreenshots = new HashMap<>();

    @Nullable
    public static ResourceLocation tryQueryTextureFor(int dim, BlockPos pos) {
        List<Tuple<BlockPos, ResourceLocation>> positions = clientScreenshots.get(dim);
        if(positions == null) {
            positions = new LinkedList<>();
            clientScreenshots.put(dim, positions);
        }
        for (Tuple<BlockPos, ResourceLocation> t : positions) {
            if(t.key.equals(pos)) {
                if(t.value.equals(nullEntry)) {
                    return null;
                } else {
                    return t.value;
                }
            }
        }
        positions.add(new Tuple<>(pos, nullEntry));
        return null;
    }

    public static void takeViewScreenshotFor(int dim, BlockPos pos) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            try {
                File dir = getDirectoryForCurrentContext();
                File worldDir = new File(dir, String.valueOf(dim));
                if(!worldDir.exists()) {
                    worldDir.mkdirs();
                }
                File store = new File(worldDir, pos.getX() + ";" + pos.getY() + ";" + pos.getZ() + ".png");

                boolean wasUIHidden = Minecraft.getMinecraft().gameSettings.hideGUI;
                EffectHandler.getInstance().renderGateway = false;
                Minecraft.getMinecraft().gameSettings.hideGUI = true;

                AstralSorcery.proxy.scheduleClientside(() -> {
                    try {
                        BufferedImage bufferedimage = ScreenShotHelper.createScreenshot(
                                Minecraft.getMinecraft().displayWidth,
                                Minecraft.getMinecraft().displayHeight,
                                Minecraft.getMinecraft().getFramebuffer());
                        ImageIO.write(bufferedimage, "png", store);
                        Minecraft.getMinecraft().gameSettings.hideGUI = wasUIHidden;
                        EffectHandler.getInstance().renderGateway = true;
                        ResourceLocation address = addScreenshot(dim, pos, store);

                        Tuple<BlockPos, ResourceLocation> addr = new Tuple<>(pos, address);
                        if(!clientScreenshots.containsKey(dim)) {
                            clientScreenshots.put(dim, new LinkedList<>());
                        }
                        List<Tuple<BlockPos, ResourceLocation>> positions = clientScreenshots.get(dim);
                        positions.removeIf(entry -> entry.key.equals(pos));
                        positions.add(addr);
                    } catch (Exception exc) {
                        AstralSorcery.log.info("[AstralSorcery] Couldn't save screenshot for position: dimid=" + dim + ", pos=" + pos.toString());
                        exc.printStackTrace();
                    }
                });
            } catch (Exception exc) {
                AstralSorcery.log.info("[AstralSorcery] Couldn't save screenshot for position: dimid=" + dim + ", pos=" + pos.toString());
                exc.printStackTrace();
            }
        });
    }

    public static void cleanUp() {
        TextureManager tm = Minecraft.getMinecraft().getTextureManager();
        for (List<Tuple<BlockPos, ResourceLocation>> rlList : clientScreenshots.values()) {
            for (Tuple<BlockPos, ResourceLocation> rl : rlList) {
                tm.deleteTexture(rl.value);
            }
        }
        addrContext = null;
        clientScreenshots.clear();
    }

    public static void loadAndInitScreenshotsFor(String addr) {
        cleanUp(); //Should've happened already.
        addrContext = addr;

        try {
            loadScreenshots(getDirectoryForCurrentContext());
            AstralSorcery.log.info("[AstralSorcery] Using gateway screenshots for folder '" + addrContext + "'");
        } catch (Exception exc) {
            AstralSorcery.log.info("[AstralSorcery] Couldn't load screenshots from local cache for address " + addrContext);
            exc.printStackTrace();
        }
    }

    private static void loadScreenshots(File directory) throws Exception {
        if(directory == null) return; //Uhh...
        for (File dimDir : directory.listFiles()) {
            if(!dimDir.isDirectory()) continue;
            String fileName = dimDir.getName();
            int dimId;
            try {
                dimId = Integer.parseInt(fileName);
            } catch (NumberFormatException exc) {
                AstralSorcery.log.info("[AstralSorcery] Couldn't load screenshots from folder " + fileName + " as its not a dimensionID. Skipping.");
                continue;
            }
            List<Tuple<BlockPos, ResourceLocation>> textures = new LinkedList<>();
            for (File imageF : dimDir.listFiles()) {
                if(imageF.isDirectory()) continue;
                String posName = imageF.getName();
                if(!posName.endsWith(".png")) continue;
                posName = posName.substring(0, posName.length() - 4);
                String[] spl = posName.split(";");
                if(spl.length != 3) continue;
                int x, y, z;
                try {
                    x = Integer.parseInt(spl[0]);
                    y = Integer.parseInt(spl[1]);
                    z = Integer.parseInt(spl[2]);
                } catch (NumberFormatException exc) {
                    continue;
                }
                BlockPos pos = new BlockPos(x, y, z);
                ResourceLocation textureAddress = addScreenshot(dimId, pos, imageF);
                Tuple<BlockPos, ResourceLocation> adress = new Tuple<>(pos, textureAddress);
                textures.add(adress);
            }
            clientScreenshots.put(dimId, textures);
        }
    }

    private static ResourceLocation addScreenshot(int dimId, BlockPos pos, File imageFile) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        ResourceLocation textureAddress = new ResourceLocation(AstralSorcery.MODID, dimId + ";;" + x + ";" + y + ";" + z);
        FileBasedTexture fbt = new FileBasedTexture(imageFile);
        Minecraft.getMinecraft().getTextureManager().loadTexture(textureAddress, fbt);
        return textureAddress;
    }

    @Nullable
    private static File getDirectoryForCurrentContext() {
        if(addrContext == null) return null; //Uh... unexpected call.
        File f = FileStorageUtil.getGeneralSubDirectory("gatewayScreenshots");
        f = new File(f, addrContext);
        if(!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    private static class FileBasedTexture extends AbstractTexture {

        private final File f;

        public FileBasedTexture(File f) {
            this.f = f;
        }

        @Override
        public void loadTexture(IResourceManager resourceManager) throws IOException {
            this.deleteGlTexture();

            BufferedImage image = TextureUtil.readBufferedImage(new FileInputStream(f));
            int pxWidth = image.getWidth();
            int pxHeight = image.getHeight();
            int nX = pxWidth / 5;
            int nH = pxHeight / 5;
            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image.getSubimage(nX, nH, pxWidth - nX, pxHeight - nH), true, false);
        }

    }

}
