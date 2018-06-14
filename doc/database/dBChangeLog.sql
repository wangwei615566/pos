-- 添加用户ID
ALTER TABLE cl_rc_scene_business_log ADD COLUMN `user_id` BIGINT(20) NULL COMMENT '用户ID' AFTER `borrow_id`;

-- 同盾贷前审核--
DROP TABLE IF EXISTS `cl_tongdun_req_log`;
CREATE TABLE `cl_tongdun_req_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(64) DEFAULT NULL COMMENT '申请订单号',
  `borrow_id` bigint(20) DEFAULT NULL COMMENT '借款标识',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户标识',
  `state` varchar(4) DEFAULT '10' COMMENT '审核状态  10 已提交申请   20 审核通过  30 审核不通过',
  `create_time` datetime DEFAULT NULL COMMENT '添加时间',
  `submit_code` varchar(10) DEFAULT NULL COMMENT '获取审核报告返回码',
  `submit_params` mediumtext COMMENT '获取审核报告响应结果',
  `report_id` varchar(64) DEFAULT '' COMMENT '风险报告编码',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '查询报告时间',
  `query_params` mediumtext COMMENT '查询报告响应结果',
  `query_code` varchar(16) DEFAULT '' COMMENT '查询报告返回码',
  `rs_state` varchar(16) DEFAULT '' COMMENT '风控结果    Accept:建议通过,Review:建议审核,Reject:建议拒绝',
  `rs_score` int(20) DEFAULT NULL COMMENT '风控分数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8 COMMENT='同盾第三方请求记录';


-- 删除不需要的支付配置息--
delete  from  arc_sys_config where code='alipay_app_id';
delete  from  arc_sys_config where code='alipay_public_key';
delete  from  arc_sys_config where code='alipay_private_key';
delete  from  arc_sys_config where code='alipay_app_apihost';

delete  from  arc_sys_config where code='bank_card_interfaceName';
delete  from  arc_sys_config where code='bank_card_apihost';
delete  from  arc_sys_config where code='bank_card_channelNo';

-- 删除系统配置中浅橙的请求地址
DELETE FROM arc_sys_config WHERE code = 'ds_qianchen_rc_url';
ALTER TABLE cl_qiancheng_req_log COMMENT='风控数据-浅橙请求结果';

-- 修改字段长度
alter table arc_rule_engine_config modify column add_ip VARCHAR(32);
-- 修改芝麻分数据类型
alter table cl_zhima modify column score decimal(12,2);

-- 修改联系人关系
INSERT INTO `arc_sys_dict`(type_code,type_name,sort,remark) VALUES ('KINSFOLK_RELATION', '直系联系人', '46', '直系联系人');

INSERT INTO `arc_sys_config` (`id`, `type`, `name`, `code`, `value`, `status`, `remark`, `creator`) VALUES (null, '80', '连连对账SFTP配置', 'lianlian_sftp', '{\"host\":\"hz-sftp1.lianlianpay.com\",\"port\":\"\",\"user\":\"\",\"passwd\":\"\",\"path\":\"\"}', '1', '连连对账SFTP配置', '1');
INSERT INTO `arc_sys_config` (`id`, `type`, `name`, `code`, `value`, `status`, `remark`, `creator`) VALUES (null, '80', '支付是否开启', 'lianlian_switch', '1', '1', '1开2关', '1');
INSERT INTO `arc_sys_config` (`id`, `type`, `name`, `code`, `value`, `status`, `remark`, `creator`) VALUES (null, '20', '借款额度', 'borrow_credit', '100,200,300,400,500,600,700,800,900,1000', '1', '借款额度', '1');
INSERT INTO `arc_sys_config` (`id`, `type`, `name`, `code`, `value`, `status`, `remark`, `creator`) VALUES (null, '20', '当日最大注册用户数', 'day_register_max', '0', '0', '当日最大注册用户数', '1');
INSERT INTO `arc_sys_config` (`id`, `type`, `name`, `code`, `value`, `status`, `remark`, `creator`) VALUES (null, '20', '版本控制', 'check_version', '{"state": "10","version": "1.0.1"}', '1', 'state是否启用10启用20禁用  version版本号', '1');

-- face++地址
INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`) VALUES ('70', 'orc请求地址', 'ocr_host', 'http://ucdevapi.ucredit.erongyun.net/faceID/ocrHost', '1', '', '1');
INSERT INTO `arc_sys_config` (`type`, `name`, `code`, `value`, `status`, `remark`, `creator`) VALUES ('70', 'face++人证对比地址', 'verify_host', 'http://ucdevapi.ucredit.erongyun.net/faceID/verify', '1', '', '1');

--系统配置逾期利率修改
UPDATE arc_sys_config SET value = '0.02,0.03,0.04' WHERE code = 'penalty_fee';

--删掉菜单中的收入统计、支付统计
UPDATE arc_sys_menu SET is_delete = 1 where name = '收入统计';
UPDATE arc_sys_menu SET is_delete = 1 where name = '支出统计';

INSERT INTO `arc_sys_menu` VALUES (null, '0', '渠道信息统计', '11244', '', null, '00000000020', null, '', null, '', '渠道信息统计', '0', '1', 'ChannelInformationStatistics', null, null, null, null);
INSERT INTO `arc_sys_menu` VALUES (null, '0', '同盾贷前审核记录', '11195', '', null, '00000000028', null, '', null, '', '同盾贷前审核记录', '0', '1', 'ShildCreditAuditRecords', null, null, null, null);
INSERT INTO `arc_sys_role_menu`(role_id,menu_id) VALUES ('1', '11288');
INSERT INTO `arc_sys_role_menu`(role_id,menu_id) VALUES ('1', '11289');
 
-- ----------------------------
-- Table structure for cl_channel_app
-- ----------------------------
DROP TABLE IF EXISTS `cl_channel_app`;
CREATE TABLE `cl_channel_app` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `channel_id` bigint(20) NOT NULL COMMENT '渠道id',
  `app_type` varchar(16) DEFAULT '' COMMENT '应用类型',
  `latest_version` varchar(16) DEFAULT '' COMMENT '最新版本',
  `min_version` varchar(16) DEFAULT '' COMMENT '最低支持版本',
  `download_url` varchar(64) DEFAULT '' COMMENT '下载地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 2017-05-10
delete  from  arc_sys_config where code='sms_apikey';
delete  from  arc_sys_config where code='sms_secretkey';

-- 2017-0-5-11 borrow表添加审核说明字段
Alter table `cl_borrow`   
  add column `remark` varchar(64) DEFAULT ''  NULL  COMMENT '备注、审核说明' after `coordinate`;

Alter table `cl_borrow`   
  add column `ip` varchar(64) DEFAULT ''  NULL  COMMENT 'ip地址' after `remark`;
  
delete  from  arc_sys_config where code='min_credit';
delete  from  arc_sys_config where code='max_credit';
delete  from  arc_sys_config where code='min_days';
delete  from  arc_sys_config where code='max_days';

-- 增加人证识别类别选择
INSERT INTO `arc_sys_config` VALUES (null, '70', '人证识别接口类别', 'verify_type', '10','1','10-face++,20-小视,30-商汤','1');

-- cl_channel_app表新增state字段
alter table `cl_channel_app` add column `state` varchar(4) DEFAULT '' COMMENT '10 启用 20 禁用' after `download_url`;

-- 补充机审通过订单
INSERT INTO `arc_sys_menu` VALUES (null, '0', '机审通过订单', '11195', '', null, '00000000015', null, '', null, '', '机审通过订单', '0', '1', 'MachinePassList', null, null, null, null);

-- 将运营商认证请求记录里的notify_params存入分表   2017-5-17  xx
DROP TABLE IF EXISTS `cl_operator_resp_detail`;
CREATE TABLE `cl_operator_resp_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `req_log_id` bigint(20) NOT NULL COMMENT '请求记录标识',
  `order_no` varchar(50) DEFAULT '' COMMENT '订单号',
  `notify_params` longtext COMMENT '异步通知结果',
  `notify_time` datetime DEFAULT NULL COMMENT '异步通知时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营商认证响应通知详情表';

INSERT INTO cl_operator_resp_detail(req_log_id, order_no, notify_params, notify_time) SELECT id, order_no, notify_params, update_time FROM cl_operator_req_log;
ALTER TABLE cl_operator_req_log DROP COLUMN `notify_params`;
ALTER TABLE cl_operator_req_log DROP COLUMN `update_time`;

--  同盾审核报告信息表 2017-5-17
CREATE TABLE `cl_tongdun_resp_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
  `req_id` bigint(20) DEFAULT NULL COMMENT '同盾请求id',
  `order_no` varchar(64) DEFAULT NULL COMMENT '同盾申请记录订单号',
  `report_id` varchar(64) DEFAULT NULL COMMENT '同盾审核报告id',
  `query_params` mediumtext COMMENT '审核报告具体信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='同盾审核报告详细信息';



INSERT into cl_tongdun_resp_detail(req_id,order_no,report_id,query_params)  select id as req_id,order_no,report_id,query_params from cl_tongdun_req_log ;

alter table `cl_tongdun_req_log`  drop column query_params; 

-- 菜单整理 2017-5-17 xx
update `arc_sys_menu` set is_delete = 1 where name = '用户认证数据统计';
update `arc_sys_menu` set is_delete = 1 where name = '审批中心';
update `arc_sys_menu` set is_delete = 1 where name = '待审批单据列表';
update `arc_sys_menu` set is_delete = 1 where name = '收入统计';
update `arc_sys_menu` set is_delete = 1 where name = '支出统计';
update `arc_sys_menu` set is_delete = 1 where name = '系统数据统计接口';
update `arc_sys_menu` set is_delete = 1 where name = '导流分析';

delete from `arc_sys_role_menu` where role_id = 17;
INSERT INTO `arc_sys_role_menu` VALUES (null, '17', '11140');
INSERT INTO `arc_sys_role_menu` VALUES (null, '17', '11161');
INSERT INTO `arc_sys_role_menu` VALUES (null, '17', '11168');
INSERT INTO `arc_sys_role_menu` VALUES (null, '17', '11280');

-- 删除sys_config中的芝麻反欺诈和行业关注黑名单的开关配置
DELETE FROM `arc_sys_config` where code = 'zhima_antiFraud_switch';
DELETE FROM `arc_sys_config` where code = 'zhima_industryConcern_switch';

-- 2017-05-23 borrow表增加字段
ALTER TABLE `cl_borrow` ADD COLUMN `audit_process`  int(2) NULL DEFAULT 0 COMMENT '审批状态（0 未审批，1 正在审批，2 已审批）';
ALTER TABLE `cl_borrow` ADD COLUMN `audit_process_time`  datetime DEFAULT NULL COMMENT '审批锁定时间';

ALTER TABLE `cl_user_base_info` ADD COLUMN `live_province`  VARCHAR(255) DEFAULT NULL COMMENT '现居住地 省';
ALTER TABLE `cl_user_base_info` ADD COLUMN `live_city`  VARCHAR(255) DEFAULT NULL COMMENT '现居住地 市';
ALTER TABLE `cl_user_base_info` ADD COLUMN `live_area`  VARCHAR(255) DEFAULT NULL COMMENT '现居住地 区';
-- 2017-08-22 user_auth表增加字段
ALTER TABLE `cl_user_auth` ADD COLUMN `phone_state_update_time` datetime DEFAULT NULL COMMENT '运营商认证状态修改时间';
-- 2017-08-23 新增表
CREATE TABLE `psaice_crawler_upload` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `token` varchar(255) DEFAULT NULL COMMENT '诚安数据上传返回唯一标识',
  `data_type` varchar(255) DEFAULT NULL COMMENT '上传数据类型 QQ JD-京东  WECHAT-微信 DINGDING-钉钉 GJJ-公积金 INSURANCE-社保 PBOC-个人版征信 OPERATOR-运营商 TERMINAL_SMS-终端短信 TERMINAL_ADDRESS_BOOK终端通讯录 TERMINAL_CALL_LOG-终端通话记录 OTHERS-其他',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updata_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='诚安爬虫信息上传表';

INSERT INTO cl_sms_tpl (type,type_name,tpl,number,state) VALUES ('lastDayInform','最后一天提醒通知','尊敬的用户，您在{$platform}借款{$loan}元，今天为最后还款期限日，请按时归还!','SMS0166985708','10');

<<<<<<< HEAD
ALTER TABLE `cl_contract` ADD COLUMN `contract_type` varchar(255) DEFAULT NULL COMMENT '文件类型 1 借款合同 2 调解协议',
=======
ALTER TABLE `cl_contract` ADD COLUMN `contract_type` varchar(255) DEFAULT NULL COMMENT '文件类型 1 借款合同 2 调解协议'


ALTER TABLE `cl_user_equipment_info` ADD COLUMN `uuid` varchar(255) DEFAULT NULL COMMENT '设备唯一标识（适用Android设备，即imei-wlanaddress如68447014881431-a0ec80ef01e9）'
ALTER TABLE `cl_user_equipment_info` ADD COLUMN `open_uuid` varchar(255) DEFAULT NULL COMMENT '设备唯一标识（适用IOS设备:18ee8ae28f3264a8f）'
ALTER TABLE `cl_user_equipment_info` ADD COLUMN `resolution` varchar(255) DEFAULT NULL COMMENT '分辨率，固定格式（1920 * 1080）'
ALTER TABLE `cl_user` ADD COLUMN `mark_channel` varchar(255) DEFAULT NULL COMMENT 'APP市场渠道'
ALTER TABLE `cl_user_equipment_info` ADD COLUMN `device_json` text COMMENT '诚安设备SDK获取终端设备信息';
>>>>>>> refs/remotes/origin/appmaster
