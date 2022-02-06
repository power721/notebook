CREATE TABLE comment
(
    id           int AUTO_INCREMENT PRIMARY KEY,
    content      text     NOT NULL,
    created_time datetime NOT NULL,
    note_id      int      NOT NULL,
    user_id      int      NOT NULL,
    CONSTRAINT FK_COMMENT_NOTE_ID FOREIGN KEY (note_id) REFERENCES note (id),
    CONSTRAINT FK_COMMENT_USER_ID FOREIGN KEY (user_id) REFERENCES user (id)
);
