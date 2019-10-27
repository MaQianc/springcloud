package com.chuanglan.mongo.service.service;

import com.chuanglan.mongo.service.parameter.FluxAccountQueryMessageParameter;
import com.chuanglan.mongo.service.vo.MessageDailyStatisVo;
import com.chuanglan.mongo.service.vo.TemplateSendStatusVo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FluxAccountMessageService {

    /**
     * 流量主平台查询数据统计
     * @param parameter
     * @return
     */
    Page<MessageDailyStatisVo> queryPage(FluxAccountQueryMessageParameter parameter);


    List<MessageDailyStatisVo> query(FluxAccountQueryMessageParameter parameter);

    /**
     * 流量主平台查询总发送量
     * @param parameter
     * @return
     */
    List<TemplateSendStatusVo> queryFluxTemplateSendStatus(FluxAccountQueryMessageParameter parameter);
}
