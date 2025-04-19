-- 使用metro数据库
USE metro;

-- 清空原有数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reply;
TRUNCATE TABLE request;
TRUNCATE TABLE feedback;
TRUNCATE TABLE announcements;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE roles;
TRUNCATE TABLE users;
TRUNCATE TABLE stop_time;
TRUNCATE TABLE train_trip;
TRUNCATE TABLE stop;
TRUNCATE TABLE route;
TRUNCATE TABLE station;
TRUNCATE TABLE line;
SET FOREIGN_KEY_CHECKS = 1;

-- 角色数据
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_SUPER_ADMIN', '超级管理员'),
(2, 'ROLE_ADMIN', '普通管理员'),
(3, 'ROLE_USER', '普通用户'),
(4, 'ROLE_STAFF', '地铁工作人员');

-- 用户数据（密码均为加密后的"password"）
INSERT INTO users (id, username, password, enabled, money, real_name, email, phone, gender, birth_date, avatar, address, bio) VALUES
(4, 'staff2', '$2a$10$rG.dR.X9xdmxzJyqdGCH8.FQCZIlO5S7iCJwTjZ5JRNYa8wO5hqde', 1, 500.00, '王工作', 'staff2@metro.com', '13800000003', '女', '1991-05-20', 'https://randomuser.me/api/portraits/women/2.jpg', '北京市海淀区', '地铁二号线工作人员'),
(6, 'user2', '$2a$10$rG.dR.X9xdmxzJyqdGCH8.FQCZIlO5S7iCJwTjZ5JRNYa8wO5hqde', 1, 156.75, '李四', 'user2@example.com', '13900000002', '女', '1998-03-25', 'https://randomuser.me/api/portraits/women/3.jpg', '北京市海淀区中关村南大街5号', '大学生，经常坐地铁去上学'),
(7, 'user3', '$2a$10$rG.dR.X9xdmxzJyqdGCH8.FQCZIlO5S7iCJwTjZ5JRNYa8wO5hqde', 1, 378.20, '王五', 'user3@example.com', '13900000003', '男', '1985-11-08', 'https://randomuser.me/api/portraits/men/5.jpg', '北京市西城区西长安街10号', '经常出差的商务人士'),
(8, 'user4', '$2a$10$rG.dR.X9xdmxzJyqdGCH8.FQCZIlO5S7iCJwTjZ5JRNYa8wO5hqde', 1, 420.00, '赵六', 'user4@example.com', '13900000004', '女', '1992-09-18', 'https://randomuser.me/api/portraits/women/4.jpg', '北京市丰台区丰台路20号', '地铁爱好者，喜欢研究线路图'),
(9, 'user5', '$2a$10$rG.dR.X9xdmxzJyqdGCH8.FQCZIlO5S7iCJwTjZ5JRNYa8wO5hqde', 1, 50.50, '孙七', 'user5@example.com', '13900000005', '男', '1990-02-14', 'https://randomuser.me/api/portraits/men/6.jpg', '北京市东城区东四十条15号', '刚来北京工作的新人'),
(10, 'user6', '$2a$10$rG.dR.X9xdmxzJyqdGCH8.FQCZIlO5S7iCJwTjZ5JRNYa8wO5hqde', 1, 245.60, '周八', 'user6@example.com', '13900000006', '女', '1988-06-30', 'https://randomuser.me/api/portraits/women/5.jpg', '北京市朝阳区国贸三期', '金融从业者，每天坐地铁上下班'),
(11, 'user7', '$2a$10$rG.dR.X9xdmxzJyqdGCH8.FQCZIlO5S7iCJwTjZ5JRNYa8wO5hqde', 1, 530.25, '吴九', 'user7@example.com', '13900000007', '男', '1994-12-03', 'https://randomuser.me/api/portraits/men/7.jpg', '北京市海淀区清华东路35号', '理工男，地铁线路规划爱好者');

-- 用户角色关联
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- superadmin是超级管理员
(2, 2), -- admin是普通管理员
(3, 4), (4, 4), -- staff是地铁工作人员
(5, 3), (6, 3), (7, 3), (8, 3), (9, 3), (10, 3), (11, 3); -- 普通用户

-- 线路数据
INSERT INTO line (id, name, code, color, operator) VALUES
(1, '1号线', 'L1', '#CC0000', '北京地铁公司'),
(2, '2号线', 'L2', '#0000CC', '北京地铁公司'),
(3, '4号线', 'L4', '#00CC00', '北京地铁公司'),
(4, '13号线', 'L13', '#CC00CC', '北京地铁公司'),
(5, '6号线', 'L6', '#FF9900', '北京地铁公司');

-- 站点数据
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
(40, '安河桥北', 'AHQB');

-- 路线数据（每条线路两个方向）
INSERT INTO route (id, line_id, name, start_station_id, end_station_id) VALUES
-- 1号线路线
(1, 1, '苹果园 → 四惠东', 1, 23),
(2, 1, '四惠东 → 苹果园', 23, 1),
-- 2号线路线（环线）
(3, 2, '西直门 → 西直门（内环）', 24, 24),
(4, 2, '西直门 → 西直门（外环）', 24, 24),
-- 4号线路线
(5, 3, '安河桥北 → 公主坟', 40, 8),
(6, 3, '公主坟 → 安河桥北', 8, 40),
-- 13号线路线
(7, 4, '西直门 → 东直门', 24, 35),
(8, 4, '东直门 → 西直门', 35, 24),
-- 6号线路线
(9, 5, '车公庄 → 朝阳门', 25, 33),
(10, 5, '朝阳门 → 车公庄', 33, 25);

-- 停靠站数据（简化版本，每条路线选取部分站点）
-- 1号线东行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(1, 1, 1, 1),
(2, 1, 2, 2),
(3, 1, 3, 3),
(4, 1, 6, 4),
(5, 1, 8, 5),
(6, 1, 9, 6),
(7, 1, 12, 7),
(8, 1, 13, 8),
(9, 1, 17, 9),
(10, 1, 18, 10),
(11, 1, 20, 11),
(12, 1, 22, 12),
(13, 1, 23, 13);

-- 1号线西行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(14, 2, 23, 1),
(15, 2, 22, 2),
(16, 2, 20, 3),
(17, 2, 18, 4),
(18, 2, 17, 5),
(19, 2, 13, 6),
(20, 2, 12, 7),
(21, 2, 9, 8),
(22, 2, 8, 9),
(23, 2, 6, 10),
(24, 2, 3, 11),
(25, 2, 2, 12),
(26, 2, 1, 13);

-- 2号线内环停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(27, 3, 24, 1),
(28, 3, 25, 2),
(29, 3, 12, 3),
(30, 3, 28, 4),
(31, 3, 18, 5),
(32, 3, 35, 6),
(33, 3, 38, 7),
(34, 3, 24, 8);

-- 2号线外环停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(35, 4, 24, 1),
(36, 4, 38, 2),
(37, 4, 35, 3),
(38, 4, 18, 4),
(39, 4, 28, 5),
(40, 4, 12, 6),
(41, 4, 25, 7),
(42, 4, 24, 8);

-- 4号线南行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(43, 5, 40, 1),
(44, 5, 24, 2),
(45, 5, 13, 3),
(46, 5, 28, 4),
(47, 5, 8, 5);

-- 4号线北行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(48, 6, 8, 1),
(49, 6, 28, 2),
(50, 6, 13, 3),
(51, 6, 24, 4),
(52, 6, 40, 5);

-- 13号线东行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(53, 7, 24, 1),
(54, 7, 34, 2),
(55, 7, 35, 3);

-- 13号线西行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(56, 8, 35, 1),
(57, 8, 34, 2),
(58, 8, 24, 3);

-- 6号线东行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(59, 9, 25, 1),
(60, 9, 28, 2),
(61, 9, 33, 3);

-- 6号线西行停靠站
INSERT INTO stop (id, route_id, station_id, seq) VALUES
(62, 10, 33, 1),
(63, 10, 28, 2),
(64, 10, 25, 3);

-- 列车行程数据
INSERT INTO train_trip (id, route_id, train_number, run_date) VALUES
-- 1号线东行列车
(1, 1, '1001', '2023-06-01'),
(2, 1, '1003', '2023-06-01'),
(3, 1, '1005', '2023-06-01'),
-- 1号线西行列车
(4, 2, '1002', '2023-06-01'),
(5, 2, '1004', '2023-06-01'),
(6, 2, '1006', '2023-06-01'),
-- 2号线内环列车
(7, 3, '2001', '2023-06-01'),
(8, 3, '2003', '2023-06-01'),
-- 2号线外环列车
(9, 4, '2002', '2023-06-01'),
(10, 4, '2004', '2023-06-01'),
-- 4号线南行列车
(11, 5, '4001', '2023-06-01'),
(12, 5, '4003', '2023-06-01'),
-- 4号线北行列车
(13, 6, '4002', '2023-06-01'),
(14, 6, '4004', '2023-06-01'),
-- 13号线东行列车
(15, 7, '13001', '2023-06-01'),
(16, 7, '13003', '2023-06-01'),
-- 13号线西行列车
(17, 8, '13002', '2023-06-01'),
(18, 8, '13004', '2023-06-01'),
-- 6号线东行列车
(19, 9, '6001', '2023-06-01'),
(20, 9, '6003', '2023-06-01'),
-- 6号线西行列车
(21, 10, '6002', '2023-06-01'),
(22, 10, '6004', '2023-06-01');

-- 到站时刻表数据
-- 1号线列车1001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(1, 1, NULL, '05:30:00', 1),
(1, 2, '05:32:00', '05:33:00', 2),
(1, 3, '05:35:00', '05:36:00', 3),
(1, 4, '05:39:00', '05:40:00', 4),
(1, 5, '05:44:00', '05:45:00', 5),
(1, 6, '05:48:00', '05:49:00', 6),
(1, 7, '05:53:00', '05:54:00', 7),
(1, 8, '05:57:00', '05:58:00', 8),
(1, 9, '06:02:00', '06:03:00', 9),
(1, 10, '06:07:00', '06:08:00', 10),
(1, 11, '06:12:00', '06:13:00', 11),
(1, 12, '06:17:00', '06:18:00', 12),
(1, 13, '06:21:00', NULL, 13);

-- 1号线列车1002的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(4, 14, NULL, '05:45:00', 1),
(4, 15, '05:48:00', '05:49:00', 2),
(4, 16, '05:53:00', '05:54:00', 3),
(4, 17, '05:58:00', '05:59:00', 4),
(4, 18, '06:03:00', '06:04:00', 5),
(4, 19, '06:08:00', '06:09:00', 6),
(4, 20, '06:13:00', '06:14:00', 7),
(4, 21, '06:18:00', '06:19:00', 8),
(4, 22, '06:23:00', '06:24:00', 9),
(4, 23, '06:28:00', '06:29:00', 10),
(4, 24, '06:33:00', '06:34:00', 11),
(4, 25, '06:38:00', '06:39:00', 12),
(4, 26, '06:42:00', NULL, 13);

-- 2号线内环列车2001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(7, 27, NULL, '06:00:00', 1),
(7, 28, '06:03:00', '06:04:00', 2),
(7, 29, '06:07:00', '06:08:00', 3),
(7, 30, '06:11:00', '06:12:00', 4),
(7, 31, '06:16:00', '06:17:00', 5),
(7, 32, '06:22:00', '06:23:00', 6),
(7, 33, '06:28:00', '06:29:00', 7),
(7, 34, '06:35:00', NULL, 8);

-- 4号线南行列车4001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(11, 43, NULL, '06:15:00', 1),
(11, 44, '06:19:00', '06:20:00', 2),
(11, 45, '06:26:00', '06:27:00', 3),
(11, 46, '06:31:00', '06:32:00', 4),
(11, 47, '06:36:00', NULL, 5);

-- 13号线东行列车13001的时刻表
INSERT INTO stop_time (train_trip_id, stop_id, arrival_time, departure_time, stop_seq) VALUES
(15, 53, NULL, '06:30:00', 1),
(15, 54, '06:36:00', '06:37:00', 2),
(15, 55, '06:42:00', NULL, 3);

-- 公告数据
INSERT INTO announcements (id, title, content, admin_id) VALUES
(1, '1号线部分站点施工通知', '尊敬的乘客，由于1号线苹果园至古城区间进行设备维护，1号线列车在此区间将会减速运行，可能导致轻微延误，请乘客朋友们合理安排出行时间。', 1),
(2, '新版Metro App上线公告', '尊敬的乘客，我们的Metro手机应用已更新至3.0版本，新增实时到站查询、线路规划、站内设施查询等功能，欢迎下载使用。', 1),
(3, '地铁4号线延长运营时间', '为配合市政府举办的城市灯光节，4号线将在周五至周日（6月5日至6月7日）延长运营时间至24:00，末班车从公主坟站发出时间为23:30。', 2),
(4, '地铁安全乘车须知', '为确保您和他人的安全，乘坐地铁时请注意：禁止携带易燃易爆物品，乘车时请站稳扶好，照顾好老人和孩子，不要在站台边缘逗留。', 2),
(5, '地铁2号线临时调整运行图', '因车辆检修，2号线列车在6月10日至6月15日期间，早高峰（7:00-9:00）列车间隔调整为6分钟一班。', 3),
(6, '地铁积分兑换活动', '使用Metro App乘车的乘客可以累积积分，积分可兑换地铁纪念品或抵扣乘车费用。详情请查看App内"积分商城"。', 1),
(7, '防疫乘车提示', '为共同防疫，请乘客朋友在乘坐地铁时全程佩戴口罩，配合测温，保持社交距离，做好个人防护。', 3);

-- 用户反馈数据
INSERT INTO feedback (id, user_id, content, rating, created_at) VALUES
(1, 5, '地铁1号线的车厢很干净，工作人员服务态度也很好，但是高峰期太挤了，希望能增加班次。', 4, '2023-05-01 08:25:33'),
(2, 6, '地铁站内的指示牌有些地方不够清晰，有时候找不到正确的出口方向。', 3, '2023-05-02 17:42:19'),
(3, 7, '地铁App的实时到站信息很准确，帮我省了很多等待时间，非常感谢！', 5, '2023-05-03 12:15:05'),
(4, 8, '建议在部分换乘站增加座椅，特别是老年人和孕妇等候时需要休息。', 4, '2023-05-06 09:30:45'),
(5, 9, '地铁4号线的空调温度调得太低了，夏天坐地铁都要带件外套，希望能调高一点。', 2, '2023-05-10 14:22:37'),
(6, 10, '最近地铁站开始提供免费WiFi，信号很好，上下班路上可以处理邮件了，非常方便。', 5, '2023-05-15 18:05:00'),
(7, 11, '希望能在每个站台增加充电桩，现在手机电量不够用，特别是长时间出行的时候。', 3, '2023-05-18 20:35:12'),
(8, 5, '2号线的屏蔽门经常出现问题，导致列车延误，希望能加强维护。', 2, '2023-05-20 07:45:28'),
(9, 6, '地铁站的厕所很干净，比以前好多了，赞一个！', 5, '2023-05-22 16:18:55'),
(10, 7, '希望能增加无障碍设施，现在部分老站出入口只有楼梯，对行动不便的人很不方便。', 3, '2023-05-25 11:05:38');

-- 需求数据
INSERT INTO request (id, user_id, title, content, status, created_at) VALUES
(1, 5, '建议增设行李寄存区', '长途旅行携带大件行李时，乘坐地铁很不方便，建议在主要换乘站设置临时行李寄存区域。', 'pending', '2023-05-02 09:15:22'),
(2, 7, '希望延长末班车时间', '周末夜间活动结束后经常错过末班车，希望周五周六能延长运营时间至凌晨1点。', 'processing', '2023-05-05 22:40:15'),
(3, 8, '建议开通直达机场的快线', '目前去机场需要换乘多次，建议开通市中心到机场的地铁快线，减少换乘次数。', 'resolved', '2023-05-08 14:30:45'),
(4, 9, '希望地铁站增设母婴室', '带婴儿出行时，换尿布或喂奶很不方便，希望在主要地铁站增设母婴室。', 'pending', '2023-05-12 16:25:10'),
(5, 10, '建议App增加多语言支持', '有很多外国游客使用地铁，但App只有中文界面，建议增加英语、日语等多语言支持。', 'processing', '2023-05-15 10:05:33'),
(6, 11, '建议增设快速安检通道', '高峰期安检排队时间太长，建议为经常出行的乘客设置快速安检通道。', 'rejected', '2023-05-18 08:45:27'),
(7, 5, '希望地铁站增加更多的自动售票机', '部分站点自动售票机太少，高峰期需要排队很久，建议增加机器数量。', 'pending', '2023-05-20 17:30:00'),
(8, 6, '建议开通夜班公交与地铁末班车的接驳', '地铁末班车结束后，希望有接驳夜班公交，方便乘客回家。', 'processing', '2023-05-23 21:15:42'),
(9, 7, '建议在车厢内增设USB充电口', '长途乘坐地铁时，手机没电很不方便，希望在车厢内增设USB充电接口。', 'pending', '2023-05-25 12:30:18'),
(10, 8, '希望优化换乘流程', '部分换乘站步行距离太长，标识不清，建议优化换乘路线并增加更多标识。', 'processing', '2023-05-28 15:20:05');

-- 回复数据
INSERT INTO reply (id, user_id, parent_id, parent_type, content, created_at, is_admin) VALUES
-- 反馈回复
(1, 3, 1, 'FEEDBACK', '感谢您的反馈，我们正在评估增加高峰期班次的可能性，预计将在下个月进行调整。', '2023-05-01 14:30:22', 1),
(2, 1, 2, 'FEEDBACK', '感谢您的建议，我们会对站内指示牌进行全面检查，并在需要的地方增加更清晰的指引。', '2023-05-03 09:15:40', 1),
(3, 4, 5, 'FEEDBACK', '非常抱歉给您带来不适，我们会尽快调整4号线的空调温度。', '2023-05-11 10:05:25', 1),
(4, 6, 3, 'FEEDBACK', '我也非常喜欢App的实时到站功能，确实很准确！', '2023-05-04 08:30:15', 0),
(5, 1, 8, 'FEEDBACK', '我们已经注意到2号线屏蔽门的问题，技术团队正在进行集中维修，预计在下周完成。', '2023-05-21 13:25:30', 1),
-- 需求回复
(6, 2, 2, 'REQUEST', '感谢您的建议，我们正在评估周末延长运营时间的可行性，需要综合考虑客流量、运营成本等因素。', '2023-05-06 10:45:18', 1),
(7, 4, 3, 'REQUEST', '您好，关于直达机场的快线建议已经被采纳，计划在明年开通市中心至大兴国际机场的快速直达线路。', '2023-05-09 15:40:12', 1),
(8, 7, 3, 'REQUEST', '太好了！期待快线开通，这将大大方便我们出行。', '2023-05-10 09:30:05', 0),
(9, 3, 5, 'REQUEST', '感谢您的建议，我们App团队正在开发多语言版本，预计在下个版本中将支持英语和日语。', '2023-05-16 14:20:33', 1),
(10, 2, 6, 'REQUEST', '很抱歉，出于安全考虑，目前无法设置快速安检通道，我们会增加安检设备数量以提高效率。', '2023-05-19 11:15:40', 1),
(11, 11, 6, 'REQUEST', '理解安全考虑，希望能增加安检设备提高效率。', '2023-05-20 08:30:25', 0),
(12, 4, 8, 'REQUEST', '您的建议很有价值，我们正在与公交公司协商，计划开通地铁末班车与夜班公交的接驳服务。', '2023-05-24 13:45:55', 1),
(13, 3, 10, 'REQUEST', '我们正在对部分换乘站进行改造，增加更多指示牌和优化换乘路线，预计在三个月内完成改造。', '2023-05-29 10:10:18', 1); 