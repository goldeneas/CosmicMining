CREATE TABLE IF NOT EXISTS player_levels
(
    -- A uuid has 36 characters.
    -- That's why we define our uuid column with a size of 36.
    -- We also say that this column should never be null.
    uuid  CHAR(36)         NOT NULL,
    -- We create a coin column with a bigint. This is equal to a long in java.
    -- We also say that this column should never be null.
    -- If you just insert a new uuid into this table the coin column will be 0 by default.
    levels INT DEFAULT 0 NOT NULL,
    -- we create a primary key "coins_pk" on the uuid column.
    -- This means that a value in the uuid column can be only one time in the column.
    PRIMARY KEY (uuid)
);