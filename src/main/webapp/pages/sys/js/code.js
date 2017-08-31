var ACTION_SEND_SMS = "10000";// 发送短信
var ACTION_CREATE_CALL = "1000";// 发起呼叫
var ACTION_CREATE_MEET = "1001";// 创建会议
var ACTION_JOIN_MEET = "1002";// 加入会议
var ACTION_LEAVE_MEET = "1003";// 离开会议
var ACTION_MEMBER_SILENT = "1004";// 禁止发言
var ACTION_MEMBER_SPEAK = "1005";// 允许发言
var ACTION_BEGIN_PLAY_VOICE = "1006";// 开始会场放音
var ACTION_STOP_PLAY_VOICE = "1007";// 停止会场放音
var ACTION_BEGIN_RECORD = "1008";// 开始会场录音
var ACTION_STOP_RECORD = "1009";// 停止会场录音
var ACTION_CLOSE_MEET = "1010";// 结束会议
var ACTION_GET_MEET_TIME = "1011";// 获取会议进行时长
var ACTION_GET_MEET_ALL_TIME = "1012";// 获取会议中所有通道的总时长
var ACTION_CALL_STATUS_REPORT = "1013";// 会议中的状态变更上报
var ACTION_GET_MEET_CALLSTATUS = "1014";// 获取会议中的与会电话状态
var ACTION_CALLLOG_REPORT = "1015";// 通话记录上报
var ACTION_MEET_REPORT_NUMBER = "1016";// 会场报数
var ACTION_CREATE_APP_CALL = "1017";// 发起APP直呼
var ACTION_VOICE_CALL = "1018";// 发起语音通知
var ACTION_GET_APP_VERSION = "1019";// 获取APP服务端当前版本号
var ACTION_CREATE_TTS = "1020";// 创建TTS
var ACTION_CREATE_CALLIN_MEET = "1023";// 创建呼入会议
var ACTION_MEET_OPER_LOCK = "1024";// 会议锁定/解锁操作

var ACTION_MONITOR_PHS = "11000";// 监控分机状态

var ACTION_POP = "00000";//来电弹屏

var ACTION_WEBSOCKET_CONNECT = "1021";// 连接WEBSOCKET
var ACTION_BYE = "1022";// 主动挂机
var ACTION_MEET_CALL_HOLD = "1025";//通话保持
var ACTION_CREATE_MEET_CALL = "1026";//发起普通电话，会议方式
var ACTION_MEET_CALL_BYE = "1027";//会议方式的普通电话挂机操作
var ACTION_MEET_CALL_HOLD_CANCEL = "1028";//取消保持

var HANGUP_TYPE_200 = "2000";// 主叫正常挂机
var HANGUP_TYPE_201 = "2001";// 被叫正常挂机
var HANGUP_TYPE_403 = "2500";// 无权限拨打号码
var HANGUP_TYPE_404 = "2501";// 被叫为空号
var HANGUP_TYPE_408 = "2502";// 被叫无人接听
var HANGUP_TYPE_486 = "2503";// 被叫正在通话中
var HANGUP_TYPE_488 = "2504";// 被叫无法接通

var YES = "3000";// 是
var NO = "3001";// 否

var SUCCESS = "4000";// 成功
var FAIL = "4001";// 失败

var REC_MODE_RING = "5000";// 录音模式－彩铃录音
var REC_MODE_TALK = "5001";// 录音模式－通话录音

var CALL_STATUS_IDLE = "6000";// 呼叫状态－空闲
var CALL_STATUS_TRING = "6001";// 呼叫状态－处理中
var CALL_STATUS_RING = "6002";// 呼叫状态－振铃
var CALL_STATUS_ANSWER = "6003";// 呼叫状态－应答
var CALL_STATUS_BYE = "6004";// 呼叫状态－挂机
var CALL_STATUS_FAIL = "6005";// 呼叫状态－失败
var CALL_STATUS_REJECT = "6006";// 呼叫状态－拒接

var CALL_STATUS_JOIN_MEET = "6007";// 呼叫状态－加入会场
var CALL_STATUS_LEAVE_MEET = "6008";// 呼叫状态－离开会场

var CALL_STATUS_CANCEL = "6009";// 呼叫状态-取消

var CALL_TYPE_CALLIN = "7000";// 呼叫类型－呼入
var CALL_TYPE_CALLOUT = "7001";// 呼叫类型－呼出

var CALL_MODE_CALL = "8000";// 通话类型－普通双呼电话
var CALL_MODE_MEET = "8001";// 通话类型－会议
var CALL_MODE_APP_CALL = "8002";// 通话类型－APP直呼电话
var CALL_MODE_VOICE_CALL = "8003";// 通话类型－语音通知

var MEET_LEVEL_AUDIENCE = "9000";// 会议级别－听众
var MEET_LEVEL_CHARIMAN = "9001";// 会议级别－主席

var MEET_TYPE_NORMAL = "1100";// 会议类型-普通会议
var MEET_TYPE_GROUP = "1101";// 会议类型-群呼会议

var CALL_MARK_MEET = "1200";//呼叫标识 会议
var CALL_MARK_CALL = "1201";//呼叫标识 电话
