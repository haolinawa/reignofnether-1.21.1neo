package com.solegendary.reignofnether.registrars;

import net.minecraft.core.Holder;


import net.minecraft.core.registries.Registries;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.commands.rtsapi.argument.BuildingArgument;
import com.solegendary.reignofnether.commands.rtsapi.argument.PlayerNameArgument;
import com.solegendary.reignofnether.commands.rtsapi.argument.UnitArgument;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class CommandArgumentRegistrar {
	
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES =
		DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, ReignOfNether.MOD_ID);
	
	public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<BuildingArgument, ?>> BUILDING_ARGUMENT =
		COMMAND_ARGUMENT_TYPES.register(
			"building",
			BuildingArgument.Info::new
		);
	
	public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<PlayerNameArgument, ?>> PLAYER_NAME_ARGUMENT =
		COMMAND_ARGUMENT_TYPES.register(
			"player_name"
			, PlayerNameArgument.Info::new
		);
	
	public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<UnitArgument, ?>> UNIT_ARGUMENT =
		COMMAND_ARGUMENT_TYPES.register(
			"unit"
			, UnitArgument.Info::new
		);
	
	public static void init(IEventBus bus) {
		COMMAND_ARGUMENT_TYPES.register(bus);
	}
}
