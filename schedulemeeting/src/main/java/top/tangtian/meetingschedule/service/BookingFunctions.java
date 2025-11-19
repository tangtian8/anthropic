package top.tangtian.meetingschedule.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import top.tangtian.meetingschedule.dto.BookingRequest;
import top.tangtian.meetingschedule.dto.BookingResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * @author tangtian
 * @date 2025-11-16 10:35
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class BookingFunctions {

	private final BookingService bookingService;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Bean
	@Description("预订会议室。需要提供会议室名称、会议主题、组织者、开始时间、结束时间等信息")
	public Function<BookingFunctionRequest, BookingFunctionResponse> bookMeetingRoom() {
		return request -> {
			log.info("AI调用预订功能: {}", request);

			try {
				LocalDateTime startTime = LocalDateTime.parse(request.startTime(), FORMATTER);
				LocalDateTime endTime = LocalDateTime.parse(request.endTime(), FORMATTER);

				BookingRequest bookingRequest = BookingRequest.builder()
						.roomName(request.roomName())
						.title(request.title())
						.organizer(request.organizer())
						.startTime(startTime)
						.endTime(endTime)
						.attendees(request.attendees())
						.description(request.description())
						.build();

				BookingResponse response = bookingService.createBooking(bookingRequest);

				return new BookingFunctionResponse(
						response.isSuccess(),
						response.getMessage(),
						response.getBookingId()
				);

			} catch (Exception e) {
				log.error("预订失败", e);
				return new BookingFunctionResponse(false, "预订失败: " + e.getMessage(), null);
			}
		};
	}

	@JsonClassDescription("预订会议室的请求参数")
	public record BookingFunctionRequest(
			@JsonProperty(required = true)
			@JsonPropertyDescription("会议室名称，如：会议室A、会议室B、会议室C、大会议厅")
			String roomName,

			@JsonProperty(required = true)
			@JsonPropertyDescription("会议主题或标题")
			String title,

			@JsonProperty(required = true)
			@JsonPropertyDescription("会议组织者姓名")
			String organizer,

			@JsonProperty(required = true)
			@JsonPropertyDescription("开始时间，格式: yyyy-MM-dd HH:mm:ss，例如: 2025-11-15 14:00:00")
			String startTime,

			@JsonProperty(required = true)
			@JsonPropertyDescription("结束时间，格式: yyyy-MM-dd HH:mm:ss，例如: 2025-11-15 16:00:00")
			String endTime,

			@JsonPropertyDescription("参会人数")
			Integer attendees,

			@JsonPropertyDescription("会议描述或备注")
			String description
	) {}

	public record BookingFunctionResponse(
			boolean success,
			String message,
			Long bookingId
	) {}
}