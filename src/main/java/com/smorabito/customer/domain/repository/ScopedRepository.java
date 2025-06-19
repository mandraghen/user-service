package com.smorabito.customer.domain.repository;

import com.smorabito.customer.dto.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ScopedRepository<T, I> extends JpaRepository<T, I> {
    Logger LOG = LoggerFactory.getLogger(ScopedRepository.class);

    Optional<T> findFullById(I id);

    Optional<T> findBasicById(I id);

    default Optional<T> find(I id, Scope scope) {
        if (scope == null) {
            LOG.debug("Scope is null, returning response with basic view for ID: {}", id);
            return this.findBasicById(id);
        }

        return switch (scope) {
            case FULL -> this.findFullById(id);
            case BASIC -> this.findBasicById(id);
            case ID_ONLY -> this.findById(id);
        };
    }
}
