package com.chuanglan.mongo.service.resp;

public class TemplateSendCountResp {

    /**
     * 广告Id
     */
    private Integer templateId;

    /**
     * 总成功发送
     */
    private Integer successCount;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
}
