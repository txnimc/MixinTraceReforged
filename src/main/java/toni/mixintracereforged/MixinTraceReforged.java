package toni.mixintracereforged;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

#if FORGE
import net.minecraftforge.fml.common.Mod;
#endif

#if NEO
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
#endif


@Mod("mixintracereforged")
public class MixinTraceReforged
{
    public static final String MODNAME = "Mixin Trace Reforged";
    public static final String ID = "mixintracereforged";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public MixinTraceReforged(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) { }

    public static void printTrace(StackTraceElement[] stackTrace, StringBuilder crashReportBuilder) {
        if (stackTrace != null && stackTrace.length > 0) {
            crashReportBuilder.append("\n[MixinTraceReforged] Mixins in Stack Trace:");

            try {
                List<String> classNames = new ArrayList<>();
                for (StackTraceElement el : stackTrace) {
                    if (!classNames.contains(el.getClassName())) classNames.add(el.getClassName());
                }

                boolean found = false;
                for (String className : classNames) {
                    ClassInfo classInfo = ClassInfo.fromCache(className);
                    if (classInfo != null) {
                        Object mixinInfoSetObject;
                        try {
                            Method getMixins = ClassInfo.class.getDeclaredMethod("getMixins");
                            getMixins.setAccessible(true);
                            mixinInfoSetObject = getMixins.invoke(classInfo);
                        } catch (Exception e) {
                            Field mixinsField = ClassInfo.class.getDeclaredField("mixins");
                            mixinsField.setAccessible(true);
                            mixinInfoSetObject = mixinsField.get(classInfo);
                        }

                        @SuppressWarnings("unchecked")
                        Set<IMixinInfo> mixinInfoSet = (Set<IMixinInfo>) mixinInfoSetObject;

                        if (mixinInfoSet.size() > 0) {
                            crashReportBuilder.append("\n\t");
                            crashReportBuilder.append(className);
                            crashReportBuilder.append(":");
                            for (IMixinInfo info : mixinInfoSet) {
                                crashReportBuilder.append("\n\t\t");
                                crashReportBuilder.append(info.getClassName());
                                crashReportBuilder.append(" (");
                                crashReportBuilder.append(info.getConfig().getName());
                                crashReportBuilder.append(")");
                            }
                            found = true;
                        }
                    }
                }
                if (!found) crashReportBuilder.append(" None found");
            } catch (Exception e) {
                crashReportBuilder.append(" Failed to find Mixin metadata: ").append(e);
            }
        }
    }
}
