package com.noCompany.BackendStableDiffusionWebApp.repository;

import com.noCompany.BackendStableDiffusionWebApp.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("select i from Image i where (i.user.username = ?#{authentication.name} and i.id = :id and not i.isModelThumbnail)")
    Optional<Image> findImageByUserWithId(Long id);

    @Query("select i from Image i where (i.isModelThumbnail and i.id = :id)")
    Optional<Image> findThumbnailWithId(Long id);

    @Query("select i from Image i where i.user.username = ?#{authentication.name} and not i.isModelThumbnail")
    List<Image> findImagesByUser();

}
