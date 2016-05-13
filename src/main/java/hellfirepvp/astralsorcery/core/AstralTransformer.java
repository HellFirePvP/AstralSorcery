package hellfirepvp.astralsorcery.core;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralTransformer
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:56
 */
public class AstralTransformer extends AccessTransformer {

    public static final String WORLD = "net.minecraft.world.World";

    public AstralTransformer() throws IOException {
        System.out.println("[AstralTransformer] Initialized.");
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        boolean needsTransform = transformedName.equalsIgnoreCase(WORLD);
        if(!needsTransform) return super.transform(name, transformedName, bytes);

        FMLLog.info("[AstralTransformer] Transforming " + name + " : " + transformedName);

        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept(node, 0);

        if(transformedName.equalsIgnoreCase(WORLD)) {
            for (MethodNode mn : node.methods) {
                if(mn.name.equalsIgnoreCase("getSunBrightnessFactor")) { //TODO check srgs
                    mn.instructions.clear();
                    mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    mn.instructions.add(new VarInsnNode(Opcodes.FLOAD, 1));
                    mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "hellfirepvp/astralsorcery/common/event/EventHandlerRedirect",
                            "getWorldBrightness",
                            "(Lnet/minecraft/world/World;F)F",
                            false));
                    mn.instructions.add(new InsnNode(Opcodes.FRETURN));
                }
                if(mn.name.equalsIgnoreCase("getSunBrightnessBody")) { //TODO check srgs
                    mn.instructions.clear();
                    mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    mn.instructions.add(new VarInsnNode(Opcodes.FLOAD, 1));
                    mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                            "hellfirepvp/astralsorcery/common/event/EventHandlerRedirect",
                            "getClientWorldBrightness",
                            "(Lnet/minecraft/world/World;F)F",
                            false));
                    mn.instructions.add(new InsnNode(Opcodes.FRETURN));
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        bytes = writer.toByteArray();
        return super.transform(name, transformedName, bytes);
    }

}
