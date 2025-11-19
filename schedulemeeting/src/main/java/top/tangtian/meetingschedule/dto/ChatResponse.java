package top.tangtian.meetingschedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tangtian
 * @date 2025-11-16 10:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
	private String response;
	private BookingInfo bookingInfo;
}
