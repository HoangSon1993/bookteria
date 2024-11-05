package org.sondev.post.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

/**
 * Sử dụng design pattern strategy (Chiến lược).
 * Dựa trên thời gian đã trôi qua kể từ 1 Instance cụ thể

 * */
@Component
public class DateTimeFormatter {
    /**
     * Với 1 map các strategy được định thời gian khác nhau (60s, 3600s, 86400);
     * Việc sử dụng LinkedHasmap giữ thứ tự các phần tử để các ngưỡng được kiểm tra theo thứ tự.
     * */
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>();

    public DateTimeFormatter() {
        strategyMap.put(60L, this::formatInSeconds);
        strategyMap.put(3600L, this::formatInMinutes);
        strategyMap.put(86400L, this::formatInHours);
        strategyMap.put(Long.MAX_VALUE, this::formatInDate);
    }

    /**
     * Phương thức format() tính toán giây đã trôi qua giữa Instant được cung cấp và thời gian hiện tại.
     * Sau đó tìm kiếm strategyMap để lấy phần tử đầu tiên có ngưỡng thời gian lớn hơn elapseSconds.
     * Phần tử khớp sẽ cung cấp hàm định dạng tương ứng, hàm này sẽ áp dụng lên Instant để trả về chuỗi định dạng.
     * */
    public String format(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());

        /*
        		if (elapseSeconds < 60) {
        			return elapseSeconds + " seconds";
        		} else if (elapseSeconds < 3600) {
        			return elapseSeconds / 60 + " minutes";
        		} else if (elapseSeconds < 86400) {
        			return elapseSeconds / 3600 + " hours";
        		}
        */

        var strategy = strategyMap.entrySet().stream()
                .filter(longFunctionEntry -> elapseSeconds < longFunctionEntry.getKey())
                .findFirst()
                .get();

        return strategy.getValue().apply(instant);
    }


    /**
     * Tính thời gian trôi qua bằng giây, dùng cho trường hợp dưới 60 giây.
     * */
    private String formatInSeconds(Instant instant) {
        long elapseSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        return elapseSeconds + " seconds";
    }

    /**
     * Tính thời gian trôi qua bằng phút, dùng cho trường hợp dưới 1 giờ.
     * */
    private String formatInMinutes(Instant instant) {
        long elapseMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return elapseMinutes + " minutes";
    }

    /**
     * Tính thời gian trôi qua bằng giờ, dùng cho trường hợp dưới 1 ngày.
     * */
    private String formatInHours(Instant instant) {
        long elapseHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return elapseHours + " hours";
    }

    /**
     * Tính thời gian trôi qua bằng ngày, dùng cho trường hợp trên 1 ngày.
     * */
    private String formatInDate(Instant instant) {
        long elapseDays = ChronoUnit.DAYS.between(instant, Instant.now());
        return elapseDays + " days";
    }
}
