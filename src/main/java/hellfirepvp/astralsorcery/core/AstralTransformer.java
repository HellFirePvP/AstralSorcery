/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.core;

import hellfirepvp.astralsorcery.core.transform.AstralPatchTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralTransformer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:56
 */
public class AstralTransformer implements IClassTransformer {

    private static List<SubClassTransformer> subTransformers = new LinkedList<>();

    public AstralTransformer() throws IOException {
        loadSubTransformers();
    }

    private void loadSubTransformers() throws IOException {
        subTransformers.add(new AstralPatchTransformer());
        //subTransformers.add(new TransformerSideStrippable());
    }

    private boolean isTransformationRequired(String trName) {
        for (SubClassTransformer transformer : subTransformers) {
            if(transformer.isTransformRequired(trName)) return true;
        }
        return false;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if(!isTransformationRequired(transformedName)) return bytes;

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        for (SubClassTransformer subTransformer : subTransformers) {
            try {
                subTransformer.transformClassNode(node, transformedName, name);
            } catch (ASMTransformationException asmException) {
                FMLLog.warning("Access transformation failed for Transformer: " + subTransformer.getIdentifier());
                FMLLog.warning("Transformer added information:");
                subTransformer.addErrorInformation();
                asmException.printStackTrace();
                throw asmException; //Rethrow
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        bytes = writer.toByteArray();

        if(false) {
            try {
                File f = new File("C:/ASTestClasses/" + transformedName + ".class");
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileOutputStream out = new FileOutputStream(f);
                out.write(bytes);
                out.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

}
