# 数据库初始化

-- 创建库
create database if not exists woj;

-- 切换库
use woj;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;


-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    answer      text                               null comment '题目答案',
    submitNum   int      default 0                 not null comment '题目提交数',
    acceptedNum int      default 0                 not null comment '题目通过数',
    judgeCase   text                               null comment '判题用例（json 数组）',
    testJudgeCase   text                           null comment '测试判题用例（json 数组）',
    judgeConfig text                               null comment '判题配置（json 对象）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '用户代码',
    judgeInfo  text                               null comment '判题信息（json 对象）',
    status     int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目提交';


-- 题目运行表
create table if not exists question_run
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '用户代码',
    judgeInfo  text                               null comment '判题信息（json 对象）',
    judgeCase  text                               null comment '判题用例（json 数组）',
    status     int      default 0                 not null comment '判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）',
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目运行表';


-- 帖子表
create table if not exists post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    specialTags   varchar(1024)                   null comment '特殊标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    commentNum int      default 0                 not null comment '评论数',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '帖子' collate = utf8mb4_unicode_ci;

-- 帖子评论表（硬删除）
create table if not exists post_comment
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    toUserId   bigint                             null comment '回复用户 id',
    parentId   bigint                             null comment '父评论 id',
    content    text                               null comment '评论内容',
    thumbNum   int      default 0                 not null comment '点赞数',
    replyNum   int      default 0                 not null comment '回复数',
    status     int      default 0                 not null comment '评论状态（0 - 待审核、1 - 审核通过、2 - 审核不通过）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子评论';

-- 帖子评论点赞表（硬删除）
create table if not exists post_comment_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    commentId  bigint                             not null comment '帖子评论 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_commentId (commentId),
    index idx_userId (userId)
) comment '帖子评论点赞';


-- 帖子点赞表（硬删除）
create table if not exists post_thumb
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子点赞';

-- 帖子收藏表（硬删除）
create table if not exists post_favour
(
    id         bigint auto_increment comment 'id' primary key,
    postId     bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_postId (postId),
    index idx_userId (userId)
) comment '帖子收藏';

-- 标签表
create table if not exists tag
(
    id         bigint auto_increment comment 'id' primary key,
    name       varchar(256)                       not null comment '标签名称',
    belongType varchar(256)                       not null comment '标签所属类型（question/post）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '标签' collate = utf8mb4_unicode_ci;

-- 插入20条记录，填充编程和计算机主题的内容
INSERT INTO post (title, content, tags, thumbNum, favourNum, commentNum, userId, createTime, updateTime, isDelete)
VALUES ('学习新的编程语言', '今天开始学习一门新的编程语言，充满了挑战和好奇。', '[ "编程", "学习" ]', 15, 10, 5,
        1740270573453344770, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('开发一个实用的应用程序', '最近致力于开发一个实用的应用程序，希望能够解决实际问题。', '[ "开发", "应用程序" ]',
        20, 8, 12, 1740669921274191873, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('分享最新的技术趋势', '发现了一些关于人工智能和机器学习的最新技术趋势，感觉很激动。', '[ "技术", "趋势" ]', 30,
        15, 7, 1740270573453344770, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('遇到的编程难题', '在项目中遇到了一个复杂的编程难题，正在寻找解决方案。', '[ "编程", "难题" ]', 25, 12, 8,
        1740669921274191873, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('参加技术Meetup活动', '今晚参加了一场技术Meetup，与同行交流经验和见解。', '[ "技术", "Meetup" ]', 18, 9, 10,
        1740270573453344770, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('编写开源软件', '贡献了一些代码到开源项目，体验到了开源社区的活力。', '[ "开源", "软件" ]', 22, 14, 6,
        1740669921274191873, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('优化数据库性能', '花了一些时间优化数据库查询，提高了应用程序的性能。', '[ "数据库", "性能优化" ]', 28, 18, 4,
        1740270573453344770, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('学习新的框架', '尝试学习和应用新的编程框架，发现了提高效率的方法。', '[ "编程", "框架" ]', 19, 11, 9,
        1740669921274191873, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('解决Bug的心得分享', '分享了在项目中遇到并成功解决的一些Bug，希望能帮助到其他开发者。', '[ "Bug", "解决" ]', 26,
        16, 3, 1740270573453344770, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('编程书籍推荐', '推荐几本最近阅读的优秀编程书籍，分享学习资源。', '[ "编程", "书籍" ]', 23, 13, 7,
        1740669921274191873, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('参加技术Meetup活动', '今晚参加了一场技术Meetup，与同行交流经验和见解。', '[ "技术", "Meetup" ]', 18, 9, 10,
        1740270573453344770, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       ('编写开源软件', '贡献了一些代码到开源项目，体验到了开源社区的活力。', '[ "开源", "软件" ]', 22, 14, 6,
        1740669921274191873, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);


-- 插入帖子评论表数据
INSERT INTO post_comment (postId, userId,toUserId, parentId, content, thumbNum, status, createTime, updateTime)
VALUES (1, 1740270573453344770,1743170947961077761, NULL, '这是一条帖子评论', 10, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 1740669921274191873,1743170947961077761, 1, '回复楼主，支持一下', 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 1740270573453344770,1743170947961077761, NULL, '另一篇帖子的评论', 8, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 1740669921274191873,1743170947961077761, NULL, '这篇帖子写得很好', 15, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 1740270573453344770, 1743170947961077761,4, '同感，作者功力深厚', 7, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 1743170947961077761, 1743170947961077761,NULL, '刚刚注册，第一次评论', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 1740270573453344770,1743170947961077761, 6, '欢迎加入，一起交流学习', 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 1740669921274191873,1743170947961077761, NULL, '这个话题很有趣', 9, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 1740270573453344770,1743170947961077761, 8, '我也觉得很有意思，一起讨论', 6, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, 1743170947961077761,1743170947961077761, NULL, '感谢分享，对我的工作很有帮助', 12, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
