package com.solegendary.reignofnether.hud.custombutton;

import net.minecraft.commands.CacheableFunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.building.BuildingPlacement;

import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomButtonActions {
	
	public static final ResourceKey<Registry<com.mojang.serialization.MapCodec<? extends CustomButtonAction>>> BUTTON_ACTIONS = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "button_action"));
	
	public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends CustomButtonAction>> BUTTON_ACTION_CODECS = DeferredRegister.create(BUTTON_ACTIONS, ReignOfNether.MOD_ID);
	
	
	public static final net.minecraft.core.Registry<com.mojang.serialization.MapCodec<? extends CustomButtonAction>> CODEC_REGISTRY =
		BUTTON_ACTION_CODECS.makeRegistry(builder -> builder.defaultKey(CustomButtonActions.BUTTON_ACTIONS.location()));
	
	static {
		BUTTON_ACTION_CODECS.register("run_command", () -> RunCommandAction.CODEC);
		BUTTON_ACTION_CODECS.register("run_function", () -> RunFunctionAction.CODEC);
		BUTTON_ACTION_CODECS.register("experience", () -> ExperienceAction.CODEC);
		BUTTON_ACTION_CODECS.register("loot", () -> LootAction.CODEC);
	}
	
	public static void init(IEventBus bus) {
		BUTTON_ACTION_CODECS.register(bus);
	}
	
	public static Codec<CustomButtonAction> getCodec() {
		return CODEC_REGISTRY.byNameCodec().dispatch(CustomButtonAction::codec, Function.identity());
	}
	
	
	public interface CustomButtonAction {
		
		com.mojang.serialization.MapCodec<? extends CustomButtonAction> codec();
		
		void execute(Entity entity);
		
		void execute(BuildingPlacement building);
		
	}
	
	public record RunCommandAction(  String command) implements CustomButtonAction {
		public static final com.mojang.serialization.MapCodec<RunCommandAction> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
				Codec.STRING.fieldOf("command").forGetter(RunCommandAction::command)
			).apply(instance, RunCommandAction::new)
		);
		
		@Override
		public com.mojang.serialization.MapCodec<? extends CustomButtonAction> codec() {
			return CODEC;
		}
		
		@Override
		public void execute(Entity entity) {
			if (entity.getServer() != null)
				entity.getServer().getCommands().performPrefixedCommand(
					entity.createCommandSourceStack(), command
				);
		}
		
		@Override
		public void execute(BuildingPlacement building) {
			if (building.level instanceof ServerLevel level) {
				ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(building.ownerName);
				
				CommandSourceStack source;
				if (player != null) {
					source = player
						.createCommandSourceStack()
						.withPosition(building.minCorner.offset(-1, 0, -1).getCenter())
						.withLevel(level)
						.withSuppressedOutput()
						.withPermission(2)
						.withSource(player);
				} else {
					source = level.getServer()
						.createCommandSourceStack()
						.withPosition(building.minCorner.offset(-1, 0, -1).getCenter())
						.withLevel(level)
						.withPermission(2)
						.withSuppressedOutput();
				}
				level.getServer().getCommands().performPrefixedCommand(source, command);
			}
		}
	}
	
	public record RunFunctionAction(ResourceLocation namespace) implements CustomButtonAction {
		public static final com.mojang.serialization.MapCodec<RunFunctionAction> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("function").forGetter(RunFunctionAction::namespace)
			).apply(instance, RunFunctionAction::new)
		);
		
		@Override
		public com.mojang.serialization.MapCodec<? extends CustomButtonAction> codec() {
			return CODEC;
		}
		
		@Override
		public void execute(Entity entity) {
			net.minecraft.commands.CacheableFunction function = new net.minecraft.commands.CacheableFunction(namespace);
			MinecraftServer minecraftserver = entity.getServer();
			if (minecraftserver != null)
				function.get(minecraftserver.getFunctions()).ifPresent((p_289236_) -> minecraftserver.getFunctions().execute(p_289236_, entity.createCommandSourceStack().withSuppressedOutput().withPermission(2)));
		}
		
		@Override
		public void execute(BuildingPlacement building) {
			net.minecraft.commands.CacheableFunction function = new net.minecraft.commands.CacheableFunction(namespace);
			
			if (building.level instanceof ServerLevel level) {
				
				MinecraftServer minecraftserver = building.level.getServer();
				function.get(minecraftserver.getFunctions()).ifPresent(
					(p_289236_) -> {
						ServerPlayer player = level.getServer().getPlayerList().getPlayerByName(building.ownerName);
						
						CommandSourceStack source;
						if (player != null) {
							source = player
								.createCommandSourceStack()
								.withPosition(building.minCorner.offset(-1, 0, -1).getCenter())
								.withLevel(level)
								.withSuppressedOutput()
								.withPermission(2)
								.withSource(player);
						} else {
							source = level.getServer()
								.createCommandSourceStack()
								.withPosition(building.minCorner.offset(-1, 0, -1).getCenter())
								.withLevel(level)
								.withPermission(2)
								.withSuppressedOutput();
						}
						minecraftserver.getFunctions().execute(
							p_289236_,
							source
						);
					}
				);
			}
		}
	}
	
	public record ExperienceAction(int points, int level) implements CustomButtonAction {
		public static final com.mojang.serialization.MapCodec<ExperienceAction> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
				Codec.INT.optionalFieldOf("points", 0).forGetter(ExperienceAction::points),
				Codec.INT.optionalFieldOf("levels", 0).forGetter(ExperienceAction::level)
			).apply(instance, ExperienceAction::new)
		);
		
		@Override
		public com.mojang.serialization.MapCodec<? extends CustomButtonAction> codec() {
			return CODEC;
		}
		
		@Override
		public void execute(Entity entity) {
			if (entity instanceof ServerPlayer player) {
				player.giveExperiencePoints(points);
				player.giveExperienceLevels(level);
			} else {
				int points = this.points;
				while (points > 0) {
					int del = entity.level().random.nextInt(5) + 1;
					ExperienceOrb orb = new ExperienceOrb(entity.level(), entity.getX(), entity.getY(), entity.getZ(), del);
					entity.level().addFreshEntity(orb);
					points -= del;
				}
			}
		}
		
		@Override
		public void execute(BuildingPlacement building) {
			int points = this.points;
			while (points > 0) {
				int del = building.level.random.nextInt(5) + 1;
				ExperienceOrb orb = new ExperienceOrb(building.level, building.centrePos.getX(), building.centrePos.getY(), building.centrePos.getZ(), del);
				building.level.addFreshEntity(orb);
				points -= del;
			}
		}
	}
	
	public record LootAction(List<ResourceLocation> loots) implements CustomButtonAction {
		public static final com.mojang.serialization.MapCodec<LootAction> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
				ResourceLocation.CODEC.listOf().optionalFieldOf("loots", List.of()).forGetter(LootAction::loots)
			).apply(instance, LootAction::new)
		);
		
		@Override
		public com.mojang.serialization.MapCodec<? extends CustomButtonAction> codec() {
			return CODEC;
		}
		
		@Override
		public void execute(Entity entity) {
			if (entity.level() instanceof ServerLevel level) {
				LootParams lootparams = (new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, entity).withLuck((entity instanceof ServerPlayer player) ? player.getLuck() : 0.0f).withParameter(LootContextParams.ORIGIN, entity.position()).create(LootContextParamSets.CHEST));
				for (ResourceLocation resourcelocation : loots) {
					for (ItemStack itemstack : level.getServer().reloadableRegistries().getLootTable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, resourcelocation)).getRandomItems(lootparams)) {
						if (entity instanceof ServerPlayer player && player.addItem(itemstack.copy())) {
							ReignOfNether.LOGGER.info("give loots {}", itemstack);
							player.inventoryMenu.broadcastChanges();
							player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
						} else {
							ItemEntity itementity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), itemstack.copy());
							itementity.setNoPickUpDelay();
							level.addFreshEntity(itementity);
						}
					}
				}
			}
		}
		
		@Override
		public void execute(BuildingPlacement building) {
			if (building.level instanceof ServerLevel level) {
				LootParams lootparams = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, building.centrePos.getCenter()).withParameter(LootContextParams.ORIGIN, building.minCorner.getCenter()).create(LootContextParamSets.CHEST);
				for (ResourceLocation resourcelocation : loots) {
					for (ItemStack itemstack : level.getServer().reloadableRegistries().getLootTable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, resourcelocation)).getRandomItems(lootparams)) {
						ItemEntity itementity = new ItemEntity(level, building.centrePos.getX(), building.centrePos.getY(), building.centrePos.getZ(), itemstack);
						itementity.setNoPickUpDelay();
						level.addFreshEntity(itementity);
					}
				}
			}
		}
	}

//	public record ExplodeAction(List<ResourceLocation> loots) implements CustomButtonAction {
//		public static final ResourceLocation TYPE = new ResourceLocation(ReignOfNether.MOD_ID, "explode");
//		public static final MapCodec<ExplodeAction> CODEC = RecordCodecBuilder.mapCodec(
//			instance -> instance.group(
//				ResourceLocation.CODEC.listOf().optionalFieldOf("loots", List.of()).forGetter(ExplodeAction::loots)
//			).apply(instance, ExplodeAction::new)
//		);
//		
//		@Override
//		public MapCodec<? extends CustomButtonAction> codec() {
//			return CODEC;
//		}
//		
//		@Override
//		public void execute(ServerPlayer player) {
//			LootParams lootparams = (new LootParams.Builder(player.serverLevel())).withParameter(LootContextParams.THIS_ENTITY, player).withParameter(LootContextParams.ORIGIN, player.position()).withLuck(player.getLuck()).create(LootContextParamSets.SELECTOR);
//			for (ResourceLocation resourcelocation : loots) {
//				for (ItemStack itemstack : player.server.getLootData().getLootTable(resourcelocation).getRandomItems(lootparams)) {
//					if (player.addItem(itemstack)) {
//						player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
//					} else {
//						ItemEntity itementity = player.drop(itemstack, false);
//						if (itementity != null) {
//							itementity.setNoPickUpDelay();
//							itementity.setTarget(player.getUUID());
//						}
//					}
//				}
//			}
//		}
//	}
}
