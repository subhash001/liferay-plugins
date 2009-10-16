/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.workflow.edoras.dao;

import com.liferay.portal.SystemException;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.workflow.edoras.NoSuchWorkflowDefinitionException;
import com.liferay.portal.workflow.edoras.dao.model.WorkflowDefinitionBridge;
import com.liferay.portal.workflow.edoras.dao.model.WorkflowEntityBridgeUtil;
import com.liferay.portal.workflow.edoras.model.WorkflowDefinition;
import com.liferay.portal.workflow.edoras.service.persistence.WorkflowDefinitionUtil;

import java.util.List;

import org.edorasframework.process.api.ex.ProcessException;
import org.edorasframework.process.api.service.ProcessModelDefinition;
import org.edorasframework.process.api.service.ProcessServiceDao;

/**
 * <a href="WorkflowDefinitionDao.java.html"><b><i>View Source</i></b></a>
 *
 * @author Micha Kiener
 */
public class WorkflowDefinitionDao
	extends AbstractWorkflowDao<WorkflowDefinitionBridge>
	implements ProcessServiceDao {

	public void clearCache() {
		WorkflowDefinitionUtil.clearCache();
	}

	public <T> void delete(T workflowEntity) {
		long primaryKey = 0;

		try {
			WorkflowDefinition workflowDefinition =
				(WorkflowDefinition)workflowEntity;

			primaryKey = workflowDefinition.getWorkflowDefinitionId();

			WorkflowDefinitionUtil.remove(primaryKey);
		}
		catch (NoSuchWorkflowDefinitionException nswde) {
			throw new ProcessException(
				"Could not delete workflow definition with id " + primaryKey,
				nswde);
		}
		catch (SystemException se) {
			throw new ProcessException(
				"Could not delete workflow definition with id " + primaryKey,
				se);
		}
	}

	public <T> T find(Class<T> clazz, Object identity) {
		long primaryKey = (Long)identity;

		try {
			WorkflowDefinition workflowDefinition =
				WorkflowDefinitionUtil.findByPrimaryKey(primaryKey);
			
			return (T) new WorkflowDefinitionBridge(workflowDefinition);
		}
		catch (NoSuchWorkflowDefinitionException nswde) {
			return null;
		}
		catch (SystemException se) {
			throw new ProcessException(
				"Could not load workflow definition with id " + primaryKey, se);
		}
	}

	public <T> T find(T workflowEntity, Object identity) {
		return (T)find(WorkflowDefinition.class, identity);
	}

	public ProcessModelDefinition findModelDefinition(
		String modelId, int modelVersion, Long tenantId) {

		long companyId = CompanyConstants.SYSTEM;

		if (tenantId != null) {
			companyId = tenantId.longValue();
		}

		try {
			WorkflowDefinition workflowDefinition =
				WorkflowDefinitionUtil.findByC_N_V(
				companyId, modelId, modelVersion);
			
			return new WorkflowDefinitionBridge(workflowDefinition);
		}
		catch (NoSuchWorkflowDefinitionException nswde) {
			return null;
		}
		catch (SystemException se) {
			throw new ProcessException(
				"Could not find workflow definition with id " + modelId +
					" and version " + modelVersion,
				se);
		}
	}

	public List loadModelDefinitions(Long tenantId) {
		try {
			List<WorkflowDefinition> workflowDefinitionList = null;
			if (tenantId == null) {
				workflowDefinitionList =
					WorkflowDefinitionUtil.findAll();
			}
			else {
				workflowDefinitionList =
					WorkflowDefinitionUtil.findByCompanyId(
					tenantId.longValue());
			}
			
			return WorkflowEntityBridgeUtil.wrapWorkflowDefinitionList(workflowDefinitionList);
		}
		catch (SystemException se) {
			throw new ProcessException(
				"Could not find workflow definitions", se);
		}
	}

	public <T> T merge(T workflowEntity) {
		return workflowEntity;
	}

	public <T> void refresh(T workflowEntity) {
	}

	public void reload(Object workflowEntity) {
	}

	public <T> void save(T workflowEntity) {
		WorkflowDefinitionBridge workflowDefinitionBridge =
			(WorkflowDefinitionBridge) workflowEntity;

		if (super.checkAndInitializeNewInstance(workflowDefinitionBridge)) {
			workflowDefinitionBridge.initializeForInsert();
		}
		else {
			workflowDefinitionBridge.initializeForUpdate();
		}

		try {
			WorkflowDefinitionUtil.update(workflowDefinitionBridge.unwrap());
		}
		catch (SystemException se) {
			throw new ProcessException(
				"Could not update workflow definition", se);
		}
	}

}