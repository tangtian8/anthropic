package top.tangtian.meetingschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tangtian
 * @date 2025-11-16 10:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
	private boolean success;
	private String message;
	private Long bookingId;
	private BookingInfo bookingInfo;
}
