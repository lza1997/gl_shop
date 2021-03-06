package com.glshop.net.logic.message;

import android.content.Context;
import android.os.Message;

import com.glshop.net.common.GlobalConfig;
import com.glshop.net.common.GlobalConstants.DataReqType;
import com.glshop.net.common.GlobalMessageType.MsgCenterMessageType;
import com.glshop.net.logic.basic.BasicLogic;
import com.glshop.net.logic.cache.DataCenter;
import com.glshop.net.logic.cache.DataCenter.DataType;
import com.glshop.net.logic.db.dao.message.IMessageDao;
import com.glshop.net.logic.db.dao.message.MessageDao;
import com.glshop.net.logic.model.RespInfo;
import com.glshop.platform.api.DataConstants;
import com.glshop.platform.api.IReturnCallback;
import com.glshop.platform.api.base.CommonResult;
import com.glshop.platform.api.message.DelMessageReq;
import com.glshop.platform.api.message.GetMessageInfoReq;
import com.glshop.platform.api.message.GetMessageListReq;
import com.glshop.platform.api.message.GetUnreadedMsgNumReq;
import com.glshop.platform.api.message.RptMessageReadedReq;
import com.glshop.platform.api.message.data.GetMessageInfoResult;
import com.glshop.platform.api.message.data.GetMessageListResult;
import com.glshop.platform.api.message.data.GetUnreadedMsgResult;
import com.glshop.platform.api.message.data.model.MessageInfoModel;
import com.glshop.platform.net.base.ProtocolType.ResponseEvent;
import com.glshop.platform.utils.BeanUtils;
import com.glshop.platform.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author : 叶跃丰
 * @version : 1.0
 *          Create Date  : 2014-8-4 下午4:26:00
 * @Description : 消息中心业务接口实现
 * @Copyright : GL. All Rights Reserved
 * @Company : 深圳市国立数码动画有限公司
 */
public class MessageLogic extends BasicLogic implements IMessageLogic {

    public MessageLogic(Context context) {
        super(context);
    }

    @Override
    public int getUnreadedNumber(String account, String companyId) {
        int count = getUnreadedNumberFromLocal(account);
        getUnreadedNumberFromServer(companyId);
        return count;
    }

    @Override
    public int getUnreadedNumberFromLocal(String account) {
        IMessageDao messageDao = MessageDao.getInstance(mcontext);
        return messageDao.queryUnreadedNumber(mcontext, account);
    }

    @Override
    public void getUnreadedNumberFromServer(String companyId) {
        GetUnreadedMsgNumReq req = new GetUnreadedMsgNumReq(this, new IReturnCallback<GetUnreadedMsgResult>() {

            @Override
            public void onReturn(Object invoker, ResponseEvent event, GetUnreadedMsgResult result) {
                if (ResponseEvent.isFinish(event)) {
                    Logger.i(TAG, "GetUnreadedMsgResult = " + result.toString());
                    Message message = new Message();
                    RespInfo respInfo = getOprRespInfo(result);
                    message.obj = respInfo;
                    if (result.isSuccess()) {
                        message.what = MsgCenterMessageType.MSG_GET_SERVER_UNREAD_NUM_SUCCESS;
                        respInfo.data = result.totalSize;
                        GlobalConfig.getInstance().setUnreadMsgNum(result.totalSize);
                    } else {
                        message.what = MsgCenterMessageType.MSG_GET_SERVER_UNREAD_NUM_FAILED;
                    }
                    sendMessage(message);
                }
            }
        });
        req.companyId = companyId;
        req.exec();
    }

    @Override
    public List<MessageInfoModel> getMessageList(String account, String companyId) {
        return getMessageListFromLocal(account);
    }

    @Override
    public List<MessageInfoModel> getMessageListFromLocal(String account) {
        IMessageDao messageDao = MessageDao.getInstance(mcontext);
        return messageDao.queryMessageInfo(mcontext, account);
    }

    @Override
    public void getMessageInfo(String id) {
        GetMessageInfoReq req = new GetMessageInfoReq(this, new IReturnCallback<GetMessageInfoResult>() {

            @Override
            public void onReturn(Object invoker, ResponseEvent event, GetMessageInfoResult result) {
                if (ResponseEvent.isFinish(event)) {
                    Logger.i(TAG, "GetMessageInfoResult = " + result.toString());
                    Message message = new Message();
                    message.obj = getOprRespInfo(result);
                    if (result.isSuccess()) {
                        message.what = MsgCenterMessageType.MSG_GET_MESSAGE_INFO_SUCCESS;
                    } else {
                        message.what = MsgCenterMessageType.MSG_GET_MESSAGE_INFO_FAILED;
                    }
                    sendMessage(message);
                }
            }
        });
        req.id = id;
        req.exec();
    }

    @Override
    public void getMessageListFromServer(String companyId, final DataConstants.SystemMsgType type, int pageIndex, int pageSize, final DataReqType reqType) {

        GetMessageListReq req = new GetMessageListReq(this, new IReturnCallback<GetMessageListResult>() {

            @Override
            public void onReturn(Object invoker, ResponseEvent event, GetMessageListResult result) {
                if (ResponseEvent.isFinish(event)) {
                    Logger.i(TAG, "GetMessageListResult = " + result.toString());
                    Message message = new Message();
                    RespInfo respInfo = getOprRespInfo(result);
                    respInfo.intArg1 = reqType.toValue();
                    respInfo.intArg2=type.toValue();
                    message.obj = respInfo;
                    if (result.isSuccess()) {

//                        respInfo.intArg2 = result.unreadTotalSize;

                        Logger.d(TAG, "type=" + type.toValue());
                        if (DataConstants.SystemMsgType.ALL == type) {
                            DataCenter.getInstance().addData(result.datas, DataType.MESSAGE_ALL_LIST, reqType);
                        } else if (DataConstants.SystemMsgType.SYSTEM == type) {
                            DataCenter.getInstance().addData(result.datas, DataType.MESSAGE_SYSTEM_LIST, reqType);
                        } else if (DataConstants.SystemMsgType.ACTIVE == type) {
                            DataCenter.getInstance().addData(result.datas, DataType.MESSAGE_ACTIVITY_LIST, reqType);
                        } else if (DataConstants.SystemMsgType.TRANSACTION == type) {
                            DataCenter.getInstance().addData(result.datas, DataType.MESSAGE_TRANSACTION_LIST, reqType);
                        } else if (DataConstants.SystemMsgType.ADVISORY == type) {
                            DataCenter.getInstance().addData(result.datas, DataType.MESSAGE_INFO_LIST, reqType);
                        }
                        message.what = MsgCenterMessageType.MSG_GET_MESSAGE_LIST_SUCCESS;
                        respInfo.data = result.datas == null ? 0 : result.datas.size();
                    } else {
                        message.what = MsgCenterMessageType.MSG_GET_MESSAGE_LIST_FAILED;
                    }
                    sendMessage(message);
                }
            }
        });
        String req_type = "";
        if (DataConstants.SystemMsgType.ALL == type) {
            req_type = "";
        } else if (DataConstants.SystemMsgType.SYSTEM == type) {
            req_type = "1";
        } else if (DataConstants.SystemMsgType.ACTIVE == type) {
            req_type = "2";
        } else if (DataConstants.SystemMsgType.TRANSACTION == type) {
            req_type = "3";
        } else if (DataConstants.SystemMsgType.ADVISORY == type) {
            req_type = "4";
        }
        req.companyId = companyId;
        req.pageIndex = pageIndex;
        req.pageSize = pageSize;
        req.type = req_type;
        req.exec();
    }

    @Override
    public List<MessageInfoModel> getUnreportedMessageList(String account) {
        IMessageDao messageDao = MessageDao.getInstance(mcontext);
        return messageDao.queryUnreportedMessageList(mcontext, account);
    }

    @Override
    public boolean updateMesageStatus(MessageInfoModel info) {
        IMessageDao messageDao = MessageDao.getInstance(mcontext);
        return messageDao.updateMessageInfo(mcontext, info);
    }

    @Override
    public boolean updateMesageStatus(List<MessageInfoModel> list) {
        boolean result = true;
        if (BeanUtils.isNotEmpty(list)) {
            for (MessageInfoModel info : list) {
                boolean subResult = updateMesageStatus(info);
                if (!subResult) {
                    result = false;
                }
            }
        }
        return result;
    }

    @Override
    public void reportMessage(MessageInfoModel info) {
        reportMessage(Arrays.asList(info));
    }

    @Override
    public void reportMessage(List<MessageInfoModel> list) {
        final List<String> idList = new ArrayList<String>();
        RptMessageReadedReq req = new RptMessageReadedReq(this, new IReturnCallback<CommonResult>() {

            @Override
            public void onReturn(Object invoker, ResponseEvent event, CommonResult result) {
                if (ResponseEvent.isFinish(event)) {
                    Logger.i(TAG, "ReportMessageResult = " + result.toString());
                    Message message = new Message();
                    RespInfo respInfo = getOprRespInfo(result);
                    message.obj = respInfo;
                    if (result.isSuccess()) {
                        message.what = MsgCenterMessageType.MSG_REPORT_MESSAGE_READED_SUCCESS;
                        respInfo.data = idList;
                    } else {
                        message.what = MsgCenterMessageType.MSG_REPORT_MESSAGE_READED_FAILED;
                    }
                    sendMessage(message);
                }
            }
        });
        if (BeanUtils.isNotEmpty(list)) {
            for (MessageInfoModel info : list) {
                idList.add(info.id);
            }
        }
        req.idList = idList;
        req.exec();
    }

    @Override
    public void delMessage(List<MessageInfoModel> ids) {
        final List<String> idList = new ArrayList<String>();
        DelMessageReq req = new DelMessageReq(this, new IReturnCallback<CommonResult>() {
            @Override
            public void onReturn(Object invoker, ResponseEvent event, CommonResult result) {
                if (ResponseEvent.isFinish(event)) {
                    Logger.i(TAG, "ReportMessageResult = " + result.toString());
                    Message message = new Message();
                    RespInfo respInfo = getOprRespInfo(result);
                    message.obj = respInfo;
                    if (result.isSuccess()) {
                        message.what = MsgCenterMessageType.MSG_DEL_MSG_SUCCESS;
                    } else {
                        message.what = MsgCenterMessageType.MSG_DEL_MSG_FAILED;
                    }
                    sendMessage(message);
                }
            }
        });
        if (BeanUtils.isNotEmpty(ids)) {
            for (MessageInfoModel info : ids) {
                idList.add(info.id);
            }
        }
        req.msgids = idList;
        req.exec();
    }

}
