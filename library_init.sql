
/*
 * Copyright © 2021 Anthony DePaul
 * Licensed under the MIT License https://ajdepaul.mit-license.org/
 */
CREATE DATABASE tagged_music;
USE tagged_music;

CREATE TABLE Library (
    version VARCHAR(255) NOT NULL PRIMARY KEY
);
INSERT INTO Library(version) VALUES ('1.0');

CREATE TABLE Users (
    user_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    pass_hash VARCHAR(255) NOT NULL
);

CREATE TABLE Songs (
    user_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    duration INT NOT NULL,
    track_num INT NULL,
    release_date DATETIME(3) NULL,
    create_date DATETIME(3) NOT NULL,
    modify_date DATETIME(3) NOT NULL,
    play_count INT NOT NULL,
    PRIMARY KEY(user_id, file_name),
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE TagTypes (
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    color INT NOT NULL,
    PRIMARY KEY(user_id, name),
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- if a tag type is deleted, tags that refer to that tag type should have their type set to null separately
CREATE TABLE Tags (
    user_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NULL,
    description TEXT NULL,
    PRIMARY KEY(user_id, name),
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id, type) REFERENCES TagTypes(user_id, name) ON DELETE NO ACTION
);

CREATE TABLE SongHasTag (
    user_id INT NOT NULL,
    song_file VARCHAR(255) NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY(user_id, song_file, tag),
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id, tag) REFERENCES Tags(user_id, name) ON DELETE CASCADE,
    FOREIGN KEY(user_id, song_file) REFERENCES Songs(user_id, file_name) ON DELETE CASCADE
);

CREATE TABLE Data (
    user_id INT NOT NULL,
    k VARCHAR(255) NOT NULL,
    v VARCHAR(255) NOT NULL,
    PRIMARY KEY(user_id, k),
    FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

/* ------------------------------------ Retrieving Procedures ----------------------------------- */

DELIMITER &&

-- Result: the version of the library.
CREATE PROCEDURE Library_get_version() BEGIN
    SELECT version FROM Library;
END &&

-- Result: the `file_name` song.
CREATE PROCEDURE Songs_select(arg_user_id INT, arg_file_name VARCHAR(255)) BEGIN
    SELECT * FROM Songs WHERE user_id = arg_user_id AND file_name = arg_file_name;
END &&

-- Result: all the songs.
CREATE PROCEDURE Songs_select_all(arg_user_id INT) BEGIN
    SELECT * FROM Songs WHERE user_id = arg_user_id;
END &&

-- Result: the `name` tag.
CREATE PROCEDURE Tags_select(arg_user_id INT, arg_name VARCHAR(255)) BEGIN
    SELECT * FROM Tags WHERE user_id = arg_user_id AND name = arg_name;
END &&

-- Result: all the tags.
CREATE PROCEDURE Tags_select_all(arg_user_id INT) BEGIN
    SELECT * FROM Tags WHERE user_id = arg_user_id;
END &&

-- Result: the default tag type.
CREATE PROCEDURE TagTypes_get_default(arg_user_id INT) BEGIN
    SELECT * FROM TagTypes WHERE user_id = arg_user_id AND name = '';
END &&

-- Result: the `name` tag type.
CREATE PROCEDURE TagTypes_select(arg_user_id INT, arg_name VARCHAR(255)) BEGIN
    SELECT * FROM TagTypes WHERE user_id = arg_user_id AND name = arg_name;
END &&

-- Result: all the tag types.
CREATE PROCEDURE TagTypes_select_all(arg_user_id INT) BEGIN
    SELECT * FROM TagTypes WHERE user_id = arg_user_id AND name <> '';
END &&

-- Result: all the tags that `file_name` song has.
CREATE PROCEDURE SongHasTag_select_song_tags(arg_user_id INT, arg_song_file VARCHAR(255)) BEGIN
    SELECT * FROM SongHasTag WHERE user_id = arg_user_id AND song_file = arg_song_file;
END &&

-- Result: all song has tags relationships.
CREATE PROCEDURE SongHasTag_select_all(arg_user_id INT) BEGIN
    SELECT * FROM SongHasTag WHERE user_id = arg_user_id;
END &&

-- Result: the `k` data entry.
CREATE PROCEDURE Data_select(arg_user_id INT, arg_k VARCHAR(255)) BEGIN
    SELECT * FROM Data where user_id = arg_user_id AND k = arg_k;
END &&

-- Result: all the data entries.
CREATE PROCEDURE Data_select_all(arg_user_id INT) BEGIN
    SELECT * FROM Data WHERE user_id = arg_user_id;
END &&

/* ------------------------------------- Updating Procedures ------------------------------------ */

-- Inserts/updates a song.
CREATE PROCEDURE Songs_put(
    arg_user_id INT,
    arg_file_name VARCHAR(255),
    arg_title VARCHAR(255),
    arg_duration INT,
    arg_track_num INT,
    arg_release_date DATETIME(3),
    arg_create_date DATETIME(3),
    arg_modify_date DATETIME(3),
    arg_play_count INT
) BEGIN
    INSERT INTO
        Songs(
            user_id,
            file_name,
            title,
            duration,
            track_num,
            release_date,
            create_date,
            modify_date,
            play_count
        )
    VALUES (
        arg_user_id,
        arg_file_name,
        arg_title,
        arg_duration,
        arg_track_num,
        arg_release_date,
        arg_create_date,
        arg_modify_date,
        arg_play_count
    )
    ON DUPLICATE KEY UPDATE
        user_id = arg_user_id,
        title = arg_title,
        duration = arg_duration,
        track_num = arg_track_num,
        release_date = arg_release_date,
        create_date = arg_create_date,
        modify_date = arg_modify_date,
        play_count = arg_play_count;
END &&

-- Removes a song.
CREATE PROCEDURE Songs_remove(arg_user_id INT, arg_file_name VARCHAR(255)) BEGIN
    DELETE FROM Songs WHERE user_id = arg_user_id AND file_name = arg_file_name;
END &&

-- Inserts/updates a tag.
CREATE PROCEDURE Tags_put(arg_user_id INT, arg_name VARCHAR(255), arg_type VARCHAR(255), arg_description TEXT) BEGIN
    INSERT INTO Tags(user_id, name, type, description)
    VALUES (arg_user_id, arg_name, arg_type, arg_description)
    ON DUPLICATE KEY UPDATE type = arg_type, description = arg_description;
END &&

-- Removes a tag.
CREATE PROCEDURE Tags_remove(arg_user_id INT, arg_name VARCHAR(255)) BEGIN
    DELETE FROM Tags WHERE user_id = arg_user_id AND name = arg_name;
END &&

-- Inserts/updates a tag type.
CREATE PROCEDURE TagTypes_put(arg_user_id INT, arg_name VARCHAR(255), arg_color INT) BEGIN
    INSERT INTO TagTypes(user_id, name, color)
    VALUES (arg_user_id, arg_name, arg_color)
    ON DUPLICATE KEY UPDATE color = arg_color;
END &&

-- Removes a tag type.
CREATE PROCEDURE TagTypes_remove(arg_user_id INT, arg_name VARCHAR(255)) BEGIN
    UPDATE Tags SET type = NULL WHERE type = arg_name;
    DELETE FROM TagTypes WHERE user_id = arg_user_id AND name = arg_name;
END &&

-- Inserts a new song has tag relationship.
CREATE PROCEDURE SongHasTag_put(arg_user_id INT, arg_song_file VARCHAR(255), arg_tag VARCHAR(255)) BEGIN
    INSERT INTO SongHasTag(user_id, song_file, tag) VALUES (arg_user_id, arg_song_file, arg_tag);
END &&

-- Removes a song has tag relationship.
CREATE PROCEDURE SongHasTag_remove(arg_user_id INT, arg_song_file VARCHAR(255), arg_tag VARCHAR(255)) BEGIN
    DELETE FROM SongHasTag WHERE user_id = arg_user_id AND song_file = arg_song_file AND tag = arg_tag;
END &&

-- Removes all song has tag relationships for a song.
CREATE PROCEDURE SongHasTag_remove_all_for_song(arg_user_id INT, arg_song_file VARCHAR(255)) BEGIN
    DELETE FROM SongHasTag WHERE user_id = arg_user_id AND song_file = arg_song_file;
END &&

-- Inserts/updates a data entry.
CREATE PROCEDURE Data_put(arg_user_id INT, arg_k VARCHAR(255), arg_v VARCHAR(255)) BEGIN
    INSERT INTO Data(user_id, k, v)
    VALUES (arg_user_id, arg_k, arg_v)
    ON DUPLICATE KEY UPDATE v = arg_v;
END &&

-- Removes a data entry.
CREATE PROCEDURE Data_remove(arg_user_id INT, arg_k VARCHAR(255)) BEGIN
    DELETE FROM Data WHERE user_id = arg_user_id AND k = arg_k;
END &&

/* --------------------------------------- User Procedures -------------------------------------- */

-- Result: the user's id, username, and password.
CREATE PROCEDURE Users_select_id(arg_user_id INT) BEGIN
    SELECT * FROM Users WHERE user_id = arg_user_id;
END &&

-- Result: the user's id, username, and password.
CREATE PROCEDURE Users_select_username(arg_username VARCHAR(255)) BEGIN
    SELECT * FROM Users WHERE username = arg_username;
END &&

-- Result: every users' id, username, and password.
CREATE PROCEDURE Users_select_all() BEGIN
    SELECT * FROM Users;
END &&

-- Inserts a new user.
CREATE PROCEDURE Users_add(arg_username VARCHAR(255), arg_pass_hash VARCHAR(255), arg_default_tag_type_color INT) BEGIN
    INSERT INTO Users(username, pass_hash)
    VALUES (arg_username, pass_hash);

    INSERT INTO TagTypes(user_id, name, color)
    SELECT user_id, '' as name, arg_default_tag_type_color as color FROM Users WHERE username = arg_username;
END &&

-- Updates a user's username.
CREATE PROCEDURE Users_update_username(arg_user_id INT, new_username VARCHAR(255)) BEGIN
    UPDATE Users SET username = new_username WHERE user_id = arg_user_id;
END &&

-- Updates a user's password.
CREATE PROCEDURE Users_update_password(arg_user_id INT, arg_pass_hash VARCHAR(255)) BEGIN
    UPDATE Users SET pass_hash = arg_pass_hash WHERE user_id = arg_user_id;
END &&

-- Removes all user data from every table.
CREATE PROCEDURE Users_remove(arg_user_id INT) BEGIN
    DELETE FROM Data WHERE user_id = arg_user_id;
    DELETE FROM SongHasTag WHERE user_id = arg_user_id;
    DELETE FROM Tags WHERE user_id = arg_user_id;
    DELETE FROM TagTypes WHERE user_id = arg_user_id;
    DELETE FROM Songs WHERE user_id = arg_user_id;
    DELETE FROM Users WHERE user_id = arg_user_id;
END &&

DELIMITER ;
