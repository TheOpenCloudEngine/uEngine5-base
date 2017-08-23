/*
 * Created on 2004. 12. 19.
 */
package org.uengine.five;

import org.uengine.kernel.*;

import java.io.Serializable;


/**
 * @author Jinyoung Jang
 */
public class InstanceDataAppendingActivityFilter implements ActivityFilter, Serializable{

	private static final long serialVersionUID = org.uengine.kernel.GlobalContext.SERIALIZATION_UID;
	
	public void afterExecute(Activity activity, final ProcessInstance instance)
		throws Exception {
		

//		if(activity instanceof HumanActivity){
//			try{
//				RoleMapping rm = ((HumanActivity)activity).getRole().getMapping(instance);
//				rm.fill(instance);
//				if(rm == null) return;
//
//
//				JPAProcessInstance jpaProcessInstance = (JPAProcessInstance) instance.getLocalInstance();
//
//				if(
//						instance.isNew()
//						&& instance.getProcessDefinition().getInitiatorHumanActivityReference(instance.getProcessTransactionContext()).getActivity().equals(activity)
//				){
//					jpaProcessInstance.getProcessInstanceEntity().setInitEp(rm.getEndpoint());
//					jpaProcessInstance.getProcessInstanceEntity().setInitRSNM(rm.getResourceName());
//					jpaProcessInstance.getProcessInstanceEntity().setInitComCd(rm.getCompanyId());
//
//					jpaProcessInstance.getProcessInstanceEntity().setPrevCurrEp(rm.getEndpoint());
//					jpaProcessInstance.getProcessInstanceEntity().setPrevCurrRSNM(rm.getResourceName());
//
//					jpaProcessInstance.getProcessInstanceEntity().setCurrEp("");
//					jpaProcessInstance.getProcessInstanceEntity().setCurrRSNM("");
//
//
//				}
//
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
		
	}

	public void afterComplete(Activity activity, ProcessInstance instance) throws Exception {

	}

	@Override
	public void onPropertyChange(Activity activity, ProcessInstance instance, String propertyName, Object changedValue) throws Exception {

	}

	public void beforeExecute(Activity activity, ProcessInstance instance)
		throws Exception {
	}

	public void onDeploy(ProcessDefinition definition) throws Exception {
	}
//
//	public void onPropertyChange(Activity activity, ProcessInstance instance, String propertyName, Object changedValue) throws Exception {
//
//		if(activity instanceof HumanActivity && "saveEndpoint".equals(propertyName)){
//			EJBProcessInstance ejbProcessInstance = (EJBProcessInstance) instance.getLocalInstance();
//			try{
//				RoleMapping rm = ((HumanActivity)activity).getRole().getMapping(instance);
//				rm.fill(instance);
//				if(rm == null) return;
//				if(
//						instance.isNew()
//						&& instance.getProcessDefinition().getInitiatorHumanActivityReference(instance.getProcessTransactionContext()).getActivity().equals(activity)
//				){
//					ejbProcessInstance.getProcessInstanceDAO().set("initEp", rm.getEndpoint());
//					ejbProcessInstance.getProcessInstanceDAO().set("initRSNM", rm.getResourceName());
//					ejbProcessInstance.getProcessInstanceDAO().set("INITCOMCD", rm.getCompanyId());
//
//					ejbProcessInstance.getProcessInstanceDAO().set("currEp", rm.getEndpoint());
//					ejbProcessInstance.getProcessInstanceDAO().set("currRSNM", rm.getResourceName());
//				} else {
//					StringBuffer endpoint = new StringBuffer();
//					StringBuffer resourceName = new StringBuffer();
//					do {
//						if (endpoint.length() > 0) endpoint.append(";");
//						endpoint.append(rm.getEndpoint());
//
//						if (resourceName.length() > 0) resourceName.append(";");
//						resourceName.append(rm.getResourceName());
//					} while (rm.next());
//					ejbProcessInstance.getProcessInstanceDAO().set("currEp", endpoint.toString());
//					ejbProcessInstance.getProcessInstanceDAO().set("currRSNM", resourceName.toString());
//					//((EJBProcessInstance)instance).getProcessInstanceDAO().set("currACT", activity.getName().getText());
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//
//			if ( instance.isNew() && instance.isSubProcess() && !instance.getInstanceId().equals(instance.getRootProcessInstanceId())) {
//				EJBProcessInstance rootProcessInstance = (EJBProcessInstance) instance.getRootProcessInstance().getLocalInstance();
//				String initEp = (String) rootProcessInstance.getProcessInstanceDAO().get("initEp");
//				String initRSNM = (String) rootProcessInstance.getProcessInstanceDAO().get("initRSNM");
//				String initComcode = (String) rootProcessInstance.getProcessInstanceDAO().get("INITCOMCD");
//				ejbProcessInstance.getProcessInstanceDAO().set("initEp", initEp);
//				ejbProcessInstance.getProcessInstanceDAO().set("initRSNM", initRSNM);
//				ejbProcessInstance.getProcessInstanceDAO().set("INITCOMCD", initComcode);
//
//				ejbProcessInstance.getProcessInstanceDAO().set("currEp",initEp);
//				ejbProcessInstance.getProcessInstanceDAO().set("currRSNM", initRSNM);
//			}
//		}
//	}
}
