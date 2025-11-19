package top.tangtian.meetingschedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.tangtian.meetingschedule.dto.BookingInfo;
import top.tangtian.meetingschedule.dto.BookingRequest;
import top.tangtian.meetingschedule.dto.BookingResponse;
import top.tangtian.meetingschedule.entity.MeetingRoom;
import top.tangtian.meetingschedule.entity.RoomBooking;
import top.tangtian.meetingschedule.repository.MeetingRoomRepository;
import top.tangtian.meetingschedule.repository.RoomBookingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangtian
 * @date 2025-11-16 10:36
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

	private final MeetingRoomRepository roomRepository;
	private final RoomBookingRepository bookingRepository;

	@Transactional
	public BookingResponse createBooking(BookingRequest request) {
		log.info("Creating booking: {}", request);

		// 验证时间
		if (request.getStartTime().isAfter(request.getEndTime())) {
			return BookingResponse.builder()
					.success(false)
					.message("开始时间不能晚于结束时间")
					.build();
		}

		if (request.getStartTime().isBefore(LocalDateTime.now())) {
			return BookingResponse.builder()
					.success(false)
					.message("不能预订过去的时间")
					.build();
		}

		// 查找会议室
		MeetingRoom room = roomRepository.findByName(request.getRoomName())
				.orElseThrow(() -> new RuntimeException("会议室不存在: " + request.getRoomName()));

		// 检查容量
		if (request.getAttendees() != null && request.getAttendees() > room.getCapacity()) {
			return BookingResponse.builder()
					.success(false)
					.message(String.format("会议室容量不足，最多容纳%d人", room.getCapacity()))
					.build();
		}

		// 检查时间冲突
		List<RoomBooking> conflicts = bookingRepository.findConflictingBookings(
				room.getId(), request.getStartTime(), request.getEndTime()
		);

		if (!conflicts.isEmpty()) {
			return BookingResponse.builder()
					.success(false)
					.message("该时间段已被预订")
					.build();
		}

		// 创建预订
		RoomBooking booking = RoomBooking.builder()
				.room(room)
				.title(request.getTitle())
				.organizer(request.getOrganizer())
				.startTime(request.getStartTime())
				.endTime(request.getEndTime())
				.attendees(request.getAttendees())
				.description(request.getDescription())
				.status("CONFIRMED")
				.build();

		booking = bookingRepository.save(booking);

		return BookingResponse.builder()
				.success(true)
				.message("预订成功")
				.bookingId(booking.getId())
				.bookingInfo(convertToInfo(booking))
				.build();
	}

	public List<BookingInfo> getAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
		List<MeetingRoom> allRooms = roomRepository.findAll();
		return allRooms.stream()
				.filter(room -> isRoomAvailable(room.getId(), startTime, endTime))
				.map(room -> BookingInfo.builder()
						.roomName(room.getName())
						.build())
				.collect(Collectors.toList());
	}

	private boolean isRoomAvailable(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
		return bookingRepository.findConflictingBookings(roomId, startTime, endTime).isEmpty();
	}

	private BookingInfo convertToInfo(RoomBooking booking) {
		return BookingInfo.builder()
				.id(booking.getId())
				.roomName(booking.getRoom().getName())
				.title(booking.getTitle())
				.organizer(booking.getOrganizer())
				.startTime(booking.getStartTime())
				.endTime(booking.getEndTime())
				.attendees(booking.getAttendees())
				.description(booking.getDescription())
				.status(booking.getStatus())
				.build();
	}
}
