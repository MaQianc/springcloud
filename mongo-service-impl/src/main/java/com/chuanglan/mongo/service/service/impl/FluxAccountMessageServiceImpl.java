package com.chuanglan.mongo.service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.chuanglan.advert.common.util.DateTimeUtil;
import com.chuanglan.mongo.service.documents.MessageDailyStatis;
import com.chuanglan.mongo.service.parameter.FluxAccountQueryMessageParameter;
import com.chuanglan.mongo.service.service.FluxAccountMessageService;
import com.chuanglan.mongo.service.vo.MessageDailyStatisVo;
import com.chuanglan.mongo.service.vo.TemplateSendStatusVo;

@Service
public class FluxAccountMessageServiceImpl implements FluxAccountMessageService {

    @Autowired
    private MongoTemplate template;

    /**
     * 流量主平台查询数据统计
     * @param parameter
     * @return
     */
    @Override
    public Page<MessageDailyStatisVo> queryPage(FluxAccountQueryMessageParameter parameter) {
        Criteria criteria = new Criteria();
        criteria.and("statis_time").lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()))
                .gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime()));
        if (parameter.getTemplateId() != null) {
            criteria.and("template_id").is(parameter.getTemplateId());
        }
        if (StringUtils.isNotBlank(parameter.getTemplateSubject())) {
            criteria.and("template_subject").is(parameter.getTemplateSubject());
        }
        if(!CollectionUtils.isEmpty(parameter.getFluxAccounts())){
            criteria.and("flux_account").in(parameter.getFluxAccounts());
        }
        if(StringUtils.isNotBlank(parameter.getFluxAccount())){
            criteria.and("flux_account").is(parameter.getFluxAccount());
        }
        if(StringUtils.isNotBlank(parameter.getFluxName())) {
            criteria.and("flux_name").regex(Pattern.compile("^"+parameter.getFluxName()+".*$", Pattern.CASE_INSENSITIVE));
        }
        if(StringUtils.isNotBlank(parameter.getAdvertiserName())) {
            criteria.and("advertiser_name").regex(Pattern.compile("^"+parameter.getAdvertiserName()+".*$", Pattern.CASE_INSENSITIVE));
        }
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "statis_time"));
        query.addCriteria(criteria);
        long count = template.count(query, MessageDailyStatis.class);
        Pageable pageable = PageRequest.of(parameter.getPageNumber(), parameter.getPageSize());
        query.with(pageable);
        List<MessageDailyStatis> messages = template.find(query, MessageDailyStatis.class);

        List<MessageDailyStatisVo> vos = new ArrayList<>();
        messages.forEach((message) -> {
            MessageDailyStatisVo vo = new MessageDailyStatisVo();
            BeanUtils.copyProperties(message, vo, "id");
            vos.add(vo);
        });
        Page<MessageDailyStatisVo> pageList = new PageImpl<>(vos, pageable, count);
        return pageList;
    }

    @Override
    public List<MessageDailyStatisVo> query(FluxAccountQueryMessageParameter parameter) {
        Criteria criteria = new Criteria();
        criteria.and("statis_time").lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()))
                .gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime()));
        if (parameter.getTemplateId() != null) {
            criteria.and("template_id").is(parameter.getTemplateId());
        }
        if (StringUtils.isNotBlank(parameter.getTemplateSubject())) {
            criteria.and("template_subject").is(parameter.getTemplateSubject());
        }
        if(!CollectionUtils.isEmpty(parameter.getFluxAccounts())){
            criteria.and("flux_account").in(parameter.getFluxAccounts());
        }
        if(StringUtils.isNotBlank(parameter.getFluxAccount())){
            criteria.and("flux_account").is(parameter.getFluxAccount());
        }
        if(StringUtils.isNotBlank(parameter.getFluxName())) {
            criteria.and("flux_name").regex(Pattern.compile("^"+parameter.getFluxName()+".*$", Pattern.CASE_INSENSITIVE));
        }
        if(StringUtils.isNotBlank(parameter.getAdvertiserName())) {
            criteria.and("advertiser_name").regex(Pattern.compile("^"+parameter.getAdvertiserName()+".*$", Pattern.CASE_INSENSITIVE));
        }
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "statis_time"));
        query.addCriteria(criteria);
        List<MessageDailyStatis> messages = template.find(query, MessageDailyStatis.class);

        List<MessageDailyStatisVo> vos = new ArrayList<>();
        messages.forEach((message) -> {
            MessageDailyStatisVo vo = new MessageDailyStatisVo();
            BeanUtils.copyProperties(message, vo, "id");
            vos.add(vo);
        });
        return vos;
    }


    @Override
    public List<TemplateSendStatusVo> queryFluxTemplateSendStatus(FluxAccountQueryMessageParameter parameter) {
        Criteria criteria = Criteria.where("statis_time").
                gte(DateTimeUtil.getBeginTimeOfDate(parameter.getStartTime())).lte(DateTimeUtil.getEndTimeOfDate(parameter.getEndTime()));
        if(parameter.getTemplateId()!=null){
            criteria.and("template_id").is(parameter.getTemplateId());
        }
        if(StringUtils.isNotBlank(parameter.getAdvertiserName())){
            criteria.and("advertiser_name").is(parameter.getAdvertiserName());
        }
        if(StringUtils.isNotBlank(parameter.getFluxName())){
            criteria.and("flux_name").is(parameter.getFluxName());
        }
        if(!CollectionUtils.isEmpty(parameter.getFluxAccounts())){
            criteria.and("flux_account").in(parameter.getFluxAccounts());
        }
        if(StringUtils.isNotBlank(parameter.getTemplateSubject())){
            criteria.and("template_subject").is(parameter.getTemplateSubject());
        }
        if(StringUtils.isNotBlank(parameter.getFluxAccount())){
            criteria.and("flux_account").is(parameter.getFluxAccount());
        }
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group().sum("unkown_count").as("unkownCount")
                        .sum("success_count").as("successCount")
                        .sum("failed_count").as("failedCount").sum("timeout_count").as("timeoutCount").sum("total_count").as("totalCount")
        .max("statis_time").as("statisTime"));
        List<TemplateSendStatusVo> list = this.template.aggregate(aggregation, "message_daily_statis", TemplateSendStatusVo.class).getMappedResults();
        return list;
    }
}
