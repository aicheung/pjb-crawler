package com.aicheung.pjbcrawler.service;

import com.aicheung.pjbcrawler.domain.Image;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Image.
 */
public interface ImageService {

    /**
     * Save a image.
     *
     * @param image the entity to save
     * @return the persisted entity
     */
    Image save(Image image);

    /**
     * Get all the images.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Image> findAll(Pageable pageable);

    /**
     * Get all the Image with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<Image> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" image.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Image> findOne(String id);

    /**
     * Delete the "id" image.
     *
     * @param id the id of the entity
     */
    void delete(String id);
}
