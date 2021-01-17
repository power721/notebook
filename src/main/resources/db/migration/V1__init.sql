CREATE TABLE IF NOT EXISTS category
(
    id           int AUTO_INCREMENT
        PRIMARY KEY,
    created_time datetime     NOT NULL,
    description  varchar(255) NOT NULL,
    name         varchar(255) NOT NULL,
    CONSTRAINT UK_CATEGORY_NAME
        UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS config
(
    name  varchar(32) NOT NULL
        PRIMARY KEY,
    type  varchar(10) NOT NULL,
    value text        NOT NULL
);

CREATE TABLE IF NOT EXISTS menu
(
    id     int AUTO_INCREMENT
        PRIMARY KEY,
    admin  bit          NOT NULL,
    auth   bit          NOT NULL,
    icon   varchar(255) NOT NULL,
    orders int          NOT NULL,
    parent int          NOT NULL,
    title  varchar(255) NOT NULL,
    uri    varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS tag
(
    id   int AUTO_INCREMENT
        PRIMARY KEY,
    name varchar(32) NOT NULL,
    CONSTRAINT UK_tag_name
        UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS user
(
    id           int AUTO_INCREMENT
        PRIMARY KEY,
    created_time datetime     NOT NULL,
    email        varchar(255) NULL,
    password     varchar(255) NOT NULL,
    role         varchar(16)  NOT NULL,
    username     varchar(32)  NOT NULL,
    avatar       varchar(255) NULL,
    CONSTRAINT UK_USER_EMAIL
        UNIQUE (email),
    CONSTRAINT UK_USER_USERNAME
        UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS audit
(
    id           int AUTO_INCREMENT
        PRIMARY KEY,
    client_ip    varchar(40)  NULL,
    content      text         NOT NULL,
    created_time datetime     NOT NULL,
    user_agent   varchar(255) NULL,
    referer      varchar(255) NULL,
    type         varchar(255) NULL,
    operator_id  int          NULL,
    target       int          NOT NULL,
    CONSTRAINT FK_AUDIT_OPERATOR_USER_ID
        FOREIGN KEY (operator_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS notebook
(
    id           int AUTO_INCREMENT
        PRIMARY KEY,
    created_time datetime     NOT NULL,
    description  varchar(255) NOT NULL,
    name         varchar(255) NOT NULL,
    updated_time datetime     NULL,
    owner_id     int          NULL,
    access       varchar(16)  NOT NULL,
    CONSTRAINT FK_OWNER_ID
        FOREIGN KEY (owner_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS note
(
    id           int AUTO_INCREMENT
        PRIMARY KEY,
    access       varchar(16)  NOT NULL,
    created_time datetime     NOT NULL,
    deleted      bit          NOT NULL,
    rid          varchar(255) NOT NULL,
    updated_time datetime     NULL,
    views        int          NOT NULL,
    author_id    int          NULL,
    category_id  int          NULL,
    content_id   int          NULL,
    notebook_id  int          NULL,
    slug         varchar(255) NULL,
    CONSTRAINT UK_NOTE_SLUG
        UNIQUE (slug),
    CONSTRAINT UK_NOTE_RID
        UNIQUE (rid),
    CONSTRAINT FK_AUTHOR_ID
        FOREIGN KEY (author_id) REFERENCES user (id),
    CONSTRAINT FK_CATEGORY_ID
        FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT FK_NOTEBOOK_ID
        FOREIGN KEY (notebook_id) REFERENCES notebook (id)
);

CREATE TABLE IF NOT EXISTS note_content
(
    id           int AUTO_INCREMENT
        PRIMARY KEY,
    content      longtext     NOT NULL,
    created_time datetime     NOT NULL,
    markdown     bit          NOT NULL,
    title        varchar(255) NOT NULL,
    version      int          NOT NULL,
    note_id      int          NULL,
    CONSTRAINT FK_CONTENT_NOTE_ID
        FOREIGN KEY (note_id) REFERENCES note (id)
);

ALTER TABLE note
    ADD CONSTRAINT FK_CONTENT_ID
        FOREIGN KEY (content_id) REFERENCES note_content (id);

CREATE TABLE IF NOT EXISTS note_tags
(
    note_id int NOT NULL,
    tag_id  int NOT NULL,
    CONSTRAINT FK_NOTE_ID
        FOREIGN KEY (note_id) REFERENCES note (id),
    CONSTRAINT FK_TAG_ID
        FOREIGN KEY (tag_id) REFERENCES tag (id)
);

### data ###
INSERT INTO category (name, description, created_time) VALUES ('默认分类', '', NOW());

INSERT INTO menu (admin, auth, icon, orders, parent, title, uri) VALUES (false, false, 'home', 1, 0, '笔记', '/notes');
INSERT INTO menu (admin, auth, icon, orders, parent, title, uri) VALUES (false, false, 'book', 2, 0, '笔记本', '/notebooks');
INSERT INTO menu (admin, auth, icon, orders, parent, title, uri) VALUES (false, false, 'idea', 3, 0, '分类', '/categories');
INSERT INTO menu (admin, auth, icon, orders, parent, title, uri) VALUES (false, false, 'tag',  4, 0, '标签', '/tags');
INSERT INTO menu (admin, auth, icon, orders, parent, title, uri) VALUES (false, false, 'info', 5, 0, '关于', '/about');
