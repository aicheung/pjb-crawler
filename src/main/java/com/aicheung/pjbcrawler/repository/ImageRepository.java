package com.aicheung.pjbcrawler.repository;

import com.aicheung.pjbcrawler.domain.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the Image entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    @Query("{}")
    Page<Image> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Image> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Image> findOneWithEagerRelationships(String id);

    Image findByImageId(int i);
}
