package com.matyrobbrt.remotes.transform;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.INameMappingService;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Map;

public class StillValidTransformer implements ILaunchPluginService {
    private static final Logger LOG = LoggerFactory.getLogger(StillValidTransformer.class);

    private final String methodName = ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_6875_");

    @Override
    public String name() {
        return "remotes_stillValid";
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.BEFORE);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @Override
    public EnumSet<Phase> handlesClass(final Type classType, final boolean isEmpty) {
        return isEmpty ? NAY : YAY;
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason)
    {
        if ((classNode.access & Opcodes.ACC_ENUM) != 0 || (classNode.access & Opcodes.ACC_RECORD) != 0) // Records or enums can't extend other classes
            return ComputeFlags.NO_REWRITE;

        final MethodNode candidate = classNode.methods.stream()
                .filter(m -> m.name.equals(methodName) && m.desc.equals("(Lnet/minecraft/world/entity/player/Player;)Z"))
                .findFirst().orElse(null);
        if (candidate == null || (candidate.access & Opcodes.ACC_ABSTRACT) != 0) { // Can't modify abstract methods
            return ComputeFlags.NO_REWRITE;
        }

        final LabelNode label = new LabelNode();
        // The check actually is "if (((BlockRemotesPlayer) pPlayer).remotes$isRemoteOpened())",
        // but we can remove the checkcast and actually invoke on Player for better performance
        final InsnList instructions = new InsnList();
        instructions.add(new IntInsnNode(Opcodes.ALOAD, 1)); // Load the player
        instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/player/Player", "remotes$isRemoteOpened", "()Z", false)); // We don't invoke on the interface, but on the Player class
        instructions.add(new JumpInsnNode(Opcodes.IFEQ, label));
        instructions.add(new InsnNode(Opcodes.ICONST_1));
        instructions.add(new InsnNode(Opcodes.IRETURN));
        instructions.add(label);

        candidate.instructions.insert(instructions);

        LOG.debug("Transforming class {}: adding stillValid remote check.", classType);

        return ComputeFlags.COMPUTE_FRAMES;
    }

    @SuppressWarnings("unchecked")
    public static void inject() throws Throwable {
        final LaunchPluginHandler handler = (LaunchPluginHandler) Reflections.HANDLE.findVarHandle(Launcher.class, "launchPlugins", LaunchPluginHandler.class)
                .get(Launcher.INSTANCE);
        final Map<String, ILaunchPluginService> plugins = (Map<String, ILaunchPluginService>) Reflections.HANDLE.findVarHandle(LaunchPluginHandler.class, "plugins", Map.class)
                .get(handler);
        final StillValidTransformer transformer = new StillValidTransformer();
        plugins.put(transformer.name(), transformer);
    }
}
