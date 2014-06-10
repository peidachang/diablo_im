package com.pajk.diablo.im.server.monitor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pajk.diablo.im.common.store.TairManagerProxy;
import com.pajk.diablo.im.common.util.KeyGenerate;
import com.pajk.diablo.im.common.util.PropertiesUtils;
import com.pajk.diablo.im.server.core.SysApplicationContext;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * <pre>
 * Created by zhaoming on 14-6-4 下午2:31
 * </pre>
 */
public class MonitorPage {

    private static final Logger     logger                  = LoggerFactory.getLogger(MonitorPage.class.getName());

    private static TairManagerProxy tairManagerProxy        = SysApplicationContext.getInstance().getTairManagerProxy();

    private static final String     headContent             = "<h4>\n"
                                                              + "\t系统监控\n"
                                                              + "</h4>\n"
                                                              + "<hr />\n"
                                                              + "<p>\n"
                                                              + "\t一 各个机器信息\n"
                                                              + "</p>\n"
                                                              + "<table style=\"width:100%;\" cellpadding=\"2\" cellspacing=\"0\" border=\"1\" bordercolor=\"#000000\">\n"
                                                              + "\t<tbody>\n" + "\t\t<tr>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>当前机器IP</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t客户端连接数\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>总的客户端消息数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>当机处理成功数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>当机消息丢失数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移数</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>消息转移失败数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移处理成功数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移处理失败数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移处理成功数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t</tr>";

    private static final String     machinebodyContentStart = "<tr>\n" + "\t\t\t<td>\n" + "\t\t\t\t{0}\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n" + "\t\t\t\t{1}\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n" + "\t\t\t\t{2}\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{3}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{4}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{5}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{6}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{7}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{8}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{9}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t</tr>";

    public static final String      machineBodyContentEnd   = "</tbody>\n"
                                                              + "</table>\n"
                                                              + "<br />\n"
                                                              + "<br />\n"
                                                              + "<hr />\n"
                                                              + "<p>\n"
                                                              + "\t<span>二 信息汇总</span> \n"
                                                              + "</p>\n"
                                                              + "<table style=\"width:100%;\" cellpadding=\"2\" cellspacing=\"0\" border=\"1\" bordercolor=\"#000000\">\n"
                                                              + "\t<tbody>\n" + "\t\t<tr>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>总客户端连接数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>总的客户端消息数</span><span></span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>当机处理成功数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>当机丢失数</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移数</span><span></span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移失败数</span><span></span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移成功数</span><span></span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移处理失败数</span><span></span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>消息转移处理成功数</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n" + "\t\t\t\t消息丢失率\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n" + "\t\t\t\t消息转发率\n"
                                                              + "\t\t\t</td>\n" + "\t\t</tr>";

    public static final String      totalBody               = "<tr>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{0}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{1}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{2}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{3}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{4}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{5}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{6}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{7}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n"
                                                              + "\t\t\t\t<span>{8}</span><br />\n" + "\t\t\t</td>\n"
                                                              + "\t\t\t<td>\n" + "\t\t\t\t<span>{9}</span><br />\n"
                                                              + "\t\t\t</td>\n" + "\t\t\t<td>\n" + "\t\t\t\t{10}\n"
                                                              + "\t\t\t</td>\n" + "\t\t</tr>\n" + "\t</tbody>\n"
                                                              + "</table>\n" + "<br />\n" + "<br />\n" + "<hr />\n"
                                                              + "<br />";

    private static final String     tailContent             = "tips:<br />\n"
                                                              + "<ol>\n"
                                                              + "\t<li>\n"
                                                              + "\t\t<span style=\"line-height:1.5;\">总的客户端消息数 = 当机处理成功数 + 当机消息丢失数 + 消息转移数</span> \n"
                                                              + "\t</li>\n"
                                                              + "\t<li>\n"
                                                              + "\t\t<span style=\"line-height:1.5;\">消息转移数 = 消息转移失败数 + 消息转移处理成功数</span> \n"
                                                              + "\t</li>\n"
                                                              + "\t<li>\n"
                                                              + "\t\t<span style=\"line-height:1.5;\">消息转移成功数 = 消息转移处理失败数 + 消息转移处理成功数</span> \n"
                                                              + "\t</li>\n"
                                                              + "\t<li>\n"
                                                              + "\t\t<span style=\"line-height:1.5;\">当机消息丢失数和消息转移处理失败数越高，说明用户的网络稳定性不高</span> \n"
                                                              + "\t</li>\n"
                                                              + "\t<li>\n"
                                                              + "\t\t<span style=\"line-height:1.5;\">消息转移数越高，说明节点间消息通信压力很大</span> \n"
                                                              + "\t</li>\n"
                                                              + "\t<li>\n"
                                                              + "\t\t<span style=\"line-height:1.5;\">丢失率= (</span><span style=\"line-height:1.5;\">当机消息丢失数 +&nbsp;消息转移失败数 +&nbsp;消息转移处理失败数</span><span style=\"line-height:1.5;\">)/&nbsp;</span><span style=\"line-height:1.5;\">总的客户端消息数</span><span style=\"line-height:1.5;\"></span> \n"
                                                              + "\t</li>\n"
                                                              + "</ol>\n"
                                                              + "<p>\n"
                                                              + "\t<span style=\"line-height:1.5;\"><br />\n"
                                                              + "</span> \n"
                                                              + "</p>\n"
                                                              + "<p>\n"
                                                              + "\t<span style=\"line-height:1.5;\">联系人：xuezhaoming@pajk.cn&nbsp;</span> \n"
                                                              + "</p>\n" + "<p>\n"
                                                              + "\t<span style=\"line-height:1.5;\"><br />\n"
                                                              + "</span> \n" + "</p>";

    public static ByteBuf getMonitorPage() {

        StringBuilder monitorContent = new StringBuilder(5000);
        monitorContent.append(headContent);

        Result<DataEntry> server = tairManagerProxy.get(KeyGenerate.getServerAggregateMonitorKey());
        if (TairManagerProxy.isSuccess(server)) {
            HashMap<String, MonitorVo> serverMap = (HashMap<String, MonitorVo>) server.getValue().getValue();

            long clientCallTotalNumsAll = 0; // 客户端数据处理的总数
            long clientCallFailNumsAll = 0; // 客户端数据处理的失败数
            long clientCallSuccessNumsAll = 0; // 客户端数据处理的成功数
            long serverTransferCallTotalNumsAll = 0; // 服务端数据转发总数
            long serverTransferCallFailNumsAll = 0; // 服务端数据转发失败数
            long serverTransferCallSuccessNumsAll = 0; // 服务端数据转发成功数
            long serverTransferCallHandleFailNumsAll = 0; // 服务端数据转发处理失败数
            long serverTransferCallHandleSuccessNumsAll = 0; // 服务端数据转发处理成功数
            long clientNumsAll = 0; // 客户端连接数目
            float failRate = 0.0f;// 消息丢失率
            float transferRate = 0.0f; // 消息转发率

            for (MonitorVo monitorVo : serverMap.values()) {
                String monitorStr = MessageFormat.format(machinebodyContentStart, monitorVo.getMachineURI(),
                                                         monitorVo.getClientNums(), monitorVo.getClientCallTotalNums(),
                                                         monitorVo.getClientCallSuccessNums(),
                                                         monitorVo.getClientCallFailNums(),
                                                         monitorVo.getServerTransferCallTotalNums(),
                                                         monitorVo.getServerTransferCallFailNums(),
                                                         monitorVo.getServerTransferCallSuccessNums(),
                                                         monitorVo.getServerTransferCallHandleFailNums(),
                                                         monitorVo.getServerTransferCallHandleSuccessNums());

                monitorContent.append(monitorStr);
                clientCallTotalNumsAll += monitorVo.getClientCallTotalNums();
                clientCallSuccessNumsAll += monitorVo.getClientCallSuccessNums();
                clientCallFailNumsAll += monitorVo.getClientCallFailNums();
                serverTransferCallTotalNumsAll += monitorVo.getServerTransferCallTotalNums();
                serverTransferCallFailNumsAll += monitorVo.getServerTransferCallFailNums();
                serverTransferCallSuccessNumsAll += monitorVo.getServerTransferCallSuccessNums();
                serverTransferCallHandleFailNumsAll += monitorVo.getServerTransferCallHandleFailNums();
                serverTransferCallHandleSuccessNumsAll += monitorVo.getServerTransferCallHandleSuccessNums();
                clientNumsAll += monitorVo.getClientNums();
                BigDecimal failCount = new BigDecimal(clientCallFailNumsAll + serverTransferCallFailNumsAll
                                                      + serverTransferCallHandleFailNumsAll);
                if (clientCallTotalNumsAll == 0) {
                    failRate = 0;
                    transferRate = 0;
                } else {
                    failRate = failCount.divide(new BigDecimal(clientCallTotalNumsAll), 5, RoundingMode.HALF_DOWN).floatValue();
                    transferRate = new BigDecimal(serverTransferCallTotalNumsAll).divide(new BigDecimal(
                                                                                                        clientCallTotalNumsAll),
                                                                                         5, RoundingMode.HALF_DOWN).floatValue();
                }
            }

            monitorContent.append(machineBodyContentEnd).append(MessageFormat.format(totalBody,
                                                                                     clientNumsAll,
                                                                                     clientCallTotalNumsAll,
                                                                                     clientCallFailNumsAll,
                                                                                     clientCallSuccessNumsAll,
                                                                                     serverTransferCallTotalNumsAll,
                                                                                     serverTransferCallFailNumsAll,
                                                                                     serverTransferCallSuccessNumsAll,
                                                                                     serverTransferCallHandleFailNumsAll,
                                                                                     serverTransferCallHandleSuccessNumsAll,
                                                                                     failRate, transferRate)).append(tailContent);

        } else {
            logger.warn("load monitor info from tair error!");
        }

        return Unpooled.copiedBuffer(monitorContent.toString(), CharsetUtil.UTF_8);
    }

    public static void main(String[] args) throws Exception {

        String format = MessageFormat.format("hello,world", 1, 2);
        System.out.println(format);

        Properties properties = PropertiesUtils.getProperties();
        TairManagerProxy proxy = TairManagerProxy.newBuilder().withProperties(properties).build();

        String serverAggregateKey4Tair = KeyGenerate.getServerAggregateMonitorKey();

        Result<DataEntry> dataEntryResult1 = proxy.get(serverAggregateKey4Tair);
        if (TairManagerProxy.isSuccess(dataEntryResult1)) {
            HashMap<String, MonitorVo> serverMap = (HashMap<String, MonitorVo>) dataEntryResult1.getValue().getValue();
            for (Map.Entry<String, MonitorVo> objectObjectEntry : serverMap.entrySet()) {
                System.out.println("key:" + objectObjectEntry.getKey() + " -- value :" + objectObjectEntry.getValue());
            }
        } else {
            System.out.println("-------------null!");
        }

        ResultCode delete = proxy.delete(serverAggregateKey4Tair);
        System.out.println(delete.getCode() + ":" + delete.getMessage());
        Result<DataEntry> dataEntryResult2 = proxy.get(serverAggregateKey4Tair);
        if (TairManagerProxy.isSuccess(dataEntryResult2)) {
            HashMap<String, MonitorVo> serverMap = (HashMap<String, MonitorVo>) dataEntryResult2.getValue().getValue();
            for (Map.Entry<String, MonitorVo> objectObjectEntry : serverMap.entrySet()) {
                System.out.println("key:" + objectObjectEntry.getKey() + " -- value :" + objectObjectEntry.getValue());
            }
        } else {
            System.out.println("-------------null!");
        }
    }
}
