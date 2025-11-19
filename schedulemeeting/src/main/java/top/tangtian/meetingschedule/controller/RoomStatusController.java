package top.tangtian.meetingschedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import top.tangtian.meetingschedule.dto.RoomStatusResponse;
import top.tangtian.meetingschedule.service.RoomStatusService;

import java.time.LocalDate;
import java.util.List;

/**
 * @author tangtian
 * @date 2025-11-19 13:06
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RoomStatusController {

	private final RoomStatusService roomStatusService;

	@GetMapping("/status")
	public List<RoomStatusResponse> getRoomStatus(
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate date) {

		if (date == null) {
			date = LocalDate.now();
		}

		log.info("获取会议室状态 - 日期: {}", date);
		return roomStatusService.getRoomStatusForDate(date);
	}
}
