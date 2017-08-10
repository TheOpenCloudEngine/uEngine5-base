package org.uengine.social.service.worklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.data.rest.webmvc.RepositorySearchesResource;
import org.springframework.hateoas.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by uengine on 2017. 8. 1..
 */
@BasePathAwareController
@RequestMapping(value = "/worklist/search")
public class WorkListController implements ResourceProcessor<RepositorySearchesResource>, ResourceAssembler<WorkList, Resource<WorkList>> {

    @Autowired
    private WorkListRepository repository;

    @Autowired
    private EntityLinks entityLinks;

    @RequestMapping(value = "/findbysome", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> findByInstId(@RequestParam(value = "instId", defaultValue = "") Long instId, Pageable pageable) {
        try {
            List<WorkList> lists = repository.findByInstId(instId);
            Resources<WorkList> resources = new Resources<>(lists);

            resources.add(linkTo(methodOn(WorkListController.class).findByInstId(instId, pageable)).withSelfRel());
            return new ResponseEntity<>(resources, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    public RepositorySearchesResource process(RepositorySearchesResource resource) {

        LinkBuilder lb = entityLinks.linkFor(WorkList.class, "instId");
        resource.add(new Link(lb.toString() + "/search/findbysome{?instId}", "findbysome"));
        return resource;
    }


    @Override
    public Resource<WorkList> toResource(WorkList workList) {
        Resource<WorkList> resource = new Resource<>(workList);
        Resources<WorkList> resources = new Resources<>(new ArrayList<>());
        return resource;
    }
}