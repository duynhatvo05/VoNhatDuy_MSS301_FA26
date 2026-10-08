package com.fudn.movieservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    // ---- ID co dinh (ObjectId 24 hex) ----
    public static final String GENRE_ACTION = "66f000000000000000000001";
    public static final String GENRE_ANIMATION = "66f000000000000000000002";
    public static final String GENRE_HORROR = "66f000000000000000000003";
    public static final String GENRE_ROMANCE = "66f000000000000000000004";
    public static final String GENRE_SCIFI = "66f000000000000000000005";

    public static final String ROOM_01 = "66f100000000000000000001";
    public static final String ROOM_02 = "66f100000000000000000002";
    public static final String ROOM_IMAX = "66f100000000000000000003";
    public static final String ROOM_04_MAINTENANCE = "66f100000000000000000004";

    public static final String MOVIE_GALAXY = "66f200000000000000000001";
    public static final String MOVIE_HAUNTED = "66f200000000000000000002";
    public static final String MOVIE_ROBOT = "66f200000000000000000003";
    public static final String MOVIE_SUMMER_ENDED = "66f200000000000000000004";

    private final MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        if (mongoTemplate.getCollection("genres").countDocuments() > 0) {
            log.info("MongoDB already has seed data - skip seeding");
            return;
        }

        mongoTemplate.getCollection("genres").insertMany(List.of(
                new Document("_id", GENRE_ACTION).append("genreName", "Hành động").append("description", "Phim hành động, võ thuật"),
                new Document("_id", GENRE_ANIMATION).append("genreName", "Hoạt hình").append("description", "Phim hoạt hình cho mọi lứa tuổi"),
                new Document("_id", GENRE_HORROR).append("genreName", "Kinh dị").append("description", "Phim kinh dị, giật gân"),
                new Document("_id", GENRE_ROMANCE).append("genreName", "Tình cảm").append("description", "Phim tình cảm, lãng mạn"),
                new Document("_id", GENRE_SCIFI).append("genreName", "Khoa học viễn tưởng").append("description", "Phim khoa học viễn tưởng")
        ));

        mongoTemplate.getCollection("cinema_rooms").insertMany(List.of(
                new Document("_id", ROOM_01).append("roomName", "Room 01").append("roomType", "STANDARD").append("seatRows", 8).append("seatsPerRow", 10).append("roomStatus", "ACTIVE"),
                new Document("_id", ROOM_02).append("roomName", "Room 02").append("roomType", "THREE_D").append("seatRows", 6).append("seatsPerRow", 8).append("roomStatus", "ACTIVE"),
                new Document("_id", ROOM_IMAX).append("roomName", "IMAX 01").append("roomType", "IMAX").append("seatRows", 10).append("seatsPerRow", 12).append("roomStatus", "ACTIVE"),
                new Document("_id", ROOM_04_MAINTENANCE).append("roomName", "Room 04").append("roomType", "STANDARD").append("seatRows", 5).append("seatsPerRow", 8).append("roomStatus", "MAINTENANCE")
        ));

        mongoTemplate.getCollection("movies").insertMany(List.of(
                new Document("_id", MOVIE_GALAXY).append("title", "Galaxy Rangers").append("description", "Đội biệt kích không gian bảo vệ dải ngân hà.")
                        .append("director", "John Carter").append("durationMinutes", 125).append("language", "English")
                        .append("ageRating", "T13").append("releaseDate", LocalDate.of(2026, 9, 20).toString())
                        .append("genreId", GENRE_SCIFI).append("movieStatus", "NOW_SHOWING"),
                new Document("_id", MOVIE_HAUNTED).append("title", "Ngôi Nhà Ma Ám").append("description", "Một gia đình chuyển đến căn nhà cổ ở Đà Lạt.")
                        .append("director", "Trần Hữu Tấn").append("durationMinutes", 100).append("language", "Tiếng Việt")
                        .append("ageRating", "T18").append("releaseDate", LocalDate.of(2026, 9, 27).toString())
                        .append("genreId", GENRE_HORROR).append("movieStatus", "NOW_SHOWING"),
                new Document("_id", MOVIE_ROBOT).append("title", "Robot Nhỏ Phiêu Lưu Ký").append("description", "Chú robot nhỏ đi tìm đường về nhà.")
                        .append("director", "Anna Lee").append("durationMinutes", 95).append("language", "English")
                        .append("ageRating", "P").append("releaseDate", LocalDate.of(2026, 11, 15).toString())
                        .append("genreId", GENRE_ANIMATION).append("movieStatus", "COMING_SOON"),
                new Document("_id", MOVIE_SUMMER_ENDED).append("title", "Mùa Hè Năm Ấy").append("description", "Câu chuyện tình đầu tuổi học trò.")
                        .append("director", "Nguyễn Quang Dũng").append("durationMinutes", 110).append("language", "Tiếng Việt")
                        .append("ageRating", "T16").append("releaseDate", LocalDate.of(2026, 6, 1).toString())
                        .append("genreId", GENRE_ROMANCE).append("movieStatus", "ENDED")
        ));

        mongoTemplate.getCollection("showtimes").insertMany(List.of(
                showtimeDoc("66f300000000000000000001", MOVIE_GALAXY, ROOM_01, "2026-12-20T19:00", 125, 95000, "SCHEDULED"),
                showtimeDoc("66f300000000000000000002", MOVIE_GALAXY, ROOM_IMAX, "2026-12-20T20:00", 125, 150000, "SCHEDULED"),
                showtimeDoc("66f300000000000000000003", MOVIE_HAUNTED, ROOM_02, "2026-12-21T21:00", 100, 95000, "SCHEDULED"),
                showtimeDoc("66f300000000000000000004", MOVIE_ROBOT, ROOM_01, "2026-12-22T09:00", 95, 75000, "SCHEDULED"),
                showtimeDoc("66f300000000000000000005", MOVIE_HAUNTED, ROOM_01, "2026-12-23T19:00", 100, 95000, "CANCELLED")
        ));

        log.info("Seeded MongoDB: {} genres, {} rooms, {} movies, {} showtimes",
                mongoTemplate.getCollection("genres").countDocuments(),
                mongoTemplate.getCollection("cinema_rooms").countDocuments(),
                mongoTemplate.getCollection("movies").countDocuments(),
                mongoTemplate.getCollection("showtimes").countDocuments());
    }

    private static Document showtimeDoc(String id, String movieId, String roomId, String start,
                                        int durationMinutes, long price, String status) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        return new Document("_id", id)
                .append("movieId", movieId)
                .append("roomId", roomId)
                .append("startTime", toDate(startTime))
                .append("endTime", toDate(endTime))
                .append("ticketPrice", new Decimal128(new BigDecimal(price)))
                .append("showtimeStatus", status);
    }

    private static Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }
}
