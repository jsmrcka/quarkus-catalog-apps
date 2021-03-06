package io.quarkus.qe.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import io.quarkus.qe.configuration.Channels;
import io.quarkus.qe.data.RepositoryEntity;
import io.quarkus.qe.data.marshallers.RepositoryMarshaller;
import io.quarkus.qe.exceptions.RepositoryAlreadyExistsException;
import io.quarkus.qe.exceptions.RepositoryNotFoundException;
import io.quarkus.qe.model.Repository;
import io.quarkus.qe.model.requests.NewRepositoryRequest;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RepositoryService {

    @Inject
    @Channel(Channels.NEW_REPOSITORY)
    Emitter<NewRepositoryRequest> newRepositoryRequestEmitter;

    @Inject
    RepositoryMarshaller repositoryMarshaller;

    public Repository findById(Long id) throws RepositoryNotFoundException {
        RepositoryEntity entity = RepositoryEntity.findById(id);
        if (entity == null) {
            throw new RepositoryNotFoundException(String.format("Repository ID %d not exist.", id));
        }

        return repositoryMarshaller.fromEntity(entity);
    }

    public List<Repository> findAll(final int pageIndex, final int size) {
        return RepositoryEntity.<RepositoryEntity> findAll()
                .page(pageIndex, size)
                .stream()
                .map(repositoryMarshaller::fromEntity)
                .collect(Collectors.toList());
    }

    public void sendNewRepositoryRequest(NewRepositoryRequest request) throws RepositoryAlreadyExistsException {
        if (RepositoryEntity.find("repoUrl", request.getRepoUrl()).count() > 0) {
            throw new RepositoryAlreadyExistsException(String.format("Repository %s already exist.", request.getRepoUrl()));
        }

        newRepositoryRequestEmitter.send(request);
    }
}
