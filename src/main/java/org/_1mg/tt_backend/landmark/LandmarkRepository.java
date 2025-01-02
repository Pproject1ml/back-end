package org._1mg.tt_backend.landmark;

import org._1mg.tt_backend.landmark.entity.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandmarkRepository extends JpaRepository<Landmark, Integer> {
}
