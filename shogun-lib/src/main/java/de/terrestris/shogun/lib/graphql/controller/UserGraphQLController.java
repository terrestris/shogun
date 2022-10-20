package de.terrestris.shogun.lib.graphql.controller;

import de.terrestris.shogun.lib.graphql.dto.MutateUser;
import de.terrestris.shogun.lib.model.User;
import de.terrestris.shogun.lib.service.UserService;
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
public class UserGraphQLController extends BaseGraphQLController<User, UserService> {

    @QueryMapping
    public List<User> allUsers() {
        return super.findAll();
    }

    @QueryMapping
    public Optional<User> userById(@Argument("id") Long id) {
        return super.findOne(id);
    }

    @QueryMapping
    public Optional<User> userByIdAndTime(@Argument("id") Long id, @Argument("time") OffsetDateTime time) {
        return super.findOneForTime(id, time);
    }

    @QueryMapping
    public Optional<Revision<Integer, User>> userByIdAndRevision(@Argument("id") Long id, @Argument("rev") Integer revId) {
        return super.findRevision(id, revId);
    }

    @QueryMapping
    public Revisions<Integer, User> userRevisionsById(@Argument("id") Long id) {
        return super.findRevisions(id);
    }

    @QueryMapping
    public List<User> allUsersByIds(@Argument("ids") List<Long> ids) {
        return super.findAllByIds(ids);
    }

    @MutationMapping
    public User createUser(@Argument("entity") MutateUser mutateUser) {
        return super.create(mutateUser);
    }

    @MutationMapping
    public User updateUser(@Argument("id") Long id, @Argument("entity") MutateUser mutateUser) {
        return super.update(id, mutateUser);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument("id") Long id) {
        return super.delete(id);
    }

}
