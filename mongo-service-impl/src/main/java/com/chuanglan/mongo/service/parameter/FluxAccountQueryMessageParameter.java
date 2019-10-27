package com.chuanglan.mongo.service.parameter;

import com.chuanglan.advert.common.parameter.AbstractPageableParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper=true)
@ApiModel(description="flux page query message parameter")
@ToString(callSuper=true)
public class FluxAccountQueryMessageParameter extends AbstractPageableParameter {

    /**
     * 用来流量主平台数据权限控制
     */
    @ApiModelProperty(value="flux accounts")
    private List<String> fluxAccounts;

    /**
     * flux account number
     */
    @ApiModelProperty("flux account number")
    private String fluxAccount;

    /**
     * flux name
     */
    @ApiModelProperty("flux name")
    private String fluxName;

    /**
     * advertiser name
     */
    @ApiModelProperty("advertiser name")
    private String advertiserName;

    /**
     * 广告ID
     */
    @ApiModelProperty(value = "template id")
    private Integer templateId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value="start time",required=true)
    @NotNull(message="开始时间不能为空")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value="end time",required=true)
    @NotNull(message="结束时间不能为空")
    private Date endTime;


    /**
     * 广告主题
     */
    @ApiModelProperty("template subject")
    private String templateSubject;


}
