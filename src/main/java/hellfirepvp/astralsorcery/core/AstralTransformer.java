package hellfirepvp.astralsorcery.core;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralTransformer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:56
 */
public class AstralTransformer extends AccessTransformer {

    //public static final String WORLD = "net.minecraft.world.World";

    public AstralTransformer() throws IOException {
        System.out.println("[AstralTransformer] Initialized.");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        boolean needsTransform = false;
        if (!needsTransform) return super.transform(name, transformedName, bytes);

        FMLLog.info("[AstralTransformer] Transforming " + name + " : " + transformedName);

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        bytes = writer.toByteArray();
        return super.transform(name, transformedName, bytes);
    }

}
