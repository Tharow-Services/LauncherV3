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

import java.util.Date;

@SuppressWarnings({"unused"})
public class NewsArticle implements INewsArticle{
    private int id;
    private String username;
    private String avatar;
    private String title;
    private String content;
    private long date;
    private String url;

    public NewsArticle() {

    }

    public NewsArticle(int id, String username, String avatar, String title, String content, long date, String url) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.title = title;
        this.content = content;
        this.date = date;
        this.url = url;

    }

    public NewsArticle(INewsArticle newsArticle){
        this.id = newsArticle.getId();
        this.username = newsArticle.getUsername();
        this.avatar = newsArticle.getAvatar();
        this.title = newsArticle.getTitle();
        this.content = newsArticle.getContent();
        this.date = newsArticle.getDate().getTime();
        this.url = newsArticle.getUrl();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getAvatar() {
        return this.avatar;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public Date getDate() {
        return new Date(this.date * 1000);
    }

    @Override
    public AuthorshipInfo getAuthorshipInfo() {
        return new AuthorshipInfo(this.getUsername(), this.getAvatar(), this.getDate());
    }

    @Override
    public String getUrl() {
        return this.url;
    }
}
