function initializeCoreMod() {
    return {
        'client_disconnect': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.Minecraft',
                'methodName': 'func_213231_b',
                'methodDesc': '(Lnet/minecraft/client/gui/screen/Screen;)V'
            },
            'transformer': function(method) {
                print('[AstralSorcery] Adding \'client_disconnect\' ASM patch...');

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var putWorld = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, 0);

                while (putWorld !== null) {
                    if (putWorld.name === 'field_71441_e' || putWorld.name === 'world') {
                        var prev = putWorld.getPrevious();

                        method.instructions.insert(prev, ASMAPI.buildMethodCall(
                            'hellfirepvp/astralsorcery/common/util/ASMHookEndpoint',
                            'onClientDisconnect',
                            '()V',
                            ASMAPI.MethodType.STATIC));
                    }
                    putWorld = ASMAPI.findFirstInstructionAfter(method, Opcodes.PUTFIELD, method.instructions.indexOf(putWorld) + 2);
                }

                print('[AstralSorcery] Added \'client_disconnect\' ASM patch!');
                return method;
            }
        }
    }
}