package com.aicheung.pjbcrawler.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Image.
 */
@Document(collection = "image")
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;

    @Field("image_id")
    private Integer imageId;

    @Field("orig_file_name")
    private String origFileName;

    @Field("uploaded_by")
    private String uploadedBy;

    @Field("views")
    private Integer views;

    @Field("favorites")
    private Integer favorites;

    @DBRef
    @Field("tags")
    private Set<Tag> tags = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getImageId() {
        return imageId;
    }

    public Image imageId(Integer imageId) {
        this.imageId = imageId;
        return this;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getOrigFileName() {
        return origFileName;
    }

    public Image origFileName(String origFileName) {
        this.origFileName = origFileName;
        return this;
    }

    public void setOrigFileName(String origFileName) {
        this.origFileName = origFileName;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public Image uploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
        return this;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Integer getViews() {
        return views;
    }

    public Image views(Integer views) {
        this.views = views;
        return this;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public Image favorites(Integer favorites) {
        this.favorites = favorites;
        return this;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Image tags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Image addTags(Tag tag) {
        this.tags.add(tag);
        tag.getImages().add(this);
        return this;
    }

    public Image removeTags(Tag tag) {
        this.tags.remove(tag);
        tag.getImages().remove(this);
        return this;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Image image = (Image) o;
        if (image.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), image.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Image{" +
            "id=" + getId() +
            ", imageId=" + getImageId() +
            ", origFileName='" + getOrigFileName() + "'" +
            ", uploadedBy='" + getUploadedBy() + "'" +
            ", views=" + getViews() +
            ", favorites=" + getFavorites() +
            "}";
    }
}
