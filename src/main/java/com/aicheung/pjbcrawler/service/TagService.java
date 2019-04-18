package com.aicheung.pjbcrawler.service;

import com.aicheung.pjbcrawler.domain.Tag;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Tag.
 */
public interface TagService {

    /**
     * Save a tag.
     *
     * @param tag the entity to save
     * @return the persisted entity
     */
    Tag save(Tag tag);

    /**
     * Get all the tags.
     *
     * @return the list of entities
     */
    List<Tag> findAll();


    /**
     * Get the "id" tag.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Tag> findOne(String id);

    /**
     * Delete the "id" tag.
     *
     * @param id the id of the entity
     */
    void delete(String id);
}
