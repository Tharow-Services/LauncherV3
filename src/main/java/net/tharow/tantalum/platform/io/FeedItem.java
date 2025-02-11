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
import java.util.Locale;

import static net.tharow.tantalum.utilslib.MD5Utils.getMD5;

@SuppressWarnings({"unused"})
public class FeedItem {
    private String user;
    private long date;
    private String content;
    private String avatar;
    private String url;

    public FeedItem() {
    }

    public String getUser() {
        return user!=null?user:TantalumConstants.SYSTEM_USERNAME;
    }

    public Date getDate() {
        return new Date(date * 1000);
    }

    public String getContent() {
        return content;
    }

    public String getAvatar() {
        if (this.avatar==null || this.avatar.isEmpty()){
            return "https://www.gravatar.com/avatar/" + getMD5(this.user.toLowerCase(Locale.ROOT)) + "?d=identicon&s=2048";
        }
        return this.avatar;
    }

    public String getUrl() {
        return url;
    }

    public AuthorshipInfo getAuthorship() {
        return new AuthorshipInfo(getUser(), getAvatar(), getDate());
    }
}
