package org.example.sharing.store.repository.spetifications;

import org.example.sharing.store.entity.Booking;
import org.example.sharing.store.entity.Booking_;
import org.example.sharing.store.entity.Flat_;
import org.example.sharing.store.entity.User_;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.example.sharing.utils.ServiceUtils.checkNulls;

public class BookingSpecification {

    public static Specification<Booking> getByUserId(UUID userId) {
        return (root, query, criteriaBuilder) -> {
            if (checkNulls(userId)) {
                Subquery<Booking> sq = query.subquery(Booking.class);
                Root<Booking> sessionActivities = sq.from(Booking.class);
                sq.select(sessionActivities)
                        .where(criteriaBuilder.equal(sessionActivities.join(Booking_.USER).get(User_.ID), userId));
                return criteriaBuilder.in(root).value(sq);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Booking> getByFlatUserId(UUID flatUserId) {
        return (root, query, criteriaBuilder) -> {
            if (checkNulls(flatUserId)) {
                Subquery<Booking> sq = query.subquery(Booking.class);
                Root<Booking> sessionActivities = sq.from(Booking.class);
                sq.select(sessionActivities)
                        .where(
                                criteriaBuilder.equal(sessionActivities
                                        .join(Booking_.FLAT)
                                        .join(Flat_.USER)
                                        .get(User_.ID), flatUserId)
                        );
                return criteriaBuilder.in(root).value(sq);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Booking> getByFlatId(UUID flatId) {
        return (root, query, criteriaBuilder) -> {
            if (checkNulls(flatId)) {
                Subquery<Booking> sq = query.subquery(Booking.class);
                Root<Booking> sessionActivities = sq.from(Booking.class);
                sq.select(sessionActivities)
                        .where(
                                criteriaBuilder.equal(sessionActivities
                                        .join(Booking_.FLAT)
                                        .get(Flat_.ID), flatId)
                        );
                return criteriaBuilder.in(root).value(sq);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Booking> getByStatuses(@NonNull Set<BookingStatusEnum> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (!statuses.isEmpty()) {
                return criteriaBuilder.in(root.get(Booking_.BOOKING_STATUS)).value(statuses);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Booking> getByEndTime(LocalDateTime time, Boolean isEndGreaterThan) {
        return (root, query, criteriaBuilder) -> {
            if (checkNulls(time, isEndGreaterThan)) {
                if (isEndGreaterThan) {
                    return criteriaBuilder.greaterThan(root.get(Booking_.END), time);
                }
                return criteriaBuilder.lessThan(root.get(Booking_.END), time);
            }
            return criteriaBuilder.conjunction();
        };
    }
}
