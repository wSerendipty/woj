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
    userProfile  varchar(512) default '此简介为默认简介，设置简介让大家更好的认识你...'                         null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;


-- 题目表
create table if not exists question
(
    id            bigint auto_increment comment 'id' primary key,
    title         varchar(512)                       null comment '标题',
    content       text                               null comment '内容',
    tags          varchar(1024)                      null comment '标签列表（json 数组）',
    answer        text                               null comment '题目答案',
    submitNum     int      default 0                 not null comment '题目提交数',
    acceptedNum   int      default 0                 not null comment '题目通过数',
    judgeCase     text                               null comment '判题用例（json 数组）',
    testJudgeCase text                               null comment '测试判题用例（json 数组）',
    judgeConfig   text                               null comment '判题配置（json 对象）',
    thumbNum      int      default 0                 not null comment '点赞数',
    favourNum     int      default 0                 not null comment '收藏数',
    solutionNum  int      default 0                 not null comment '题解数',
    difficulty    varchar(100)                       not null comment '题目难度（简单、中等、困难）',
    userId        bigint                             not null comment '创建用户 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on
        update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;

-- 题目模板表
create table if not exists question_template
(
    id         bigint auto_increment comment 'id' primary key,
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '模板代码',
    userId     bigint                             not null comment '创建用户 id',
    questionId bigint                             not null comment '题目 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目提交';

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

-- 题解表
create table if not exists solution
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    specialTags varchar(1024)                      null comment '特殊标签列表（json 数组）',
    questionId  bigint							   not null comment '题目 id',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目题解' collate = utf8mb4_unicode_ci;

-- 题目完成表
create table if not exists question_status
(
    id          bigint auto_increment comment 'id' primary key,
    type        varchar(50) default 'normal'       not null comment '题目类型 normal、daily',
    status      int      default 0                 not null comment '题目状态（0 - 未开始、1 - 通过、2 - 尝试过）',
    questionId  bigint							   not null comment '题目id',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目状态表';


-- 帖子表
create table if not exists post
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    specialTags varchar(1024)                      null comment '特殊标签列表（json 数组）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    commentNum  int      default 0                 not null comment '评论数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
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


-- 每日表
create table if not exists daily
(
    id         bigint auto_increment comment 'id' primary key,
    questionId bigint                             null comment '题目id',
    postId     bigint                             null comment '帖子id',
    belongType varchar(256)                       not null comment '所属类型（question/post）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '每日' collate = utf8mb4_unicode_ci;


-- 竞赛表
create table if not exists contest
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    startTime  datetime                           not null comment '开始时间',
    endTime    datetime                           not null comment '结束时间',
    duration   int                                not null comment '持续时间',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '竞赛' collate = utf8mb4_unicode_ci;

-- 竞赛报名表
create table if not exists join
(
    id         bigint auto_increment comment 'id' primary key,
    contestId  bigint                             not null comment '竞赛id',
    userId     bigint                             not null comment '报名用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (contestId)
) comment '报名' collate = utf8mb4_unicode_ci;

-- 竞赛题目表
create table if not exists contest_question
(
    id         bigint auto_increment comment 'id' primary key,
    contestId  bigint                             not null comment '竞赛id',
    questionId bigint                             not null comment '题目 id',
    difficulty varchar(255)                       not null comment '题目难度',
    point      int      default 0                 not null comment '题目分值',
    passNum    int      default 0                 not null comment '用户通过数',
    tryNum     int      default 0                 not null comment '用户尝试数',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (contestId)
) comment '竞赛题目' collate = utf8mb4_unicode_ci;

-- 竞赛提交表
create table if not exists contest_submit
(
    id           bigint auto_increment comment 'id' primary key,
    contestId    bigint                             not null comment '竞赛id',
    questionId   bigint                             not null comment '题目 id',
    userId       bigint                             not null comment '提交用户 id',
    score        int      default 0                 not null comment '获得的分值',
    completeTime int      default 0                 not null comment '题目完成时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    index idx_userId (contestId)
) comment '竞赛提交' collate = utf8mb4_unicode_ci;

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
INSERT INTO post_comment (postId, userId, toUserId, parentId, content, thumbNum, status, createTime, updateTime)
VALUES (1, 1740270573453344770, 1743170947961077761, NULL, '这是一条帖子评论', 10, 1, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (1, 1740669921274191873, 1743170947961077761, 1, '回复楼主，支持一下', 5, 1, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (1, 1740270573453344770, 1743170947961077761, NULL, '另一篇帖子的评论', 8, 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (2, 1740669921274191873, 1743170947961077761, NULL, '这篇帖子写得很好', 15, 2, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (2, 1740270573453344770, 1743170947961077761, 4, '同感，作者功力深厚', 7, 1, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (3, 1743170947961077761, 1743170947961077761, NULL, '刚刚注册，第一次评论', 2, 1, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (3, 1740270573453344770, 1743170947961077761, 6, '欢迎加入，一起交流学习', 4, 1, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (4, 1740669921274191873, 1743170947961077761, NULL, '这个话题很有趣', 9, 1, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (4, 1740270573453344770, 1743170947961077761, 8, '我也觉得很有意思，一起讨论', 6, 0, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (5, 1743170947961077761, 1743170947961077761, NULL, '感谢分享，对我的工作很有帮助', 12, 1, CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);


INSERT INTO `question` VALUES (1,'两数之和', '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。\n\n你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。\n\n你可以按任意顺序返回答案。', '[\"数组\",\"哈希表\"]', '方法一：暴力枚举\n思路及算法\n\n最容易想到的方法是枚举数组中的每一个数 x，寻找数组中是否存在 target - x。\n\n当我们使用遍历整个数组的方式寻找 target - x 时，需要注意到每一个位于 x 之前的元素都已经和 x 匹配过，因此不需要再进行匹配。而每一个元素不能被使用两次，所以我们只需要在 x 后面的元素中寻找 target - x。\n\n``` java\nclass Solution {\n    public int[] twoSum(int[] nums, int target) {\n        int n = nums.length;\n        for (int i = 0; i < n; ++i) {\n            for (int j = i + 1; j < n; ++j) {\n                if (nums[i] + nums[j] == target) {\n                    return new int[]{i, j};\n                }\n            }\n        }\n        return new int[0];\n    }\n}\n```', 8, 5, '[{\"input\":\"1 2\",\"output\":\"3\"},{\"input\":\"2 3\",\"output\":\"5\"}]', '[{\"input\":\"1 2\",\"output\":\"3\"},{\"input\":\"2 3\",\"output\":\"5\"},{\"input\":\"3 4\",\"output\":\"7\"}]', '{\"timeLimit\":1000,\"memoryLimit\":62914560,\"stackLimit\":100000}', 0, 0, 0, '简单', 1743170947961077761, '2024-01-25 10:27:43', '2024-01-28 00:07:58', 0);
INSERT INTO `question` VALUES (2,'两数相加', '给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。\n\n请你将两个数相加，并以相同形式返回一个表示和的链表。\n\n你可以假设除了数字 0 之外，这两个数都不会以 0 开头。', '[\"链表\"]', '方法一：模拟\n思路与算法\n\n由于输入的两个链表都是逆序存储数字的位数的，因此两个链表中同一位置的数字可以直接相加。\n\n我们同时遍历两个链表，逐位计算它们的和，并与当前位置的进位值相加。具体而言，如果当前两个链表处相应位置的数字为 n1,n2n1,n2n1,n2，进位值为 carry\\textit{carry}carry，则它们的和为 n1+n2+carryn1+n2+\\textit{carry}n1+n2+carry；其中，答案链表处相应位置的数字为 (n1+n2+carry) mod 10(n1+n2+\\textit{carry}) \\bmod 10(n1+n2+carry)mod10，而新的进位值为 ⌊n1+n2+carry10⌋\\lfloor\\frac{n1+n2+\\textit{carry}}{10}\\rfloor⌊ \n10\nn1+n2+carry\n​\n ⌋。\n\n如果两个链表的长度不同，则可以认为长度短的链表的后面有若干个 000 。\n\n此外，如果链表遍历结束后，有 carry>0\\textit{carry} > 0carry>0，还需要在答案链表的后面附加一个节点，节点的值为 carry\\textit{carry}carry。\n``` java\nclass Solution {\n    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {\n        ListNode head = null, tail = null;\n        int carry = 0;\n        while (l1 != null || l2 != null) {\n            int n1 = l1 != null ? l1.val : 0;\n            int n2 = l2 != null ? l2.val : 0;\n            int sum = n1 + n2 + carry;\n            if (head == null) {\n                head = tail = new ListNode(sum % 10);\n            } else {\n                tail.next = new ListNode(sum % 10);\n                tail = tail.next;\n            }\n            carry = sum / 10;\n            if (l1 != null) {\n                l1 = l1.next;\n            }\n            if (l2 != null) {\n                l2 = l2.next;\n            }\n        }\n        if (carry > 0) {\n            tail.next = new ListNode(carry);\n        }\n        return head;\n    }\n}\n\n```', 3, 2, '[{\"input\":\"1 2\",\"output\":\"3\"}]', '[{\"input\":\"1 2\",\"output\":\"3\"}]', '{\"timeLimit\":1000,\"memoryLimit\":62914560,\"stackLimit\":100000}', 0, 0, 0, '中等', 1743170947961077761, '2024-01-25 10:43:15', '2024-01-28 00:13:06', 0);
INSERT INTO `question` VALUES (3,'无重复字符的最长子串', '给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。', '[\"哈希表\",\"字符串\"]', 'answer', 0, 0, '[{\"input\":\"abcabcbb\",\"output\":\"3\"},{\"input\":\"bbbbb\",\"output\":\"1\"},{\"input\":\"pwwkew\",\"output\":\"3\"}]', '[{\"input\":\"abcabcbb\",\"output\":\"3\"},{\"input\":\"bbbbb\",\"output\":\"1\"},{\"input\":\"pwwkew\",\"output\":\"3\"}]', '{\"timeLimit\":1000,\"memoryLimit\":100000000,\"stackLimit\":100000}', 0, 0, 0, '中等', 1743170947961077761, '2024-01-28 17:10:47', '2024-01-28 17:11:13', 0);
INSERT INTO `question` VALUES (4,'最长回文子串', '给你一个字符串找到 **s**中最长的回文子串。\n\n如果字符串的反序与原始字符串相同，则该字符串称为回文字符串。', '[\"数组\"]', 'answer', 0, 0, '[{\"input\":\"babad\",\"output\":\"bab\"},{\"input\":\"cbbd\",\"output\":\"bb\"}]', '[{\"input\":\"babad\",\"output\":\"bab\"},{\"input\":\"cbbd\",\"output\":\"bb\"}]', '{\"timeLimit\":1000,\"memoryLimit\":100000000,\"stackLimit\":100000}', 0, 0, 0, '中等', 1743170947961077761, '2024-01-28 17:16:45', '2024-01-28 17:16:45', 0);
INSERT INTO `question` VALUES (5,'最长有效括号', '给你一个只包含 **\'(\'** 和 **\')\'** 的字符串，找出最长有效（格式正确且连续）括号子串的长度。', '[\"字符串\"]', 'answer', 0, 0, '[{\"input\":\"(()\",\"output\":\"2\"},{\"input\":\")()())\",\"output\":\"4\"},{\"input\":\"\",\"output\":\"0\"}]', '[{\"input\":\"(()\",\"output\":\"2\"},{\"input\":\")()())\",\"output\":\"4\"},{\"input\":\"\",\"output\":\"0\"}]', '{\"timeLimit\":1000,\"memoryLimit\":100000000,\"stackLimit\":10000}', 0, 0, 0, '困难', 1743170947961077761, '2024-01-28 17:21:50', '2024-01-28 17:21:50', 0);
INSERT INTO `question` VALUES (6,'水壶问题', '有两个水壶，容量分别为 jug1Capacity 和 jug2Capacity 升。水的供应是无限的。确定是否有可能使用这两个壶准确得到 targetCapacity 升。\n\n如果可以得到 targetCapacity 升水，最后请用以上水壶中的一或两个来盛放取得的 targetCapacity 升水。\n\n你可以：\n\n- 装满任意一个水壶\n- 清空任意一个水壶\n- 从一个水壶向另外一个水壶倒水，直到装满或者倒空', '[\"数学\"]', 'answer', 0, 0, '[{\"input\":\"3 5 4\",\"output\":\"true\"},{\"input\":\"2 6 5\",\"output\":\"false\"},{\"input\":\"1 2 3\",\"output\":\"true\"}]', '[{\"input\":\"3 5 4\",\"output\":\"true\"},{\"input\":\"2 6 5\",\"output\":\"false\"},{\"input\":\"1 2 3\",\"output\":\"true\"}]', '{\"timeLimit\":1000,\"memoryLimit\":100000000,\"stackLimit\":10000}', 0, 0, 0, '中等', 1743170947961077761, '2024-01-28 17:32:00', '2024-01-28 17:32:00', 0);



