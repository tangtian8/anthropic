package top.tangtian.meetingschedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2025-11-16 10:30
 */
@Entity
@Table(name = "meeting_room")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private Integer capacity;

	private String location;

	private String facilities;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
