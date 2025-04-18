-- 清空已有数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE stop_time;
TRUNCATE TABLE train_trip;
TRUNCATE TABLE stop;
TRUNCATE TABLE route;
TRUNCATE TABLE station;
TRUNCATE TABLE line;
SET FOREIGN_KEY_CHECKS = 1;

-- 线路数据
INSERT INTO line (id, name, code, color, operator) VALUES
(1, '1号线', 'L1', '#CC0000', '北京地铁公司'),
(2, '2号线', 'L2', '#0000CC', '北京地铁公司'),
(3, '4号线', 'L4', '#00CC00', '北京地铁公司'),
(4, '13号线', 'L13', '#CC00CC', '北京地铁公司'),
(5, '6号线', 'L6', '#FF9900', '北京地铁公司');

-- 站点数据，独立于线路
INSERT INTO station (id, name, code) VALUES
(1, '苹果园', 'PGY'),
(2, '古城', 'GC'),
(3, '八角游乐园', 'BJYLM'),
(4, '八宝山', 'BBS'),
(5, '玉泉路', 'YQL'),
(6, '五棵松', 'WKS'),
(7, '万寿路', 'WSL'),
(8, '公主坟', 'GZF'),
(9, '军事博物馆', 'JSBWG'),
(10, '木樨地', 'MXD'),
(11, '南礼士路', 'NLSL'),
(12, '复兴门', 'FXM'),
(13, '西单', 'XD'),
(14, '天安门西', 'TAMX'),
(15, '天安门东', 'TAMD'),
(16, '王府井', 'WFJ'),
(17, '东单', 'DD'),
(18, '建国门', 'JGM'),
(19, '永安里', 'YAL'),
(20, '国贸', 'GM'),
(21, '大望路', 'DWL'),
(22, '四惠', 'SH'),
(23, '四惠东', 'SHD'),
(24, '西直门', 'XZM'),
(25, '车公庄', 'CGZ'),
(26, '阜成门', 'FCM'),
(27, '长椿街', 'CCJ'),
(28, '宣武门', 'XWM'),
(29, '和平门', 'HPM'),
(30, '前门', 'QM'),
(31, '崇文门', 'CWM'),
(32, '北京站', 'BJS'),
(33, '朝阳门', 'CYM'),
(34, '东四十条', 'DSSTC'),
(35, '东直门', 'DZM'),
(36, '雍和宫', 'YHG'),
(37, '安定门', 'ADM'),
(38, '鼓楼大街', 'GLDJ'),
(39, '积水潭', 'JST'),
(40, '安河桥北', 'AHQB'),
(41, '北宫门', 'BGM'),
(42, '西苑', 'XY'),
(43, '圆明园', 'YMY'),
(44, '北京大学东门', 'BJDXDM'),
(45, '中关村', 'ZGC'),
(46, '海淀黄庄', 'HDHZ'),
(47, '人民大学', 'RMDX'),
(48, '魏公村', 'WGC'),
(49, '国家图书馆', 'GJLSG'),
(50, '动物园', 'DWY'),
(51, '新街口', 'XJK'),
(52, '平安里', 'PAL'),
(53, '西四', 'XS'),
(54, '灵境胡同', 'LJHT'),
(55, '菜市口', 'CSK'),
(56, '陶然亭', 'TRT'),
(57, '北京南站', 'BJNZ'),
(58, '马家堡', 'MJB'),
(59, '角门西', 'JMX'),
(60, '公益西桥', 'GYXQ'),
(61, '大钟寺', 'DZS'),
(62, '知春路', 'ZCL'),
(63, '五道口', 'WDK'),
(64, '上地', 'SD'),
(65, '西二旗', 'XEQ'),
(66, '龙泽', 'LZ'),
(67, '回龙观', 'HLG'),
(68, '霍营', 'HY'),
(69, '立水桥', 'LSQ'),
(70, '北苑', 'BY'),
(71, '望京西', 'WJX'),
(72, '芍药居', 'SYJ'),
(73, '光熙门', 'GXM'),
(74, '柳芳', 'LF'),
(75, '海淀五路居', 'HDWLJ'),
(76, '慈寿寺', 'CSS'),
(77, '花园桥', 'HYQ'),
(78, '白石桥南', 'BSQN'),
(79, '车公庄西', 'CGZX'),
(80, '北海北', 'BHB'),
(81, '南锣鼓巷', 'NLGX'),
(82, '东四', 'DS'),
(83, '东大桥', 'DDQ'),
(84, '呼家楼', 'HJL'),
(85, '金台路', 'JTL'),
(86, '十里堡', 'SLB'),
(87, '青年路', 'QNL'),
(88, '褡裢坡', 'DLP'),
(89, '黄渠', 'HQ'),
(90, '常营', 'CY'),
(91, '草房', 'CF');

-- 路线数据（每条线路两个方向）
INSERT INTO route (id, line_id, name, start_station_id, end_station_id) VALUES
-- 1号线路线
(1, 1, '苹果园 → 四惠东', 1, 23),
(2, 1, '四惠东 → 苹果园', 23, 1),

-- 2号线路线（环线）
(3, 2, '西直门 → 西直门（内环）', 24, 24),
(4, 2, '西直门 → 西直门（外环）', 24, 24),

-- 4号线路线
(5, 3, '安河桥北 → 公益西桥', 40, 60),
(6, 3, '公益西桥 → 安河桥北', 60, 40),

-- 13号线路线
(7, 4, '西直门 → 东直门', 24, 35),
(8, 4, '东直门 → 西直门', 35, 24),

-- 6号线路线
(9, 5, '海淀五路居 → 草房', 75, 91),
(10, 5, '草房 → 海淀五路居', 91, 75);

-- 停靠站数据
-- 1号线东行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(1, 1, 1), (1, 2, 2), (1, 3, 3), (1, 4, 4), (1, 5, 5), 
(1, 6, 6), (1, 7, 7), (1, 8, 8), (1, 9, 9), (1, 10, 10),
(1, 11, 11), (1, 12, 12), (1, 13, 13), (1, 14, 14), (1, 15, 15),
(1, 16, 16), (1, 17, 17), (1, 18, 18), (1, 19, 19), (1, 20, 20),
(1, 21, 21), (1, 22, 22), (1, 23, 23);

-- 1号线西行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(2, 23, 1), (2, 22, 2), (2, 21, 3), (2, 20, 4), (2, 19, 5),
(2, 18, 6), (2, 17, 7), (2, 16, 8), (2, 15, 9), (2, 14, 10),
(2, 13, 11), (2, 12, 12), (2, 11, 13), (2, 10, 14), (2, 9, 15),
(2, 8, 16), (2, 7, 17), (2, 6, 18), (2, 5, 19), (2, 4, 20),
(2, 3, 21), (2, 2, 22), (2, 1, 23);

-- 2号线内环停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(3, 24, 1), (3, 25, 2), (3, 26, 3), (3, 12, 4), (3, 27, 5),
(3, 28, 6), (3, 29, 7), (3, 30, 8), (3, 31, 9), (3, 32, 10),
(3, 18, 11), (3, 33, 12), (3, 34, 13), (3, 35, 14), (3, 36, 15),
(3, 37, 16), (3, 38, 17), (3, 39, 18), (3, 24, 19);

-- 2号线外环停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(4, 24, 1), (4, 39, 2), (4, 38, 3), (4, 37, 4), (4, 36, 5),
(4, 35, 6), (4, 34, 7), (4, 33, 8), (4, 18, 9), (4, 32, 10),
(4, 31, 11), (4, 30, 12), (4, 29, 13), (4, 28, 14), (4, 27, 15),
(4, 12, 16), (4, 26, 17), (4, 25, 18), (4, 24, 19);

-- 4号线南行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(5, 40, 1), (5, 41, 2), (5, 42, 3), (5, 43, 4), (5, 44, 5),
(5, 45, 6), (5, 46, 7), (5, 47, 8), (5, 48, 9), (5, 49, 10),
(5, 50, 11), (5, 24, 12), (5, 51, 13), (5, 52, 14), (5, 53, 15),
(5, 54, 16), (5, 13, 17), (5, 28, 18), (5, 55, 19), (5, 56, 20),
(5, 57, 21), (5, 58, 22), (5, 59, 23), (5, 60, 24);

-- 4号线北行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(6, 60, 1), (6, 59, 2), (6, 58, 3), (6, 57, 4), (6, 56, 5),
(6, 55, 6), (6, 28, 7), (6, 13, 8), (6, 54, 9), (6, 53, 10),
(6, 52, 11), (6, 51, 12), (6, 24, 13), (6, 50, 14), (6, 49, 15),
(6, 48, 16), (6, 47, 17), (6, 46, 18), (6, 45, 19), (6, 44, 20),
(6, 43, 21), (6, 42, 22), (6, 41, 23), (6, 40, 24);

-- 13号线东行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(7, 24, 1), (7, 61, 2), (7, 62, 3), (7, 63, 4), (7, 64, 5),
(7, 65, 6), (7, 66, 7), (7, 67, 8), (7, 68, 9), (7, 69, 10),
(7, 70, 11), (7, 71, 12), (7, 72, 13), (7, 73, 14), (7, 74, 15),
(7, 35, 16);

-- 13号线西行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(8, 35, 1), (8, 74, 2), (8, 73, 3), (8, 72, 4), (8, 71, 5),
(8, 70, 6), (8, 69, 7), (8, 68, 8), (8, 67, 9), (8, 66, 10),
(8, 65, 11), (8, 64, 12), (8, 63, 13), (8, 62, 14), (8, 61, 15),
(8, 24, 16);

-- 6号线东行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(9, 75, 1), (9, 76, 2), (9, 77, 3), (9, 78, 4), (9, 79, 5),
(9, 25, 6), (9, 52, 7), (9, 80, 8), (9, 81, 9), (9, 82, 10),
(9, 33, 11), (9, 83, 12), (9, 84, 13), (9, 85, 14), (9, 86, 15),
(9, 87, 16), (9, 88, 17), (9, 89, 18), (9, 90, 19), (9, 91, 20);

-- 6号线西行停靠站
INSERT INTO stop (route_id, station_id, seq) VALUES
(10, 91, 1), (10, 90, 2), (10, 89, 3), (10, 88, 4), (10, 87, 5),
(10, 86, 6), (10, 85, 7), (10, 84, 8), (10, 83, 9), (10, 33, 10),
(10, 82, 11), (10, 81, 12), (10, 80, 13), (10, 52, 14), (10, 25, 15),
(10, 79, 16), (10, 78, 17), (10, 77, 18), (10, 76, 19), (10, 75, 20);

-- 获取各路线的stop_id
SET @stop_1_1 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 1);
SET @stop_1_2 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 2);
SET @stop_1_3 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 3);
SET @stop_1_4 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 4);
SET @stop_1_5 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 5);
SET @stop_1_6 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 6);
SET @stop_1_7 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 7);
SET @stop_1_8 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 8);
SET @stop_1_9 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 9);
SET @stop_1_10 = (SELECT id FROM stop WHERE route_id = 1 AND station_id = 10);

SET @stop_5_40 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 40);
SET @stop_5_41 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 41);
SET @stop_5_42 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 42);
SET @stop_5_43 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 43);
SET @stop_5_44 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 44);
SET @stop_5_45 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 45);
SET @stop_5_46 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 46);
SET @stop_5_47 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 47);
SET @stop_5_48 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 48);
SET @stop_5_49 = (SELECT id FROM stop WHERE route_id = 5 AND station_id = 49);

SET @stop_7_24 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 24);
SET @stop_7_61 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 61);
SET @stop_7_62 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 62);
SET @stop_7_63 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 63);
SET @stop_7_64 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 64);
SET @stop_7_65 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 65);
SET @stop_7_66 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 66);
SET @stop_7_67 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 67);
SET @stop_7_68 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 68);
SET @stop_7_69 = (SELECT id FROM stop WHERE route_id = 7 AND station_id = 69);

-- 列车行程数据
INSERT INTO train_trip (id, route_id, train_number, run_date) VALUES
-- 1号线东行列车
(1, 1, '1001', '2023-06-01'),
(2, 1, '1003', '2023-06-01'),
(3, 1, '1005', '2023-06-01'),
(4, 1, '1007', '2023-06-01'),
(5, 1, '1009', '2023-06-01'),

-- 4号线南行列车
(11, 5, '4001', '2023-06-01'),
(12, 5, '4003', '2023-06-01'),
(13, 5, '4005', '2023-06-01'),
(14, 5, '4007', '2023-06-01'),
(15, 5, '4009', '2023-06-01'),

-- 13号线东行列车
(21, 7, '13001', '2023-06-01'),
(22, 7, '13003', '2023-06-01'),
(23, 7, '13005', '2023-06-01'),
(24, 7, '13007', '2023-06-01'),
(25, 7, '13009', '2023-06-01');

-- 时刻表数据
-- 1号线列车1001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(1, @stop_1_1, null, '05:30:00', 1),
(1, @stop_1_2, '05:32:00', '05:33:00', 2),
(1, @stop_1_3, '05:35:00', '05:36:00', 3),
(1, @stop_1_4, '05:38:00', '05:39:00', 4),
(1, @stop_1_5, '05:41:00', '05:42:00', 5),
(1, @stop_1_6, '05:44:00', '05:45:00', 6),
(1, @stop_1_7, '05:47:00', '05:48:00', 7),
(1, @stop_1_8, '05:50:00', '05:51:00', 8),
(1, @stop_1_9, '05:53:00', '05:54:00', 9),
(1, @stop_1_10, '05:56:00', '05:57:00', 10);

-- 4号线列车4001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(11, @stop_5_40, null, '06:00:00', 1),
(11, @stop_5_41, '06:03:00', '06:04:00', 2),
(11, @stop_5_42, '06:07:00', '06:08:00', 3),
(11, @stop_5_43, '06:11:00', '06:12:00', 4),
(11, @stop_5_44, '06:15:00', '06:16:00', 5),
(11, @stop_5_45, '06:19:00', '06:20:00', 6),
(11, @stop_5_46, '06:23:00', '06:24:00', 7),
(11, @stop_5_47, '06:27:00', '06:28:00', 8),
(11, @stop_5_48, '06:31:00', '06:32:00', 9),
(11, @stop_5_49, '06:35:00', '06:36:00', 10);

-- 13号线列车13001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(21, @stop_7_24, null, '05:45:00', 1),
(21, @stop_7_61, '05:49:00', '05:50:00', 2),
(21, @stop_7_62, '05:54:00', '05:55:00', 3),
(21, @stop_7_63, '05:59:00', '06:00:00', 4),
(21, @stop_7_64, '06:04:00', '06:05:00', 5),
(21, @stop_7_65, '06:09:00', '06:10:00', 6),
(21, @stop_7_66, '06:14:00', '06:15:00', 7),
(21, @stop_7_67, '06:19:00', '06:20:00', 8),
(21, @stop_7_68, '06:24:00', '06:25:00', 9),
(21, @stop_7_69, '06:29:00', '06:30:00', 10);

-- 添加更多的当天列车
-- 1号线东行列车1003的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(2, @stop_1_1, null, '06:00:00', 1),
(2, @stop_1_2, '06:02:00', '06:03:00', 2),
(2, @stop_1_3, '06:05:00', '06:06:00', 3),
(2, @stop_1_4, '06:08:00', '06:09:00', 4),
(2, @stop_1_5, '06:11:00', '06:12:00', 5),
(2, @stop_1_6, '06:14:00', '06:15:00', 6),
(2, @stop_1_7, '06:17:00', '06:18:00', 7),
(2, @stop_1_8, '06:20:00', '06:21:00', 8),
(2, @stop_1_9, '06:23:00', '06:24:00', 9),
(2, @stop_1_10, '06:26:00', '06:27:00', 10);

-- 4号线南行列车4003的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(12, @stop_5_40, null, '06:15:00', 1),
(12, @stop_5_41, '06:18:00', '06:19:00', 2),
(12, @stop_5_42, '06:22:00', '06:23:00', 3),
(12, @stop_5_43, '06:26:00', '06:27:00', 4),
(12, @stop_5_44, '06:30:00', '06:31:00', 5),
(12, @stop_5_45, '06:34:00', '06:35:00', 6),
(12, @stop_5_46, '06:38:00', '06:39:00', 7),
(12, @stop_5_47, '06:42:00', '06:43:00', 8),
(12, @stop_5_48, '06:46:00', '06:47:00', 9),
(12, @stop_5_49, '06:50:00', '06:51:00', 10);

-- 13号线东行列车13003的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(22, @stop_7_24, null, '06:00:00', 1),
(22, @stop_7_61, '06:04:00', '06:05:00', 2),
(22, @stop_7_62, '06:09:00', '06:10:00', 3),
(22, @stop_7_63, '06:14:00', '06:15:00', 4),
(22, @stop_7_64, '06:19:00', '06:20:00', 5),
(22, @stop_7_65, '06:24:00', '06:25:00', 6),
(22, @stop_7_66, '06:29:00', '06:30:00', 7),
(22, @stop_7_67, '06:34:00', '06:35:00', 8),
(22, @stop_7_68, '06:39:00', '06:40:00', 9),
(22, @stop_7_69, '06:44:00', '06:45:00', 10);

-- 添加未来日期的列车
INSERT INTO train_trip (id, route_id, train_number, run_date) VALUES
-- 1号线东行列车（明天）
(101, 1, '1001', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(102, 1, '1003', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(103, 1, '1005', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),

-- 4号线南行列车（明天）
(111, 5, '4001', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(112, 5, '4003', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(113, 5, '4005', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),

-- 13号线东行列车（明天）
(121, 7, '13001', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(122, 7, '13003', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),
(123, 7, '13005', DATE_ADD(CURDATE(), INTERVAL 1 DAY)),

-- 1号线东行列车（后天）
(201, 1, '1001', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(202, 1, '1003', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),

-- 4号线南行列车（后天）
(211, 5, '4001', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(212, 5, '4003', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),

-- 13号线东行列车（后天）
(221, 7, '13001', DATE_ADD(CURDATE(), INTERVAL 2 DAY)),
(222, 7, '13003', DATE_ADD(CURDATE(), INTERVAL 2 DAY));

-- 明天1号线列车1001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(101, @stop_1_1, null, '05:30:00', 1),
(101, @stop_1_2, '05:32:00', '05:33:00', 2),
(101, @stop_1_3, '05:35:00', '05:36:00', 3),
(101, @stop_1_4, '05:38:00', '05:39:00', 4),
(101, @stop_1_5, '05:41:00', '05:42:00', 5),
(101, @stop_1_6, '05:44:00', '05:45:00', 6),
(101, @stop_1_7, '05:47:00', '05:48:00', 7),
(101, @stop_1_8, '05:50:00', '05:51:00', 8),
(101, @stop_1_9, '05:53:00', '05:54:00', 9),
(101, @stop_1_10, '05:56:00', '05:57:00', 10);

-- 明天4号线列车4001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(111, @stop_5_40, null, '06:00:00', 1),
(111, @stop_5_41, '06:03:00', '06:04:00', 2),
(111, @stop_5_42, '06:07:00', '06:08:00', 3),
(111, @stop_5_43, '06:11:00', '06:12:00', 4),
(111, @stop_5_44, '06:15:00', '06:16:00', 5),
(111, @stop_5_45, '06:19:00', '06:20:00', 6),
(111, @stop_5_46, '06:23:00', '06:24:00', 7),
(111, @stop_5_47, '06:27:00', '06:28:00', 8),
(111, @stop_5_48, '06:31:00', '06:32:00', 9),
(111, @stop_5_49, '06:35:00', '06:36:00', 10);

-- 查询验证数据
SELECT 'Lines count:' as Info, COUNT(*) as Count FROM line
UNION ALL
SELECT 'Stations count:', COUNT(*) FROM station
UNION ALL
SELECT 'Routes count:', COUNT(*) FROM route
UNION ALL
SELECT 'Stops count:', COUNT(*) FROM stop
UNION ALL
SELECT 'Train trips count:', COUNT(*) FROM train_trip
UNION ALL
SELECT 'Stop times count:', COUNT(*) FROM stop_time; 