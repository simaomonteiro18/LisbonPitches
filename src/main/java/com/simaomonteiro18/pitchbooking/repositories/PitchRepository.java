package com.simaomonteiro18.pitchbooking.repositories;

import com.simaomonteiro18.pitchbooking.entities.Pitch;
import com.simaomonteiro18.pitchbooking.entities.enums.PitchAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PitchRepository extends JpaRepository<Pitch, Long> {

    List<Pitch> findByPitchAccess(PitchAccess pitchAccess);

    List<Pitch> findByFeaturedTrueAndPitchAccess(PitchAccess pitchAccess);

    boolean existsByName(String name);

    @Query("SELECT p FROM Pitch p WHERE " +
            "(:city IS NULL OR p.city = :city) AND " +
            "(:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%')) AND " +
            "(:pitchAccess IS NULL OR p.pitchAccess = :pitchAccess)")
    List<Pitch> search(@Param("city") String city, @Param("name") String name, @Param("pitchAccess") PitchAccess pitchAccess);

}
