package org.example.sharing.store.repository.spetifications;

import org.example.sharing.rest.dto.request.FlatFilterRequestDTO;
import org.example.sharing.store.entity.Address;
import org.example.sharing.store.entity.Address_;
import org.example.sharing.store.entity.Booking;
import org.example.sharing.store.entity.Booking_;
import org.example.sharing.store.entity.Flat;
import org.example.sharing.store.entity.Flat_;
import org.example.sharing.store.entity.enums.BookingStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class FlatSpecification {
    public static Specification<Flat> filterBy(FlatFilterRequestDTO flatFilterRequestDTO) {
        return Specification.where(getByStatus())
                .and(getByCity(flatFilterRequestDTO.getCity()))
                .and(getByCountRooms(flatFilterRequestDTO.getNumberOfRooms()))
                .and(getByPrice(flatFilterRequestDTO.getMinPrice(), flatFilterRequestDTO.getMaxPrice()))
                .and(getByBookingDate(flatFilterRequestDTO.getBookingStart(), flatFilterRequestDTO.getBookingEnd()));
    }

    private static Specification<Flat> getByStatus() {
        return (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Flat_.IS_HIDDEN), true)));
    }

    private static Specification<Flat> getByCity(String city) {
        return ((root, query, criteriaBuilder) -> {
            if (StringUtils.isNotEmpty(city)) {
                Subquery<Flat> sq = query.subquery(Flat.class);
                Root<Flat> sessionActivities = sq.from(Flat.class);
                Join<Flat, Address> sqEmp = sessionActivities.join(Flat_.ADDRESS);
                sq.select(sessionActivities)
                        .where(
                                criteriaBuilder.equal(sqEmp.get(Address_.CITY), city)
                        );
                return criteriaBuilder.in(root)
                        .value(sq);
            }
            return criteriaBuilder.conjunction();
        });
    }

    private static Specification<Flat> getByCountRooms(Integer countRooms) {
        return ((root, query, criteriaBuilder) -> {
            if (countRooms != null && countRooms != 0) {
                return criteriaBuilder.equal(root.get(Flat_.NUMBER_OF_ROOMS), countRooms);
            }
            return criteriaBuilder.conjunction();
        });
    }

    private static Specification<Flat> getByPrice(Integer minPrice, Integer maxPrice) {
        return ((root, query, criteriaBuilder) -> {
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get(Flat_.PRICE), minPrice, maxPrice);
            }

            if (maxPrice != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Flat_.PRICE), maxPrice);
            }

            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Flat_.PRICE), minPrice);
            }

            return criteriaBuilder.conjunction();
        });
    }

    private static Specification<Flat> getByBookingDate(Long startBooking, Long endBooking) {
        return ((root, query, criteriaBuilder) -> {
            if (startBooking != null || endBooking != null) {
                Subquery<Flat> flatSubquery = query.subquery(Flat.class);
                Root<Flat> flatRoot = flatSubquery.from(Flat.class);

                Subquery<Booking> bookingSubquery = query.subquery(Booking.class);
                Root<Booking> bookingRoot = bookingSubquery.from(Booking.class);

                if (startBooking != null && endBooking != null) {
                    bookingSubquery
                            .select(bookingRoot.get(Booking_.FLAT).get(Flat_.ID))
                            .where(criteriaBuilder.and(
                                    criteriaBuilder.or(
                                            criteriaBuilder.between(bookingRoot.get(Booking_.START), startBooking, endBooking),
                                            criteriaBuilder.between(bookingRoot.get(Booking_.END), startBooking, endBooking))
                                    ), criteriaBuilder.notEqual(bookingRoot.get(Booking_.BOOKING_STATUS), BookingStatusEnum.BOOKING_DENY)
                            );
                } else if (startBooking != null) {
                    bookingSubquery
                            .select(bookingRoot.get(Booking_.FLAT).get(Flat_.ID))
                            .where(criteriaBuilder.and(
                                    criteriaBuilder.greaterThan(bookingRoot.get(Booking_.END), startBooking)),
                                    criteriaBuilder.notEqual(bookingRoot.get(Booking_.BOOKING_STATUS), BookingStatusEnum.BOOKING_DENY)
                            );
                } else {
                    bookingSubquery
                            .select(bookingRoot.get(Booking_.FLAT).get(Flat_.ID))
                            .where(criteriaBuilder.and(
                                    criteriaBuilder.lessThan(bookingRoot.get(Booking_.START), endBooking)),
                                    criteriaBuilder.notEqual(bookingRoot.get(Booking_.BOOKING_STATUS), BookingStatusEnum.BOOKING_DENY)
                            );
                }
                flatSubquery
                        .select(flatRoot)
                        .where(
                                criteriaBuilder
                                        .in(flatRoot.get(Flat_.ID))
                                        .value(bookingSubquery)
                                        .not()
                        );
                return criteriaBuilder.in(root).value(flatSubquery);
            }

            return criteriaBuilder.conjunction();
        });
    }
}
