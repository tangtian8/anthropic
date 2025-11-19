package top.tangtian.meetingschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.tangtian.meetingschedule.entity.MeetingRoom;

import java.util.List;
import java.util.Optional;

/**
 * @author tangtian
 * @date 2025-11-16 10:37
 */
@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
	Optional<MeetingRoom> findByName(String name);

	@Query("SELECT r FROM MeetingRoom r WHERE r.capacity >= :capacity")
	List<MeetingRoom> findByCapacityGreaterThanEqual(@Param("capacity") Integer capacity);
}