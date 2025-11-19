package top.tangtian.meetingschedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import top.tangtian.meetingschedule.entity.RoomBooking;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tangtian
 * @date 2025-11-16 10:37
 */
@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

	@Query("SELECT b FROM RoomBooking b WHERE b.room.id = :roomId " +
			"AND b.status = 'CONFIRMED' " +
			"AND b.startTime < :endTime AND b.endTime > :startTime")
	List<RoomBooking> findConflictingBookings(
			@Param("roomId") Long roomId,
			@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime
	);

	@Query("SELECT b FROM RoomBooking b WHERE b.organizer = :organizer " +
			"ORDER BY b.startTime DESC")
	List<RoomBooking> findByOrganizer(@Param("organizer") String organizer);

	@Query("SELECT b FROM RoomBooking b WHERE b.startTime >= :start " +
			"AND b.endTime <= :end ORDER BY b.startTime")
	List<RoomBooking> findBookingsBetween(
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end
	);
}
