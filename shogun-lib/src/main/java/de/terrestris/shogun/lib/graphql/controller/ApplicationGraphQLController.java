package de.terrestris.shogun.lib.graphql.controller;

import de.terrestris.shogun.lib.graphql.dto.MutateApplication;
import de.terrestris.shogun.lib.model.Application;
import de.terrestris.shogun.lib.service.ApplicationService;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ApplicationGraphQLController extends BaseGraphQLController<Application, ApplicationService> {

    @QueryMapping
    public List<Application> allApplications() {
        return super.findAll();
    }

    @QueryMapping
    public Optional<Application> applicationById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public Optional<Application> applicationByIdAndTime(@Argument("id") Long id, @Argument("time") OffsetDateTime time) {
        return super.findOneForTime(id, time);
    }

    @QueryMapping
    public Optional<Revision<Integer, Application>> applicationByIdAndRevision(@Argument("id") Long id, @Argument("rev") Integer revId) {
        return super.findRevision(id, revId);
    }

    @QueryMapping
    public Revisions<Integer, Application> applicationRevisionsById(@Argument("id") Long id) {
        return super.findRevisions(id);
    }

    @QueryMapping
    public List<Application> allApplicationsByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

    @MutationMapping
    public Application createApplication(@Argument("entity") MutateApplication mutateApplication) {
        return super.create(mutateApplication);
    }

    @MutationMapping
    public Application updateApplication(@Argument("id") Long id, @Argument("entity") MutateApplication mutateApplication) {
        return super.update(id, mutateApplication);
    }

    @MutationMapping
    public Boolean deleteApplication(@Argument("id") Long id) {
        return super.delete(id);
    }

}
