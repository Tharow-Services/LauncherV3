/*
 * This file is part of Technic Minecraft Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Minecraft Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Minecraft Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Minecraft Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.minecraftcore.mojang.auth.request;

import net.tharow.tantalum.minecraftcore.mojang.auth.response.Response;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class RefreshRequest extends Response {
	private String accessToken;
	private String clientToken;

	public RefreshRequest() {

	}

	public RefreshRequest(String accessToken, String clientToken) {
		this.accessToken = accessToken;
		this.clientToken = clientToken;
	}
}
