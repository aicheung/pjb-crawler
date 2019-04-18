package com.aicheung.pjbcrawler.web.rest;

import com.aicheung.pjbcrawler.PjbCrawlerApp;

import com.aicheung.pjbcrawler.domain.Image;
import com.aicheung.pjbcrawler.repository.ImageRepository;
import com.aicheung.pjbcrawler.service.ImageService;
import com.aicheung.pjbcrawler.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;


import static com.aicheung.pjbcrawler.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ImageResource REST controller.
 *
 * @see ImageResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PjbCrawlerApp.class)
public class ImageResourceIntTest {

    private static final Integer DEFAULT_IMAGE_ID = 1;
    private static final Integer UPDATED_IMAGE_ID = 2;

    private static final String DEFAULT_ORIG_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIG_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_UPLOADED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPLOADED_BY = "BBBBBBBBBB";

    private static final Integer DEFAULT_VIEWS = 1;
    private static final Integer UPDATED_VIEWS = 2;

    private static final Integer DEFAULT_FAVORITES = 1;
    private static final Integer UPDATED_FAVORITES = 2;

    @Autowired
    private ImageRepository imageRepository;

    @Mock
    private ImageRepository imageRepositoryMock;

    @Mock
    private ImageService imageServiceMock;

    @Autowired
    private ImageService imageService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restImageMockMvc;

    private Image image;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ImageResource imageResource = new ImageResource(imageService);
        this.restImageMockMvc = MockMvcBuilders.standaloneSetup(imageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createEntity() {
        Image image = new Image()
            .imageId(DEFAULT_IMAGE_ID)
            .origFileName(DEFAULT_ORIG_FILE_NAME)
            .uploadedBy(DEFAULT_UPLOADED_BY)
            .views(DEFAULT_VIEWS)
            .favorites(DEFAULT_FAVORITES);
        return image;
    }

    @Before
    public void initTest() {
        imageRepository.deleteAll();
        image = createEntity();
    }

    @Test
    public void createImage() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // Create the Image
        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(image)))
            .andExpect(status().isCreated());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate + 1);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getImageId()).isEqualTo(DEFAULT_IMAGE_ID);
        assertThat(testImage.getOrigFileName()).isEqualTo(DEFAULT_ORIG_FILE_NAME);
        assertThat(testImage.getUploadedBy()).isEqualTo(DEFAULT_UPLOADED_BY);
        assertThat(testImage.getViews()).isEqualTo(DEFAULT_VIEWS);
        assertThat(testImage.getFavorites()).isEqualTo(DEFAULT_FAVORITES);
    }

    @Test
    public void createImageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // Create the Image with an existing ID
        image.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(image)))
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllImages() throws Exception {
        // Initialize the database
        imageRepository.save(image);

        // Get all the imageList
        restImageMockMvc.perform(get("/api/images?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId())))
            .andExpect(jsonPath("$.[*].imageId").value(hasItem(DEFAULT_IMAGE_ID)))
            .andExpect(jsonPath("$.[*].origFileName").value(hasItem(DEFAULT_ORIG_FILE_NAME.toString())))
            .andExpect(jsonPath("$.[*].uploadedBy").value(hasItem(DEFAULT_UPLOADED_BY.toString())))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS)))
            .andExpect(jsonPath("$.[*].favorites").value(hasItem(DEFAULT_FAVORITES)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllImagesWithEagerRelationshipsIsEnabled() throws Exception {
        ImageResource imageResource = new ImageResource(imageServiceMock);
        when(imageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restImageMockMvc = MockMvcBuilders.standaloneSetup(imageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restImageMockMvc.perform(get("/api/images?eagerload=true"))
        .andExpect(status().isOk());

        verify(imageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllImagesWithEagerRelationshipsIsNotEnabled() throws Exception {
        ImageResource imageResource = new ImageResource(imageServiceMock);
            when(imageServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restImageMockMvc = MockMvcBuilders.standaloneSetup(imageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restImageMockMvc.perform(get("/api/images?eagerload=true"))
        .andExpect(status().isOk());

            verify(imageServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    public void getImage() throws Exception {
        // Initialize the database
        imageRepository.save(image);

        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", image.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(image.getId()))
            .andExpect(jsonPath("$.imageId").value(DEFAULT_IMAGE_ID))
            .andExpect(jsonPath("$.origFileName").value(DEFAULT_ORIG_FILE_NAME.toString()))
            .andExpect(jsonPath("$.uploadedBy").value(DEFAULT_UPLOADED_BY.toString()))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS))
            .andExpect(jsonPath("$.favorites").value(DEFAULT_FAVORITES));
    }

    @Test
    public void getNonExistingImage() throws Exception {
        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateImage() throws Exception {
        // Initialize the database
        imageService.save(image);

        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Update the image
        Image updatedImage = imageRepository.findById(image.getId()).get();
        updatedImage
            .imageId(UPDATED_IMAGE_ID)
            .origFileName(UPDATED_ORIG_FILE_NAME)
            .uploadedBy(UPDATED_UPLOADED_BY)
            .views(UPDATED_VIEWS)
            .favorites(UPDATED_FAVORITES);

        restImageMockMvc.perform(put("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedImage)))
            .andExpect(status().isOk());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getImageId()).isEqualTo(UPDATED_IMAGE_ID);
        assertThat(testImage.getOrigFileName()).isEqualTo(UPDATED_ORIG_FILE_NAME);
        assertThat(testImage.getUploadedBy()).isEqualTo(UPDATED_UPLOADED_BY);
        assertThat(testImage.getViews()).isEqualTo(UPDATED_VIEWS);
        assertThat(testImage.getFavorites()).isEqualTo(UPDATED_FAVORITES);
    }

    @Test
    public void updateNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Create the Image

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImageMockMvc.perform(put("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(image)))
            .andExpect(status().isBadRequest());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    public void deleteImage() throws Exception {
        // Initialize the database
        imageService.save(image);

        int databaseSizeBeforeDelete = imageRepository.findAll().size();

        // Delete the image
        restImageMockMvc.perform(delete("/api/images/{id}", image.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Image.class);
        Image image1 = new Image();
        image1.setId("id1");
        Image image2 = new Image();
        image2.setId(image1.getId());
        assertThat(image1).isEqualTo(image2);
        image2.setId("id2");
        assertThat(image1).isNotEqualTo(image2);
        image1.setId(null);
        assertThat(image1).isNotEqualTo(image2);
    }
}
