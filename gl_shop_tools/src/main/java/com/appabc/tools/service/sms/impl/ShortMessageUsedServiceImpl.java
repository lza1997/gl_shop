/**
 *
 */
package com.appabc.tools.service.sms.impl;

import com.appabc.bean.pvo.TShortMessageUsed;
import com.appabc.common.base.QueryContext;
import com.appabc.tools.dao.sms.IShortMessageUsedDao;
import com.appabc.tools.service.sms.IShortMessageUsedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description : 
 * @Copyright   : GL. All Rights Reserved
 * @Company     : 江苏国立网络技术有限公司
 * @author      : 杨跃红
 * @version     : 1.0
 * Create Date  : 2014年9月9日 下午2:22:03
 */
@Service
@Transactional(propagation=Propagation.REQUIRED)
public class ShortMessageUsedServiceImpl implements IShortMessageUsedService{

	@Autowired
	private IShortMessageUsedDao shortMessageUsedDao;
	
	public void add(TShortMessageUsed entity) {
		shortMessageUsedDao.save(entity);
	}

	public void modify(TShortMessageUsed entity) {
		shortMessageUsedDao.update(entity);
	}

	public void delete(TShortMessageUsed entity) {
		this.shortMessageUsedDao.delete(entity);
	}

	public void delete(Serializable id) {
		shortMessageUsedDao.delete(id);
	}

	public TShortMessageUsed query(TShortMessageUsed entity) {
		return shortMessageUsedDao.query(entity);
	}

	public TShortMessageUsed query(Serializable id) {
		return shortMessageUsedDao.query(id);
	}

	public List<TShortMessageUsed> queryForList(TShortMessageUsed entity) {
		return shortMessageUsedDao.queryForList(entity);
	}

	public List<TShortMessageUsed> queryForList(Map<String, ?> args) {
		return this.shortMessageUsedDao.queryForList(args);
	}

	public QueryContext<TShortMessageUsed> queryListForPagination(
			QueryContext<TShortMessageUsed> qContext) {
		return this.shortMessageUsedDao.queryListForPagination(qContext);
	}

}
