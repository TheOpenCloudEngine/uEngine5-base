package org.uengine.five.service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.metaworks.dwr.MetaworksRemoteService;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;
import org.uengine.five.entity.ProcessInstanceEntity;
import org.uengine.kernel.ProcessInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by uengine on 2017. 11. 11..
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Relation(value = "instance", collectionRelation = "instances")
public class InstanceResource extends ResourceSupport {

    String name;

    String instanceId;
        public String getInstanceId() {
            return instanceId;
        }
        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }


    public InstanceResource(){}

    public InstanceResource(ProcessInstance processInstance) throws Exception {
        setName(processInstance.getName());
        setInstanceId(processInstance.getInstanceId());

        add(
                linkTo(
                        methodOn(InstanceService.class).getInstance(processInstance.getInstanceId())
                ).withSelfRel()
        );

        add(
                linkTo(
                        methodOn(InstanceService.class).getProcessVariables(processInstance.getInstanceId())
                ).withRel("variables")
        );

        add(
                linkTo(
                        methodOn(org.uengine.five.service.RoleMappingServiceImpl.class).getRoleMapping(processInstance.getInstanceId(), "")
                ).withRel("role-mapping")
        );

        add(
                linkTo(
                        methodOn(DefinitionService.class).getDefinition(processInstance.getProcessDefinition().getId())
                ).withRel("definition")
        );

        if(!processInstance.isRunning(""))
            add(
                    linkTo(
                            methodOn(InstanceService.class).start(processInstance.getInstanceId())
                    ).withRel("start")
            );
        else{

            //TODO: create stop method
            add(
                    linkTo(
                            methodOn(InstanceService.class).stop(processInstance.getInstanceId())
                    ).withRel("stop")
            );

            //TODO: create resume method
            if(processInstance.isSuspended("")){
                add(
                        linkTo(
                                methodOn(InstanceService.class).resume(processInstance.getInstanceId())
                        ).withRel("resume")
                );

            }else{
                //TODO: create suspend method
                add(
                        linkTo(
                                methodOn(InstanceService.class).stop(processInstance.getInstanceId())
                        ).withRel("suspend")
                );

            }
        }

        EntityLinks entityLinks = MetaworksRemoteService.getInstance().getBeanFactory().getBean(EntityLinks.class);

        if(entityLinks!=null){
            add(
                    entityLinks.linkForSingleResource(ProcessInstanceEntity.class, new Long(processInstance.getInstanceId())).withRel("entity")
            );
        }

    }

    public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

}
