package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<UploadedFile, Integer> {

    UploadedFile findByFileID(Integer fileID);

}