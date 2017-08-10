package org.uengine.social.service.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.uengine.social.service.rolemapping.RoleMappingEntity;
import org.uengine.social.service.worklist.WorklistEntity;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 4..
 */
@RepositoryEventHandler(ProcessInstanceEntity.class)
public class ProcessEventHandler {
    @HandleBeforeCreate
    public void handleBeforeCreate(ProcessInstanceEntity instance) {
        this.saveWithRelations(instance);
    }

    @HandleBeforeSave
    public void handleBeforeSave(ProcessInstanceEntity instance) {
        this.saveWithRelations(instance);
    }

    private void saveWithRelations(ProcessInstanceEntity instance) {
        if (instance.getDummyRoleMappings() != null && instance.getDummyRoleMappings().size() > 0) {
            instance.setRoleMappings(new ArrayList<RoleMappingEntity>());
            for (DummyRoleMapping dummyRoleMapping : instance.getDummyRoleMappings()) {
                Map map = new ObjectMapper().convertValue(dummyRoleMapping, Map.class);
                RoleMappingEntity roleMapping = new ObjectMapper().convertValue(map, RoleMappingEntity.class);
                roleMapping.setProcessInstance(instance);
                instance.getRoleMappings().add(roleMapping);
            }
        }

        if (instance.getDummyWorkLists() != null && instance.getDummyWorkLists().size() > 0) {
            instance.setWorkLists(new ArrayList<WorklistEntity>());
            for (DummyWorkList dummyWorkList : instance.getDummyWorkLists()) {
                Map map = new ObjectMapper().convertValue(dummyWorkList, Map.class);
                WorklistEntity workList = new ObjectMapper().convertValue(map, WorklistEntity.class);
                workList.setProcessInstance(instance);
                instance.getWorkLists().add(workList);
            }
        }
    }
}
