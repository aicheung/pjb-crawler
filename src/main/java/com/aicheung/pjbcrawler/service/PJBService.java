package com.aicheung.pjbcrawler.service;

import com.aicheung.pjbcrawler.domain.Image;
import com.aicheung.pjbcrawler.domain.Tag;
import com.aicheung.pjbcrawler.repository.ImageRepository;
import com.aicheung.pjbcrawler.repository.TagRepository;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PJBService {
    private static final Logger log = LoggerFactory.getLogger(PJBService.class);

    @Value("${pjb.imageBasePath}")
    private String imageBasePath;

    @Value("${pjb.imageSaveFolder}")
    private String imageSaveFolder;

    @Value("${pjb.startIndex}")
    private Integer startIndex;

    @Value("${pjb.endIndex}")
    private Integer endIndex;

    private boolean isDone = false;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(20);

    private final RestTemplate http = new RestTemplate();

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TagRepository tagRepository;

    @Scheduled(fixedDelay = 1000)
    public void startDownload() {
        if(isDone) {
            return;
        }
        download();
        isDone = true;
    }

    private void download() {
        for(int i = startIndex; i <= endIndex; i++) {
            Runnable thread = new DownloadThread(imageBasePath + i, i);
            threadPool.submit(thread);
        }
    }

    private class DownloadThread implements Runnable{
        private String url;
        private int i;

        public DownloadThread(String url, int i) {
            this.url = url;
            this.i = i;
        }

        @Override
        public void run() {
            try {
                Image existing = imageRepository.findByImageId(i);
                if(existing != null) {
                    log.warn("ALREADY GOT THE IMAGE " + i);
                    return;
                }
                Document doc = Jsoup.connect(url).get();
                Elements imgElem = doc.select("div#bigwall > img");
                Elements uploadedElem = doc.select("a.right.user-link");
                Elements statElem = doc.select("div.stats.left");
                Elements tagsElem = doc.select(".taglist li a");

                String createdBy = Optional.of(uploadedElem.html()).orElse("").trim();
                //log.info("Created: " + createdBy);

                Pattern statPattern = Pattern.compile("(\\d+).+Views Today: (\\d+).+Favorites: (\\d+)");
                Matcher match = statPattern.matcher(Optional.of(statElem.html()).orElse(""));
                //log.info("Stat: " + statElem.html());

                int views = 0;
                int favorites = 0;

                if(match.find()) {
                    //log.info(match.group(1) + " " + match.group(2) + " " + match.group(3));
                    views = Integer.parseInt(match.group(1));
                    favorites = Integer.parseInt(match.group(3));
                }
                
                List<String> tags = new ArrayList<>();
                for (Element t :
                    tagsElem) {
                    if(t.text() != null ) {
                        String tag = StringUtils.replace(t.text(), "•", "").trim();
                        tags.add(tag);
                    }
                }
                //log.info("Tags: " + tags);

                if(imgElem != null && imgElem.outerHtml() != null) {
                    //log.info("IMG ELEM: " + imgElem.toString());
                    String url = imgElem.attr("src");
                    log.info("GETTING IMAGE:" + url);
                    byte[] imageBytes = http.getForObject(url, byte[].class);
                    Path imgPath = Paths.get(imageSaveFolder + i + ".jpg" );
                    if(!imgPath.getParent().toFile().exists()) {
                        Files.createDirectory(imgPath.getParent());
                    }
                    Files.write(Paths.get(imageSaveFolder + i + ".jpg" ), imageBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                    Image img = new Image();
                    imageRepository.save(img);
                    img.setImageId(i);
                    img.setOrigFileName(url);
                    img.setUploadedBy(createdBy);
                    img.setFavorites(favorites);
                    img.setViews(views);

                    Set<Tag> tagList = tags.stream().map(s -> {
                        Tag t = new Tag();
                        t.getImages().add(img);
                        t.setName(s);
                        t = tagRepository.save(t);
                        return t;
                    }).collect(Collectors.toSet());

                    img.setTags(tagList);
                    imageRepository.save(img);

                } else {
                    log.warn("URL has no iamge: " + url);
                }
            }
            catch (Exception e) {
                log.error("Failed: " + url, e);
            }

        }
    }
}
