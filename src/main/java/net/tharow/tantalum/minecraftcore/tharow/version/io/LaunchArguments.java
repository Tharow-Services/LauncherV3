package net.tharow.tantalum.minecraftcore.tharow.version.io;

import net.tharow.tantalum.minecraftcore.tharow.version.io.argument.ArgumentList;

@SuppressWarnings({"unused"})
public class LaunchArguments {

	private ArgumentList game;
	private ArgumentList jvm;

	public ArgumentList getGameArgs() {
		return game;
	}

	public ArgumentList getJvmArgs() {
		return jvm;
	}

}
