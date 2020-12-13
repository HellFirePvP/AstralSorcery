function initializeCoreMod() {
    return {
        'render_particles': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.particle.ParticleManager',
                'methodName': 'renderParticles',
                'methodDesc': '(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/renderer/ActiveRenderInfo;FLnet/minecraft/client/renderer/culling/ClippingHelper;)V'
            },
            'transformer': function(method) {
                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                ASMAPI.log('INFO', 'Adding \'render_particles\' ASM patch...');

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var ret = ASMAPI.findFirstInstructionAfter(method, Opcodes.RETURN, 0);

                while (ret !== null) {
                    method.instructions.insertBefore(ret, new VarInsnNode(Opcodes.ALOAD, 1)); //MatrixStack
                    method.instructions.insertBefore(ret, new VarInsnNode(Opcodes.FLOAD, 5)); //partialTicks
                    method.instructions.insertBefore(ret, ASMAPI.buildMethodCall(
                        'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                        'renderParticles',
                        "(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V",
                        ASMAPI.MethodType.STATIC));

                    ret = ASMAPI.findFirstInstructionAfter(method, Opcodes.RETURN, method.instructions.indexOf(ret) + 3);
                }

                ASMAPI.log('INFO', 'Added \'render_particles\' ASM patch!');
                return method;
            }
        }
    }
}