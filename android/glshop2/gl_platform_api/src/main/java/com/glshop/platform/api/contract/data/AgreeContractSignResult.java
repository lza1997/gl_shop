package com.glshop.platform.api.contract.data;

import com.glshop.platform.api.base.CommonResult;
import com.glshop.platform.api.contract.data.model.AgreeContractInfoModel;

/**
 * @Description : 待确认的合同签订结果
 * @Copyright   : GL. All Rights Reserved
 * @Company     : 深圳市国立数码动画有限公司
 * @author      : 叶跃丰
 * @version     : 1.0
 * Create Date  : 2014-7-24 上午9:44:05
 */
public class AgreeContractSignResult extends CommonResult {

	/**
	 * 确认结果
	 */
	public AgreeContractInfoModel data;

}
