package com.aicheung.pjbcrawler.service.impl;

import com.aicheung.pjbcrawler.service.ImageService;
import com.aicheung.pjbcrawler.domain.Image;
import com.aicheung.pjbcrawler.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service Implementation for managing Image.
 */
@Service
public class ImageServiceImpl implements ImageService {

    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Save a image.
     *
     * @param image the entity to save
     * @return the persisted entity
     */
    @Override
    public Image save(Image image) {
        log.debug("Request to save Image : {}", image);
        return imageRepository.save(image);
    }

    /**
     * Get all the images.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<Image> findAll(Pageable pageable) {
        log.debug("Request to get all Images");
        return imageRepository.findAll(pageable);
    }

    /**
     * Get all the Image with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    public Page<Image> findAllWithEagerRelationships(Pageable pageable) {
        return imageRepository.findAllWithEagerRelationships(pageable);
    }
    

    /**
     * Get one image by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<Image> findOne(String id) {
        log.debug("Request to get Image : {}", id);
        return imageRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the image by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Image : {}", id);
        imageRepository.deleteById(id);
    }
}
