package top.tangtian.meetingschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author tangtian
 * @date 2025-11-19 13:07
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSchedule {
	private String time;
	private String title;
	private String organizer;
}