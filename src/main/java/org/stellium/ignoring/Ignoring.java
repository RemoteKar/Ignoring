package org.stellium.ignoring;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
public class Ignoring implements ModInitializer {
 public void onInitialize() {
  if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) new IgnoringClient().onInitialize();
  else org.slf4j.LoggerFactory.getLogger("Ignoring").info("Initialized (dedicated server: client features inactive)");
 }
}
