package net.tharow.tantalum.platform.io;

import java.util.Date;

public interface INewsArticle {
    int getId();

    String getUsername();

    String getAvatar();

    String getTitle();

    String getContent();

    Date getDate();

    AuthorshipInfo getAuthorshipInfo();

    String getUrl();
}
