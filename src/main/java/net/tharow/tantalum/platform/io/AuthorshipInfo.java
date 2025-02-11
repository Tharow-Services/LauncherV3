/**
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.platform.io;

import net.tharow.tantalum.launchercore.TantalumConstants;

import java.util.Date;

@SuppressWarnings({"unused"})
public class AuthorshipInfo {
    private String user;
    private String avatar;
    private Date date;

    public AuthorshipInfo() {
    }

    public AuthorshipInfo(String user, String avatar, Date date) {
        this.user = user;
        this.avatar = avatar;
        this.date = date;
    }

    public String getUser() {
        return user!=null?user:TantalumConstants.SYSTEM_USERNAME;
    }

    public String getAvatar() {
        return this.avatar!=null?this.avatar:TantalumConstants.AVATAR_URL+this.getUser()+".png";
    }

    public Date getDate() {
        return date;
    }
}
