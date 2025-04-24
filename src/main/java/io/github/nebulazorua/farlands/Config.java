package io.github.nebulazorua.farlands;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Farlands.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	private static final ForgeConfigSpec.IntValue FARLAND_DISTANCE_X = BUILDER
			.comment("The distance to the Farlands on the X axis")
			.defineInRange("xDistance", 12550800, 0, Integer.MAX_VALUE);

	private static final ForgeConfigSpec.IntValue FARLAND_DISTANCE_Z = BUILDER
			.comment("The distance to the Farlands on the Z axis")
			.defineInRange("zDistance", 12550800, 0, Integer.MAX_VALUE);

	static final ForgeConfigSpec SPEC = BUILDER.build();

	public static int xDistance;
	public static int zDistance;

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		xDistance = FARLAND_DISTANCE_X.get();
		zDistance = FARLAND_DISTANCE_X.get();

	}
}
