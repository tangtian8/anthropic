package top.tangtian.meetingschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * @author tangtian
 * @date 2025-11-19 13:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatusResponse {
	private String name;
	private Integer capacity;
	private String location;
	private String facilities;
	private String status; // available, partial, booked
	private List<BookingSchedule> schedules;
}