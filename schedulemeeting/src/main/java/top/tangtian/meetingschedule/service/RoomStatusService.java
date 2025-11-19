package top.tangtian.meetingschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.tangtian.meetingschedule.dto.BookingSchedule;
import top.tangtian.meetingschedule.dto.RoomStatusResponse;
import top.tangtian.meetingschedule.entity.MeetingRoom;
import top.tangtian.meetingschedule.entity.RoomBooking;
import top.tangtian.meetingschedule.repository.MeetingRoomRepository;
import top.tangtian.meetingschedule.repository.RoomBookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2025-11-19 13:08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomStatusService {

	private final MeetingRoomRepository roomRepository;
	private final RoomBookingRepository bookingRepository;
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	public List<RoomStatusResponse> getRoomStatusForDate(LocalDate date) {
		log.info("查询日期 {} 的会议室状态", date);

		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

		List<MeetingRoom> allRooms = roomRepository.findAll();
		List<RoomBooking> bookingsForDay = bookingRepository.findBookingsBetween(startOfDay, endOfDay);

		return allRooms.stream()
				.map(room -> buildRoomStatus(room, bookingsForDay))
				.collect(Collectors.toList());
	}

	private RoomStatusResponse buildRoomStatus(MeetingRoom room, List<RoomBooking> allBookings) {
		// 过滤出该会议室的预订
		List<RoomBooking> roomBookings = allBookings.stream()
				.filter(booking -> booking.getRoom().getId().equals(room.getId()))
				.filter(booking -> "CONFIRMED".equals(booking.getStatus()))
				.sorted((b1, b2) -> b1.getStartTime().compareTo(b2.getStartTime()))
				.collect(Collectors.toList());

		// 转换为前端需要的格式
		List<BookingSchedule> schedules = roomBookings.stream()
				.map(this::convertToSchedule)
				.collect(Collectors.toList());

		// 确定状态
		String status = determineStatus(schedules.size());

		return RoomStatusResponse.builder()
				.name(room.getName())
				.capacity(room.getCapacity())
				.location(room.getLocation())
				.facilities(room.getFacilities())
				.status(status)
				.schedules(schedules)
				.build();
	}

	private BookingSchedule convertToSchedule(RoomBooking booking) {
		String startTime = booking.getStartTime().format(TIME_FORMATTER);
		String endTime = booking.getEndTime().format(TIME_FORMATTER);
		String timeRange = startTime + "-" + endTime;

		return BookingSchedule.builder()
				.time(timeRange)
				.title(booking.getTitle())
				.organizer(booking.getOrganizer())
				.build();
	}

	private String determineStatus(int bookingCount) {
		if (bookingCount == 0) {
			return "available";
		} else if (bookingCount <= 2) {
			return "partial";
		} else {
			return "booked";
		}
	}
}