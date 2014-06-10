package com.pajk.diablo.im.server.command;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.pajk.diablo.im.common.enums.RequestTypeEnum;

/**
 * <pre>
 * Created by zhaoming on 14-5-29 下午3:58
 * 消息处理命令的工厂类
 * </pre>
 */
public class MessageCammandFactory {

    protected static final Logger                                logger = LoggerFactory.getLogger(MessageCammandFactory.class.getName());

    private static LoadingCache<RequestTypeEnum, MessageCommand> cache  = CacheBuilder.newBuilder().build(new CacheLoader<RequestTypeEnum, MessageCommand>() {

                                                                                                              @Override
                                                                                                              public MessageCommand load(RequestTypeEnum key)
                                                                                                                                                             throws Exception {

                                                                                                                  MessageCommand result = null;
                                                                                                                  if (key.equals(RequestTypeEnum.MESSAGE_TRANSFER)) {
                                                                                                                      result = new MessageTransferCommand();
                                                                                                                  } else if (key.equals(RequestTypeEnum.TALK)) {
                                                                                                                      result = new MessageTalkCommand();
                                                                                                                  } else if (key.equals(RequestTypeEnum.REGISTER)) {
                                                                                                                      result = new MessageRegisterCommand();
                                                                                                                  } else if (key.equals(RequestTypeEnum.GROUP_TALK)) {
                                                                                                                      result = new MessageGroupTalkCommand();
                                                                                                                  } else {
                                                                                                                      throw new IllegalArgumentException(
                                                                                                                                                         "invalid type :"
                                                                                                                                                                 + key.getType());
                                                                                                                  }

                                                                                                                  return result;
                                                                                                              }
                                                                                                          });

    /**
     * 根据类型获取指定的命令
     * 
     * @param typeEnum 请求类型
     * @return
     */
    public static MessageCommand getCommand(RequestTypeEnum typeEnum) {

        Preconditions.checkNotNull(typeEnum, "typeEnum is null!");

        try {
            return cache.get(typeEnum);
        } catch (ExecutionException e) {

            logger.error("get command error ! type : {} ", typeEnum.toString(), e);
        }

        throw new IllegalArgumentException("invalid type :" + typeEnum.toString());
    }
}
