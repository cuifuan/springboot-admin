CREATE TABLE IF NOT EXISTS `admin_user`
(
    `user_id`    bigint                                  NOT NULL AUTO_INCREMENT COMMENT '自增 ID',
    `username`   varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
    `user_phone` varchar(255) COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '手机号',
    `password`   varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
    `avatar`     varchar(255) COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '头像',
    `is_delete`  int                                     NOT NULL DEFAULT '0' COMMENT '逻辑删除 0-未删除 1-已删除',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

INSERT INTO `admin_user` (username, user_phone, `password`, avatar)
values ('root', '', '$2a$10$a.6pvIt48KwZ.Zu/Sz.wruTK10WhIx8WK1iglIE1ARVHuJYcBlfYm', ''),
       ('user', '', '$2a$10$a.6pvIt48KwZ.Zu/Sz.wruTK10WhIx8WK1iglIE1ARVHuJYcBlfYm', '');