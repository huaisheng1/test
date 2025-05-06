/*
 Navicat Premium Data Transfer

 Source Server         : 2024-Demo
 Source Server Type    : MySQL
 Source Server Version : 80032 (8.0.32)
 Source Host           : localhost:3306
 Source Schema         : children_storybook

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32)
 File Encoding         : 65001

 Date: 05/05/2025 15:27:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for child
-- ----------------------------
DROP TABLE IF EXISTS `child`;
CREATE TABLE `child`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '关联的用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '儿童姓名',
  `age` int NOT NULL COMMENT '儿童年龄',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `child_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '儿童信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of child
-- ----------------------------
INSERT INTO `child` VALUES (1, 1, 'xiao', 5, NULL, NULL, '2025-04-27 22:35:24', '2025-04-27 22:35:24');
INSERT INTO `child` VALUES (2, 1, 'xiuxi', 5, NULL, NULL, '2025-05-05 12:31:24', '2025-05-05 12:31:24');
INSERT INTO `child` VALUES (3, 1, 'x', 5, NULL, NULL, '2025-05-05 12:50:01', '2025-05-05 12:50:01');
INSERT INTO `child` VALUES (4, 1, 'cc', 5, NULL, NULL, '2025-05-05 13:01:09', '2025-05-05 13:01:09');
INSERT INTO `child` VALUES (5, 1, '111', 5, '男', NULL, '2025-05-05 13:20:00', '2025-05-05 13:20:00');

-- ----------------------------
-- Table structure for education_content
-- ----------------------------
DROP TABLE IF EXISTS `education_content`;
CREATE TABLE `education_content`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：饮食卫生/作息规律/生活自理/安全自我保护',
  `age_range` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '适用年龄范围',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '生活教育内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of education_content
-- ----------------------------
INSERT INTO `education_content` VALUES (1, '健康的早餐', '一份健康的早餐应该包含谷物、蛋白质和水果。这会给你一整天的能量！', '饮食卫生', '3-6', 'breakfast.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `education_content` VALUES (2, '刷牙的正确方法', '每天早晚刷牙，每次刷牙时间不少于2分钟，刷牙时要注意上下、左右、里外都要刷到。', '饮食卫生', '3-6', 'toothbrush.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `education_content` VALUES (3, '按时睡觉', '晚上9点前上床睡觉，保证充足的睡眠时间，有助于身体健康成长。', '作息规律', '3-6', 'sleep.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `education_content` VALUES (4, '系鞋带', '学习如何系鞋带是一项重要的生活技能，跟着图示一步步练习吧！', '生活自理', '5-6', 'shoelace.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `education_content` VALUES (5, '认识交通信号灯', '红灯停，绿灯行，黄灯等一等。过马路时一定要遵守交通规则。', '安全自我保护', '3-6', 'traffic_light.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');

-- ----------------------------
-- Table structure for learning_content
-- ----------------------------
DROP TABLE IF EXISTS `learning_content`;
CREATE TABLE `learning_content`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：阅读倾听/交流表达/益智教育/思维锻炼/艺术启发/运动教育',
  `age_range` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '适用年龄范围',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习成长内容表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of learning_content
-- ----------------------------
INSERT INTO `learning_content` VALUES (1, '有趣的字母A', 'A是字母表的第一个字母，它看起来像一座小山，可以发\"啊\"的音。', '阅读倾听', '3-6', 'letter_a.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `learning_content` VALUES (2, '学习表达感受', '当你感到开心、难过或生气时，你可以这样表达出来...', '交流表达', '3-6', 'emotions.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `learning_content` VALUES (3, '数字游戏1-10', '学习数字1到10，每个数字都代表不同数量的物品。', '益智教育', '3-6', 'numbers.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `learning_content` VALUES (4, '形状认知', '圆形、方形、三角形...这些不同的形状在我们的生活中随处可见。', '思维锻炼', '3-6', 'shapes.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `learning_content` VALUES (5, '简单的手指画', '用手指蘸上颜料，在纸上创作出美丽的图案。', '艺术启发', '3-6', 'finger_painting.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `learning_content` VALUES (6, '球类游戏', '学习如何投球、接球，锻炼协调能力和团队合作精神。', '运动教育', '4-6', 'ball_games.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');

-- ----------------------------
-- Table structure for story_content
-- ----------------------------
DROP TABLE IF EXISTS `story_content`;
CREATE TABLE `story_content`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `record_id` int NOT NULL COMMENT '关联的故事记录ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '故事内容',
  `speaker` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容发言者: ai/child',
  `sequence` int NOT NULL COMMENT '顺序号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `record_id`(`record_id` ASC) USING BTREE,
  CONSTRAINT `story_content_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `story_record` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '故事内容记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of story_content
-- ----------------------------
INSERT INTO `story_content` VALUES (1, 1, '故事精灵正在准备一个精彩的故事...\n\n从\n\n前\n\n，有\n\n一个\n\n活\n\n泼\n\n好\n\n动\n\n的小\n\n朋友\n\n叫\n\n小\n\n明\n\n(m\n\ní\n\nng\n\n)。\n\n小\n\n明\n\n最\n\n喜欢\n\n的\n\n事情\n\n就是\n\n听\n\n故事\n\n和\n\n画\n\n画\n\n。有\n\n一天\n\n，他\n\n的\n\n妈妈\n\n给\n\n他\n\n买\n\n了一\n\n本\n\n新的\n\n图\n\n画\n\n书\n\n，\n\n书\n\n里面\n\n有\n\n很多\n\n可\n\n爱的\n\n动物\n\n和\n\n颜色\n\n鲜\n\n艳\n\n的\n\n图\n\n案\n\n。\n\n\n\n小\n\n明\n\n打开\n\n图\n\n画\n\n书\n\n，\n\n发现\n\n里面\n\n有\n\n一\n\n只\n\n小\n\n狐狸\n\n(f\n\nú\n\n lí\n\n)\n\n在\n\n森林\n\n(l\n\nín\n\n)\n\n里\n\n迷\n\n路\n\n了\n\n。\n\n小\n\n狐狸\n\n看\n\n起来\n\n有\n\n些\n\n伤\n\n心\n\n，因为\n\n找\n\n不到\n\n回\n\n家的\n\n路\n\n。\n\n小\n\n明\n\n决定\n\n帮助\n\n这\n\n只\n\n小\n\n狐狸\n\n。他\n\n想\n\n：“\n\n如果\n\n我\n\n能\n\n找到\n\n线\n\n索\n\n，\n\n或\n\n许\n\n就\n\n能\n\n帮\n\n小\n\n狐狸\n\n找到\n\n回\n\n家的\n\n路\n\n。”\n\n\n\n小\n\n明\n\n仔\n\n细\n\n地\n\n看\n\n着\n\n图\n\n画\n\n书\n\n，\n\n书\n\n的\n\n每\n\n一\n\n页\n\n都有\n\n不同\n\n的\n\n动物\n\n和\n\n风\n\n景\n\n(f\n\nē\n\nng\n\n j\n\nǐ\n\nng\n\n)。\n\n这\n\n时候\n\n，他\n\n遇\n\n到了\n\n第\n\n一个\n\n选择\n\n：\n\n\n\n1\n\n.\n\n 小\n\n明\n\n可以\n\n先\n\n去\n\n问\n\n问\n\n图\n\n画\n\n书\n\n里的\n\n小\n\n鸟\n\n(\n\nni\n\nǎ\n\no\n\n)，\n\n看看\n\n它\n\n们\n\n有没有\n\n看到\n\n小\n\n狐狸\n\n的\n\n家\n\n。\n\n\n2\n\n.\n\n 小\n\n明\n\n也\n\n可以\n\n试\n\n着\n\n根据\n\n小\n\n狐狸\n\n的\n\n脚\n\n印\n\n(y\n\nìn\n\n)，\n\n沿\n\n着\n\n森林\n\n的小\n\n路\n\n走\n\n，\n\n看看\n\n能\n\n不能\n\n找到\n\n小\n\n狐狸\n\n的\n\n家\n\n。\n\n\n\n小\n\n朋友\n\n，你\n\n觉得\n\n小\n\n明\n\n应该\n\n选择\n\n哪\n\n一个\n\n呢\n\n？\n\n是\n\n去\n\n问\n\n小\n\n鸟\n\n，\n\n还是\n\n跟\n\n着\n\n脚\n\n印\n\n走\n\n呢\n\n？\n\n\n\n\n\n', 'ai', 1, '2025-04-27 22:35:24');
INSERT INTO `story_content` VALUES (2, 1, '你好\n', 'user', 2, '2025-04-27 22:35:24');
INSERT INTO `story_content` VALUES (3, 1, '\n\n在\n\n一个\n\n美\n\n丽\n\n的\n\n森林\n\n里\n\n，\n\n住\n\n着\n\n一\n\n只\n\n叫\n\n小\n\n熊\n\n的\n\n可\n\n爱\n\n熊\n\n宝宝\n\n。\n\n小\n\n熊\n\n最\n\n喜欢\n\n的\n\n事情\n\n就是\n\n在\n\n森林\n\n里\n\n探\n\n险\n\n，\n\n寻找\n\n新的\n\n朋友\n\n和\n\n学习\n\n新的\n\n东西\n\n。\n\n\n\n一天\n\n，小\n\n熊\n\n在\n\n森林\n\n里\n\n发现\n\n了一\n\n条\n\n小\n\n溪\n\n。\n\n小\n\n溪\n\n的\n\n水\n\n流\n\n得\n\n很\n\n快\n\n，\n\n闪\n\n闪\n\n发\n\n光\n\n。\n\n小\n\n熊\n\n想\n\n知道\n\n这\n\n条\n\n小\n\n溪\n\n通\n\n向\n\n哪里\n\n。\n\n于是\n\n，小\n\n熊\n\n准备\n\n开始\n\n一次\n\n新的\n\n探\n\n险\n\n。\n\n\n\n小\n\n熊\n\n走\n\n着\n\n走\n\n着\n\n，\n\n突然\n\n看到\n\n小\n\n溪\n\n边\n\n有\n\n一\n\n只\n\n小\n\n鸟\n\n。\n\n小\n\n鸟\n\n正在\n\n用\n\n它\n\n的小\n\n嘴\n\n巴\n\n喝\n\n水\n\n，看\n\n起来\n\n很\n\n开心\n\n。\n\n小\n\n熊\n\n好\n\n奇\n\n地\n\n问\n\n：“\n\n你好\n\n，小\n\n鸟\n\n，你\n\n在\n\n这里\n\n做\n\n什么\n\n呢\n\n？”\n\n\n\n小\n\n鸟\n\n抬\n\n起\n\n头\n\n，\n\n友\n\n好\n\n地\n\n说\n\n：“\n\n你好\n\n，小\n\n熊\n\n！\n\n我\n\n在\n\n这里\n\n喝\n\n水\n\n，小\n\n溪\n\n的\n\n水\n\n又\n\n清\n\n又\n\n甜\n\n。\n\n你\n\n要\n\n和\n\n我\n\n一起\n\n去\n\n找到\n\n小\n\n溪\n\n的\n\n尽\n\n头\n\n吗\n\n？”\n\n\n\n小\n\n熊\n\n觉得\n\n这是\n\n一个\n\n很\n\n好的\n\n主\n\n意\n\n，于\n\n是\n\n决定\n\n和\n\n小\n\n鸟\n\n一起\n\n探\n\n险\n\n。他\n\n们\n\n沿\n\n着\n\n小\n\n溪\n\n走\n\n了一\n\n会\n\n儿\n\n，\n\n发现\n\n了一\n\n棵\n\n大\n\n树\n\n。\n\n树\n\n上\n\n挂\n\n着\n\n许\n\n多\n\n美\n\n丽\n\n的\n\n水果\n\n，每\n\n一个\n\n都\n\n看\n\n起来\n\n很好\n\n吃\n\n。\n\n\n\n现在\n\n，小\n\n熊\n\n需要\n\n做\n\n一个\n\n选择\n\n：\n\n\n1\n\n.\n\n 小\n\n熊\n\n决定\n\n尝\n\n试\n\n摘\n\n一个\n\n水果\n\n来\n\n尝\n\n尝\n\n。\n\n\n2\n\n.\n\n 小\n\n熊\n\n和\n\n小\n\n鸟\n\n一起\n\n继续\n\n探索\n\n小\n\n溪\n\n，\n\n看看\n\n还有\n\n什么\n\n有\n\n趣\n\n的\n\n东西\n\n。\n\n\n\n小\n\n朋友\n\n，你\n\n觉得\n\n小\n\n熊\n\n应该\n\n选择\n\n哪个\n\n呢\n\n？\n\n\n\n\n\n', 'ai', 3, '2025-04-27 22:35:24');
INSERT INTO `story_content` VALUES (4, 2, '当然可以！下面是为5岁的小朋友“xiuxi”创作的互动成长故事的开头部分：\n\n---\n\n# 🌟**xiuxi和神奇的绘本屋**🌟\n\n从前从前，在一个阳光明媚的下午，小朋友**xiuxi**和妈妈一起去图书馆借书。xiuxi最喜欢五颜六色的绘本，里面有很多小动物和奇妙的故事。\n\n今天，xiuxi在角落里发现了一本特别的绘本。绘本封面上写着：\n\n> **《神奇的绘本屋》**\n\n当xiuxi轻轻打开这本书的时候，突然间——**“哗！”** 一道彩色的光把xiuxi吸了进去！\n\n“哇——我在书里面啦！”xiuxi睁开眼睛，发现自己站在一间会说话的屋子前。\n\n“你好，小朋友，我是绘本屋！”屋子开口说话了，“我这里藏着很多很多好玩的知识和游戏，你愿意来冒险学习吗？”\n\n### 现在，小朋友有几个选择：\n\n1. **进去屋子**：xiuxi走进绘本屋，看看里面有什么神奇的书和游戏。\n2. **在外面问一问**：xiuxi先不进去，而是站在门口问绘本屋：“里面有什么呀？”\n\n你想选择哪一个呢？👀\n\n请说出你的选择的编号（1 或 2），xiuxi就会跟着你的决定继续冒险啦！✨', 'ai', 1, '2025-05-05 12:31:38');
INSERT INTO `story_content` VALUES (5, 2, '进去屋子', 'child', 2, '2025-05-05 12:31:46');
INSERT INTO `story_content` VALUES (6, 2, '在一个阳光明媚的早晨，小熊波波在森林里玩耍。突然，他看到一座小小的房子，门开着，里面传来轻轻的笑声。波波很好奇，于是决定走进屋子看看。\n\n他轻轻地推开门，走进了屋子。屋子里温暖而又明亮，墙上挂着五彩缤纷的画，桌子上放着一些玩具和书本。波波一眼就看到了角落里有一个大大的软垫，软垫上坐着一个小兔子，正在开心地吃胡萝卜。\n\n小兔子看到波波进来，笑着说：“你好呀！我是小白兔，欢迎来到我的家！你也想吃些胡萝卜吗？”\n\n波波点点头，但他发现桌子上还有一些其他的食物，看起来也非常好吃。\n\n### 现在，小朋友有几个选择：\n\n1. **选择一**：波波决定和小白兔一起吃胡萝卜，和小兔子聊天。\n2. **选择二**：波波决定去尝试桌子上的其他食物，看看它们是什么。\n\n你想选择哪一个呢？', 'ai', 3, '2025-05-05 12:31:52');
INSERT INTO `story_content` VALUES (7, 3, '故事精灵正在准备一个精彩的故事... 当然可以！下面 是为5 岁的小朋友“xiuxi”创作的互动成长故事的开头部分：\n\n---\n\n# 🌟**xiuxi和神奇的 绘本屋**🌟\n\n从前 从前，在一个阳 光明媚的下午，小朋友**xi uxi**和妈妈一起去图书馆借书。xiuxi最喜欢五颜六色的 绘本，里面有很多小动物和奇妙 的故事。\n\n今天，xiuxi在角 落里发现了一本特别的绘本。绘本 封面上写着：\n\n> **《神奇 的绘本屋》**\n\n当xiuxi 轻轻打开这本书的时候，突然间 ——**“哗！”** 一道彩色的 光把xiuxi吸 了进去！\n\n“哇—— 我在书里面啦！”xiuxi 睁开眼睛，发现 自己站在一间 会说话的屋子前。\n\n“你好，小朋友，我是绘本屋！”屋子开口说话了，“我这里藏着很多很多好玩的 知识和游戏，你愿意来冒险 学习吗？”\n\n### 现在，小朋友有几个选择：\n\n1. **进去屋 子**：xiux i走进绘本屋，看看里面 有什么神奇的书和游戏。\n2. **在外面问一 问**： xiuxi先不进去，而是 站在门口问绘本屋 ：“里面有什么呀？”\n\n你想选择哪一个 呢？👀\n\n请说 出你的选择的编号（1 或 2），xiuxi就会跟 着你的决定继续冒险啦！✨ ', 'ai', 1, '2025-05-05 12:32:45');
INSERT INTO `story_content` VALUES (8, 3, '进去屋子', 'user', 2, '2025-05-05 12:32:45');
INSERT INTO `story_content` VALUES (9, 3, '在 一个阳光明媚的早晨，小熊波波在森林里玩耍。突然，他 看到一座小小的房子，门开着，里面传来轻轻的笑 声。波波很好奇，于是决定走进屋子看看。\n\n他轻轻地推开门，走 进了屋子。屋子里温暖而又明亮，墙 上挂着五彩缤纷的画，桌子上放着一些玩 具和书本。波波一眼就看到了角落里有一个大大的软垫， 软垫上坐着一个小兔子，正在开心地吃胡萝卜。\n\n小兔子 看到波波进来，笑着说：“你好呀！我是小白兔，欢迎来到我的家！你 也想吃些胡萝卜吗？”\n\n波波点点头，但他发现桌子上 还有一些其他的食物，看起来也 非常好吃。\n\n### 现在，小朋友有几个选择：\n\n1. **选择一** ：波波决定和小白兔一起吃胡萝卜，和小兔子聊天。\n2. **选择二**：波波决定去尝试桌子上的 其他食物，看看它们是什么。\n\n你想选择哪一个呢？ ', 'ai', 3, '2025-05-05 12:32:45');
INSERT INTO `story_content` VALUES (10, 4, '当然可以！下面是为5岁小朋友 **x** 量身定做的互动故事，希望小x能喜欢这个有趣又能学到知识的故事！\n\n---\n\n# 🌟小X和神奇的绘本森林🌳  \n\n从前从前，有一个喜欢画画和讲故事的小朋友，名字叫**小X**。有一天，小X在家里的书架上发现了一本厚厚的、闪着光的绘本📖。封面上写着：“进入绘本森林，开启智慧之旅！”  \n\n小X一翻开书本，就“呼——”地被一阵风卷进了一个五彩缤纷的森林中。树叶像彩纸一样闪闪发亮，小鸟会唱儿歌，还有一本大大的、会说话的绘本正在等他！\n\n“你好，小X！”绘本笑眯眯地说，“欢迎来到绘本森林！这里到处都有故事和谜题，只要你愿意探索，我会陪你一起成长！”\n\n小X拍拍衣服，兴奋地点点头：“我准备好了！”\n\n绘本翻开了第一页，出现了一条小路，小路的尽头有三个不同的地方：  \n\n- 一个用彩虹搭成的桥，通往🧩拼图屋；  \n- 一个会飘动的书页，指向📚阅读树屋；  \n- 一朵正在唱歌的云朵，飞向🎨颜色花园。\n\n---\n\n### 现在，小朋友有几个选择：\n\n1. **去拼图屋🧩**：动动脑筋，解开拼图谜题，考考小X的观察力！\n2. **去阅读树屋📚**：和会说话的小动物一起阅读故事，学习新词语。\n3. **去颜色花园🎨**：用画笔和颜色给大花朵上色，发挥想象力！\n\n你想选择哪一个呢？  \n👉 请说出你的选择的数字，例如：“我选2”。我们就会继续故事哦！', 'ai', 1, '2025-05-05 12:50:23');
INSERT INTO `story_content` VALUES (11, 4, '我选2\n', 'child', 2, '2025-05-05 12:50:30');
INSERT INTO `story_content` VALUES (12, 4, '好的，故事继续发展啦！让我们一起看看发生了什么：\n\n---\n\n**故事继续：**\n\n小熊跳进了森林，来到了一个巨大的树洞。树洞里面有好多彩色的光点，好像是小精灵在里面玩耍。小熊觉得很神奇，走近一点，好奇地问：“你们在干什么呢？”\n\n小精灵们停下来，微笑着说：“我们在玩捉迷藏呀！不过，今天我们有一个新的任务，我们需要一个小朋友来帮忙！”\n\n小熊高兴地说：“我来帮忙！”\n\n小精灵们开心地拍了拍手，说：“太好了！你可以选择帮我们找树叶，或者你可以帮我们收集小石子。你喜欢哪个呢？”\n\n### 现在，小朋友有几个选择：\n\n1. **找树叶**：你可以帮助小精灵们找一些美丽的树叶，把它们放进一个大篮子里。\n2. **收集小石子**：你可以帮助小精灵们在森林里找到五颜六色的小石子，把它们放进篮子里。\n\n你想选择哪一个呢？', 'ai', 3, '2025-05-05 12:50:35');
INSERT INTO `story_content` VALUES (13, 5, '### 《cc的小小冒险》\n\n从前，有一个可爱的小朋友叫**cc**，他有一双大大的眼睛和一颗好奇的心。每天，cc都会和他的爸爸妈妈一起去公园玩耍，或者在家里做一些有趣的事情。\n\n一天，cc在家里找到了一本漂亮的书。书的封面上画着五颜六色的图画，书名叫做《奇妙的冒险》。cc对这本书很感兴趣，便迫不及待地翻开了第一页。\n\n**cc**开始读起了书上的故事：  \n“在一个神奇的森林里，住着许多小动物。它们每天都过得非常快乐，因为它们有一个重要的任务——帮助别人！今天，小狐狸要帮助小熊找到失落的彩虹。”\n\ncc停了下来，觉得这个故事好像很有意思。可是，书中突然出现了一个问题。小狐狸的任务非常重要，但它不知道该先做什么。于是，书上给出了两个选择。\n\n### 现在，cc有几个选择：\n\n1. **选择一**：cc帮助小狐狸先去找彩虹的线索。\n2. **选择二**：cc帮助小狐狸去找其他动物，大家一起合作。\n\n你想选择哪一个呢？', 'ai', 1, '2025-05-05 13:01:27');
INSERT INTO `story_content` VALUES (14, 6, '当然可以！下面是为5岁小朋友**xiuxi**精心创作的互动故事：\n\n---\n\n## 🌟《xiuxi和神奇绘本森林》🌟\n\n在一个阳光明媚的早晨，xiuxi睁开眼睛，发现自己的枕边多了一本奇怪的书。这不是平时看的故事书，它的封面会发光，而且还会说话！\n\n“xiuxi，快打开我，我们要去一个神奇的地方！”书本轻声说。\n\nxiuxi小心地打开书页，一阵微风吹过，**哗——**她竟然被吸进了书里！\n\nxiuxi睁开眼，发现自己站在一片色彩斑斓的**绘本森林**里。树叶是五颜六色的，就像蜡笔画出来的一样，动物们都会说话，还有好多漂浮在空中的拼音字母！\n\n突然，一只戴着眼镜的小狐狸走过来，它看起来很聪明。\n\n“小朋友，你能帮我找到三个掉进森林里的拼音吗？它们是森林里学习魔法的钥匙哦！”\n\n---\n\n### 现在，小朋友有几个选择：\n\n1. **帮小狐狸一起找拼音字母**：xiuxi喜欢挑战，想要去看看那些拼音藏在哪里。\n2. **先去森林深处探险看看有没有别的神奇事情**：xiuxi对这个五彩世界充满好奇，想先到处转转。\n\n你想选择哪一个呢？😊', 'ai', 1, '2025-05-05 13:29:14');
INSERT INTO `story_content` VALUES (15, 6, '帮小狐狸一起找拼音字母：xiuxi喜欢挑战， 想要去看看那些拼音藏在哪里。', 'child', 2, '2025-05-05 13:29:35');
INSERT INTO `story_content` VALUES (16, 6, '当然可以！我们继续小狐狸的拼音探险故事吧！\n\n---\n\n## 🌟小狐狸找拼音的冒险旅程（续）\n\n小狐狸 **xiuxi** 是个聪明又爱挑战的小家伙。今天，它带上了自己的小背包，决定去找一找神秘的拼音字母们藏在哪里。\n\n“听说拼音字母会躲猫猫，藏在森林的各个角落！”xiuxi 眨着大眼睛说道。\n\n走着走着，它来到了一个**拼音树林**。树上不是长着果子，而是挂着一些小牌子，上面写着像“b、p、m、f”这样的拼音字母！\n\nxiuxi 高兴地跳起来：“哇，我找到拼音啦！可它们怎么像小精灵一样飞走了？”\n\n这时候，一只会说话的小鸟飞过来，对 xiuxi 说：\n\n> “想抓住拼音字母的秘密，必须完成它们的小游戏哦！”\n\n小鸟叽叽喳喳地指着前方的两条小路：\n\n---\n\n### 现在，小朋友有几个选择：\n\n1. **走进“拼音跳跳桥”**：跳过一座桥，桥上的每一块板子上都有一个拼音字母，要跳对发音才不会掉下去。\n2. **钻进“声母洞洞屋”**：这是一个神奇的山洞，里面住着声母精灵，它们会出谜语，答对就能学到新的拼音。\n\n你想选择哪一个呢？😊  \n👉 **告诉我：选1还是选2？**', 'ai', 3, '2025-05-05 13:29:48');
INSERT INTO `story_content` VALUES (17, 6, '1', 'child', 4, '2025-05-05 13:30:03');
INSERT INTO `story_content` VALUES (18, 6, '当然可以！我们继续这个为5岁小朋友设计的互动故事吧。根据小朋友刚才选择的“1”，让我们看看接下来发生了什么吧！\n\n---\n\n## 🐾神奇的早晨：小猫咪露露的冒险（续）\n\n露露选择了**跟着会唱歌的小鸟一起走进森林**。\n\n小鸟在前面唱着歌，露露一边跳一边走。她们走进了森林深处，阳光从树叶间洒下来，就像金色的雨点，落在露露的胡须上。\n\n“叽叽喳喳～要不要看看森林学校？”小鸟问。\n\n“森林里还有学校？”露露眨眨眼。\n\n“当然有啦！”小鸟拍拍翅膀说，“在那里，小动物们学怎么洗手、怎么分享玩具，还学怎么安全过马路哦！”\n\n露露好奇极了，于是她跟着小鸟来到了树下的一座小小木屋，门口写着：\n\n**森林学校欢迎你！**\n\n老师是一只戴眼镜的老猫，他笑咪咪地说：“欢迎你，露露！我们今天要上‘好朋友分享课’！”\n\n露露坐好后，老师拿出一盒五颜六色的果冻，说：“现在，每个小朋友都要学会怎样和朋友们一起分享。”\n\n这时，露露看到旁边的小兔子一脸想吃的样子，可是自己也好想尝尝果冻……\n\n---\n\n### 现在，小朋友有几个选择：\n\n1. **露露决定把果冻分一半给小兔子**  \n   她想：“好朋友就是要一起分享！”\n\n2. **露露决定先自己吃一点，再问小兔子要不要**  \n   她想：“我先尝一下味道，再分享也不迟！”\n\n你想选择哪一个呢？', 'ai', 5, '2025-05-05 13:30:19');
INSERT INTO `story_content` VALUES (19, 7, '故事精灵正在准备一个精彩的故事... 当然可以！下面 是为5岁小朋友 **xiuxi**精心创作 的互动故事：\n\n---\n\n## 🌟《xiuxi和神 奇绘本森林》🌟 在一个阳光明媚 的早晨，xiuxi 睁开眼睛，发现自己的枕 边多了一本奇怪的 书。这不是平时看的故事书，它的 封面会发光，而且还会说话！\n\n“xiuxi，快打开我，我们 要去一个神奇的地方！”书本 轻声说。\n\nxiuxi小心地打开 书页，一阵微风吹过，** 哗——**她竟然被吸 进了书里！\n\nxiuxi睁 开眼，发现自己站在一片色彩 斑斓的**绘本森林** 里。树叶是五颜六色的，就像蜡笔画出来的一样， 动物们都会说话，还有 好多漂浮在空中的拼音字母！ 突然，一只戴着眼镜的小狐狸 走过来，它看起来很聪明。 “小朋友，你能帮我找到三个 掉进森林里的拼音吗？它们是森林里学习魔法的 钥匙哦！”\n\n---\n\n### 现在，小朋友 有几个选择：\n\n1. **帮小狐狸一起找拼音 字母**：xiuxi喜欢挑战， 想要去看看那些拼音 藏在哪里。\n2. ** 先去森林深处探险看看有没有别的 神奇事情**：xiuxi 对这个五彩世界充满好 奇，想先到处转转。 你想选择哪一个呢？😊 ', 'ai', 1, '2025-05-05 13:30:27');
INSERT INTO `story_content` VALUES (20, 7, '帮小狐狸一起找拼音字母：xiuxi喜欢挑战， 想要去看看那些拼音藏在哪里。', 'user', 2, '2025-05-05 13:30:27');
INSERT INTO `story_content` VALUES (21, 7, '当然可以！我们继续 小狐狸的拼音探险 故事吧！\n\n---\n\n## 🌟小狐狸找拼音 的冒险旅程（续）\n\n小狐狸 **xi uxi** 是个聪明又爱挑战的小家 伙。今天，它带上了自己的小背包，决定去找一找神秘的拼音字母 们藏在哪里。\n\n“听说拼 音字母会躲猫 猫，藏在森林的各个角落！”xiuxi 眨着大眼睛说道。\n\n走 着走着，它来到了一个**拼音树林 **。树上不是长着 果子，而是挂着一些小牌子，上 面写着像“b、p、m、f” 这样的拼音字母！\n\nxiuxi 高兴 地跳起来：“哇，我找到拼音啦！ 可它们怎么像小精灵 一样飞走了？”\n\n这时候，一只会说 话的小鸟飞过来，对 xiuxi 说：\n\n> “ 想抓住拼音字母的秘密，必须完成 它们的小游戏哦！”\n\n小鸟叽叽 喳喳地指着前方的两条小路：\n\n--- ### 现在，小朋友有几个选择：\n\n1. **走进“拼音 跳跳桥”**：跳过一座桥，桥 上的每一块板子上都有一个拼音 字母，要跳对发音才 不会掉下去。\n2. **钻进“声 母洞洞屋”**：这是一个神奇的 山洞，里面住着声母精灵，它们会 出谜语，答对就能学到新的拼音。\n\n你想选择哪一个呢？😊  \n👉 **告诉我： 选1还是选2？** ', 'ai', 3, '2025-05-05 13:30:27');
INSERT INTO `story_content` VALUES (22, 7, '1', 'user', 4, '2025-05-05 13:30:27');
INSERT INTO `story_content` VALUES (23, 7, '当然 可以！ 我们继续这个为5岁 小朋友设计的互动故事吧。根据小朋友刚才选择的“1”，让我们看看接下来发生了什么 吧！\n\n---\n\n## 🐾神奇 的早晨：小猫咪 露露的冒险（续）\n\n露 露选择了**跟着会唱 歌的小鸟一起走进 森林**。\n\n小鸟在前面唱着歌，露 露一边跳 一边走。她们走进 了森林深处，阳光从树 叶间洒下来，就像金色的雨点， 落在露露的 胡须上。\n\n“叽叽 喳喳～要不要看看森林学校？” 小鸟问。\n\n“森林 里还有学校？”露露眨眨 眼。\n\n“当然有啦！”小鸟 拍拍翅膀说，“在那里，小动物们学怎么洗手、怎么 分享玩具，还学怎么安全过马 路哦！”\n\n露露好奇极了，于是她跟着小鸟来 到了树下的一座小 小木屋， 门口写着：\n\n**森林 学校欢迎你！**\n\n老师是一只戴 眼镜的老猫，他笑咪咪地 说：“欢迎你，露露！我们 今天要上‘好朋友分享课’！” 露露坐好后，老师 拿出一盒五颜 六色的果冻，说 ：“现在，每个小朋友都要学会 怎样和朋友们一起分享。”\n\n这时，露露看到旁边的小 兔子一脸想吃的样子，可是 自己也好想尝尝果冻 ……\n\n---\n\n### 现在，小朋友有几个选择： 1. **露露决定把果冻 分一半给小兔子** 她想：“好朋友就是要一起 分享！”\n\n2. **露露决定先自己吃 一点，再问小兔子要不要** 她想：“我先尝一下味道，再 分享也不迟！”\n\n你想选择哪一个呢？ ', 'ai', 5, '2025-05-05 13:30:27');

-- ----------------------------
-- Table structure for story_record
-- ----------------------------
DROP TABLE IF EXISTS `story_record`;
CREATE TABLE `story_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `child_id` int NOT NULL COMMENT '关联的儿童ID',
  `story_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '故事类型：生活教育/学习成长',
  `story_theme` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '故事主题',
  `duration` int NULL DEFAULT 0 COMMENT '使用时长(秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `child_id`(`child_id` ASC) USING BTREE,
  CONSTRAINT `story_record_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `child` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '故事记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of story_record
-- ----------------------------
INSERT INTO `story_record` VALUES (1, 1, '学习成长', '友谊分享', 16, '2025-04-27 22:35:24');
INSERT INTO `story_record` VALUES (2, 2, '学习成长', '友谊分享', 14, '2025-05-05 12:31:24');
INSERT INTO `story_record` VALUES (3, 2, '学习成长', '友谊分享', 83, '2025-05-05 12:32:45');
INSERT INTO `story_record` VALUES (4, 3, '学习成长', '友谊分享', 22, '2025-05-05 12:50:01');
INSERT INTO `story_record` VALUES (5, 4, '学习成长', '情绪管理', 17, '2025-05-05 13:01:09');
INSERT INTO `story_record` VALUES (6, 2, '学习成长', '友谊分享', 13, '2025-05-05 13:29:01');
INSERT INTO `story_record` VALUES (7, 2, '学习成长', '友谊分享', 87, '2025-05-05 13:30:27');

-- ----------------------------
-- Table structure for story_scene
-- ----------------------------
DROP TABLE IF EXISTS `story_scene`;
CREATE TABLE `story_scene`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场景标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场景描述',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：生活教育/学习成长',
  `theme` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主题',
  `age_range` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '适用年龄范围',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '场景图片URL',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '故事场景表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of story_scene
-- ----------------------------
INSERT INTO `story_scene` VALUES (1, '魔法森林冒险', '在一个神奇的森林里，小动物们生活在一起，每天都有新的冒险。', '学习成长', '友谊分享', '3-6', 'magic_forest.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `story_scene` VALUES (2, '太空探险记', '乘坐宇宙飞船，探索浩瀚的宇宙，认识各种星球和星座。', '学习成长', '探索宇宙', '4-6', 'space.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `story_scene` VALUES (3, '海底世界', '潜入蓝色的海洋，认识各种奇妙的海洋生物，了解海洋保护的重要性。', '学习成长', '动物世界', '3-6', 'underwater.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `story_scene` VALUES (4, '刷牙小英雄', '小牙刷和牙膏的冒险故事，学习正确的刷牙方法和口腔卫生知识。', '生活教育', '刷牙洗脸', '3-5', 'toothbrush_hero.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `story_scene` VALUES (5, '蔬菜王国历险记', '在蔬菜王国里，各种蔬菜都有自己的特点和营养价值。', '生活教育', '健康饮食', '3-6', 'vegetable_kingdom.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');
INSERT INTO `story_scene` VALUES (6, '安全小卫士', '学习在各种场景下如何保护自己，认识安全标志和紧急电话。', '生活教育', '安全知识', '4-6', 'safety_guard.jpg', '2025-04-26 23:55:14', '2025-04-26 23:55:14');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '角色: user/admin',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', 'admin@example.com', '12345678', NULL, 'admin', '2025-04-26 23:55:14', '2025-04-27 22:36:47');
INSERT INTO `user` VALUES (2, 'zhangsan', '123456', 'zhangsan@163.com', '12345678912', NULL, 'user', '2025-05-05 13:07:19', '2025-05-05 13:07:19');

SET FOREIGN_KEY_CHECKS = 1;
