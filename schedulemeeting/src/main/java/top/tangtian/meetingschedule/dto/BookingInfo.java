package top.tangtian.meetingschedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author tangtian
 * @date 2025-11-16 10:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingInfo {
	private Long id;
	private String roomName;
	private String title;
	private String organizer;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	private Integer attendees;
	private String description;
	private String status;
}
