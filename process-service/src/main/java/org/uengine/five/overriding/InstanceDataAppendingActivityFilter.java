/*
 * Created on 2004. 12. 19.
 */
package org.uengine.five.overriding;

import org.uengine.kernel.*;

import java.io.Serializable;


/**
 * @author Jinyoung Jang
 */
public class InstanceDataAppendingActivityFilter implements ActivityFilter, Serializable{

	private static final long serialVersionUID = GlobalContext.SERIALIZATION_UID;
	
	public void afterExecute(Activity activity, final ProcessInstance instance)
		throws Exception {
		

		if(activity instanceof HumanActivity){
			try{
				RoleMapping rm = ((HumanActivity)activity).getRole().getMapping(instance);
				rm.fill(instance);
				if(rm == null) return;


				JPAProcessInstance jpaProcessInstance = (JPAProcessInstance) instance.getLocalInstance();

				if(
						instance.isNew()
						&& instance.getProcessDefinition().getInitiatorHumanActivityReference(instance.getProcessTransactionContext()).getActivity().equals(activity)
				){
					jpaProcessInstance.getProcessInstanceEntity().setInitEp(rm.getEndpoint());
					jpaProcessInstance.getProcessInstanceEntity().setInitRsNm(rm.getResourceName());
					jpaProcessInstance.getProcessInstanceEntity().setInitComCd(rm.getCompanyId());

					jpaProcessInstance.getProcessInstanceEntity().setPrevCurrEp(rm.getEndpoint());
					jpaProcessInstance.getProcessInstanceEntity().setPrevCurrRsNm(rm.getResourceName());

					jpaProcessInstance.getProcessInstanceEntity().setCurrEp("");
					jpaProcessInstance.getProcessInstanceEntity().setCurrRsNm("");


				}

			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}

	public void afterComplete(Activity activity, ProcessInstance instance) throws Exception {

	}


	public void beforeExecute(Activity activity, ProcessInstance instance)
		throws Exception {
	}

	public void onDeploy(ProcessDefinition definition) throws Exception {
	}

	public void onPropertyChange(Activity activity, ProcessInstance instance, String propertyName, Object changedValue) throws Exception {

		if(activity instanceof HumanActivity && "saveEndpoint".equals(propertyName)){
			JPAProcessInstance ejbProcessInstance = (JPAProcessInstance) instance.getLocalInstance();
			try{
				RoleMapping rm = ((HumanActivity)activity).getRole().getMapping(instance);
				rm.fill(instance);
				if(rm == null) return;
				if(
						instance.isNew()
						&& instance.getProcessDefinition().getInitiatorHumanActivityReference(instance.getProcessTransactionContext()).getActivity().equals(activity)
				){
					ejbProcessInstance.getProcessInstanceEntity().setInitEp(rm.getEndpoint());
					ejbProcessInstance.getProcessInstanceEntity().setInitRsNm(rm.getResourceName());
					ejbProcessInstance.getProcessInstanceEntity().setInitComCd(rm.getCompanyId());

					ejbProcessInstance.getProcessInstanceEntity().setCurrEp(rm.getEndpoint());
					ejbProcessInstance.getProcessInstanceEntity().setCurrRsNm(rm.getResourceName());
				} else {
					StringBuffer endpoint = new StringBuffer();
					StringBuffer resourceName = new StringBuffer();
					do {
						if (endpoint.length() > 0) endpoint.append(";");
						endpoint.append(rm.getEndpoint());

						if (resourceName.length() > 0) resourceName.append(";");
						resourceName.append(rm.getResourceName());
					} while (rm.next());

					ejbProcessInstance.getProcessInstanceEntity().setCurrEp(endpoint.toString());
					ejbProcessInstance.getProcessInstanceEntity().setCurrRsNm(resourceName.toString());
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			if ( instance.isNew() && instance.isSubProcess() && !instance.getInstanceId().equals(instance.getRootProcessInstanceId())) {
				EJBProcessInstance rootProcessInstance = (EJBProcessInstance) instance.getRootProcessInstance().getLocalInstance();
				String initEp = (String) rootProcessInstance.getProcessInstanceDAO().get("initEp");
				String initRSNM = (String) rootProcessInstance.getProcessInstanceDAO().get("initRSNM");
				String initComcode = (String) rootProcessInstance.getProcessInstanceDAO().get("INITCOMCD");
				ejbProcessInstance.getProcessInstanceEntity().setInitEp(initEp);
				ejbProcessInstance.getProcessInstanceEntity().setInitRsNm(initRSNM);
				ejbProcessInstance.getProcessInstanceEntity().setInitComCd(initComcode);

				ejbProcessInstance.getProcessInstanceEntity().setCurrEp(initEp);
				ejbProcessInstance.getProcessInstanceEntity().setCurrRsNm(initRSNM);
			}
		}
	}
}
