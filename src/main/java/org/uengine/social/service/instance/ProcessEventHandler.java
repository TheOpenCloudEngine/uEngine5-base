package org.uengine.social.service.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.uengine.social.service.rolemapping.RoleMapping;
import org.uengine.social.service.worklist.WorkList;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 4..
 */
@RepositoryEventHandler(ProcessInstance.class)
public class ProcessEventHandler {
    @HandleBeforeCreate
    public void handleBeforeCreate(ProcessInstance instance) {
        this.saveWithRelations(instance);
    }

    @HandleBeforeSave
    public void handleBeforeSave(ProcessInstance instance) {
        this.saveWithRelations(instance);
    }

    private void saveWithRelations(ProcessInstance instance) {
        if (instance.getDummyRoleMappings() != null && instance.getDummyRoleMappings().size() > 0) {
            instance.setRoleMappings(new ArrayList<RoleMapping>());
            for (DummyRoleMapping dummyRoleMapping : instance.getDummyRoleMappings()) {
                Map map = new ObjectMapper().convertValue(dummyRoleMapping, Map.class);
                RoleMapping roleMapping = new ObjectMapper().convertValue(map, RoleMapping.class);
                roleMapping.setProcessInstance(instance);
                instance.getRoleMappings().add(roleMapping);
            }
        }

        if (instance.getDummyWorkLists() != null && instance.getDummyWorkLists().size() > 0) {
            instance.setWorkLists(new ArrayList<WorkList>());
            for (DummyWorkList dummyWorkList : instance.getDummyWorkLists()) {
                Map map = new ObjectMapper().convertValue(dummyWorkList, Map.class);
                WorkList workList = new ObjectMapper().convertValue(map, WorkList.class);
                workList.setProcessInstance(instance);
                instance.getWorkLists().add(workList);
            }
        }
    }
}
