package top.tangtian.meetingschedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2025-11-16 10:38
 */
@Entity
@Table(name = "room_booking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomBooking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "room_id", nullable = false)
	private MeetingRoom room;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String organizer;

	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalDateTime endTime;

	private Integer attendees;

	private String description;

	@Column(length = 20)
	@Builder.Default
	private String status = "CONFIRMED";

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		if (status == null) {
			status = "CONFIRMED";
		}
	}
}
