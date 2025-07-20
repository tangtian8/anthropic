package com.example.adoptions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author tangtian
 * @date 2025-07-18 11:11
 */
@Repository
public interface DogRepository extends JpaRepository<Dog, Integer> {

}
