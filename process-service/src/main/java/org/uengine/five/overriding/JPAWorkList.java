package org.uengine.five.overriding;

import org.springframework.beans.factory.annotation.Autowired;
import org.uengine.five.entity.WorklistEntity;
import org.uengine.five.repository.WorklistRepository;
import org.uengine.kernel.KeyedParameter;
import org.uengine.kernel.Role;
import org.uengine.processmanager.TransactionContext;
import org.uengine.util.UEngineUtil;
import org.uengine.webservices.worklist.DefaultWorkList;
import org.uengine.webservices.worklist.WorkList;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 13..
 */
public class JPAWorkList implements WorkList {

    public String reserveWorkItem(String userId, KeyedParameter[] parameters, TransactionContext tc)
            throws RemoteException {
        Map parameterMap = getParameterMap(parameters);

        return addWorkItemImpl(null, userId, parameterMap, true, tc);
    }

    public String addWorkItem(String userId, KeyedParameter[] parameters, TransactionContext tc) throws RemoteException{
        Map parameterMap = getParameterMap(parameters);

        return addWorkItemImpl(null, userId, parameterMap, false, tc);
    }

    public String addWorkItem(String reservedTaskId, String userId, KeyedParameter[] parameters, TransactionContext tc) throws RemoteException {
        Map parameterMap = getParameterMap(parameters);

        return addWorkItemImpl(reservedTaskId, userId, parameterMap, false, tc);
    }

    @Autowired
    WorklistRepository worklistRepository;

    protected String addWorkItemImpl(String reservedTaskId, String userId, Map parameterMap, boolean isReservation, TransactionContext tc) throws RemoteException {

        try{

            Calendar startDate = Calendar.getInstance();

            if(parameterMap.containsKey("startedTime")){
                startDate.setTimeInMillis(Long.parseLong((String) parameterMap.get("startedTime")));
            }

            Calendar dueDate = null;
            try{
                String dueDateInMSStr = ""+parameterMap.get(KeyedParameter.DUEDATE);
                long dueDateInMS = Long.parseLong(dueDateInMSStr);
                dueDate = Calendar.getInstance();
                dueDate.setTimeInMillis(dueDateInMS);
            }catch(Exception e){
            }

            if(dueDate==null){
                int duration = 0;
                {
                    try{
                        String durationStr = ""+parameterMap.get(KeyedParameter.DURATION);
                        duration = Integer.parseInt(durationStr);
                    }catch(Exception e){
                    }
                }

                if(duration>0){
                    dueDate = Calendar.getInstance();
                    dueDate.setTimeInMillis(startDate.getTimeInMillis() + (long)duration * 86400000L);
                    int dayOfMonth = dueDate.get(Calendar.DAY_OF_MONTH);
                    int year = dueDate.get(Calendar.YEAR);
                    int month = dueDate.get(Calendar.MONTH);
                }
            }

            Number priority = new Integer(1);
            {
                try{
                    String priorityStr = ""+parameterMap.get(KeyedParameter.PRIORITY);
                    priority = new Integer(priorityStr);
                }catch(Exception e){
                }
            }

            final WorklistEntity wl;

            Long taskId;
            if(reservedTaskId!=null){
                taskId = new Long(reservedTaskId);
                wl = worklistRepository.findOne(taskId);
            }
            else{
                wl = new WorklistEntity();
                worklistRepository.save(wl);

                taskId = wl.getTaskId();
            }

            if(dueDate!=null)
                wl.setDueDate(dueDate.getTime());
            else
                wl.setDueDate(null);

            String definitionName = (String)parameterMap.get("definitionName");
            String instanceName = (String)parameterMap.get("instanceName");
            String instanceId = (String)parameterMap.get(KeyedParameter.INSTANCEID);

            wl.setPriority(priority);
            wl.setTool(""+parameterMap.get(KeyedParameter.TOOL));
            wl.setEndpoint(userId);

            //modified
            Timestamp startedTime;
            startedTime = new Timestamp(startDate.getTimeInMillis());
            wl.setStartDate(startedTime);

            wl.setTitle(""+parameterMap.get(KeyedParameter.TITLE));
            wl.setTrcTag(""+parameterMap.get(KeyedParameter.TRACINGTAG));
            wl.setInstId(new Long(""+parameterMap.get(KeyedParameter.INSTANCEID)));
            wl.setRootInstId(new Long(""+parameterMap.get(KeyedParameter.ROOTINSTANCEID)));
            wl.setDefId(""+parameterMap.get(KeyedParameter.PROCESSDEFINITION));
            wl.setDefName(""+parameterMap.get(KeyedParameter.PROCESSDEFINITIONNAME));
            wl.setRoleName(""+parameterMap.get("roleName"));
            wl.setRefRoleName(""+parameterMap.get("referenceRoleName"));
            wl.setResName(""+parameterMap.get("resourceName"));

//            int i=1;
//            while(parameterMap.containsKey("dispatchParam" + i)){
//                wl.setDispatchParam(" +i, ""+parameterMap.get("dispatchParam" + i));
//                i++;
//            }

            if(parameterMap.containsKey("executionScope")){
                wl.setExecScope((String)parameterMap.get("executionScope"));
                wl.setTitle(wl.getTitle() + "(" + parameterMap.get("executionScopeName") + ")");
            }

            if(parameterMap.containsKey("extValue1")){
                wl.setExt1((String) parameterMap.get("extValue1"));
            }
            if(parameterMap.containsKey("extValue2")){
                wl.setExt2((String) parameterMap.get("extValue2"));
            }
            if(parameterMap.containsKey("extValue3")){
                wl.setExt3((String) parameterMap.get("extValue3"));
            }
            if(parameterMap.containsKey("extValue4")){
                wl.setExt4((String) parameterMap.get("extValue4"));
            }
            if(parameterMap.containsKey("extValue5")){
                wl.setExt5((String) parameterMap.get("extValue5"));
            }

            //dispatching option//////
            try{
                int dispatchingOption = Integer.parseInt((String)parameterMap.get(KeyedParameter.DISPATCHINGOPTION));
                wl.setDispatchOption(dispatchingOption);
            }catch(Exception e){
                wl.setDispatchOption(Role.DISPATCHINGOPTION_ALL);
            }

            //status//////
            String defaultStatus = parameterMap.containsKey(KeyedParameter.DEFAULT_STATUS) ?
                    ""+parameterMap.get(KeyedParameter.DEFAULT_STATUS)
                    :
                    (isReservation ? DefaultWorkList.WORKITEM_STATUS_RESERVED : DefaultWorkList.WORKITEM_STATUS_NEW);
            wl.setStatus(defaultStatus);
            //

            worklistRepository.save(wl);

            return ""+taskId;

        }catch(Exception e){

            System.out.println("====================HARD-TO-FIND-ERR: pi=" + parameterMap.get(KeyedParameter.INSTANCEID) + "  tracingTag=" + parameterMap.get(KeyedParameter.TRACINGTAG));

            throw new RemoteException("ExtWorkList", e);
        }
    }

    public void cancelWorkItem(String taskID, KeyedParameter[] options, TransactionContext tc)
            throws RemoteException {

        try{

            WorklistEntity wl = worklistRepository.findOne(new Long(taskID));

            if(wl==null) return;

            wl.setStatus(DefaultWorkList.WORKITEM_STATUS_CANCELLED);
            for(int i=0; i<options.length; i++){
                KeyedParameter parameter = options[i];
                if("status".equals(parameter.getKey()) && UEngineUtil.isNotEmpty((String)parameter.getValue())){
                    wl.setStatus(((String)parameter.getValue()).toUpperCase());
                }
            }

            worklistRepository.save(wl);

        }catch(Exception e){
            throw new RemoteException("ExtWorkList", e);
        }
    }

    public void completeWorkItem(String taskID, KeyedParameter[] options, TransactionContext tc)
            throws RemoteException {

        try{
            Calendar now = Calendar.getInstance();

            WorklistEntity wl = worklistRepository.findOne(new Long(taskID));
            wl.setStatus(DefaultWorkList.WORKITEM_STATUS_COMPLETED);
            wl.setEndDate(new Timestamp(now.getTimeInMillis()));

            worklistRepository.save(wl);

        }catch(Exception e){
            throw new RemoteException("ExtWorkList", e);
        }
    }

    public void updateWorkItem(
            String taskId,
            String userId,
            KeyedParameter[] parameters,
            TransactionContext tc)
            throws RemoteException {

        try{

            WorklistEntity wlDAO = worklistRepository.findOne(new Long(taskId));

            if(userId!=null)
                wlDAO.setEndpoint(userId);

            for(int i=0; i<parameters.length; i++){
                KeyedParameter parameter = parameters[i];

                if(KeyedParameter.DISPATCHINGOPTION.equals(parameter.getKey())){
                    wlDAO.setDispatchOption(Integer.parseInt(""+parameter.getValue()));
                }else
                if("dispatchParam1".equals(parameter.getKey())){
                    wlDAO.setDispatchParam1(""+parameter.getValue());
                }else
                if(KeyedParameter.DUEDATE.equals(parameter.getKey())){
                    wlDAO.setDueDate((Date)parameter.getValue());
                }else
                if(KeyedParameter.DEFAULT_STATUS.equals(parameter.getKey())){
                    wlDAO.setStatus((String)parameter.getValue());
                }else
                if("endDate".equals(parameter.getKey())){
                    wlDAO.setEndDate((Date)parameter.getValue());
                }else
                if("saveDate".equals(parameter.getKey())){ //임시저장 시간 저장 (11.23)
                    wlDAO.setSaveDate((Date)parameter.getValue());
                }
            }

            worklistRepository.save(wlDAO);

        }catch(Exception e){
            throw new RemoteException("ExtWorkList", e);
        }
    }

    private Map getParameterMap(KeyedParameter[] parameters){
        Map parameterMap = new HashMap();

        for(int i=0; i<parameters.length; i++){
            parameterMap.put(parameters[i].getKey(), parameters[i].getValue());
        }

        return parameterMap;
    }


}
